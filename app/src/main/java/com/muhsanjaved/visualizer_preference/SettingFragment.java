package com.muhsanjaved.visualizer_preference;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import java.util.Objects;

public class SettingFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

        addPreferencesFromResource(R.xml.pref_visualizer);

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen prefScreen = getPreferenceScreen();
        int count = prefScreen.getPreferenceCount();

        for (int i = 0; i < count; i++) {
            Preference p = prefScreen.getPreference(i);

            if (!(p instanceof CheckBoxPreference)) {
                assert sharedPreferences != null;
                String value = sharedPreferences.getString(p.getKey(), "");
                setPreferenceSummary(p, value);
            }
        }

        Preference preference = findPreference(getString(R.string.pref_size_key));
        assert preference != null;
        preference.setOnPreferenceChangeListener(this);

    }


    //  This method should check if the preference is a listPreference and , If so, find the label associated with the value.
    //  You can do this by using the findIndexOfValue and getEntries method of Preference.
    // Updates the summary for the preference
    // preference ---> The preference to be updated
    // value ---> The value that the preference was updated to
    private void setPreferenceSummary(Preference preference, String value) {
        // ListPreference
        if (preference instanceof ListPreference) {

            // For List preference, figure out the label of the selected value
            ListPreference listPreference = (ListPreference) preference;

            int prefIndex = listPreference.findIndexOfValue(value);

            if (prefIndex >= 0) {
                // Set the summary to that label
                listPreference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        }
        // EditTextPreference
        else if (preference instanceof EditTextPreference) {
            // For EditTextPreference, set the summary to the value's simple string representation
            preference.setSummary(value);
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //  Figure out which preference was changed
        Preference preference = findPreference(key);

        if (null != preference) {
            //  Updates the summary for the preference
            if (!(preference instanceof CheckBoxPreference)) {
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
        Objects.requireNonNull(getPreferenceScreen().getSharedPreferences()).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
        // In This conText, we're using the onPreferenceChange Listener for checking whether the size setting was set to a valid value
        Toast error = Toast.makeText(getContext(), "Please select a number between 0.1 and 3",Toast.LENGTH_SHORT);

        // Double check that the preference is the size preference
        String sizeKey  = getString(R.string.pref_size_key);

        if (preference.getKey().equals(sizeKey)){
            String stringSize = (String) newValue;

            try {
                float size =  Float.parseFloat(stringSize);
                // If the number is outside of the acceptable range, show an error.
                if (size > 3 || size <= 0){
                    error.show();
                    return true;
                }
            }catch (NumberFormatException numberFormatException){
                // If whatever the user entered can't be parsed to a number, show an error
                error.show();
            }
        }
        return true;
    }
}
