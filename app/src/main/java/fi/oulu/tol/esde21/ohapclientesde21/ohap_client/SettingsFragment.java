package fi.oulu.tol.esde21.ohapclientesde21.ohap_client;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

import fi.oulu.tol.esde21.ohapclientesde21.R;

public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener{

    public static final String KEY_EDIT_TEXT_PREFERENCE = "pref_key_URL";
    public static final String KEY_EDIT_TEXT_USERNAME = "pref_key_userName";
    public static final String KEY_EDIT_TEXT_PASSWORD = "pref_key_password";
//    public static final String KEY_CHECKBOX_SENSOR = "pref_key_sensor";
    public static final String KEY_LIST_SENSOR = "pref_key_sensorlist";

    private final String TAG = "SettingsFragment";

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        //Context context = getActivity();
        //SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        //set summary for the user name preference
        EditTextPreference userNamePreference = (EditTextPreference)findPreference(KEY_EDIT_TEXT_USERNAME);
        userNamePreference.setSummary(userNamePreference.getText());

        EditTextPreference urlPreference = (EditTextPreference)findPreference(KEY_EDIT_TEXT_PREFERENCE);

        //set listener to check if the entered string is a valid URL
        urlPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                boolean isSuccessful = false;
                EditTextPreference pref = (EditTextPreference) preference;
                String urlString = (String)newValue;

                //just try to create a new URL to see if it is valid, we don't need it for anything
                try{
                    new URL (urlString);
                    isSuccessful = true;
                }
                catch(MalformedURLException e){
                    Log.d(TAG, "malformed url exception caught, error text shown");
                    pref.setText("http://ohap.opimobi.com:18000/");
                    Toast.makeText(getActivity()
                            ,getString(R.string.prefences_urlError)
                            ,Toast.LENGTH_LONG)
                            .show();
                }

                return isSuccessful;
            }
        });

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

        Preference preference = findPreference(key);

        
        if(preference instanceof  EditTextPreference) {
            EditTextPreference editTextPreference = (EditTextPreference) findPreference(key);

            //do not set the summary text visible for the password, let's keep it a secret!
            if(!preference.getKey().equals(KEY_EDIT_TEXT_PASSWORD))
                editTextPreference.setSummary(editTextPreference.getText());
        }
    }
}