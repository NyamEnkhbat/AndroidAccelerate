package eu.isawsm.accelerate.Model;

/**
 * Created by Oliver on 31.01.2015.
 */
public class Course {
    private Track track;
    private String condition;
    private String name;
    private boolean inReverse;
    private long minTime;
    private long maxTime;

    public Course(Track track, String condition, String name, boolean inReverse, long minTime, long maxTime) {
        this.track = track;
        this.condition = condition;
        this.name = name;
        this.inReverse = inReverse;
        this.minTime = minTime;
        this.maxTime = maxTime;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isInReverse() {
        return inReverse;
    }

    public void setInReverse(boolean inReverse) {
        this.inReverse = inReverse;
    }

    public long getMinTime() {
        return minTime;
    }

    public void setMinTime(long minTime) {
        this.minTime = minTime;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(long maxTime) {
        this.maxTime = maxTime;
    }
}
