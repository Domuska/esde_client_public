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
import java.util.ArrayList;

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

        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(this);

        try {
            Log.d(TAG, "creating new Central unit");
            URL url = new URL(sharedPref.getString(SettingsFragment.KEY_EDIT_TEXT_PREFERENCE, getString(R.string.pref_URL_default)));
            String userName = sharedPref.getString(SettingsFragment.KEY_EDIT_TEXT_USERNAME, getString(R.string.pref_userName_default));
            String password = sharedPref.getString(SettingsFragment.KEY_EDIT_TEXT_PASSWORD, getString(R.string.pref_password_default));
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
