package eu.isawsm.accelerate.ax.viewmodel;

/**
 * Created by ofade_000 on 20.02.2015.
 */
public class ConnectionSetup {
    @Override
    public boolean equals(Object o) {
        return o instanceof ConnectionSetup;
    }


    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }
}
