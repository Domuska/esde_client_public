package fi.oulu.tol.esde21.ohapclientesde21.ohap;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import fi.oulu.tol.esde21.ohapclientesde21.opimobi_ohap_files.CentralUnit;
import fi.oulu.tol.esde21.ohapclientesde21.opimobi_ohap_files.Container;
import fi.oulu.tol.esde21.ohapclientesde21.opimobi_ohap_files.Device;

/**
 * Created by Domu on 05-Apr-15.
 *
 * Concrete implementation of the abstract CentralUnit class
 */
public class CentralUnitConnection extends CentralUnit{

    private final String TAG = "CentralUnitConnection";

    private CentralUnitConnection thisConnection;
    private int nListeners = 0;
    private InputStream inputStream;
    private OutputStream outputStream;

    private String loginName;
    private String password;



    public CentralUnitConnection(URL url, String loginName, String password){
        super(url);

        thisConnection = this;
        this.loginName = loginName;
        this.password = password;
    }

    @Override
    protected void listeningStateChanged(Container container, boolean listening) {

        // if we wanted to start to listen, increase amount of networkListeners,
        // start networking procedures
        if (listening == true){

            if (nListeners == 0) {
                startNetworking();

            }
            sendListeningStart(container);
            nListeners ++;
        } // since listening argument is not true, we want to stop listening to container
        else{
            sendListeningStop(container);
            nListeners--;

            if (nListeners == 0){
                stopNetworking();
            }
        }
    }

    /*@Override
    protected void changeBinaryValue(Device device, boolean value) {

        Log.d(TAG, "starting to send value change request (binary) to server, device(binary) id: " + device.getId()
                    + "value: " + value);

        if(outputStream != null)
            Log.d(TAG, "changeBinaryValue: outputstream is not null");
        else
            Log.d(TAG, "WARNING: changeBinaryValue: outputstream is NULL!");

        Log.d(TAG, "changeBinaryValue: " + 0x0a + " " + device.getId() + " " + value);
        OutgoingMessage outgoingMessage = new OutgoingMessage();
        outgoingMessage.integer8(0x0a)
                .integer32(device.getId())
                .binary8(value)
                .writeTo(outputStream);

        new HandlerThread().start();

    }*/

    @Override
    protected void changeBinaryValue(Device device, boolean value) {

        OutgoingMessage outgoingPingMessage = new OutgoingMessage();
        outgoingPingMessage.integer8(0x0a)
                .integer32(device.getId())
                .binary8(value)
                .writeTo(outputStream);
    }

    @Override
    protected void changeDecimalValue(Device device, double value) {

        Log.d(TAG, "sending value change request (decimal) to server, device(decimal) id: " + device.getId()
                + " value " + value);
        //code for decimal value change 0x09
        OutgoingMessage outgoingMessage = new OutgoingMessage();
        outgoingMessage.integer8(0x09)
                .integer32(device.getId())
                .decimal64(value)
                .writeTo(outputStream);
    }

    private void startNetworking(){

        Log.d(TAG, "startNetworking, starting networking...");
        HbdpConnection hbdpConnection = new HbdpConnection(this.getURL());
        inputStream = hbdpConnection.getInputStream();
        outputStream = hbdpConnection.getOutputStream();

        Log.d(TAG, "startNetworking, gotten connections");

        //log in into the server
        /*
        OutgoingMessage outgoingMessage = new OutgoingMessage();
        outgoingMessage.integer8(0x00)      // message-type-login
                .integer8(0x01)      // protocol-version
                .text("Domuska")        // login-name
                .text("ra7f2mYL")    // login-password
                .writeTo(outputStream);
        */
        OutgoingMessage outgoingMessage = new OutgoingMessage();
        outgoingMessage.integer8(0x00)      // message-type-login
                .integer8(0x01)      // protocol-version
                .text(loginName)        // login-name
                .text(password)    // login-password
                .writeTo(outputStream);

        Log.d(TAG, "login stuff sent to germany");

        new HandlerThread().start();

    }

