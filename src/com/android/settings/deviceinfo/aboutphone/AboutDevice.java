/*
 * Copyright (C) 2020 The ConquerOS Project
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

import android.app.Activity;
import android.app.settings.SettingsEnums;
import android.content.Context;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.os.UserManager;
import android.provider.SearchIndexableResource;
import android.view.View;

import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.deviceinfo.DeviceNamePreferenceController;
import com.android.settings.deviceinfo.BuildNumberPreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.search.Indexable;
import com.android.settingslib.search.SearchIndexable;

import java.util.ArrayList;
import java.util.List;

@SearchIndexable
public class AboutDevice extends DashboardFragment 
        implements DeviceNamePreferenceController.DeviceNamePreferenceHost {

    private static final String LOG_TAG = "HalcyonAboutDevice";

    private BuildNumberPreferenceController mBuildNumberPreferenceController;

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.halcyon_about_device;
    }

    @Override
    protected String getLogTag() {
        return LOG_TAG;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        use(DeviceNamePreferenceController.class).setHost(this /* parent */);
        mBuildNumberPreferenceController = use(BuildNumberPreferenceController.class);
        mBuildNumberPreferenceController.setHost(this /* parent */);
    }

    @Override
    public int getMetricsCategory() {
        return SettingsEnums.DIALOG_FIRMWARE_VERSION;
    }

    @Override
    public void showDeviceNameWarningDialog(String deviceName) {
        DeviceNameWarningDialog.show(this);
    }

    public void onSetDeviceNameConfirm(boolean confirm) {
        final DeviceNamePreferenceController controller = use(DeviceNamePreferenceController.class);
        controller.updateDeviceName(confirm);
    }

    @Override
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        final List<AbstractPreferenceController> controllers = new ArrayList<>();
        controllers.add(new HalcyonInfoPreferenceController(context));
        return controllers;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mBuildNumberPreferenceController.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                        boolean enabled) {
                    final ArrayList<SearchIndexableResource> result = new ArrayList<>();

                    final SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.halcyon_about_device;
                    result.add(sir);
                    return result;
                }

            };
}