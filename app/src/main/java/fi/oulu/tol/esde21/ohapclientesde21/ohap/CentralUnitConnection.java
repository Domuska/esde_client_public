package fi.oulu.tol.esde21.ohapclientesde21.ohap;

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


    private int nListeners = 0;


    public CentralUnitConnection(URL url){
        super(url);
        populateCU();
    }

    @Override
    protected void listeningStateChanged(Container container, boolean listening) {

        // if we wanted to start to listen, increase amount of networkListeners,
        // start networking procedures
        if (listening == true){

            if (nListeners == 0) {
                startNetworking();

            }
            sendLIsteningStart(container);
            nListeners ++;
        }
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
        //do something...
    }

    @Override
    protected void changeDecimalValue(Device device, double value) {
        //do something...
    }

    private void startNetworking(){

    }

    private void stopNetworking(){

    }

    private void sendLIsteningStart(Container container){

    }

    private void sendListeningStop(Container container){

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
}
