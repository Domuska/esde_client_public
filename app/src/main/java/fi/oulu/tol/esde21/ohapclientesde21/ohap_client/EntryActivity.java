package fi.oulu.tol.esde21.ohapclientesde21.ohap_client;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.net.MalformedURLException;
import java.net.URL;

import fi.oulu.tol.esde21.ohapclientesde21.R;
import fi.oulu.tol.esde21.ohapclientesde21.ohap.CentralUnitConnection;
import fi.oulu.tol.esde21.ohapclientesde21.opimobi_ohap_files.Container;
import fi.oulu.tol.esde21.ohapclientesde21.opimobi_ohap_files.Device;
import fi.oulu.tol.esde21.ohapclientesde21.opimobi_ohap_files.Item;

public class EntryActivity extends Activity {

    static CentralUnitConnection centralUnit;

    private final static String EXTRA_PREFIX_STRING = "prefixData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(this);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        try {
            URL url = new URL(sharedPref.getString("pref_key_URL", "http://www.google.com"));
            centralUnit = new CentralUnitConnection(url) {
            };
        }
        catch (MalformedURLException e){
            //do stuff with the exception...
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
        i.putExtra(ItemListActivity.EXTRA_CONTAINER_ID, centralUnit.getId());
        i.putExtra(EXTRA_PREFIX_STRING, centralUnit.getName());
        i.putExtra(ItemListActivity.EXTRA_CENTRAL_UNIT_URL, centralUnit.getURL().toString());

        startActivity(i);
    }


    //class method for getting containers in the CU (or the CU itself).
    //gets an ID for the container that is wanted and returns the container in question,
    //it can be either a subcontainer in the central unit or the central unit itself.

    /**
     *
     * @param id for the item to be returned
     * @return returns item under the central unit with the given ID, or the central unit itself
     */
    public static Item getCentralUnitItem(long id){

        if(id == centralUnit.getId()){
            return centralUnit;
        }
        else{
            return centralUnit.getItemById(id);
        }
    }





}
