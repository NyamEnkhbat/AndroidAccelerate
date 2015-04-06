package eu.isawsm.accelerate.Model;

/**
 * Created by ofade_000 on 04.04.2015.
 */
public interface IModel {
    IManufacturer getManufacturer();

    void setManufacturer(IManufacturer IManufacturer);

    String getName();

    void setName(String name);

    String getDrivetrain();

    void setDrivetrain(String drivetrain);

    String getMotor();

    void setMotor(String motor);

    String getType();

    void setType(String type);

    String getScale();

    void setScale(String scale);
}
