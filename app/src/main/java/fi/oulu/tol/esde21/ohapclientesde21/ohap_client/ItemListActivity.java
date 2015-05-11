package fi.oulu.tol.esde21.ohapclientesde21.ohap_client;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import fi.oulu.tol.esde21.ohapclientesde21.R;
import fi.oulu.tol.esde21.ohapclientesde21.ohap.ConnectionManager;
import fi.oulu.tol.esde21.ohapclientesde21.opimobi_ohap_files.CentralUnit;
import fi.oulu.tol.esde21.ohapclientesde21.opimobi_ohap_files.Container;
import fi.oulu.tol.esde21.ohapclientesde21.opimobi_ohap_files.Device;
import fi.oulu.tol.esde21.ohapclientesde21.opimobi_ohap_files.Item;

/**
 * Created by Domu on 07-Apr-15.
 *
 * Activity for displaying a list that holds a number of Items
 */


public class ItemListActivity extends Activity {


    private static final String TAG = "ItemListActivity";

    public final static String EXTRA_CONTAINER_ID = "fi.oulu.tol.esde.esde21.CONTAINER_ID";
    public final static String EXTRA_PREFIX_STRING = "prefixData"; // TÄMÄ TULEE OLEMAAN TURHA, POISTETAAN
    public final static String EXTRA_CENTRAL_UNIT_URL = "fi.oulu.tol.esde.esde21.CENTRAL_UNIT_URL";

    ListView listView;

    // string holding the "path" of the item
    String extraPrefix;

    //the id for the container of this hierarchy
    Long extraContainerId;

    //url for the central unit
    String extraURL;

    Container thisContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);


        // get the current path of the item
        extraPrefix = getIntent().getStringExtra(EXTRA_PREFIX_STRING) + "/";
        Log.d(TAG, "Gotten extra prefix string: " + extraPrefix);
        //TODO: handle missing container ID

        extraContainerId = getIntent().getLongExtra(EXTRA_CONTAINER_ID, -1);
        Log.d(TAG, "Gotten container ID Extra: " + extraContainerId);

        //TODO: https://wiki.oulu.fi/display/esde/2+Doings || jatka: Now that you have the URL
        // && extra prefix ei enää ole tarpeellinen, hiukka ylempänä wikissä siitä...

        extraURL = getIntent().getStringExtra(EXTRA_CENTRAL_UNIT_URL);
        Log.d(TAG, "Gotten central unit URL: " + extraURL);


        // try to use the extra url string to get the central unit in question from ConnectionManger
        try{
            URL centralUnitURL = new URL(extraURL);

            // if the ID we get is for the central unit under where we are, the container central unit itself
            if(ConnectionManager.getInstance().getCentralUnit(centralUnitURL).getId() == extraContainerId) {
                thisContainer = ConnectionManager.getInstance().getCentralUnit(centralUnitURL);
            }
            //otherwise get the central unit by querying the central unit with the item id
            else{
                thisContainer =
                        (Container) ConnectionManager.getInstance()
                                .getCentralUnit(centralUnitURL).getItemById(extraContainerId);
            }
            Log.d(TAG, "URL: " + extraURL + " used to initialize container");
            Log.d(TAG, "Container name: " + thisContainer.getName());
        }
        catch (MalformedURLException e){
            Log.d(TAG, "URL extra from previous activity wasn't a proper url");
        }

        listView = (ListView) findViewById(R.id.deviceListView);
        listView.setAdapter(new OhapListAdapter(thisContainer));


        //thisContainer = ConnectionManager.getInstance().getCentralUnit(centralUnitURL).getItemById(extraContainerId);

        // listener for list's items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d(TAG, "Starting list onItemClick");
                Log.d(TAG, "Position: " + Integer.toString(position) + " Id: " + Long.toString(id));

                //if the selected element is container, open a new list, else open the device page
                if (thisContainer.getItemByIndex(position) instanceof Container) {

                    // set the file path to contain the container's name
                    extraPrefix += EntryActivity.getCentralUnitItem(id).getName();

                    Intent containerIntent = new Intent(ItemListActivity.this, ItemListActivity.class);
                    //containerIntent.putExtra(EXTRA_PREFIX_STRING, extraPrefix);
                    containerIntent.putExtra(EXTRA_CONTAINER_ID, id);
                    containerIntent.putExtra(EXTRA_CENTRAL_UNIT_URL, extraURL);
                    startActivity(containerIntent);

                } else {
                    Intent deviceIntent = new Intent(ItemListActivity.this, DeviceActivity.class);

                    //give the ID of the device and its' path as extra to the activity
                    deviceIntent.putExtra(DeviceActivity.EXTRA_DEVICE_ID, id);
                    deviceIntent.putExtra(DeviceActivity.EXTRA_CENTRAL_UNIT_URL, extraURL);
                    //deviceIntent.putExtra(EXTRA_PREFIX_STRING, extraPrefix);
                    startActivity(deviceIntent);
                }
            }
        });

        // set the up the "up" navigation
        getActionBar().setDisplayHomeAsUpEnabled(true);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //commented out because we don't actually want a menu in the item list, should propably delete the xml as well if menus will not be added here
        //getMenuInflater().inflate(R.menu.menu_item_list, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
