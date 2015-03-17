package eu.isawsm.accelerate.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import eu.isawsm.accelerate.ax.Util.AxPreferences;


/**
 * Created by olfad on 29.01.2015.
 */
public class Driver {
    private long id;
    private String firstname;
    private String lastname;
    private String acronym;
    private Bitmap image;
    private URI mail;
    private String password;
    private String Salt;
    private ArrayList<Car> cars;

    public Driver(String firstname, String lastname, String acronym, Bitmap image, URI mail, Context context) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.acronym = acronym;
        this.image = image;
        this.mail = mail;
        cars = new ArrayList<>();
        AxPreferences.setDriver(context, this);
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname, Context context) {
        this.firstname = firstname;
        AxPreferences.setDriver(context, this);
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname, Context context) {
        this.lastname = lastname;
        AxPreferences.setDriver(context, this);
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym, Context context) {
        this.acronym = acronym;
        AxPreferences.setDriver(context, this);
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image, Context context) {
        this.image = image;
        AxPreferences.setDriver(context, this);
    }

    public URI getMail() {
        return mail;
    }

    public void setMail(URI mail, Context context) {
        this.mail = mail;
        AxPreferences.setDriver(context, this);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password, Context context) {
        this.password = password;
        AxPreferences.setDriver(context, this);
    }

    public String getSalt() {
        return Salt;
    }

    public void setSalt(String salt, Context context) {
        Salt = salt;
        AxPreferences.setDriver(context, this);
    }

    public void addCar(Car car, Context context){
        cars.add(car);
        AxPreferences.setDriver(context, this);
    }

    public void setCars(ArrayList<Car> cars, Context context){
        this.cars = cars;
        AxPreferences.setDriver(context, this);
    }

    public void removeCar(Car car, Context context){
        cars.remove(car);
        AxPreferences.setDriver(context, this);
    }

    public static Driver get(Context context){
        return AxPreferences.getDriver(context);
    }

    public ArrayList<Car> getCars() {
        return cars;
    }
}
