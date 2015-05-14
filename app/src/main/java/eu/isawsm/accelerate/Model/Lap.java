package eu.isawsm.accelerate.Model;

/**
 * Created by olfad on 29.01.2015.
 */
public class Lap implements ILap {
    private long time;
    private Course Course;

    public Lap(long time, Course Course) {
        this.time = time;
        this.Course = Course;
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
        return Course;
    }

    @Override
    public void setCourse(Course Course) {
        this.Course = Course;
    }
}
