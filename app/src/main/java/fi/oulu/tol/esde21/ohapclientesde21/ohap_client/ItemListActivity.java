package fi.oulu.tol.esde21.ohapclientesde21.ohap_client;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import fi.oulu.tol.esde21.ohapclientesde21.R;
import fi.oulu.tol.esde21.ohapclientesde21.opimobi_ohap_files.CentralUnit;
import fi.oulu.tol.esde21.ohapclientesde21.opimobi_ohap_files.Device;

/**
 * Created by Domu on 07-Apr-15.
 *
 * Activity for displaying a list that holds a number of Items
 */

public class ItemListActivity extends ActionBarActivity {

    CentralUnit centralUnit;
    Device device;
    public static ArrayList<Device> deviceList;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        //populate list with dummy data
        populateList();

        listView = (ListView) findViewById(R.id.deviceListView);
        listView.setAdapter(new OhapListAdapter("prefixi ", deviceList));

        //listener for list's items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(ItemListActivity.this, DeviceActivity.class);
                //TODO: is this the right number to pass to the activity? are we sure that the position is the same as item's ID in our arraylist?
                i.putExtra("deviceId", position);
                startActivity(i);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_list, menu);
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

        try {
            URL url = new URL("http://ohap.opimobi.com:8080/");
            centralUnit = new ConcreteCentralUnit(url) {
            };
        }
        catch (MalformedURLException e){
            //do stuff with the exception...
        }

        centralUnit.setName("OHAP Test server");

        device = new Device(centralUnit, 1, Device.Type.ACTUATOR, Device.ValueType.DECIMAL );
        device.setDecimalValue(70);
        device.setMinMaxValues(0,100);

        device.setName("A bloody ceiling lamp");
        device.setDescription("A lamp. In ceiling. It is not actually bloody.");

        deviceList = new ArrayList<>();
        deviceList.add(device);


        Device device2 = new Device (centralUnit, 2, Device.Type.ACTUATOR, Device.ValueType.BINARY);
        device2.setName("Another sodding lamp");
        device2.setDescription("Old lamp. On or off.");
        device2.changeBinaryValue(true);


        deviceList.add(device2);

        Device device3 = new Device (centralUnit, 3, Device.Type.SENSOR, Device.ValueType.BINARY);
        device3.setName("Fancy hi-tech button lamp");
        device3.setDescription("a sensor for a fancy lamp");
        device3.setBinaryValue(true);

        deviceList.add(device3);



    }
}
