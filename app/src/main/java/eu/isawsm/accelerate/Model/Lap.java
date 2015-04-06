package eu.isawsm.accelerate.Model;

/**
 * Created by olfad on 29.01.2015.
 */
public class Lap implements ILap {
    private long time;
    private ICourse ICourse;

    public Lap(long time, ICourse ICourse) {
        this.time = time;
        this.ICourse = ICourse;
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public ICourse getCourse() {
        return ICourse;
    }

    @Override
    public void setCourse(ICourse ICourse) {
        this.ICourse = ICourse;
    }
}
