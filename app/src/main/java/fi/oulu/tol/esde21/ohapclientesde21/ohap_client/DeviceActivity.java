package fi.oulu.tol.esde21.ohapclientesde21.ohap_client;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.media.audiofx.BassBoost;
import android.os.Build;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v4.app.NotificationCompat;
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
import android.widget.Toast;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import fi.oulu.tol.esde21.ohapclientesde21.R;
import fi.oulu.tol.esde21.ohapclientesde21.ohap.CentralUnitConnection;
import fi.oulu.tol.esde21.ohapclientesde21.ohap.ConnectionManager;
import fi.oulu.tol.esde21.ohapclientesde21.opimobi_ohap_files.Container;
import fi.oulu.tol.esde21.ohapclientesde21.opimobi_ohap_files.Device;


public class DeviceActivity extends Activity implements SensorEventListener {

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

    Activity thisActivity;

    private SensorManager mSensorManager;
    private Sensor mSensorSignificant = null;
    private Sensor mAccelerometer = null;
    private TriggerEventListener mListener = new TriggerListener();
    SharedPreferences sharedPref;

    // let's use a hard coded value for now, perhaps later on put
    // the value into sharedPreferences?
    Boolean isTracked = true;

    private static final String TAG = "DeviceActivity";

    public static final String EXTRA_CENTRAL_UNIT_URL = "fi.oulu.tol.esde21.CENTRAL_UNIT_URL";
    public static final String EXTRA_DEVICE_ID = "fi.oulu.tol.esde21.DEVICE_ID";


    // variables related to usage of accelerometer sensor, credit to Sashen Govender at:
    // http://code.tutsplus.com/tutorials/using-the-accelerometer-on-android--mobile-22125

    private long timeSinceLastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_TRIGGER = 600;


    CentralUnitConnection centralUnit;
    URL centralUnitUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        long deviceId = getIntent().getLongExtra(EXTRA_DEVICE_ID, 0);

        sharedPref = PreferenceManager
                .getDefaultSharedPreferences(this);


        try {
            centralUnitUrl = new URL(getIntent().getStringExtra(EXTRA_CENTRAL_UNIT_URL));
        }
        catch (MalformedURLException e){
            Log.d(TAG, "onCreate malformedURLexception");
        }