    private void stopNetworking(){

        Log.d(TAG, "logging out");
        OutgoingMessage outgoingMessage = new OutgoingMessage();
        outgoingMessage.integer8(0x01)
                .text("")
                .writeTo(outputStream);
        Log.d(TAG, "logout stuff sent to germany, closing connection");

        try {
            outputStream.close();
        }
        catch(IOException e){
            Log.d(TAG, "exception when closing the outputStream");
            //TODO: handle the exception better?
        }

    }

    private void sendListeningStart(Container container){

        Log.d(TAG, "sending request to start to listen to container " + container.getName()
                    + " id: " + container.getId());

        OutgoingMessage outgoingMessage = new OutgoingMessage();
        outgoingMessage.integer8(0x0c)
                .integer32(container.getId())
                .writeTo(outputStream);
        Log.d(TAG, "finished trying to send startListening stuff sent to germany");

    }

    private void sendListeningStop(Container container) {

        Log.d(TAG, "sending request to stop to listen to container " + container.getName()
                + " id: " + container.getId());

        OutgoingMessage outgoingMessage = new OutgoingMessage();
        outgoingMessage.integer8(0x0d)
                .integer32(container.getId())
                .writeTo(outputStream);
        Log.d(TAG, "stopListening stuff sent to germany");
    }



    // Thread for handling incoming messages and delivering them to responsible activities
    private class HandlerThread extends Thread{

        Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void run() {

            boolean loopVariable = true;

            Log.d(TAG, "going into the handlerThread loop...");

            while (loopVariable) {
                final IncomingMessage incomingMessage = new IncomingMessage();
                // dont try to read from the stream unless there's no problems
                if (incomingMessage.readFrom(inputStream) == true) {
                    Log.d(TAG, "posting a new handler");
                    handler.post(new IncomingMessageHandler(incomingMessage));

                }
                else{
                    try{
                        Log.d(TAG, "sleeping for 500ms...");
                        sleep(500);
                    }
                    catch(InterruptedException ie){
                        interrupt();
                        Log.d(TAG, "sleep interrupted: interruptedException " + ie.toString());
                    }
                }

                Log.d(TAG, "Starting a new HandlerThread...");
                new HandlerThread().run();
                loopVariable = false;
            }
        }   // run ends
    }       // handlerThread ends


    private class IncomingMessageHandler implements Runnable{

        IncomingMessage storedMessage;

        //stuff for all items
        long itemIdentifier;
        long itemParentIdentifier;
        String itemName;
        String itemDescription;
        boolean itemInternal;
        double itemCoordinateX;
        double itemCoordinateY;
        double itemCoordinateZ;


        //stuff for decimal devices
        double decimalValue;
        double decimalMin;
        double decimalMax;
        String decimalUnit;
        String decimalAbbreviation;

        //stuff for binary devices
        boolean binaryValue;

        public IncomingMessageHandler(IncomingMessage message){
            storedMessage = message;
            if(storedMessage == null)
                Log.d(TAG, "IncomingMessageHandler: IncomingMessage received is null");

        }

