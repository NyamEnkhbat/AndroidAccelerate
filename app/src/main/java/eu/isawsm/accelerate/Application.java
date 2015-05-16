package eu.isawsm.accelerate;

import org.acra.*;
import org.acra.annotation.*;

        @ReportsCrashes( // will not be used
                mailTo = "ofaderbauer@gmail.com",
                mode = ReportingInteractionMode.TOAST,
                resToastText = R.string.crash_toast_text)

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // The following line triggers the initialization of ACRA
        System.out.println("Starting ACRA");
        ACRA.init(this);
    }
}