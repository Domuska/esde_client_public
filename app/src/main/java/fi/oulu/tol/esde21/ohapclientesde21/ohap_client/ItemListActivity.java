package fi.oulu.tol.esde21.ohapclientesde21.ohap_client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.net.MalformedURLException;
import java.net.URL;

import fi.oulu.tol.esde21.ohapclientesde21.R;
import fi.oulu.tol.esde21.ohapclientesde21.ohap.ConnectionManager;
import fi.oulu.tol.esde21.ohapclientesde21.opimobi_ohap_files.Container;

/**
 * Created by Domu on 07-Apr-15.
 *
 * Activity for displaying a list that holds a number of Items, AKA ContainerActivity
 */


public class ItemListActivity extends Activity {


    private static final String TAG = "ItemListActivity";

    public final static String EXTRA_CONTAINER_ID = "fi.oulu.tol.esde.esde21.CONTAINER_ID";
    public final static String EXTRA_CENTRAL_UNIT_URL = "fi.oulu.tol.esde.esde21.CENTRAL_UNIT_URL";

    ListView listView;

    //the id for the container of this hierarchy
    Long extraContainerId;

    //url for the central unit
    String extraURL;

    Container thisContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        //TODO: handle missing container ID

        extraContainerId = getIntent().getLongExtra(EXTRA_CONTAINER_ID, 0);
        Log.d(TAG, "Gotten container ID Extra: " + extraContainerId);

        extraURL = getIntent().getStringExtra(EXTRA_CENTRAL_UNIT_URL);
        Log.d(TAG, "Gotten central unit URL: " + extraURL);

        // try to use the extra url string to get the central unit in question from ConnectionManger
        try{
            URL centralUnitURL = new URL(extraURL);

            // if the ID we get is for the central unit under where we are, the container central unit itself
            // note, this is most likely pointless since now with opimobi files 1.2 CU registers itself to its' list of items
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



        thisContainer.startListening();

        listView = (ListView) findViewById(R.id.deviceListView);
        listView.setAdapter(new ItemListAdapter(thisContainer));


        // listener for list's items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d(TAG, "Starting list onItemClick");
                Log.d(TAG, "Position: " + Integer.toString(position) + " Id: " + Long.toString(id));

                //if the selected element is container, open a new list, else open the device page
                if (thisContainer.getItemByIndex(position) instanceof Container) {

                    Intent containerIntent = new Intent(ItemListActivity.this, ItemListActivity.class);
                    containerIntent.putExtra(EXTRA_CONTAINER_ID, id);
                    containerIntent.putExtra(EXTRA_CENTRAL_UNIT_URL, extraURL);
                    startActivity(containerIntent);

                } else {
                    Intent deviceIntent = new Intent(ItemListActivity.this, DeviceActivity.class);

                    //give the ID of the device and central unit's URL as extras
                    deviceIntent.putExtra(DeviceActivity.EXTRA_DEVICE_ID, id);
                    deviceIntent.putExtra(DeviceActivity.EXTRA_CENTRAL_UNIT_URL, extraURL);
                    startActivity(deviceIntent);
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //TODO: read below
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