        // here we have the actual handling of the messages; what will happen when each message is received
        // specifications for the messages: http://ohap.opimobi.com/ohap_specification_20150504.html
        @Override
        public void run() {
            Log.d(TAG, "Starting to read a new message...");
            int messageType = storedMessage.integer8();
            Log.d(TAG, "Message type is: " + messageType);
            switch(messageType){

                case 0x01: // logout
                    Log.d(TAG, "message received: logout");
                    String errorText = storedMessage.text();
                    Log.d(TAG, "logout error message: " + errorText);
                    //TODO: inform user of log out
                    break;

                case 0x02: //ping
                    Log.d(TAG, "message received: ping");
                    long pingIdentifier = storedMessage.integer32();
                    OutgoingMessage outgoingPingMessage = new OutgoingMessage();
                    outgoingPingMessage.integer8(0x03)
                            .integer32(pingIdentifier)
                            .writeTo(outputStream);
                    Log.d(TAG, "pong sent");
                    break;

                case 0x03: //pong
                    Log.d(TAG, "message received: pong");
                    //TODO: do something when pong is received?
                    break;

                case 0x04: // decimal sensor
                    Log.d(TAG, "message received: new decimal sensor");
                    handleDecimalDevice(4);
                    break;

                case 0x05: //decimal actuator
                    Log.d(TAG, "message received: new decimal actuator");
                    handleDecimalDevice(5);
                    break;

                case 0x06: // binary sensor
                    Log.d(TAG, "message received: new binary sensor");
                    handleBinaryDevice(6);
                    break;

                case 0x07: // binary actuator
                    Log.d(TAG, "message received: new binary actuator");
                    handleBinaryDevice(7);
                    break;

                case 0x08: // container
                    Log.d(TAG, "message received: new container");
                    itemIdentifier = storedMessage.integer32();
                    Log.d(TAG, "received container's id: " + itemIdentifier);
                    getGeneralItemData();

                    if(itemIdentifier == 0){
                        Log.d(TAG, "initializing central unit");
                        thisConnection.setName(itemName);
                        thisConnection.setDescription(itemDescription);
                        thisConnection.setInternal(itemInternal);
                        thisConnection.setLocation((int) itemCoordinateX, (int) itemCoordinateY, (int) itemCoordinateZ);
                        Log.d(TAG, "central unit's name: " + thisConnection.getName());

                    }
                    else{
                        Log.d(TAG, "initializing container");
                        Container container = new Container((Container)ConnectionManager.getInstance()
                                .getCentralUnit(thisConnection.getURL())
                                .getItemById(itemParentIdentifier)
                                , itemIdentifier);

                        container.setName(itemName);
                        container.setDescription(itemDescription);
                        container.setInternal(itemInternal);
                        container.setLocation((int)itemCoordinateX, (int)itemCoordinateY, (int)itemCoordinateZ);
                        Log.d(TAG, "container's name: " + container.getName());

                    }
                    break;

                case 0x09: //decimal changed
                    Log.d(TAG, "message received: decimal value changed");

                    itemIdentifier = storedMessage.integer8();
                    Log.d(TAG, "received device's id: " + itemIdentifier);
                    decimalValue = storedMessage.decimal64();

                    Log.d(TAG,"getting item by id" + itemIdentifier + "from central unit");
                    Device decimalDevice = (Device)ConnectionManager.getInstance()
                                    .getCentralUnit(thisConnection.getURL())
                                    .getItemById(itemIdentifier);
                    Log.d(TAG, "changing value of " + decimalDevice.getName() + " to " + decimalValue);
                    decimalDevice.setDecimalValue(decimalValue);
                    break;

                case 0x0a: //binary changed
                    Log.d(TAG, "message received: binary value changed");
                    itemIdentifier = storedMessage.integer32();
                    Log.d(TAG, "received device's id: " + itemIdentifier);
                    binaryValue = storedMessage.binary8();

                    Log.d(TAG,"getting item by id" + itemIdentifier + "from central unit");
                    Device binaryDevice = (Device)ConnectionManager.getInstance()
                                            .getCentralUnit(thisConnection.getURL())
                                            .getItemById(itemIdentifier);

                    binaryDevice.setBinaryValue(binaryValue);
                    break;

                case 0x0b: //item removed
                    Log.d(TAG, "message received: item removed");

                    itemIdentifier = storedMessage.integer32();
                    Log.d(TAG, "received device's id: " + itemIdentifier);

                    Log.d(TAG,"getting item by id" + itemIdentifier + "from central unit");
                    ConnectionManager.getInstance()
                            .getCentralUnit(thisConnection.getURL())
                            .getItemById(itemIdentifier)
                            .destroy();
                    break;


                default:
                    Log.d(TAG, "message received: unknown");
                    break;
            }



        }

