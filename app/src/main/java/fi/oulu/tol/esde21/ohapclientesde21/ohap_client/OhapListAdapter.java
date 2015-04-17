package fi.oulu.tol.esde21.ohapclientesde21.ohap_client;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import fi.oulu.tol.esde21.ohapclientesde21.R;
import fi.oulu.tol.esde21.ohapclientesde21.opimobi_ohap_files.Container;
import fi.oulu.tol.esde21.ohapclientesde21.opimobi_ohap_files.Device;
import fi.oulu.tol.esde21.ohapclientesde21.opimobi_ohap_files.Item;

/**
 * Created by Domu on 07-Apr-15.
 *
 * Adapter for the list to tie data into the list
 */


public class OhapListAdapter implements android.widget.ListAdapter {

    private String prefix;
    private String containerId;

    Container container;

    public OhapListAdapter(String prefix, String containerId){
        this.prefix = prefix;
        this.containerId = containerId;

    }


    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    //here we should actually return the amount of items we have in this container
    @Override
    public int getCount() {
        container = (Container)EntryActivity.getCentralUnitItem(Long.parseLong(containerId));
        return container.getItemCount();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }


    // ask the container f
    @Override
    public long getItemId(int position) {
        return container.getItemByIndex(position).getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        ViewHolder viewHolder;

        //if we're generating first row, we need to create the "rubber stamp" first
        if(convertView == null){

            Context context = parent.getContext();
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_row, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.rowTextView = (TextView)convertView.findViewById(R.id.rowTextView);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.rowTextView.setText(prefix + container.getItemByIndex(position).getName());

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    private static class ViewHolder {

        public TextView rowTextView;
    }
}
