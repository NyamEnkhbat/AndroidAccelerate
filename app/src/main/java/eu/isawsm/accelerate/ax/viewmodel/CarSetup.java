package eu.isawsm.accelerate.ax.viewmodel;

/**
 * Created by ofade_000 on 21.02.2015.
 */
public class CarSetup {
    @Override
    public boolean equals(Object o) {
        return o instanceof CarSetup;
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }
}
