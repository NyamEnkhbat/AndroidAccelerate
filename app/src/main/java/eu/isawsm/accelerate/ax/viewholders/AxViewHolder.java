package eu.isawsm.accelerate.ax.viewholders;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URI;
import java.net.URISyntaxException;

import eu.isawsm.accelerate.Model.Club;
import eu.isawsm.accelerate.R;
import eu.isawsm.accelerate.ax.AxAdapter;
import eu.isawsm.accelerate.ax.AxCardItem;
import eu.isawsm.accelerate.ax.Util.AxPreferences;
import eu.isawsm.accelerate.ax.Util.AxSocket;
import eu.isawsm.accelerate.ax.viewmodel.ConnectionSetup;


/**
 * Created by ofade_000 on 21.02.2015.
 */
public abstract class AxViewHolder extends AxAdapter.ViewHolder{

    public static AxAdapter axAdapter;
    public static Activity context;
    public static Socket socket;




    public AxViewHolder(View v, AxAdapter axAdapter, Activity context) {
        super(v);
        if(AxViewHolder.axAdapter == null) AxViewHolder.axAdapter = axAdapter;
        if(AxViewHolder.context == null) AxViewHolder.context = context;

        //TODO: figure out how to clean up Socket:
//        socket.disconnect();
//        socket.off();
    }
    public static void showToast(final String s) {

        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, s, Toast.LENGTH_LONG).show();
            }
        });
    }


    public abstract void onBindViewHolder(AxAdapter.ViewHolder holder, int position);
}
