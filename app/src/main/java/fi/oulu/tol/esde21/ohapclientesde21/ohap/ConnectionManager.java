package fi.oulu.tol.esde21.ohapclientesde21.ohap;

/**
 * Created by Domu on 06-May-15.
 */

import android.util.Log;

import java.net.URL;
import java.util.HashMap;

import fi.oulu.tol.esde21.ohapclientesde21.opimobi_ohap_files.CentralUnit;

/**
 * Singleton class taking care of the central unit connections
 */
public class ConnectionManager {

    private static ConnectionManager connectionManager;
    private HashMap<URL, CentralUnitConnection> connections = new HashMap<>();


    private String TAG = "ConnectionManager";

    private ConnectionManager(){
    }

    /**
     * get an instance of the ConnectionManager class
     */
    public static ConnectionManager getInstance(){

        if (connectionManager == null){
            connectionManager = new ConnectionManager();
        }

        return connectionManager;
    }

    /**
     * Creates a new central unit with the given URL, user name and password and returns it
     * @param url URL for the central unit
     * @return returns the instance of the central unit
     */
    public CentralUnitConnection getCentralUnit(URL url){

        Log.d(TAG, "getCentralUnit called with url: " + url.toString());
        if (connections.get(url) == null){
            connections.put(url, new CentralUnitConnection(url));
        }
        Log.d(TAG, "central unit connection's name: " + connections.get(url).getName());
        return connections.get(url);
    }

    public DummyCentralUnitConnection getDummyCentralUnit(URL url){

        return new DummyCentralUnitConnection(url);
    }

}
