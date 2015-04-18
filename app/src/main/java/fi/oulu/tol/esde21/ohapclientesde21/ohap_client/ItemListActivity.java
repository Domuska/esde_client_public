package fi.oulu.tol.esde21.ohapclientesde21.ohap_client;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
import fi.oulu.tol.esde21.ohapclientesde21.opimobi_ohap_files.CentralUnit;
import fi.oulu.tol.esde21.ohapclientesde21.opimobi_ohap_files.Container;
import fi.oulu.tol.esde21.ohapclientesde21.opimobi_ohap_files.Device;
import fi.oulu.tol.esde21.ohapclientesde21.opimobi_ohap_files.Item;

/**
 * Created by Domu on 07-Apr-15.
 *
 * Activity for displaying a list that holds a number of Items
 */


//TODO:
public class ItemListActivity extends Activity {

    CentralUnit centralUnit;
    Device device;

    //public list of items that are in the list. This wont work in future.
    //public static ArrayList<Item> itemList;

    private final static String EXTRA_CONTAINER_ID = "containerId";
    private final static String EXTRA_PREFIX_STRING = "prefixData";

    ListView listView;
    String extraPrefix;

    //the id for the container of this hierarchy
    String extraContainerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        //populate list with dummy data
        populateList();

        extraPrefix = getIntent().getStringExtra(EXTRA_PREFIX_STRING);
        extraContainerId = getIntent().getStringExtra(EXTRA_CONTAINER_ID);

        listView = (ListView) findViewById(R.id.deviceListView);
        listView.setAdapter(new OhapListAdapter(extraPrefix + "/", extraContainerId));


        //listener for list's items

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //if the selected element is container, open a new list, else open the device page
                if(EntryActivity.getCentralUnitItem(id) instanceof Container){


                    Intent containerIntent = new Intent(ItemListActivity.this, ItemListActivity.class);
                    containerIntent.putExtra("prefixData", extraPrefix);
                    containerIntent.putExtra(EXTRA_CONTAINER_ID, Long.toString(id));
                    startActivity(containerIntent);

                }
                else {
                    Intent deviceIntent = new Intent(ItemListActivity.this, DeviceActivity.class);
                    //as id we need to query entryactivity for an item and ask its' id
                    deviceIntent.putExtra("deviceId", id);
                    startActivity(deviceIntent);
                }
            }
        });

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

    //populate list with dummy data
    private void populateList(){








    }
}
