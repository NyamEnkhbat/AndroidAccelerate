package eu.isawsm.accelerate.Model;

/**
 * Created by olfad on 29.01.2015.
 */
public class Track implements ITrack {
    private String type;
    private ICourse ICourse;

    public Track(String type, ICourse ICourse) {
        this.type = type;
        this.ICourse = ICourse;
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
    public ICourse getCourse() {
        return ICourse;
    }

    @Override
    public void setCourse(ICourse ICourse) {
        this.ICourse = ICourse;
    }
}
