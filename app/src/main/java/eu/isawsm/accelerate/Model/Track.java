package eu.isawsm.accelerate.Model;

/**
 * Created by olfad on 29.01.2015.
 */
public class Track {
    private String name;
    private String type;
    private String course;
    private String club;

    public Track(String name, String type, String course, String club) {
        this.name = name;
        this.type = type;
        this.course = course;
        this.club = club;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getClub() {
        return club;
    }

    public void setClub(String club) {
        this.club = club;
    }
}
