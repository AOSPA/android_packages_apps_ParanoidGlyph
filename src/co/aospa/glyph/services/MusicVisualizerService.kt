/*
 * SPDX-FileCopyrightText: Paranoid Android
 * SPDX-License-Identifier: Apache-2.0
 */

package co.aospa.glyph.services

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.AudioPlaybackConfiguration
import android.media.audiofx.Visualizer
import android.os.IBinder
import co.aospa.glyph.manager.AnimationManager
import co.aospa.glyph.utils.Constants
import kotlin.math.sqrt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

private const val FFT_BUCKETS = 100
private const val NOISE_FLOOR = 10.0f
private const val NORMALIZE_DIVISOR = 195.0
private const val NORMALIZE_SCALE = 2.5
private const val MIN_BAND_ENERGY = 0.02
private const val SILENCE_RMS_MB = -6000
private const val SIGNAL_RMS_MB = -5700
private const val SIGNAL_CONFIRMATION_FRAMES = 3
private const val SILENCE_CONFIRMATION_FRAMES = 5

/** One band's FFT bucket range and its adaptive trigger/decay tuning. */
private data class BandConfig(
    val bucketStart: Int,
    val bucketCount: Int,
    val decayWindowMs: Long,
    val triggerRatio: Double,
    val triggerDecay: Double,
    val ceilingDecay: Double,
)

/** Nothing's AudioReactiveGlyph tuning, 5-channel config. */
private val BAND_CONFIGS = listOf(
    BandConfig(8, 4, 1000, 0.75, 0.56, 0.65),
    BandConfig(15, 8, 500, 0.7, 0.66, 0.65),
    BandConfig(0, 1, 300, 0.75, 0.56, 0.95),
    BandConfig(30, 15, 200, 0.7, 0.66, 0.63),
    BandConfig(55, 20, 100, 0.7, 0.66, 0.63),
)

private class BandState {
    private var ceiling = 0.0
    private var lastBeatTime = 0L
    var brightness = 0.0
        private set

    fun reset() {
        ceiling = 0.0
        lastBeatTime = 0L
        brightness = 0.0
    }

    fun update(value: Double, config: BandConfig, now: Long) {
        if (value > ceiling) ceiling = value

        if (value > MIN_BAND_ENERGY && value >= ceiling * config.triggerRatio) {
            brightness = 1.0
            lastBeatTime = now
        } else {
            decayBrightness(config)
        }

        if (now - lastBeatTime > config.decayWindowMs &&
            value < ceiling * config.triggerRatio
        ) {
            ceiling = maxOf(
                ceiling * config.ceilingDecay,
                MIN_BAND_ENERGY / config.triggerRatio,
            )
        }
    }

    fun decayBrightness(config: BandConfig) {
        brightness *= config.triggerDecay
        if (brightness < 0.05) brightness = 0.0
    }
}

/** The active media playback's session ID, or null if none is playing. */
private fun List<AudioPlaybackConfiguration>.activeMediaSessionId(): Int? =
    firstOrNull {
        it.audioAttributes.usage == AudioAttributes.USAGE_MEDIA && it.isActive
    }?.sessionId

/**
 * Drives the Glyph LEDs from the live output mix's FFT.
 *
 * Each band tracks its own adaptive peak, so a beat is a value clearing a fraction of that
 * band's recent ceiling rather than a fixed threshold, and brightness decays exponentially
 * between beats instead of a fixed-length flash. FFT captures are volume-normalized while the
 * unscaled RMS level gates silence and quiet track endings.
 */
