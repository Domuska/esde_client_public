package fi.oulu.tol.esde21.ohapclientesde21.ohap_client;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.net.MalformedURLException;
import java.net.URL;

import fi.oulu.tol.esde21.ohapclientesde21.R;
import fi.oulu.tol.esde21.ohapclientesde21.ohap.CentralUnitConnection;
import fi.oulu.tol.esde21.ohapclientesde21.ohap.ConnectionManager;

public class EntryActivity extends Activity {

    static CentralUnitConnection centralUnit;

    private final static String EXTRA_PREFIX_STRING = "prefixData";
    private final String TAG = "EntryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

    }


    // get a new CU from ConnectionManager here so if we return from settingsActivity,
    // we'll get the new connection info from shared preferences
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart method starting");

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        // if the sharedpreference number of servers is 0, we're starting app up for the very first time
        // set number of servers in the sharedprefs to 1 since OHAP test server is saved there nevertheless
        if(sharedPreferences.getInt(SettingsFragment.KEY_NUMBER_OF_SERVERS, 0) == 0) {


            PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(SettingsFragment.KEY_NUMBER_OF_SERVERS, 1);
            editor.commit();
        }

        Log.d(TAG, "number of servers in entryActivity: " + sharedPreferences.getInt(SettingsFragment.KEY_NUMBER_OF_SERVERS, -1));


        int numberOfServers = sharedPreferences.getInt(SettingsFragment.KEY_NUMBER_OF_SERVERS, 0);

        // initialize ServerPreferenceManager array with the number of servers that is actually stored in shared prefs
        for(int i = 0; i < numberOfServers; i++){

            ServerPreferenceManager.getInstance().addKey("Server " + (i + 1));
            Log.d(TAG, "added server number " + (i+1) + " to ServerPreferenceManager");
        }


        try {
            Log.d(TAG, "creating new Central unit");
            URL url = new URL(sharedPreferences.getString(SettingsFragment.KEY_EDIT_TEXT_PREFERENCE, getString(R.string.pref_URL_default)));
            String userName = sharedPreferences.getString(SettingsFragment.KEY_EDIT_TEXT_USERNAME, getString(R.string.pref_userName_default));
            String password = sharedPreferences.getString(SettingsFragment.KEY_EDIT_TEXT_PASSWORD, getString(R.string.pref_password_default));
            centralUnit = ConnectionManager.getInstance().getCentralUnit(url);
            centralUnit.setLoginCredentials(userName, password);
        }
        catch (MalformedURLException e){
            Log.d(TAG, "ERROR: URL stored in preferences was invalid. Why wasn't this checked in preferences?");
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_entry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(EntryActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // this method is the onClick method for the button in the middle of the screen
    // it starts the first list activity and passes the ID of the CU to it
    public void onEnterButtonClicked(View v){

        Intent i = new Intent(this, ItemListActivity.class);
        Log.d(TAG, "putting extras in, CU name: " + centralUnit.getName());
        i.putExtra(ItemListActivity.EXTRA_CONTAINER_ID, centralUnit.getId());
        i.putExtra(EXTRA_PREFIX_STRING, centralUnit.getName());
        //i.putExtra(ItemListActivity.EXTRA_CENTRAL_UNIT_URL, centralUnit.getURL().toString());
        i.putExtra(ItemListActivity.EXTRA_CENTRAL_UNIT_URL, ItemListActivity.EXTRA_NULL_CENTRAL_UNIT);
        startActivity(i);
    }

}
