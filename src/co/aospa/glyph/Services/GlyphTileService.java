/*
 * Copyright (C) 2015 The CyanogenMod Project
 * Copyright (C) 2017 The LineageOS Project
 * Copyright (C) 2022 Paranoid Android
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

package co.aospa.glyph.Services;

import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import co.aospa.glyph.R;
import co.aospa.glyph.Constants.Constants;
import co.aospa.glyph.Manager.StatusManager;
import co.aospa.glyph.Utils.FileUtils;

/** Quick settings tile: Glyph **/
public class GlyphTileService extends TileService {

    @Override
    public void onStartListening() {
        super.onStartListening();
        updateState();
    }

    private void updateState() {
        boolean enabled = getEnabled();
        getQsTile().setContentDescription(enabled ?
                getString(R.string.glyph_accessibility_quick_settings_on) :
                getString(R.string.glyph_accessibility_quick_settings_off));
        getQsTile().setState(enabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        getQsTile().updateTile();
    }

    @Override
    public void onClick() {
        super.onClick();
        setEnabled(!getEnabled());
        updateState();
    }

    private boolean getEnabled() {
        return StatusManager.isAllLedActive();
    }

    private void setEnabled(boolean enabled) {
        StatusManager.setAllLedsActive(enabled);
        FileUtils.writeLine(Constants.ALLWHITELEDPATH, enabled ? 4095 : 0);
    }
}
