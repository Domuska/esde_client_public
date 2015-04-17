package fi.oulu.tol.esde21.ohapclientesde21.ohap_client;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import fi.oulu.tol.esde21.ohapclientesde21.R;
import fi.oulu.tol.esde21.ohapclientesde21.opimobi_ohap_files.Device;


public class DeviceActivity extends ActionBarActivity {

    //CentralUnit centralUnit;
    //Device device;
    Device aDevice;
    TextView deviceName;
    TextView deviceDescription;
    TextView deviceMinValue;
    TextView deviceMaxValue;
    TextView deviceType;
    EditText currentValue;
    SeekBar seekbar;
    Switch aSwitch;
    Button setButton;

    Boolean isTracked;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        long deviceId = getIntent().getLongExtra("deviceId", 0);

        /*try {
            URL url = new URL("http://ohap.opimobi.com:8080/");
            centralUnit = new ConcreteCentralUnit(url) {
            };
        }
        catch (MalformedURLException e){
            //do stuff with the exception...
        }*/

        /*centralUnit.setName("OHAP Test server");

        device = new Device(centralUnit, 1, Device.Type.ACTUATOR, Device.ValueType.DECIMAL );
        device.setDecimalValue(70);
        device.setMinMaxValues(0,100);

        device.setName("A bloody ceiling lamp");
        device.setDescription("A lamp. In ceiling. It is not actually bloody.");*/


        //get the item from the ItemListActivity's public list of items, cast it into Device.
        //This might be a source of bugs, since here there are no checks if the item we get is an actual device or not
        aDevice = (Device)EntryActivity.getCentralUnitItem(deviceId);

        deviceName = (TextView) findViewById(R.id.DeviceName);
        deviceName.setText(aDevice.getName());

        deviceDescription = (TextView) findViewById(R.id.DeviceDescription);
        deviceDescription.setText(aDevice.getDescription());


        seekbar = (SeekBar) findViewById(R.id.DeviceStatus_decimal);
        aSwitch = (Switch) findViewById(R.id.DeviceStatus_binary);
        currentValue = (EditText) findViewById(R.id.editText_currentValue);
        deviceMinValue = (TextView) findViewById(R.id.text_minvalue);
        deviceMaxValue = (TextView) findViewById(R.id.text_maxvalue);
        setButton = (Button) findViewById(R.id.currentValue_setButton);
        deviceType = (TextView) findViewById(R.id.DeviceType);


        //if device is binary, hide UI elements related to decimal devices
        if(aDevice.getValueType() == Device.ValueType.BINARY){
            seekbar.setVisibility(View.GONE);
            deviceMinValue.setVisibility(View.GONE);
            deviceMaxValue.setVisibility(View.GONE);
            currentValue.setVisibility(View.GONE);
            setButton.setVisibility(View.GONE);

        }
        //if device isn't binary it is decimal so hide binary device UI elements and set values
        //for decimal device
        //TODO: check for invalid values (0-100 valid atm)
        else {
            aSwitch.setVisibility(View.GONE);

            deviceMinValue.setText(Double.toString(aDevice.getMinValue()));
            deviceMaxValue.setText(Double.toString(aDevice.getMaxValue()));
            currentValue.setText(Double.toString(aDevice.getDecimalValue()));

            seekbar.setMax((int)aDevice.getMaxValue());
            seekbar.setProgress((int)aDevice.getDecimalValue());

            //set the onseekbarchangelistener to update currentValue if seekbar is touched
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

        //if device is a sensor, set switch and seekbar disabled
        if(aDevice.getType() == Device.Type.SENSOR){
            seekbar.setEnabled(false);
            currentValue.setEnabled(false);
            aSwitch.setEnabled(false);
            deviceType.setText("Sensor");
        }
        //set device type text actuator
        else
            deviceType.setText("Actuator");






        setTitle(aDevice.getName());

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

        double newValue = -1;

        //check if the field is empty
        if(!TextUtils.isEmpty(currentValue.getText().toString()))
            newValue = Double.parseDouble(currentValue.getText().toString());

        //if not, check if the value is valid and act accordingly
        if (newValue >= 0 && newValue <= 100)
            seekbar.setProgress((int)newValue);


    }

}
