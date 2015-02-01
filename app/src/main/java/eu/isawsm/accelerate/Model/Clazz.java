package eu.isawsm.accelerate.Model;


/**
 * Created by Oliver on 31.01.2015.
 */
public class Clazz {
    private String name;
    private String description;

    public Clazz(String name, String description) {
        this.name = name;
        this.description = description;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
