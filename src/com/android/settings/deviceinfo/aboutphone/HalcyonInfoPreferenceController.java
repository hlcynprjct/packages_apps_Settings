/*
 * Copyright (C) 2020 Wave-OS
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

package com.android.settings.deviceinfo.aboutphone;

import android.content.Context;
import android.os.Build;
import android.os.SystemProperties;
import android.widget.TextView;

import androidx.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settings.deviceinfo.aboutphone.SpecUtils;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.LayoutPreference;

public class HalcyonInfoPreferenceController extends AbstractPreferenceController {

    private static final String KEY_HALCYON_INFO = "about_phone_info_header";

    private static final String PROP_HALCYON_DEVICE = "ro.product.model";

    public HalcyonInfoPreferenceController(Context context) {
        super(context);
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        final LayoutPreference halcyonInfoPreference = screen.findPreference(KEY_HALCYON_INFO);
        final TextView device = (TextView) halcyonInfoPreference.findViewById(R.id.device_summary);
        final TextView storage = (TextView) halcyonInfoPreference.findViewById(R.id.memory_storage_summary);
        final TextView battery = (TextView) halcyonInfoPreference.findViewById(R.id.battery_size_summary);
        final TextView infoScreen = (TextView) halcyonInfoPreference.findViewById(R.id.screen_res_summary);
        final String halcyonDevice = SystemProperties.get(PROP_HALCYON_DEVICE, mContext.getString(R.string.device_info_default));
        device.setText(halcyonDevice);
        storage.setText(String.valueOf(SpecUtils.getTotalInternalMemorySize()) + "GB ROM + " + String.valueOf(SpecUtils.getTotalRAM()) + "GB RAM");
        battery.setText(SpecUtils.getBatteryCapacity(mContext) + " mAh");
        infoScreen.setText(SpecUtils.getScreenRes(mContext));
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getPreferenceKey() {
        return KEY_HALCYON_INFO;
    }
}