class MusicVisualizerService : Service() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO.limitedParallelism(1))
    private val audioManager by lazy { getSystemService(AudioManager::class.java) }
    private var visualizer: Visualizer? = null
    private var sessionId: Int? = null

    private val bands = List(BAND_CONFIGS.size) { BandState() }
    private val measurement = Visualizer.MeasurementPeakRms()
    private var signalPresent = false
    private var signalConfirmationFrames = 0
    private var wasSilent = true

    private val dataCaptureListener = object : Visualizer.OnDataCaptureListener {
        override fun onWaveFormDataCapture(
            visualizer: Visualizer,
            waveform: ByteArray,
            samplingRate: Int,
        ) {}

        override fun onFftDataCapture(visualizer: Visualizer, fft: ByteArray, samplingRate: Int) {
            if (visualizer !== this@MusicVisualizerService.visualizer) return
            processFft(visualizer, fft)
        }
    }

    private val playbackCallback = object : AudioManager.AudioPlaybackCallback() {
        override fun onPlaybackConfigChanged(configs: MutableList<AudioPlaybackConfiguration>) {
            updateVisualizer(configs.activeMediaSessionId())
        }
    }

    override fun onCreate() {
        audioManager.registerAudioPlaybackCallback(playbackCallback, null)
        updateVisualizer(audioManager.activePlaybackConfigurations.activeMediaSessionId())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        audioManager.unregisterAudioPlaybackCallback(playbackCallback)
        releaseVisualizer()
        scope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun updateVisualizer(activeSessionId: Int?) {
        scope.launch {
            if (activeSessionId == null) {
                releaseVisualizer()
            } else {
                attachVisualizer(activeSessionId)
            }
        }
    }

    private fun attachVisualizer(newSessionId: Int) {
        if (newSessionId == sessionId && visualizer != null) return
        releaseVisualizer()
        sessionId = newSessionId
        try {
            val newVisualizer = Visualizer(newSessionId)
            visualizer = newVisualizer
            newVisualizer.apply {
                setServerDiedListener {
                    if (this@MusicVisualizerService.visualizer === newVisualizer) releaseVisualizer()
                }
                captureSize = Visualizer.getCaptureSizeRange()[1]
                scalingMode = Visualizer.SCALING_MODE_NORMALIZED
                measurementMode = Visualizer.MEASUREMENT_MODE_PEAK_RMS
                setDataCaptureListener(dataCaptureListener, Visualizer.getMaxCaptureRate(), false, true)
                enabled = true
            }
        } catch (_: RuntimeException) {
            releaseVisualizer()
        }
    }

    private fun releaseVisualizer() {
        sessionId = null
        val releasedVisualizer = visualizer
        visualizer = null
        bands.forEach { it.reset() }
        signalPresent = false
        signalConfirmationFrames = 0
        wasSilent = true
        AnimationManager.updateLedFrame(IntArray(bands.size))
        try {
            releasedVisualizer?.apply {
                enabled = false
                release()
            }
        } catch (_: RuntimeException) {
            // The audio server may already be unavailable.
        }
    }

    private fun processFft(visualizer: Visualizer, fft: ByteArray) {
        val buckets = bucketMagnitudes(fft)
        val bandEnergies = DoubleArray(BAND_CONFIGS.size) { i ->
            val config = BAND_CONFIGS[i]
            buckets.slice(config.bucketStart until config.bucketStart + config.bucketCount).average()
        }
        if (!hasAudibleSignal(visualizer) || bandEnergies.all { it <= MIN_BAND_ENERGY }) {
            fadeToSilence()
            return
        }
        wasSilent = false

        val now = System.currentTimeMillis()
        bands.forEachIndexed { i, state -> state.update(bandEnergies[i], BAND_CONFIGS[i], now) }

        val maxBrightness = Constants.getMaxBrightness()
        writeLedFrame(IntArray(bands.size) { i -> (bands[i].brightness * maxBrightness).toInt() })
    }

    private fun hasAudibleSignal(visualizer: Visualizer): Boolean {
        if (visualizer.getMeasurementPeakRms(measurement) != Visualizer.SUCCESS) {
            return signalPresent
        }
        val threshold = if (signalPresent) SILENCE_RMS_MB else SIGNAL_RMS_MB
        val signalDetected = measurement.mRms > threshold
        if (signalDetected == signalPresent) {
            signalConfirmationFrames = 0
        } else {
            val confirmationFrames = if (signalDetected) {
                SIGNAL_CONFIRMATION_FRAMES
            } else {
                SILENCE_CONFIRMATION_FRAMES
            }
            if (++signalConfirmationFrames >= confirmationFrames) {
                signalPresent = signalDetected
                signalConfirmationFrames = 0
            }
        }
        return signalPresent
    }

    private fun fadeToSilence() {
        bands.forEachIndexed { i, state -> state.decayBrightness(BAND_CONFIGS[i]) }
        if (bands.all { it.brightness == 0.0 }) {
            if (!wasSilent) {
                bands.forEach { it.reset() }
                wasSilent = true
                writeLedFrame(IntArray(bands.size))
            }
            return
        }

        wasSilent = false
        val maxBrightness = Constants.getMaxBrightness()
        writeLedFrame(IntArray(bands.size) { i -> (bands[i].brightness * maxBrightness).toInt() })
    }

    private fun writeLedFrame(pattern: IntArray) {
        scope.launch { AnimationManager.updateLedFrame(pattern) }
    }

    private fun bucketMagnitudes(fft: ByteArray): DoubleArray {
        val numComplexBins = (fft.size / 2) - 1
        val binsPerBucket = numComplexBins / FFT_BUCKETS
        return DoubleArray(FFT_BUCKETS) { i ->
            var sum = 0f
            for (j in 0 until binsPerBucket) {
                val bin = 1 + (i * binsPerBucket) + j
                val idx = bin * 2
                val re = fft[idx].toFloat()
                val im = fft[idx + 1].toFloat()
                sum += (sqrt(re * re + im * im) - NOISE_FLOOR).coerceAtLeast(0f)
            }
            (sum / binsPerBucket / NORMALIZE_DIVISOR) * NORMALIZE_SCALE
        }
    }
}
