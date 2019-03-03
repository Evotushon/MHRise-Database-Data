package com.gatheringhallstudios.mhworlddatabase.features.app

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Switch
import androidx.fragment.app.Fragment
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.gatheringhallstudios.mhworlddatabase.AppSettings
import com.gatheringhallstudios.mhworlddatabase.MainActivity
import com.gatheringhallstudios.mhworlddatabase.R
import com.gatheringhallstudios.mhworlddatabase.data.MHWDatabase
import com.gatheringhallstudios.mhworlddatabase.data.entities.Language

/**
 * Fragment used to display app preferences
 */
class PreferencesFragment : PreferenceFragmentCompat() {
    private val restartListener = RestartOnLocaleChangeListener(this)

    // add listener on resume
    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(restartListener)
    }

    // remove listener on pause
    override fun onPause() {
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(restartListener)
        super.onPause()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = AppSettings.SETTINGS_FILE_NAME

        setPreferencesFromResource(R.xml.preferences, rootKey)
        initDataLanguages()
        initWeaponValues()
    }

    private fun initDataLanguages() {
        val localePref = findPreference(AppSettings.PROP_DATA_LOCALE) as ListPreference

        // Get the list of languages. Add a "default" language to the front
        val defaultLanguage = Language("", getString(R.string.preference_language_default))
        val languages = listOf(defaultLanguage) + MHWDatabase.getDatabase(context).languages
        val languageCodes = languages.map { it.id }
        val languageNames = languages.map { it.name }

        localePref.entryValues = languageCodes.toTypedArray()
        localePref.entries = languageNames.toTypedArray()
        localePref.value = AppSettings.configuredDataLocale // ensure a value is set
    }

    private fun initWeaponValues() {
        val attackValuePref = findPreference(AppSettings.PROP_ATTACK_VALUE_TYPE) as SwitchPreference

        attackValuePref.switchTextOn = getString(R.string.preference_weapons_attack_values_enabled)
        attackValuePref.switchTextOff = getString(R.string.preference_weapons_attack_values_disabled)
    }

    /**
     * Internal class to restart the app if the locale changes
     */
    class RestartOnLocaleChangeListener(val fragment: androidx.fragment.app.Fragment) : SharedPreferences.OnSharedPreferenceChangeListener {
        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
            if (key == AppSettings.PROP_DATA_LOCALE) {
                (fragment.activity as? MainActivity)?.restartApp()
            }
        }
    }
}