package fi.oulu.tol.esde21.ohapclientesde21.ohap_client;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

import fi.oulu.tol.esde21.ohapclientesde21.R;

public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener{

    public static final String KEY_EDIT_TEXT_PREFERENCE = "Server 1";
    public static final String KEY_EDIT_TEXT_USERNAME = "pref_key_userName";
    public static final String KEY_EDIT_TEXT_PASSWORD = "pref_key_password";
    public static final String KEY_LIST_SENSOR = "pref_key_sensorlist";
    public static final String KEY_NUMBER_OF_SERVERS = "pref_key_number_of_servers";

    private final String TAG = "SettingsFragment";

    private PreferenceScreen preferenceScreen;
    private int numberOfServers;
    SharedPreferences sharedPreferences;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        preferenceScreen = getPreferenceManager().createPreferenceScreen(getActivity());

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        numberOfServers = sharedPreferences.getInt(KEY_NUMBER_OF_SERVERS, -1);


        //set summary for the user name preference
        EditTextPreference userNamePreference = (EditTextPreference)findPreference(KEY_EDIT_TEXT_USERNAME);
        userNamePreference.setSummary(userNamePreference.getText());

        EditTextPreference urlPreference = (EditTextPreference)findPreference(KEY_EDIT_TEXT_PREFERENCE);

        //set listener to check if the entered string is a valid URL
        addUrlFieldPreferenceChangeListener(urlPreference);

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

    private void addUrlFieldPreferenceChangeListener(EditTextPreference preference){

        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
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

    }

    public void addNewServer(){


        //Help for this gotten from inazaruk's response on
        // http://stackoverflow.com/questions/6129384/programatically-populating-preferences-with-checkboxes

        // add the original categories into the preference
        preferenceScreen.addPreference(getPreferenceManager().findPreference("DEFAULT_DEVICE_PREFERENCE_CATEGORY"));
        preferenceScreen.addPreference(getPreferenceManager().findPreference("DEFAULT_SERVER_PREFERENCE_CATEGORY"));


        PreferenceCategory category  = new PreferenceCategory(getActivity());

        numberOfServers++;

        String preferenceKey = "Server " + numberOfServers;

        category.setTitle(preferenceKey + " preferences");


        preferenceScreen.addPreference(category);

        EditTextPreference newUrlPreference = new EditTextPreference(getActivity());
        newUrlPreference.setTitle("URL");
        newUrlPreference.setKey(preferenceKey);
        addUrlFieldPreferenceChangeListener(newUrlPreference);


        ServerPreferenceManager.getInstance().addKey(preferenceKey);

        category.addPreference(newUrlPreference);

        EditTextPreference newUserNamePreference  = new EditTextPreference(getActivity());
        newUserNamePreference.setTitle(getActivity().getResources().getString(R.string.preferences_userName));
        newUserNamePreference.setKey(preferenceKey + "_UserName");

        category.addPreference(newUserNamePreference);

        EditTextPreference newPasswordPreference = new EditTextPreference(getActivity());
        newPasswordPreference.setTitle(getActivity().getResources().getString(R.string.preferences_password));
        newPasswordPreference.setKey(preferenceKey + "_Password");

        category.addPreference(newPasswordPreference);


        //update the amount of servers number that is stored in sharedpreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_NUMBER_OF_SERVERS, (numberOfServers));
        editor.commit();

        Log.d(TAG, "number of servesr is now : " + sharedPreferences.getInt(KEY_NUMBER_OF_SERVERS, -1));



        setPreferenceScreen(preferenceScreen);

    }
}