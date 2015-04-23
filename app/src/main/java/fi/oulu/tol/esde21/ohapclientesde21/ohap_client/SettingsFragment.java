package fi.oulu.tol.esde21.ohapclientesde21.ohap_client;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fi.oulu.tol.esde21.ohapclientesde21.R;

public class SettingsFragment extends PreferenceFragment {

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = getActivity();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        addPreferencesFromResource(R.xml.preferences);
    }
}