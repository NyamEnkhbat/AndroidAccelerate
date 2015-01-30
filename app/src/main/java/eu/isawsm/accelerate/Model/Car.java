package eu.isawsm.accelerate.Model;

import android.media.Image;

/**
 * Created by olfad on 29.01.2015.
 */
public class Car {
    private String manufacturer;
    private String model;
    private String clazz;
    private Image picture;

    public Car(String manufacturer, String model, String clazz, Image picture) {
        this.manufacturer = manufacturer;
        this.model = model;
        this.clazz = clazz;
        this.picture = picture;

    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public Image getPicture() {
        return picture;
    }

    public void setPicture(Image picture) {
        this.picture = picture;
    }
}