        /**
         * Method for getting data regarding decimal devices
         * and for creating those devices after getting data
         * @param deviceType the type of the device (sensor(4) or actuator(5))
         */
        private void handleDecimalDevice(int deviceType){

            itemIdentifier = storedMessage.integer32();
            Log.d(TAG, "received device's id: " + itemIdentifier);
            decimalValue = storedMessage.decimal64();

            getGeneralItemData();

            decimalMin = storedMessage.decimal64();
            decimalMax = storedMessage.decimal64();
            decimalUnit = storedMessage.text();
            decimalAbbreviation = storedMessage.text();

            Device newDevice;

            if(deviceType == 4){
                Log.d(TAG, "creating a new decimal sensor " + itemName + " ID: " + itemIdentifier);
                newDevice = new Device((Container)ConnectionManager.getInstance()
                        .getCentralUnit(thisConnection.getURL())
                        .getItemById(itemParentIdentifier)
                        ,itemIdentifier, Device.Type.SENSOR, Device.ValueType.DECIMAL);

            }
            else {
                Log.d(TAG, "creating a new decimal actuator " + itemName + " ID: " + itemIdentifier);
                newDevice = new Device((Container)ConnectionManager.getInstance()
                        .getCentralUnit(thisConnection.getURL())
                        .getItemById(itemParentIdentifier)
                        ,itemIdentifier, Device.Type.ACTUATOR, Device.ValueType.DECIMAL);
            }


            newDevice.setDecimalValue(decimalValue);

            newDevice.setName(itemName);
            newDevice.setDescription(itemDescription);
            newDevice.setInternal(itemInternal);
            newDevice.setLocation((int)itemCoordinateX, (int)itemCoordinateY, (int)itemCoordinateZ);

            newDevice.setMinMaxValues(decimalMin, decimalMax);
            newDevice.setUnit(decimalUnit, decimalAbbreviation);

        }

        /**
         * Method for getting data regarding binary devices
         * and for creating those devices after getting data
         * @param deviceType the type of the device (sensor(6) or actuator(7))
         */
        private void handleBinaryDevice(int deviceType){

            itemIdentifier = storedMessage.integer32();
            Log.d(TAG, "received device's id: " + itemIdentifier);
            binaryValue = storedMessage.binary8();

            getGeneralItemData();

            Device newDevice;

            if(deviceType == 6){
                Log.d(TAG, "creating a new binary sensor " + itemName + " ID: " + itemIdentifier);
                newDevice = new Device((Container)ConnectionManager.getInstance()
                        .getCentralUnit(thisConnection.getURL())
                        .getItemById(itemParentIdentifier)
                        , itemIdentifier, Device.Type.SENSOR, Device.ValueType.BINARY);
            }
            else {
                Log.d(TAG, "creating a new binary actuator with name " + itemName + " ID: " + itemIdentifier);
                newDevice = new Device((Container)ConnectionManager.getInstance()
                        .getCentralUnit(thisConnection.getURL())
                        .getItemById(itemParentIdentifier)
                        , itemIdentifier, Device.Type.ACTUATOR, Device.ValueType.BINARY);

            }


            newDevice.setName(itemName);
            newDevice.setBinaryValue(binaryValue);
            newDevice.setDescription(itemDescription);
            newDevice.setInternal(itemInternal);
            newDevice.setLocation((int)itemCoordinateX, (int)itemCoordinateY, (int)itemCoordinateZ);


        }

        /**
         * Helper method for getting general item data from
         * incomingMessage, gets following things that are
         * shared by all items: parent identifier, name,
         * description, internal and item coordinates x, y and z.
         * NOTE: item's ID is not gotten, get it before calling this
         */
        private void getGeneralItemData(){
            Log.d(TAG, "Getting general item data...");
            itemParentIdentifier = storedMessage.integer32();
            itemName = storedMessage.text();
            itemDescription = storedMessage.text();
            itemInternal = storedMessage.binary8();
            itemCoordinateX = storedMessage.decimal64();
            itemCoordinateY = storedMessage.decimal64();
            itemCoordinateZ = storedMessage.decimal64();
        }

    }

}
