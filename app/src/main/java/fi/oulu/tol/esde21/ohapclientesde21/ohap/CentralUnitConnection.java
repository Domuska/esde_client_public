package fi.oulu.tol.esde21.ohapclientesde21.ohap;

import android.os.Handler;
import android.os.Looper;
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



    public CentralUnitConnection(URL url){
        super(url);
        //populateCU();
        thisConnection = this;

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

    @Override
    protected void changeBinaryValue(Device device, boolean value) {

        Log.d(TAG, "sending value change request to server, device(binary) id: " + device.getId());

        OutgoingMessage outgoingMessage = new OutgoingMessage();
        outgoingMessage.integer8(0x0a)
                .integer32(device.getId())
                .binary8(value)
                .writeTo(outputStream);

    }

    @Override
    protected void changeDecimalValue(Device device, double value) {

        Log.d(TAG, "sending value change request to server, device(decimal) id: " + device.getId());
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
        // TODO: get login credentials from shared preferences
        OutgoingMessage outgoingMessage = new OutgoingMessage();
        outgoingMessage.integer8(0x00)      // message-type-login
                .integer8(0x01)      // protocol-version
                .text("Domuska")        // login-name
                .text("ra7f2mYL")    // login-password
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

    private void sendListeningStop(Container container){

        Log.d(TAG, "sending request to stop to listen to container " + container.getName()
                + " id: " + container.getId());

        OutgoingMessage outgoingMessage = new OutgoingMessage();
        outgoingMessage.integer8(0x0d)
                .integer32(container.getId())
                .writeTo(outputStream);
        Log.d(TAG, "stopListening stuff sent to germany");
    }

    //create dummy data
    private void populateCU(){

        this.setName("OHAP Test server");

        Device device = new Device(this, 1, Device.Type.ACTUATOR, Device.ValueType.DECIMAL );
        device.setDecimalValue(70);
        device.setMinMaxValues(0, 100);

        device.setName("A bloody ceiling lamp");
        device.setDescription("A lamp. In ceiling. It is not actually bloody.");

        Device device2 = new Device (this, 2, Device.Type.ACTUATOR, Device.ValueType.BINARY);
        device2.setName("Another sodding lamp");
        device2.setDescription("Old lamp. On or off.");
        device2.changeBinaryValue(true);


        Device device3 = new Device (this, 3, Device.Type.SENSOR, Device.ValueType.BINARY);
        device3.setName("Fancy hi-tech lamp's sensor");
        device3.setDescription("Sensor sensing the fancy lamp.");
        device3.setBinaryValue(true);

        Container container1 = new Container(this, 4);
        container1.setName("Seppo's working room");
        container1.setDescription("The wonderful room of Seppo, 5/5. No one will see this text!");

        Container container2 = new Container(this, 6);
        container2.setName("Hermandos' Working Closet");

        Device device4 = new Device(container1, 5, Device.Type.ACTUATOR, Device.ValueType.BINARY);
        device4.setName("Surprise device!");



        Container container3 = new Container(container1, 7);
        container3.setName("Seppo's room's broom closet");

        // let's create a couple more to for testing memory usage and responsiveness...
        for(int i = 8; i < 155; i++){

            Device deviceFor = new Device(container1, i, Device.Type.SENSOR, Device.ValueType.DECIMAL);
            deviceFor.setName("Markku Markkula proximity sensor");
            deviceFor.setDescription("Sensor for calculating the propability of Markku Markkula approaching");
            deviceFor.setMinMaxValues(0, 100);

            deviceFor.setDecimalValue(5);
        }

    }



    // Thread for handling incoming messages and delivering them to responsible activities

    private class HandlerThread extends Thread{

        Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void run() {

            //TODO: not sure if this variable is actually necessary, test to see if stuff breaks when it's removed
            boolean loopVariable = true;



            //IncomingMessage incomingMessage = new IncomingMessage();
            Log.d(TAG, "going into the handlerThread loop...");


            while (loopVariable) {
                final IncomingMessage incomingMessage = new IncomingMessage();
                // dont try to read from the stream unless there's no problems
                // (IncomingMessage will return false if there's an exception while reading)
                if (incomingMessage.readFrom(inputStream) == true) {
                    //IncomingMessage incomingMessage = new IncomingMessage();
                    //incomingMessage.readFrom(inputStream);
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
            //Log.d(TAG, "Starting a new HandlerThread...");
            //new HandlerThread().run();
        } // run ends
    } //handlerThread ends


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
                    //TODO: how to inform user that he's been logged out?
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

                    //itemIdentifier = storedMessage.integer32();
                    //decimalValue = storedMessage.decimal64();

                    // get parent ID, name, description, internal and X, Y, Z coords
                    handleDecimalDevice(4);

                    /*itemParentIdentifier = storedMessage.integer32();
                    itemName = storedMessage.text();
                    itemDescription = storedMessage.text();
                    itemInternal = storedMessage.binary8();
                    itemCoordinateX = storedMessage.decimal64();
                    itemCoordinateY = storedMessage.decimal64();
                    itemCoordinateZ = storedMessage.decimal64();*/

                    /*decimalMin = storedMessage.decimal64();
                    decimalMax = storedMessage.decimal64();
                    decimalUnit = storedMessage.text();
                    decimalAbbreviation = storedMessage.text();*/


                    /*Log.d(TAG, "creating a new decimal sensor");
                    newDevice = new Device((Container)ConnectionManager.getInstance()
                            .getCentralUnit(thisConnection.getURL())
                            .getItemById(itemParentIdentifier)
                            ,itemIdentifier, Device.Type.SENSOR, Device.ValueType.DECIMAL);*/

                    /*newDevice.setDecimalValue(decimalValue);

                    newDevice.setName(itemName);
                    newDevice.setDescription(itemDescription);
                    newDevice.setInternal(itemInternal);
                    newDevice.setLocation((int)itemCoordinateX, (int)itemCoordinateY, (int)itemCoordinateZ);

                    newDevice.setMinMaxValues(decimalMin, decimalMax);
                    newDevice.setUnit(decimalUnit, decimalAbbreviation);*/




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

                        /*OutgoingMessage outgoingMessage = new OutgoingMessage();
                        outgoingMessage.integer8(0x0c)
                                    .integer32(thisConnection.getId())
                                    .writeTo(outputStream);*/
                        //new HandlerThread().run();
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
                //TODO: default case here?

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
                Log.d(TAG, "creating a new decimal sensor");
                newDevice = new Device((Container)ConnectionManager.getInstance()
                        .getCentralUnit(thisConnection.getURL())
                        .getItemById(itemParentIdentifier)
                        ,itemIdentifier, Device.Type.SENSOR, Device.ValueType.DECIMAL);

            }
            else {
                Log.d(TAG, "creating a new decimal actuator");
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
                Log.d(TAG, "creating a new binary sensor");
                newDevice = new Device((Container)ConnectionManager.getInstance()
                        .getCentralUnit(thisConnection.getURL())
                        .getItemById(itemParentIdentifier)
                        , itemIdentifier, Device.Type.SENSOR, Device.ValueType.BINARY);
            }
            else {
                Log.d(TAG, "creating a new binary actuator");
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
