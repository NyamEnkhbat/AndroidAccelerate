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
public class AxDataset<T> implements Parcelable{

    private AxAdapter adapter;
    private LinkedHashSet<T> linkedHashSet;


    public AxDataset(AxAdapter adapter){
        linkedHashSet = new LinkedHashSet<>();
        this.adapter = adapter;
    }

    public boolean add(T t) {
        if(linkedHashSet.add(t)) {
            adapter.notifyItemInserted(size());
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(adapter,flags);
        dest.writeList(new ArrayList<>(linkedHashSet));
    }
    public static final Creator<AxDataset> CREATOR = new Creator<AxDataset>() {
        @Override
        public AxDataset createFromParcel(Parcel source) {
            AxDataset retVal =new AxDataset(source.<AxAdapter>readParcelable(null));
            retVal.linkedHashSet = (LinkedHashSet) source.readValue(null);
            return retVal;
        }

        @Override
        public AxDataset[] newArray(int size) {
            return new AxDataset[size];
        }
    };

    private AxDataset(Parcel in) {
        super();
        adapter = in.readParcelable(null);
        linkedHashSet = (LinkedHashSet<T>) in.readValue(null);
    }
}
