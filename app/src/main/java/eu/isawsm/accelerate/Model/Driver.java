package eu.isawsm.accelerate.Model;

import java.util.List;

/**
 * Created by olfad on 29.01.2015.
 */
public class Driver {
    private List<Car> cars;
    private String name;

    public Driver(List<Car> cars, String name) {
        this.cars = cars;
        this.name = name;
    }

    public List<Car> getCars() {
        return cars;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
