package fi.oulu.tol.esde21.ohapclientesde21.ohap_client;

import android.content.Context;
import android.database.DataSetObservable;
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
import fi.oulu.tol.esde21.ohapclientesde21.opimobi_ohap_files.EventSource;
import fi.oulu.tol.esde21.ohapclientesde21.opimobi_ohap_files.Item;

/**
 * Created by Domu on 07-Apr-15.
 *
 * Adapter for the list to tie data into the list, AKA ContainerListAdapter
 */


public class ItemListAdapter implements android.widget.ListAdapter
                                        ,EventSource.Listener<Container,Item>{

    static final String TAG = "OhapListAdapter";
    //this followin prefix string is not actually utilized anywhere, we can perhaps delete it in future but let's leave it for now
    private String prefix;
    private String containerId;
    private DataSetObservable dataSetObservable = new DataSetObservable();



    Container container;


    public ItemListAdapter(Container c){
        this.prefix = prefix;
        this.containerId = containerId;
        container = c;
        Log.d(TAG, "initialized container in itemListAdapter constructor");

        // I have no bloody clue how this works. And if this is what's supposed to be here. *shrug*
        c.itemAddedEventSource.addListener(this);
        c.itemRemovedEventSource.addListener(this);

    }

    @Override
    public void onEvent(Container container, Item item) {
        Log.d(TAG, "onEvent method called");
        Log.d(TAG, "Container id: " + container.getId()
                 + " Item id: " + item.getId());

        dataSetObservable.notifyChanged();


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
        Log.d(TAG, "registering observer...");
        dataSetObservable.registerObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        Log.d(TAG, "unregistering observer...");
        dataSetObservable.unregisterObserver(observer);
    }


    @Override
    public int getCount() {
        return container.getItemCount();
    }


    @Override
    public Object getItem(int position) {
        return container.getItemByIndex(position);
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


        // create the prefix before item's name
        String itemRowText = "";

        Container parentVariable = container;

        if(parentVariable != null) {

            Log.d(TAG, "Starting do-while loop to create item path");
            Log.d(TAG, "Parent name: " + parentVariable.getName());
            do {

                itemRowText = parentVariable.getName() + "/" + itemRowText;
                parentVariable = parentVariable.getParent();

            } while (parentVariable != null);

        }
        else{
            itemRowText = container.getName() + "/";
        }


        //add the item's name after the prefix
        itemRowText += container.getItemByIndex(position).getName();
        viewHolder.rowTextView.setText(itemRowText);

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
