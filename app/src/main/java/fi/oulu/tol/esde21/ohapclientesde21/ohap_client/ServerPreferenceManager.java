package fi.oulu.tol.esde21.ohapclientesde21.ohap_client;

import android.app.Activity;


import java.util.ArrayList;

/**
 * Created by Domu on 17-Jun-15.
 * Singleton class used for storing the specific keys that can be used
 * to access URLs stored in sharedpreferences
 */
public class ServerPreferenceManager extends Activity{

    private static ServerPreferenceManager serverPreferenceManager;
    private ArrayList<String> preferenceKeyList = new ArrayList();

    private ServerPreferenceManager(){
        //for now we will make sure the opimobi test server is on the list of URLs always
        //preferenceKeyList.add("Server 1");
    }


    public static ServerPreferenceManager getInstance(){

        if(serverPreferenceManager == null)
            serverPreferenceManager = new ServerPreferenceManager();

        return serverPreferenceManager;
    }

    public void addKey(String key){

        if(!preferenceKeyList.contains(key)) {
            preferenceKeyList.add(key);

        }

    }



    public ArrayList<String> getPreferenceKeyList(){
        return preferenceKeyList;
    }



}
