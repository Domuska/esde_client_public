package fi.oulu.tol.esde21.ohapclientesde21.ohap_client;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fi.oulu.tol.esde21.ohapclientesde21.R;

public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String KEY_EDIT_TEXT_PREFERENCE = "pref_key_URL";

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        //Context context = getActivity();
        //SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
        updatePreference(KEY_EDIT_TEXT_PREFERENCE);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updatePreference(key);
    }
    private void updatePreference(String key){
        if (key.equals(KEY_EDIT_TEXT_PREFERENCE)){
            Preference preference = findPreference(key);
            if (preference instanceof EditTextPreference){
                EditTextPreference editTextPreference =  (EditTextPreference)preference;
                if (editTextPreference.getText().trim().length() > 0){
                    editTextPreference.setSummary(editTextPreference.getText());
                }else{
                    editTextPreference.setSummary("hurr-dURL");
                }
            }
        }
    }

}