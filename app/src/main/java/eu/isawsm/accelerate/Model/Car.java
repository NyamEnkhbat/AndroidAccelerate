package eu.isawsm.accelerate.Model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by olfad on 29.01.2015.
 */
public class Car implements Parcelable {
    public static final Creator<Car> CREATOR = new Creator<Car>() {
        @Override
        public Car createFromParcel(Parcel source) {
            Car retVal = new Car((Model) source.readParcelable(null), (Clazz)
                    source.readParcelable(null), source.readLong(), (Bitmap) source.readValue(null));

            return retVal;
        }

        @Override
        public Car[] newArray(int size) {
            return new Car[size];
        }
    };
    private static final int MINLAPS = 10;
    private Model model;
    private Clazz clazz;
    private long transponderID;
    private Bitmap picture;
    private List<Lap> laps;

    public Car(Model model, Clazz clazz, long transponderID, Bitmap picture) {
        this.model = model;
        this.clazz = clazz;
        this.transponderID = transponderID;
        this.picture = picture;
        laps = new ArrayList<>();
    }

    private Car(Parcel in) {
        super();
        setModel((Model) in.readParcelable(null));
        setClazz((Clazz) in.readParcelable(null));
        setTransponderID(in.readLong());
        setPicture((Bitmap) in.readValue(null));
        laps = new ArrayList<>();
    }

    private static double median(double[] m) {
        Arrays.sort(m);
        int middle = m.length / 2;
        if (m.length % 2 == 1) {
            return m[middle];
        } else {
            return (m[middle - 1] + m[middle]) / 2.0;
        }
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
        if (laps.isEmpty()) return 0.0;

        long sum = 0;
        for (Lap l : laps) {
            sum += l.getTime();
        }
        return sum / laps.size();
    }

    public double getBestTime() {
        if (laps.isEmpty()) return 0;
        long bestTime = laps.get(0).getTime();

        for (Lap l : laps) {
            if (l.getTime() < bestTime) {
                bestTime = l.getTime();
            }
        }
        return bestTime;
    }

    public int getLapCount() {
        return laps.size();
    }

    public double getConsistency() {
        if (laps.size() < MINLAPS) return -1;
        double[] times = new double[laps.size()];
        int i = 0;
        for (Lap l : laps) {
            times[i] = l.getTime();
            i++;
        }
        //Median for all Times
        double median1 = median(times);

        //Each times Variance to Median 1
        double[] variance = new double[times.length];

        for (i = 0; i < times.length; i++) {
            variance[i] = Math.abs(times[i] - median1);
        }

        //Median of Variance
        double median2 = median(variance);

        //Variance in Percent
        double varPercent = 1 - (median2 / median1);


        double consistency = (((varPercent - 0.4) / 60) * 100) * 100;

        BigDecimal bd = new BigDecimal(consistency).setScale(2, RoundingMode.HALF_EVEN);

        return bd.doubleValue();
    }

    public int getRank() {
        return 0;
    }

    public List<Lap> getLaps() {
        return laps;
    }

    public void addLap(Lap lap) {
        laps.add(lap);
    }

    public String getName() {
        String retVal = "";
        retVal += getModel().getManufacturer().getName();
        retVal += " "+getModel().getName();
        return retVal.trim();
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
        dest.writeValue(getModel());
        dest.writeValue(getClazz());
        dest.writeLong(transponderID);
        dest.writeValue(picture);
    }
}
