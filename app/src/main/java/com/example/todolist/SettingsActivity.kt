package com.example.todolist

import android.os.Bundle
import android.preference.CheckBoxPreference
import android.preference.EditTextPreference
import android.preference.Preference
import android.preference.PreferenceActivity
import android.widget.Toast
import androidx.annotation.Nullable

@Suppress("DEPRECATION")
class SettingsActivity() : PreferenceActivity(), Preference.OnPreferenceChangeListener {
    var cbp: CheckBoxPreference? = null
    var edtp: EditTextPreference? = null

    public override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)
        cbp = findPreference("remember") as CheckBoxPreference
        edtp = findPreference("login") as EditTextPreference
//        cbp!!.setOnPreferenceChangeListener(this)
        cbp!!.onPreferenceChangeListener = this
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
        val t = Toast.makeText(
            this,
            "click cb:" + newValue.toString()
                    + " pref manipulee : " + preference.key, Toast.LENGTH_SHORT
        )
        t.show()
        if ((newValue == false)) {
            edtp!!.text = ""
        }
        return true
    }
}
