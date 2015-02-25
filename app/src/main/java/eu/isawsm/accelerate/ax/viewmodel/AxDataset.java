package eu.isawsm.accelerate.ax.viewmodel;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import eu.isawsm.accelerate.ax.AxAdapter;

/**
 * Created by olfad on 24.02.2015.
 */
public class AxDataset<T> extends ArrayList<T> {

    private AxAdapter adapter;

    public AxDataset(AxAdapter adapter){
        super();
        this.adapter = adapter;
    }

    @Override
    public void add(int index, T t) {
        super.add(index, t);
        adapter.notifyItemInserted(index);
        adapter.notifyItemRangeChanged(index, size());
    }

    @Override
    public boolean add(T t) {
        super.add(t);
        adapter.notifyItemInserted(size()-1);
        return true;
    }

    @Override
    public void clear() {
        super.clear();
        adapter.notifyItemRangeRemoved(0, size());
        adapter.notifyItemRangeChanged(0, size());
    }

    @Override
    public T set(int index, T t) {
        T prevItem = super.set(index, t);
            adapter.notifyItemChanged(index);
        return prevItem;
    }

    @Override
    public boolean remove(Object object) {
        boolean listModified = super.remove(object);
        adapter.notifyItemRemoved(indexOf(object));
        adapter.notifyDataSetChanged();
        return listModified;
    }

    @Override
    public T remove(int index) {
        T removedObject = super.remove(index);
        adapter.notifyItemRemoved(index);
        return removedObject;
    }
}
