package eu.isawsm.accelerate.Model;

/**
 * Created by olfad on 29.01.2015.
 */
public class Lap {
    private long time;
    private Course course;

    public Lap(long time, Course course) {
        this.time = time;
        this.course = course;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
