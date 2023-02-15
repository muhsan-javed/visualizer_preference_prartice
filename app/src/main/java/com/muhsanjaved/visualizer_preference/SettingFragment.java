package com.muhsanjaved.visualizer_preference;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

public class SettingFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

        addPreferencesFromResource(R.xml.pref_visualizer);

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen prefScreen = getPreferenceScreen();
        int count = prefScreen.getPreferenceCount();

        for (int i=0; i<count; i++){
            Preference p = prefScreen.getPreference(i);

            if (!(p instanceof CheckBoxPreference))
            {
                String value= sharedPreferences.getString(p.getKey(), "");
                setPreferenceSummary(p, value);
            }


        }

    }

    //  This method should check if the preference is a listPreference and , If so, find the label associated with the value.
    //  You can do this by using the findIndexOfValue and getEntries method of Preference.
    // Updates the summary for the preference
    // preference ---> The preference to be updated
    // value ---> The value that the preference was updated to
    private void setPreferenceSummary(Preference preference, String value)
    {
        // ListPreference
        if (preference instanceof ListPreference){

            // For List preference, figure out the label of the selected value
            ListPreference listPreference = (ListPreference) preference;

            int prefIndex = listPreference.findIndexOfValue(value);

            if (prefIndex >= 0){
                // Set the summary to that label
                listPreference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        }
        // EditTextPreference
        else if (preference instanceof EditTextPreference){
            // For EditTextPreference, set the summary to the value's simple string representation
            preference.setSummary(value);
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //  Figure out which preference was changed
        Preference preference = findPreference(key);

        if (null != preference){
            //  Updates the summary for the perference
            if (!(preference instanceof CheckBoxPreference)){
                String value = sharedPreferences.getString(preference.getKey(), "");
                setPreferenceSummary(preference, value);
            }
        }

    }

    //  Register and unregister the OnSharePreferenceChange listener (this class ) in
    //  OnCreate and onDestroy respectively
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }
    //  UnRegister
    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
