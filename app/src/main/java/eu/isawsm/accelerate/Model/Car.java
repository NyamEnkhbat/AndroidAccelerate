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
public class Car implements ICar {

    private static final int MINLAPS = 10;
    private Model IModel;
    private Clazz IClazz;
    private long transponderID;
    private Bitmap picture;
    private List<Lap> ILaps;

    public Car(IModel IModel, IClazz IClazz, long transponderID, Bitmap picture) {
        this.IModel = (Model) IModel;
        this.IClazz = (Clazz) IClazz;
        this.transponderID = transponderID;
        this.picture = picture;
        ILaps = new ArrayList<>();
    }

    private Car(Parcel in) {
        super();
        setModel((IModel) in.readParcelable(null));
        setClazz((IClazz) in.readParcelable(null));
        setTransponderID(in.readLong());
        setPicture((Bitmap) in.readValue(null));
        ILaps = new ArrayList<>();
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

    @Override
    public IModel getModel() {
        return IModel;
    }

    @Override
    public void setModel(IModel IModel) {
        this.IModel = (Model) IModel;
    }

    @Override
    public IClazz getClazz() {
        return IClazz;
    }

    @Override
    public void setClazz(IClazz IClazz) {
        this.IClazz = (Clazz) IClazz;
    }

    @Override
    public long getTransponderID() {
        return transponderID;
    }

    @Override
    public void setTransponderID(long transponderID) {
        this.transponderID = transponderID;
    }

    @Override
    public Bitmap getPicture() {
        return picture;
    }

    @Override
    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    @Override
    public double getAvgTime(ICourse course) {
        if (ILaps.isEmpty()) return 0.0;

        long sum = 0;
        for (ILap l : ILaps) {
            if(l.getCourse().equals(course))
              sum += l.getTime();
        }
        return sum / ILaps.size();
    }

    @Override
    public double getBestTime(ICourse course) {
        if (ILaps.isEmpty()) return 0;
        long bestTime = ILaps.get(0).getTime();

        for (ILap l : ILaps) {
            if(l.getCourse().equals(course))
                if (l.getTime() < bestTime) {

                    bestTime = l.getTime();
                }
        }
        return bestTime;
    }

    @Override
    public int getLapCount(ICourse course) {

        int retVal = 0;

        for(ILap l: ILaps){
            if(l.getCourse().equals(course))
                retVal++;
        }
        return retVal;
    }

    @Override
    public double getConsistency(ICourse course) {
        if (getLapCount(course) < MINLAPS) return -1;
        double[] times = new double[ILaps.size()];
        int i = 0;
        for (ILap l : ILaps) {
            if(l.getCourse().equals(course)){
                times[i] = l.getTime();
                i++;
            }
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

    @Override
    public int getRank() {
        return 0;
    }

    @Override
    public List<Lap> getLaps() {
        return ILaps;
    }

    @Override
    public void addLap(ILap ILap) {
        ILaps.add((Lap) ILap);
    }

    @Override
    public String getName() {
        String retVal = "";
        retVal += getModel().getManufacturer().getName();
        retVal += " "+getModel().getName();
        return retVal.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Car car = (Car) o;

        if (transponderID != car.transponderID) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (transponderID ^ (transponderID >>> 32));
    }
}
