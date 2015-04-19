package eu.isawsm.accelerate.Model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.net.URI;
import java.util.ArrayList;


/**
 * Created by olfad on 29.01.2015.
 */
public class Driver implements Parcelable, IDriver {
    public static final Creator<IDriver> CREATOR = new Creator<IDriver>() {
        @Override
        public IDriver createFromParcel(Parcel source) {
            IDriver retVal = new Driver(source.readString(), (Bitmap) source.readValue(null), (URI) source.readValue(null));
            retVal.setCars((ArrayList<Car>) source.readValue(null));
            return retVal;
        }

        @Override
        public IDriver[] newArray(int size) {
            return new IDriver[size];
        }
    };
    private String name;
    private Bitmap image;
    private URI mail;
    private ArrayList<Car> cars;

    public Driver(String name, Bitmap image, URI mail) {
        this.name = name;
        this.image = image;
        this.mail = mail;
        cars = new ArrayList<>();
    }

    public Driver() {
        cars = new ArrayList<>();
    }

    private Driver(Parcel in) {
        super();
        setName(in.readString());
        setImage((Bitmap) in.readValue(null));
        setMail((URI) in.readValue(null));
        setCars((ArrayList<Car>) in.readValue(null));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String firstname) {
        this.name = firstname;
    }

    @Override
    public Bitmap getImage() {
        return image;
    }

    @Override
    public void setImage(Bitmap image) {
        this.image = image;
    }

    @Override
    public URI getMail() {
        return mail;
    }

    @Override
    public void setMail(URI mail) {
        this.mail = mail;
    }

    @Override
    public void addCar(Car car) {
        cars.add(car);
    }

    @Override
    public void removeCar(Car car) {
        cars.remove(car);
    }

    @Override
    public ArrayList<Car> getCars() {
        return cars;
    }

    @Override
    public void setCars(ArrayList<Car> cars) {
        this.cars = cars;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getName());
        dest.writeValue(getImage());
        dest.writeValue(getMail());
        dest.writeList(getCars());
    }
}
