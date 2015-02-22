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

    public Car toCar() {
        return (Car) t;
    }

    public Club toClub(){
        return (Club) t;
    }

    public AxCardItem(T t){
        this.t = t;
    }
}
