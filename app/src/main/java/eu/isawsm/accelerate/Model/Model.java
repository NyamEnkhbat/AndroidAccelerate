package eu.isawsm.accelerate.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Oliver on 31.01.2015.
 */
public class Model implements Parcelable, IModel {
    public static final Creator<IModel> CREATOR = new Creator<IModel>() {
        @Override
        public IModel createFromParcel(Parcel source) {
            IModel retVal = new Model((IManufacturer) source.readValue(null), source.readString(), source.readString(), source.readString(), source.readString(), source.readString());
            return retVal;
        }

        @Override
        public IModel[] newArray(int size) {
            return new IModel[size];
        }
    };
    private Manufacturer IManufacturer;
    private String name;
    private String drivetrain;
    private String motor;
    private String type;
    private String scale;

    public Model(IManufacturer IManufacturer, String name, String drivetrain, String motor, String type, String scale) {
        this.IManufacturer = (Manufacturer) IManufacturer;
        this.name = name;
        this.drivetrain = drivetrain;
        this.motor = motor;
        this.type = type;
        this.scale = scale;
    }

    private Model(Parcel in) {
        super();
        setManufacturer((IManufacturer) in.readValue(null));
        setName(in.readString());
        setDrivetrain(in.readString());
        setMotor(in.readString());
        setType(in.readString());
        setScale(in.readString());
    }

    @Override
    public IManufacturer getManufacturer() {
        return IManufacturer;
    }

    @Override
    public void setManufacturer(IManufacturer IManufacturer) {
        this.IManufacturer = (Manufacturer) IManufacturer;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDrivetrain() {
        return drivetrain;
    }

    @Override
    public void setDrivetrain(String drivetrain) {
        this.drivetrain = drivetrain;
    }

    @Override
    public String getMotor() {
        return motor;
    }

    @Override
    public void setMotor(String motor) {
        this.motor = motor;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getScale() {
        return scale;
    }

    @Override
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
        dest.writeValue(IManufacturer);
        dest.writeString(name);
        dest.writeString(drivetrain);
        dest.writeString(motor);
        dest.writeString(type);
        dest.writeString(scale);
    }
}
