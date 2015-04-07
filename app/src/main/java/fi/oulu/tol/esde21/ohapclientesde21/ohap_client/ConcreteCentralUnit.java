package fi.oulu.tol.esde21.ohapclientesde21.ohap_client;

import java.net.URL;

import fi.oulu.tol.esde21.ohapclientesde21.opimobi_ohap_files.CentralUnit;
import fi.oulu.tol.esde21.ohapclientesde21.opimobi_ohap_files.Container;
import fi.oulu.tol.esde21.ohapclientesde21.opimobi_ohap_files.Device;

/**
 * Created by Domu on 05-Apr-15.
 *
 * Concrete implementation of the abstract CentralUnit class
 */


public class ConcreteCentralUnit extends CentralUnit{

    public ConcreteCentralUnit (URL url){
        super(url);

    }

    @Override
    protected void listeningStateChanged(Container container, boolean listening) {
        //do something...
    }

    @Override
    protected void changeBinaryValue(Device device, boolean value) {
        //do something...
    }

    @Override
    protected void changeDecimalValue(Device device, double value) {
        //do something...
    }
}
