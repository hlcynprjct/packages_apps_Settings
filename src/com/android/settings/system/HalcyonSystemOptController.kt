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

package com.android.settings.system

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.LinearLayout
import androidx.fragment.app.FragmentActivity
import androidx.preference.PreferenceScreen
import com.android.settings.R
import com.android.settingslib.core.AbstractPreferenceController
import com.android.settingslib.widget.LayoutPreference

class HalcyonSystemOptController(context: Context) : AbstractPreferenceController(context) {

    override fun displayPreference(screen: PreferenceScreen) {
        super.displayPreference(screen)

        val HalcyonSystemOpt = screen.findPreference<LayoutPreference>(KEY_HALCYON_SYSOPT)!!

        val clickMap = mapOf(
            R.id.language_settings to Intent().setComponent(ComponentName("com.android.settings", "com.android.settings.Settings\$LanguageSettingsActivity")),
            R.id.keyboard_settings to Intent().setComponent(ComponentName("com.android.settings", "com.android.settings.Settings\$KeyboardSettingsActivity")),
            R.id.time_settings to Intent().setComponent(ComponentName("com.android.settings", "com.android.settings.Settings\$DateTimeSettingsActivity")),
            R.id.gesture_settings to Intent().setComponent(ComponentName("com.android.settings", "com.android.settings.Settings\$SystemGestureActivity"))
        )

        clickMap.forEach { (id, intent) ->
            HalcyonSystemOpt.findViewById<LinearLayout>(id)?.setOnClickListener {
                mContext.startActivity(intent)
            }
        }
    }

    override fun isAvailable(): Boolean {
        return true
    }

    override fun getPreferenceKey(): String {
        return KEY_HALCYON_SYSOPT
    }

    companion object {
        private const val KEY_HALCYON_SYSOPT = "halcyon_system_opt"
    }
}