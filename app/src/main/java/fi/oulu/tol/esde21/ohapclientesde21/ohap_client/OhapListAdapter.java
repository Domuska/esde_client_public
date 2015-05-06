package fi.oulu.tol.esde21.ohapclientesde21.ohap_client;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
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

    static final String TAG = "OhapListAdapter";
    //this followin prefix string is not actually utilized anywhere, we can perhaps delete it in future but let's leave it for now
    private String prefix;
    private String containerId;
    //this is t he real prefix used in the program now.
    String itemPrefix;


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


    @Override
    public int getCount() {
        container = (Container)EntryActivity.getCentralUnitItem(Long.parseLong(containerId));
        return container.getItemCount();
    }

    //TODO: does this even work correctly?
    @Override
    public Object getItem(int position) {
        return EntryActivity.getCentralUnitItem(position);
    }


    // ask the container for the id of the item of this index
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

        //old way of building the item's path
        //viewHolder.rowTextView.setText(prefix + container.getItemByIndex(position).getName());


        // build the prefix for the item's "path"
        //Container parentVariable = EntryActivity.getCentralUnitItem(getItemId(position)).getParent();
        Container parentVariable = container.getParent();
        itemPrefix = "";

        if(parentVariable != null) {

            Log.d(TAG, "Starting do-while loop");
            Log.d(TAG, "Parent name: " + parentVariable.getName());
            do {

                itemPrefix = parentVariable.getName() + "/" + itemPrefix;
                parentVariable = parentVariable.getParent();

            } while (parentVariable != null);

        }
        else{
            itemPrefix = container.getName() + "/";
        }

        viewHolder.rowTextView.setText(itemPrefix + container.getItemByIndex(position).getName());
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