        centralUnit = ConnectionManager.getInstance().getCentralUnit(centralUnitUrl);
        thisActivity = this;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);






        if(deviceId != 0) {
            //get the item from the ItemListActivity's public list of items, cast it into Device.
            //This might be a source of bugs, since here there are no checks if the item we get is an actual device or not
            aDevice = (Device) centralUnit.getItemById(deviceId);

            deviceName = (TextView) findViewById(R.id.DeviceName);
            deviceName.setText(aDevice.getName());

            deviceDescription = (TextView) findViewById(R.id.DeviceDescription);
            deviceDescription.setText(aDevice.getDescription());

            devicePath = (TextView) findViewById(R.id.DeviceHierarchyPath);

            seekbar = (SeekBar) findViewById(R.id.DeviceStatus_decimal);
            aSwitch = (Switch) findViewById(R.id.DeviceStatus_binary);
            currentValue = (EditText) findViewById(R.id.editText_currentValue);
            deviceMinValue = (TextView) findViewById(R.id.text_minvalue);
            deviceMaxValue = (TextView) findViewById(R.id.text_maxvalue);
            setButton = (Button) findViewById(R.id.currentValue_setButton);
            deviceType = (TextView) findViewById(R.id.DeviceType);


            //if device is binary, hide UI elements related to decimal devices
            if (aDevice.getValueType() == Device.ValueType.BINARY) {
                seekbar.setVisibility(View.GONE);
                deviceMinValue.setVisibility(View.GONE);
                deviceMaxValue.setVisibility(View.GONE);
                currentValue.setVisibility(View.GONE);
                setButton.setVisibility(View.GONE);

                //set switch status based on device's status
                aSwitch.setChecked(aDevice.getBinaryValue());

            }
            //if device isn't binary it is decimal so hide binary device UI elements and set values
            //for decimal device
            else {
                aSwitch.setVisibility(View.GONE);

                deviceMinValue.setText(Double.toString(aDevice.getMinValue()));
                deviceMaxValue.setText(Double.toString(aDevice.getMaxValue()));
                currentValue.setText(Double.toString(aDevice.getDecimalValue()));

                //TODO: should seekbar be used? values can't go under 0...
                seekbar.setMax((int) aDevice.getMaxValue());
                seekbar.setProgress((int) aDevice.getDecimalValue());

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
            if (aDevice.getType() == Device.Type.SENSOR) {
                seekbar.setEnabled(false);
                currentValue.setEnabled(false);
                aSwitch.setEnabled(false);
                deviceType.setText("Sensor");
                setButton.setEnabled(false);
            }
            //set device type text actuator
            else
                deviceType.setText("Actuator");


            setTitle(aDevice.getName());

            Container parentVariable = aDevice.getParent();
            String prefix_ = "";

            Log.d(TAG, "Starting do-while loop");
            Log.d(TAG, "Parent name: " + parentVariable.getName());
            do {

                prefix_ = parentVariable.getName() + "/" + prefix_;
                parentVariable = parentVariable.getParent();

            } while (parentVariable != null);

            devicePath.setText(prefix_);



            // if user has set in setting sensor activity to be on, initialize sensor business
            if (sharedPref.getString("pref_key_sensorlist", "None").contentEquals("Significant")){   //contentEquals("Significant")) {

                Log.d(TAG, "registering significant motion sensor");
                // check if the current API level is enough for this sensor and that the device is an actuator
                if (Build.VERSION.SDK_INT >= 18 && (aDevice.getType() == Device.Type.ACTUATOR))
                    mSensorSignificant = mSensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);
            }

            else if(sharedPref.getString("pref_key_sensorlist", "None")
                    .contentEquals("Accelerometer")){
                Log.d(TAG, "registering accelerometer sensor");
                mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            }



            // create a notification to be fired when the activity is created (this will change later to actually respond to changes in the actual device)
            // http://developer.android.com/guide/topics/ui/notifiers/notifications.html#CreateNotification
            final NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this).
                            setSmallIcon(R.drawable.ic_system_update_white_24dp).
                            setContentTitle(aDevice.getName()).
                            setContentText("Something happened to this device...");

            Intent resultIntent = new Intent(this, DeviceActivity.class);
            resultIntent.putExtra(EXTRA_DEVICE_ID, deviceId);


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


            if (isTracked == true) {
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
        else{
            Log.d(TAG, "no ID for the device was found");
            Toast.makeText(this, "Sorry, something went wrong...", Toast.LENGTH_LONG);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        // if mSensorSignificant is null, then API level is too low for Significant Motion Sensor
        if(mSensorSignificant != null){
            Log.d(TAG, "re-registering sigmo sensor");
            mSensorManager.registerListener(this, mSensorSignificant, SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.requestTriggerSensor(mListener, mSensorSignificant);
        }
        else if(mAccelerometer != null){
            Log.d(TAG, "re-registering accelerometer sensor");
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // if mSensorSignificant is null, then API level is too low for Significant Motion Sensor, or the chosen option is None or Accelerometer
        if(mSensorSignificant != null){
            Log.d(TAG, "unregistering sigmo sensor");
            mSensorManager.unregisterListener(this);
            mSensorManager.cancelTriggerSensor(mListener, mSensorSignificant);

        }
        if(mSensorSignificant != null){
            Log.d(TAG, "unregistering sigmo sensor");
            mSensorManager.unregisterListener(this);
            mSensorManager.cancelTriggerSensor(mListener, mSensorSignificant);

        }
        if(mAccelerometer != null){
            Log.d(TAG, "unregistering accelerometer sensor");
            mSensorManager.unregisterListener(this);
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

        SharedPreferences.Editor editor = sharedPref.edit();
        int id = item.getItemId();

        if (id == R.id.monitor_enable) {
            isTracked = true;
            //TODO: save value in preferences for this device
            return true;
        }
        if(id == R.id.monitor_disable){
            isTracked = false;
            //TODO: save value in preferences for this device
            return true;
        }
        if(id == R.id.sensor_enable_disable){

            // the button is a toggle, if current value is true set shared pref value to false and other way around
            if(sharedPref.getString("pref_key_sensorlist", "None")
                    .contentEquals("None")) {
                editor.putString(SettingsFragment.KEY_LIST_SENSOR, "Significant");
                Log.d(TAG, "setting sharedpref sensor value to sigmo");

                Toast.makeText(this, getResources().getString(R.string.device_sensorToSigMo),
                                Toast.LENGTH_SHORT).show();

                if(mSensorSignificant != null){
                    Log.d(TAG, "registering sigmo listener");
                    mSensorManager.registerListener(this, mSensorSignificant, SensorManager.SENSOR_DELAY_NORMAL);
                    mSensorManager.requestTriggerSensor(mListener, mSensorSignificant);
                }

//                if(mSensorSignificant != null){
//                    mSensorManager.unregisterListener(this);
//                    mSensorManager.cancelTriggerSensor(mListener, mSensorSignificant);
//                }

                editor.commit();
            }

            else if(sharedPref.getString("pref_key_sensorlist", "None")
                    .contentEquals("Significant")) {
                editor.putString(SettingsFragment.KEY_LIST_SENSOR, "Accelerometer");
                Log.d(TAG, "setting sharedpref sensor value to accelerometer");

                Toast.makeText(this, getResources().getString(R.string.device_sensorToAccel),
                        Toast.LENGTH_SHORT).show();

                if(mSensorSignificant != null){
                    Log.d(TAG, "unregistering sigmo listener and triggersensor");
                    mSensorManager.unregisterListener(this);
                    mSensorManager.cancelTriggerSensor(mListener, mSensorSignificant);
                }

                if(mAccelerometer != null){
                    Log.d(TAG, "registering accelerometer listener");
                    mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                }

                editor.commit();
            }

            else if(sharedPref.getString("pref_key_sensorlist", "None")
                    .contentEquals("Accelerometer")) {
                editor.putString(SettingsFragment.KEY_LIST_SENSOR, "None");
                Log.d(TAG, "setting sharedpref sensor value to none");

                Toast.makeText(this, getResources().getString(R.string.device_sensorDisable),
                        Toast.LENGTH_SHORT).show();

                if(mAccelerometer != null){
                    Log.d(TAG, "unregistering accelerometer listener");
                    mSensorManager.unregisterListener(this);
                }
                editor.commit();
            }
//            else {
//                editor.putBoolean(SettingsFragment.KEY_CHECKBOX_SENSOR, true);
//                Log.d(TAG, "setting shared pref value to true");
//
//                Toast.makeText(this, getResources().getString(R.string.device_sensorEnable),
//                        Toast.LENGTH_SHORT).show();
//
//                if(mSensorSignificant != null){
//                    mSensorManager.registerListener(this, mSensorSignificant, SensorManager.SENSOR_DELAY_NORMAL);
//                    mSensorManager.requestTriggerSensor(mListener, mSensorSignificant);
//                }
//
//                editor.commit();
//            }
//
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

    // change teh value of the actuator (device takes care of sending request to the server)
    public void onClickSetButton (View v){

        double newValue = -1;

        //check if the field is empty
        if(!TextUtils.isEmpty(currentValue.getText().toString())) {
            newValue = Double.parseDouble(currentValue.getText().toString());
            aDevice.changeDecimalValue(newValue);
        }

        //if not, check if the value is valid and act accordingly
        if (newValue >= aDevice.getMinValue() && newValue <= aDevice.getMaxValue())
            seekbar.setProgress((int)newValue);

    }

    // change device's (which will send an request for server) state
    // NOTE: if device's ID is 2, the program will crash. No clue why!
    public void onBinaryButtonClicked (View v){
        Log.d(TAG, "binary button clicked: " + aSwitch.isChecked());
        boolean status = aSwitch.isChecked();
        aDevice.changeBinaryValue(status);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        //Credit for simple Accelerometer usage tutorial to Sashen Govender at:
        // http://code.tutsplus.com/tutorials/using-the-accelerometer-on-android--mobile-22125

        Sensor sensor = event.sensor;

        if(sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            // Extracts current coordinates for device from sensor
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            long currentTime = System.currentTimeMillis();

            if((currentTime - timeSinceLastUpdate) > 100)
            {
                long timeDifference = (currentTime - timeSinceLastUpdate);
                timeSinceLastUpdate = currentTime;

                float shake_speed = Math
                        .abs(x + y + z - last_x - last_y - last_z)/ timeDifference * 10000;

                if (shake_speed > SHAKE_TRIGGER){

                    Log.d(TAG, "shaken, not stirred with accelerometer");
                    //TODO: define what happens when shaked (random value for slider)

                    Random random = new Random();

                    if(aDevice.getValueType() == Device.ValueType.DECIMAL) {
                        int newInt = random.nextInt(((int)aDevice.getMaxValue() - (int)aDevice.getMinValue() + 1) + (int)aDevice.getMinValue());
                        aDevice.changeDecimalValue(newInt);

                        Toast.makeText(thisActivity,
                                getResources().getString(R.string.device_sensorToast)
                                        + newInt,
                                Toast.LENGTH_SHORT).show();

                        Log.d(TAG, "set random decimal value as " + newInt);
                        seekbar.setProgress(newInt);
                        currentValue.setText(Integer.toString(newInt));
                    }
                    else{
                        boolean newBoolean = random.nextBoolean();
                        aDevice.changeBinaryValue(newBoolean);

                        Toast.makeText(thisActivity,
                                getResources().getString(R.string.device_sensorToast)
                                        + newBoolean,
                                Toast.LENGTH_SHORT).show();

                        Log.d(TAG, "set random binary value as " + newBoolean);
                        aSwitch.setChecked(newBoolean);

                    }
                }

                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    // inner class for the significant motion sensor
    class TriggerListener extends TriggerEventListener{

        Random random = new Random();

        @Override
        public void onTrigger(TriggerEvent e){

            if(aDevice.getValueType() == Device.ValueType.DECIMAL) {
                int newInt = random.nextInt(((int)aDevice.getMaxValue() - (int)aDevice.getMinValue() + 1) + (int)aDevice.getMinValue());
                aDevice.changeDecimalValue(newInt);

                Toast.makeText(thisActivity,
                                getResources().getString(R.string.device_sensorToast)
                                + newInt,
                                Toast.LENGTH_SHORT).show();

                Log.d(TAG, "set random decimal value as " + newInt);
                seekbar.setProgress(newInt);
                currentValue.setText(Integer.toString(newInt));
            }
            else{
                boolean newBoolean = random.nextBoolean();
                aDevice.changeBinaryValue(newBoolean);

                Toast.makeText(thisActivity,
                                getResources().getString(R.string.device_sensorToast)
                                + newBoolean,
                                Toast.LENGTH_SHORT).show();

                Log.d(TAG, "set random binary value as " + newBoolean);
                aSwitch.setChecked(newBoolean);

            }

            // re-register trigger since significant motion sensor is fire-once only
            mSensorManager.requestTriggerSensor(this, mSensorSignificant);
        }


    }
}
