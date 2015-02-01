package eu.isawsm.accelerate.Model;

/**
 * Created by olfad on 29.01.2015.
 */
public class Track {
    private String type;
    private Club club;

    public Track(String type, Club club) {
        this.type = type;
        this.club = club;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }
}
