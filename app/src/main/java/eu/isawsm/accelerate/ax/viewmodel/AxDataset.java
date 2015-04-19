package eu.isawsm.accelerate.ax.viewmodel;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

import java.util.LinkedHashSet;


import eu.isawsm.accelerate.ax.AxAdapter;

/**
 * Created by olfad on 24.02.2015.
 */
public class AxDataset<T> {

    private AxAdapter adapter;
    private LinkedHashSet<T> linkedHashSet;


    public AxDataset(AxAdapter adapter){
        linkedHashSet = new LinkedHashSet<>();
        this.adapter = adapter;
    }

    public boolean add(T t) {
        if(linkedHashSet.add(t)) {
            adapter.notifyItemInserted(0);
            return true;
        } else {
            Log.d(this.getClass().getName(), t.toString() + " is already in DataSet => skipping");
            return false;
        }
    }

    public int size(){
        return new ArrayList<>(linkedHashSet).size();
    }

    public void clear() {
        linkedHashSet.clear();
        adapter.notifyItemRangeRemoved(0, size());
    }

    public T remove(int index) {
        int reverseIndex = Math.abs(index -(size()-1));
        ArrayList<T> tmp = new ArrayList<>(linkedHashSet);
        T t = tmp.remove(reverseIndex);
        linkedHashSet = new LinkedHashSet<>(tmp);
        adapter.notifyItemRemoved(reverseIndex);
        return t;
    }

    public T get(int index) {
        int reverseIndex = Math.abs(index -(size()-1));
        return new ArrayList<>(linkedHashSet).get(reverseIndex);
    }
}
