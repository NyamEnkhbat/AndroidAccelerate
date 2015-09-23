package eu.isawsm.accelerate.Model;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;

import Shared.Car;
import Shared.Driver;

/**
 * Created by ofade_000 on 29.03.2015.
 */
public class AxUser extends Driver implements Parcelable {
    public static final Creator<AxUser> CREATOR = new Creator<AxUser>() {
        @Override
        public AxUser createFromParcel(Parcel source) {
            AxUser retVal = new AxUser(source.readString(), (Bitmap) source.readValue(null), (URI) source.readValue(null));
            retVal.setCars(new HashSet<>((ArrayList<Car>) source.readValue(null)));
            return retVal;
        }

        @Override
        public AxUser[] newArray(int size) {
            return new AxUser[size];
        }
    };


    public AxUser(String name, Bitmap image, URI mail) {
        super(name, image, mail);
    }

    public AxUser(Parcel in) {
        super();
        setName(in.readString());
        setImage((Bitmap) in.readValue(null));
        setMail((URI) in.readValue(null));
        setCars(new HashSet<Car>((ArrayList<Car>) in.readValue(null)));
    }

    public AxUser() {
        super();
    }

    public AxUser getCopy() {
        AxUser retVal = new AxUser(getName(), getImage(), getMail());
        retVal.setCars(new HashSet<>(getCars()));
        return retVal;
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
        dest.writeList(new ArrayList<>(getCars()));
    }
}
