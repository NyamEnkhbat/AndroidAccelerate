package eu.isawsm.accelerate.Model;

/**
 * Created by ofade_000 on 04.04.2015.
 */
public interface ICourse {
    String getCondition();

    void setCondition(String condition);

    String getName();

    void setName(String name);

    boolean isInReverse();

    void setInReverse(boolean inReverse);

    long getMinTime();

    void setMinTime(long minTime);

    long getMaxTime();

    void setMaxTime(long maxTime);
}
