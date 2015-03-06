package eu.isawsm.accelerate.ax;

import eu.isawsm.accelerate.Model.Car;
import eu.isawsm.accelerate.Model.Club;

/**
 * Created by ofade_000 on 20.02.2015.
 */
public class AxCardItem<T> {

    private T t;

    public T get() {
        return t;
    }

    public void set(T object){
        this.t = object;
    }

    public AxCardItem(T t){
        this.t = t;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof  AxCardItem){
            AxCardItem<T> cardItem = (AxCardItem<T>) o;
            return this.get().equals(cardItem.get());
        }
        else
            return super.equals(o);
    }

    @Override
    public int hashCode() {
        return t.hashCode();
    }
}
