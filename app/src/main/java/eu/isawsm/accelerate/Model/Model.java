package eu.isawsm.accelerate.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Oliver on 31.01.2015.
 */
public class Model implements Parcelable {
    public static final Creator<Model> CREATOR = new Creator<Model>() {
        @Override
        public Model createFromParcel(Parcel source) {
            Model retVal = new Model((Manufacturer) source.readValue(null), source.readString(), source.readString(), source.readString(), source.readString(), source.readString());
            return retVal;
        }

        @Override
        public Model[] newArray(int size) {
            return new Model[size];
        }
    };
    private Manufacturer manufacturer;
    private String name;
    private String drivetrain;
    private String motor;
    private String type;
    private String scale;

    public Model(Manufacturer manufacturer, String name, String drivetrain, String motor, String type, String scale) {
        this.manufacturer = manufacturer;
        this.name = name;
        this.drivetrain = drivetrain;
        this.motor = motor;
        this.type = type;
        this.scale = scale;
    }

    private Model(Parcel in) {
        super();
        setManufacturer((Manufacturer) in.readValue(null));
        setName(in.readString());
        setDrivetrain(in.readString());
        setMotor(in.readString());
        setType(in.readString());
        setScale(in.readString());
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDrivetrain() {
        return drivetrain;
    }

    public void setDrivetrain(String drivetrain) {
        this.drivetrain = drivetrain;
    }

    public String getMotor() {
        return motor;
    }

    public void setMotor(String motor) {
        this.motor = motor;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
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
        dest.writeValue(manufacturer);
        dest.writeString(name);
        dest.writeString(drivetrain);
        dest.writeString(motor);
        dest.writeString(type);
        dest.writeString(scale);
    }
}
