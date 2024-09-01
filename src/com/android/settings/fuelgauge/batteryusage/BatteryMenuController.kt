/*
 * Copyright (C) 2023 Halcyon Project
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

package com.android.settings.fuelgauge.batteryusage

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.LinearLayout
import androidx.fragment.app.FragmentActivity
import androidx.preference.PreferenceScreen
import com.android.settings.R
import com.android.settingslib.core.AbstractPreferenceController
import com.android.settingslib.widget.LayoutPreference

class BatteryMenuController(context: Context) : AbstractPreferenceController(context) {

    override fun displayPreference(screen: PreferenceScreen) {
        super.displayPreference(screen)

        val BatteryMenu = screen.findPreference<LayoutPreference>(KEY_HALCYON_BATTMENU)!!

        val clickMap = mapOf(
            R.id.battery_saver to Intent().setComponent(ComponentName("com.android.settings", "com.android.settings.Settings\$BatterySaverSettingsActivity")),
            R.id.smart_battery_manager to Intent().setComponent(ComponentName("com.android.settings", "com.android.settings.Settings\$SmartBatterySettingsActivity")),
            R.id.charging_control to Intent().setComponent(ComponentName("com.android.settings", "com.android.settings.Settings\$ChargingControlActivity")),
            R.id.battery_info to Intent().setComponent(ComponentName("com.android.settings", "com.android.settings.Settings\$BatteryInfoActivity"))
        )

        clickMap.forEach { (id, intent) ->
            BatteryMenu.findViewById<LinearLayout>(id)?.setOnClickListener {
                mContext.startActivity(intent)
            }
        }
    }

    override fun isAvailable(): Boolean {
        return true
    }

    override fun getPreferenceKey(): String {
        return KEY_HALCYON_BATTMENU
    }

    companion object {
        private const val KEY_HALCYON_BATTMENU = "battery_menus"
    }
}
