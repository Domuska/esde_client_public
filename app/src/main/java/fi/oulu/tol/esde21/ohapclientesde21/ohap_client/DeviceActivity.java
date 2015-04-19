package fi.oulu.tol.esde21.ohapclientesde21.ohap_client;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v4.app.NavUtils;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.lang.reflect.Method;

import fi.oulu.tol.esde21.ohapclientesde21.R;
import fi.oulu.tol.esde21.ohapclientesde21.opimobi_ohap_files.Device;


public class DeviceActivity extends Activity {

    //CentralUnit centralUnit;
    //Device device;
    Device aDevice;
    TextView deviceName;
    TextView deviceDescription;
    TextView deviceMinValue;
    TextView deviceMaxValue;
    TextView deviceType;
    TextView devicePath;
    EditText currentValue;
    SeekBar seekbar;
    Switch aSwitch;
    Button setButton;


    // let's use a hard coded value for now, perhaps later on put
    // the value into sharedPreferences?
    Boolean isTracked = true;
    String prefixString;

    static final String DEVICE_ID = "deviceId";
    private final static String EXTRA_PREFIX_STRING = "prefixData";
    private static final String TAG = "DeviceActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        long deviceId = getIntent().getLongExtra(DEVICE_ID, 0);
        prefixString = getIntent().getStringExtra(EXTRA_PREFIX_STRING);


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

        devicePath = (TextView) findViewById(R.id.DeviceHierarchyPath);
        devicePath.setText(prefixString);

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


        // set the up the "up" navigation
        // TODO: käytetään ehkä itemListActivityssä onSaveInstanceState tai jotain
        // tallentamaan polku tai containerin id? Näin tämä ei toimi, sillä
        // aktiviteetti aloitetaan uudestaan ja itemlistactivity tarvii ekstroina ainakin containerin
        // (tän devicen parentin) id:n.
        getActionBar().setDisplayHomeAsUpEnabled(true);




        // create a notification to be fired when the activity is created (this will change later to actually respond to changes in the actual device)
        // http://developer.android.com/guide/topics/ui/notifiers/notifications.html#CreateNotification
        final NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this).
                        setSmallIcon(R.drawable.ic_system_update_white_24dp).
                        setContentTitle(aDevice.getName()).
                setContentText("Something happened to this device...");

        Intent resultIntent = new Intent(this, DeviceActivity.class);
        resultIntent.putExtra(DEVICE_ID, deviceId);


        //requires API level 16

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addParentStack(DeviceActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);




        final NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //mNotificationManager.notify(1, mBuilder.build());


        if(isTracked == true) {
            new CountDownTimer(10000, 1000) {


                @Override
                public void onFinish() {
                    mNotificationManager.notify(1, mBuilder.build());
                }

                @Override
                public void onTick(long millisUntilFinished) {
                    //do nothing
                }
            }.start();
        }



    }

    // override the onMenuOpened method to set icons visible in the overflow menu
    // credit to Simon @ http://stackoverflow.com/questions/18374183/how-to-show-icons-in-overflow-menu-in-actionbar
    @Override
    public boolean onMenuOpened(int featureId, Menu menu)
    {
        if(featureId == Window.FEATURE_ACTION_BAR && menu != null){
            if(menu.getClass().getSimpleName().equals("MenuBuilder")){
                try{
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                }
                catch(NoSuchMethodException e){
                    Log.e(TAG, "onMenuOpened called ", e);
                }
                catch(Exception e){
                    throw new RuntimeException(e);
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
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
        if (id == R.id.monitor_enable) {
            isTracked = true;
            return true;
        }
        if(id == R.id.monitor_disable){
            isTracked = false;
            return true;
        }
        if(id == android.R.id.home){
            Intent i = NavUtils.getParentActivityIntent(this);
            // if the activity is on Android's back stack, do not recreate the activity
            // but rather bring the existing activity forth (the itemListActivity)
            // credit to yonojoy @ http://stackoverflow.com/questions/13293772/how-to-navigate-up-to-the-same-parent-state/17342137#17342137
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            NavUtils.navigateUpTo(this, i);
            return true;

        }


        return super.onOptionsItemSelected(item);
    }

    //implementation of the "set" button
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
