package eu.isawsm.accelerate.Model;

/**
 * Created by olfad on 29.01.2015.
 */
public class Lap {
    private Car car;
    private long time;
    private Course course;

    public Lap(Car car, long time, Course course) {
        this.car = car;
        this.time = time;
        this.course = course;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
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
