package fi.oulu.tol.esde21.ohapclientesde21.ohap_client;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;

import fi.oulu.tol.esde21.ohapclientesde21.R;
import fi.oulu.tol.esde21.ohapclientesde21.opimobi_ohap_files.CentralUnit;
import fi.oulu.tol.esde21.ohapclientesde21.opimobi_ohap_files.Device;


public class DeviceActivity extends ActionBarActivity {

    CentralUnit centralUnit;
    Device device;
    TextView deviceName;
    TextView deviceDescription;
    TextView deviceMinValue;
    TextView deviceMaxValue;
    EditText currentValue;
    SeekBar seekbar;
    Switch aSwitch;




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

        device = new Device(centralUnit, 1, Device.Type.ACTUATOR, Device.ValueType.DECIMAL );
        device.setDecimalValue(70);
        device.setMinMaxValues(0,100);

        device.setName("A bloody ceiling lamp");
        device.setDescription("A lamp. In ceiling. It is not actually bloody.");

        deviceName = (TextView) findViewById(R.id.DeviceName);
        deviceName.setText(device.getName());

        deviceDescription = (TextView) findViewById(R.id.DeviceDescription);
        deviceDescription.setText(device.getDescription());


        seekbar = (SeekBar) findViewById(R.id.DeviceStatus_decimal);
        aSwitch = (Switch) findViewById(R.id.DeviceStatus_binary);


        if(device.getValueType() == Device.ValueType.BINARY){
            seekbar.setVisibility(View.GONE);
            deviceMinValue.setVisibility(View.GONE);
            deviceMaxValue.setVisibility(View.GONE);
            currentValue.setVisibility(View.GONE);

        }
        else {
            aSwitch.setVisibility(View.GONE);

            deviceMinValue = (TextView) findViewById(R.id.text_minvalue);
            deviceMaxValue = (TextView) findViewById(R.id.text_maxvalue);
            currentValue = (EditText) findViewById(R.id.editText_currentValue);

            deviceMinValue.setText(Double.toString(device.getMinValue()));
            deviceMaxValue.setText(Double.toString(device.getMaxValue()));
            currentValue.setText(Double.toString(device.getDecimalValue()));

            seekbar.setMax((int)device.getMaxValue());
            seekbar.setProgress((int)device.getDecimalValue());

            //set the onseekbarchangelistener
            seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                double newProgress = 0;

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    newProgress = progress;
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    //nothing to do here...
                }

                //when user changes value of seekbar, update the editText value
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                    currentValue.setText(Double.toString(newProgress));
                }
            });


        }

        if(device.getType() == Device.Type.SENSOR){
            seekbar.setEnabled(false);
            currentValue.setEnabled(false);
        }


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

    //implementation of the set button
    public void setValue (View v){

        int newValue = Integer.parseInt(currentValue.getText().toString());

        if (newValue >= 0 && newValue <= 100)
            seekbar.setProgress(newValue);


    }

}
