package fi.oulu.tol.esde21.ohapclientesde21.ohap;

import java.net.URL;

import fi.oulu.tol.esde21.ohapclientesde21.opimobi_ohap_files.CentralUnit;
import fi.oulu.tol.esde21.ohapclientesde21.opimobi_ohap_files.Container;
import fi.oulu.tol.esde21.ohapclientesde21.opimobi_ohap_files.Device;

/**
 * Created by Domu on 17-Jun-15.
 *
 * Just a dummy central unit connection, used to showcase the UI for supporting multiple central units
 */
public class DummyCentralUnitConnection extends CentralUnit{


    public DummyCentralUnitConnection(URL url){
        super(url);
        addDummyData();
        this.setName("dummy server");

    }

    @Override
    protected void changeBinaryValue(Device device, boolean value) {

    }

    @Override
    protected void changeDecimalValue(Device device, double value) {

    }

    @Override
    protected void listeningStateChanged(Container container, boolean listening) {

    }


    private void addDummyData(){

        Container container1 = new Container(this, 1);
        container1.setName("Small room");

        Device device1 = new Device(this, 2, Device.Type.ACTUATOR, Device.ValueType.DECIMAL);
        device1.setName("A lamp");
        device1.setDescription("it's in the ceiling. It should glow.");
        device1.setUnit("lumen", "l");
        device1.setMinMaxValues(2, 500);
        device1.setDecimalValue(100);

        Device device2 = new Device((Container) this.getItemById(1),
                                    3, Device.Type.SENSOR, Device.ValueType.BINARY);
        device2.setName("Unordinary button");
        device2.setDescription("A sensor for sensing what a weird button does");
        device2.setBinaryValue(true);

        Device device3 = new Device((Container) this.getItemById(1),
                                    4, Device.Type.ACTUATOR, Device.ValueType.BINARY);
        device3.setName("a weird knob");


    }
}
