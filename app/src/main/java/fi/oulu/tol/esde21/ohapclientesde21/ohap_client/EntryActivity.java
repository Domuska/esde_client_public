package fi.oulu.tol.esde21.ohapclientesde21.ohap_client;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import fi.oulu.tol.esde21.ohapclientesde21.R;
import fi.oulu.tol.esde21.ohapclientesde21.opimobi_ohap_files.CentralUnit;
import fi.oulu.tol.esde21.ohapclientesde21.opimobi_ohap_files.Container;
import fi.oulu.tol.esde21.ohapclientesde21.opimobi_ohap_files.Device;
import fi.oulu.tol.esde21.ohapclientesde21.opimobi_ohap_files.Item;

public class EntryActivity extends ActionBarActivity {

    static ConcreteCentralUnit centralUnit;
    private final static String EXTRA_CONTAINER_ID = "containerId";
    private final static String EXTRA_PREFIX_STRING = "prefixData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        try {
            URL url = new URL("http://ohap.opimobi.com:8080/");
            centralUnit = new ConcreteCentralUnit(url) {
            };
        }
        catch (MalformedURLException e){
            //do stuff with the exception...
        }

        centralUnit.setName("OHAP Test server");

        //populate CU with dummy data
        populateCU();

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // this method is the onClick method for the button in the middle of the screen
    // it starts the first list activity and passes the ID of the CU to it
    public void openList(View v){

        Intent i = new Intent(this, ItemListActivity.class);
        i.putExtra(EXTRA_CONTAINER_ID, Long.toString(centralUnit.getId()));
        i.putExtra(EXTRA_PREFIX_STRING, centralUnit.getName());
        startActivity(i);
    }


    //class method for getting containers in the CU (or the CU itself).
    //gets an ID for the container that is wanted and returns the container in question,
    //it can be either a subcontainer in the central unit or the central unit itself.
    public static Item getCentralUnitItem(long id){

        if(id == centralUnit.getId()){

            return centralUnit;

        }
        else{

            return centralUnit.getItemById(id);

        }

    }



    private void populateCU(){


        Device device = new Device(centralUnit, 1, Device.Type.ACTUATOR, Device.ValueType.DECIMAL );
        device.setDecimalValue(70);
        device.setMinMaxValues(0, 100);

        device.setName("A bloody ceiling lamp");
        device.setDescription("A lamp. In ceiling. It is not actually bloody.");

        Device device2 = new Device (centralUnit, 2, Device.Type.ACTUATOR, Device.ValueType.BINARY);
        device2.setName("Another sodding lamp");
        device2.setDescription("Old lamp. On or off.");
        device2.changeBinaryValue(true);


        Device device3 = new Device (centralUnit, 3, Device.Type.SENSOR, Device.ValueType.BINARY);
        device3.setName("Fancy hi-tech button lamp");
        device3.setDescription("a sensor for a fancy lamp");
        device3.setBinaryValue(true);

        Container container1 = new Container(centralUnit, 4);
        container1.setName("Seppo's working room");
        container1.setDescription("The wonderful room of Seppo, 5/5");


        Device device4 = new Device(container1, 5, Device.Type.ACTUATOR, Device.ValueType.BINARY);
        device4.setName("hahaa!");
    }

}
