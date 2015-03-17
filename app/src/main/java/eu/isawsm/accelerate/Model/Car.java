package eu.isawsm.accelerate.Model;

import android.graphics.Bitmap;
import android.media.Image;

/**
 * Created by olfad on 29.01.2015.
 */
public class Car {
    private long id;
    private Driver driver;
    private Model model;
    private Clazz clazz;
    private long transponderID;
    private Bitmap picture;

    public Car(Model model, Clazz clazz, long transponderID, Bitmap picture) {
        this.model = model;
        this.clazz = clazz;
        this.transponderID = transponderID;
        this.picture = picture;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public Clazz getClazz() {
        return clazz;
    }

    public void setClazz(Clazz clazz) {
        this.clazz = clazz;
    }

    public long getTransponderID() {
        return transponderID;
    }

    public void setTransponderID(long transponderID) {
        this.transponderID = transponderID;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public double getAvgTime() {
        //TODO:
        return 0.0;
    }

    public double getBestTime() {
        //TODO
        return 0;
    }

    public int getLapCount() {
        //TODO
        return 0;
    }

    public String getFullName() {
        String retVal = "";
        retVal += getModel().getManufacturer().getName();
        retVal += " "+getModel().getName();
        return retVal.trim();
    }
}
