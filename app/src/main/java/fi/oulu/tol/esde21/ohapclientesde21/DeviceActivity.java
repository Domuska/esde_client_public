package fi.oulu.tol.esde21.ohapclientesde21;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;

import fi.oulu.tol.esde21.ohapclientesde21.SupportPackage.CentralUnit;
import fi.oulu.tol.esde21.ohapclientesde21.SupportPackage.Device;


public class DeviceActivity extends ActionBarActivity {

    CentralUnit centralUnit;
    Device device;
    TextView deviceName;
    TextView deviceDescription;
    SeekBar seekbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        try {
            URL url = new URL("http://ohap.opimobi.com:8080/");
            centralUnit = new ConcreteCentralUnit(url) {
            };
        }
        catch (MalformedURLException e){
            //do stuff with the exception...
        }

        centralUnit.setName("OHAP Test server");

        device = new Device(centralUnit, 1, Device.Type.ACTUATOR, Device.ValueType.BINARY );

        device.setName("A bloody ceiling lamp");
        device.setDescription("A lamp. In ceiling. It is not actually bloody.");

        deviceName = (TextView) findViewById(R.id.DeviceName);
        deviceName.setText(device.getName());

        deviceDescription = (TextView) findViewById(R.id.DeviceDescription);
        deviceDescription.setText(device.getDescription());
        
        seekbar = (SeekBar) findViewById(R.id.DeviceStatus_decimal);
        seekbar.setVisibility(View.GONE);

        setTitle(device.getName());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_device, menu);
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
