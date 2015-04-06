package eu.isawsm.accelerate.Model;

/**
 * Created by Oliver on 31.01.2015.
 */
public class Course implements ICourse {
    private String condition;
    private String name;
    private boolean inReverse;
    private long minTime;
    private long maxTime;

    public Course(String condition, String name, boolean inReverse, long minTime, long maxTime) {
        this.condition = condition;
        this.name = name;
        this.inReverse = inReverse;
        this.minTime = minTime;
        this.maxTime = maxTime;
    }

    @Override
    public String getCondition() {
        return condition;
    }

    @Override
    public void setCondition(String condition) {
        this.condition = condition;
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
    public boolean isInReverse() {
        return inReverse;
    }

    @Override
    public void setInReverse(boolean inReverse) {
        this.inReverse = inReverse;
    }

    @Override
    public long getMinTime() {
        return minTime;
    }

    @Override
    public void setMinTime(long minTime) {
        this.minTime = minTime;
    }

    @Override
    public long getMaxTime() {
        return maxTime;
    }

    @Override
    public void setMaxTime(long maxTime) {
        this.maxTime = maxTime;
    }
}
