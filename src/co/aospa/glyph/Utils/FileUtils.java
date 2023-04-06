/*
 * Copyright (C) 2022-2023 Paranoid Android
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package co.aospa.glyph.Utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import co.aospa.glyph.R;
import co.aospa.glyph.Constants.Constants;

public final class FileUtils {

    private static final String TAG = "GlyphFileUtils";
    private static final boolean DEBUG = true;

    private static Context context = Constants.CONTEXT;

    public static String readLine(String fileName) {
        String line = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fileName), 512);
            line = reader.readLine();
        } catch (FileNotFoundException e) {
            Log.w(TAG, "No such file " + fileName + " for reading", e);
        } catch (IOException e) {
            Log.e(TAG, "Could not read from file " + fileName, e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                // Ignored, not much we can do anyway
            }
        }
        return line;
    }

    public static int readLineInt(String fileName) {
        try {
            return Integer.parseInt(readLine(fileName));
        }
        catch (NumberFormatException e) {
            Log.e(TAG, "Could not convert string to int from file " + fileName, e);
        }
        return 0;
    }

    public static void writeLine(String fileName, String value) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(value);
        } catch (FileNotFoundException e) {
            Log.w(TAG, "No such file " + fileName + " for writing", e);
        } catch (IOException e) {
            Log.e(TAG, "Could not write to file " + fileName, e);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                // Ignored, not much we can do anyway
            }
        }
    }

    public static void writeLine(String fileName, int value) {
        writeLine(fileName, Integer.toString(value));
    }

    public static void writeLine(String fileName, float value) {
        writeLine(fileName, Float.toString(value));
    }

    public static void writeLineFromSlug(String slug, String value) {
        String[] slugs = context.getResources().getStringArray(R.array.glyph_settings_paths);
        String[] paths = context.getResources().getStringArray(R.array.glyph_settings_paths_absolute);
        writeLine(paths[Arrays.asList(slugs).indexOf(slug)], value);
    }

    public static void writeLineFromSlug(String slug, int value) {
        writeLineFromSlug(slug, Integer.toString(value));
    }

    public static void writeLineFromSlug(String slug, float value) {
        writeLineFromSlug(slug, Float.toString(value));
    }

    public static void writeSingleLed(String led, String value) {
        writeLine(context.getString(R.string.glyph_settings_paths_single_absolute), led + " " + value);
    }

    public static void writeSingleLed(int led, String value) {
        writeSingleLed(Integer.toString(led), value);
    }

    public static void writeSingleLed(String led, int value) {
        writeSingleLed(led, Integer.toString(value));
    }

    public static void writeSingleLed(String led, float value) {
        writeSingleLed(led, Integer.toString(Math.round(value)));
    }

    public static void writeSingleLed(int led, float value) {
        writeSingleLed(Integer.toString(led), Integer.toString(Math.round(value)));
    }
}
