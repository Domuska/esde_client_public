package fi.oulu.tol.esde21.ohapclientesde21.ohap_client;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


import fi.oulu.tol.esde21.ohapclientesde21.R;

public class SettingsActivity extends Activity {

    SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settingsFragment = new SettingsFragment();

        //Display the fragment as main content
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, settingsFragment)
                .commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_add_server){

            settingsFragment.addNewServer();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}