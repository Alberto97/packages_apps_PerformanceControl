/*
 * Performance Control - An Android CPU Control application Copyright (C) 2012
 * James Roberts
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.brewcrewfoo.performance.activities;

import net.margaritov.preference.colorpicker.ColorPickerPreference;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import com.brewcrewfoo.performance.R;
import com.brewcrewfoo.performance.util.ActivityThemeChangeInterface;
import com.brewcrewfoo.performance.util.Constants;
import com.brewcrewfoo.performance.util.Helpers;

public class PCSettings extends PreferenceActivity implements Constants,
        ActivityThemeChangeInterface, OnPreferenceChangeListener {

    SharedPreferences          mPreferences;
    private CheckBoxPreference mLightThemePref;
    private ColorPickerPreference mWidgetTextColorPref;
    private Preference mVersion;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        addPreferencesFromResource(R.xml.pc_settings);
        mLightThemePref = (CheckBoxPreference) findPreference("use_light_theme");
        mWidgetTextColorPref = (ColorPickerPreference) findPreference("widget_text_color");
        mWidgetTextColorPref.setOnPreferenceChangeListener(this);
        mVersion = (Preference) findPreference("version_info");
        mVersion.setTitle("Version - " + VERSION_NUM);
        setTheme();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        String key = preference.getKey();
        if ("use_light_theme".equals(key)) {
            Helpers.restartPC(this);
            return true;
        } else if ("use_light_widget_bg".equals(key)) {
            Helpers.updateAppWidget(this);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mWidgetTextColorPref) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            final SharedPreferences.Editor editor = mPreferences
                    .edit();
            editor.putInt(PREF_WIDGET_TEXT_COLOR, intHex);
            editor.commit();
            Helpers.updateAppWidget(this);
            return true;
        }
        return false;
    }

    @Override
    public boolean isThemeChanged() {
        final boolean is_light_theme = mPreferences.getBoolean(
                PREF_USE_LIGHT_THEME, false);
        return is_light_theme != mLightThemePref.isChecked();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void setTheme() {
        final boolean is_light_theme = mPreferences.getBoolean(
                PREF_USE_LIGHT_THEME, false);
        setTheme(is_light_theme ? R.style.Theme_Light : R.style.Theme_Dark);
        getListView().setBackgroundDrawable(
                getResources().getDrawable(
                        is_light_theme ? R.drawable.background_holo_light
                                : R.drawable.background_holo_dark));
    }
}