package eu.isawsm.accelerate.ax.viewholders;

import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.Toast;

import eu.isawsm.accelerate.ax.AxAdapter;
import eu.isawsm.accelerate.ax.AxCardItem;
import eu.isawsm.accelerate.ax.MainActivity;


/**
 * Created by ofade_000 on 21.02.2015.
 */
public abstract class AxViewHolder extends AxAdapter.ViewHolder {

    public static AxAdapter axAdapter;
    public static MainActivity context;


    private SwipeRefreshLayout swipeLayout;

    public AxViewHolder(View v, AxAdapter axAdapter, MainActivity context) {
        super(v);
        if(AxViewHolder.axAdapter == null) AxViewHolder.axAdapter = axAdapter;
        if(AxViewHolder.context == null) AxViewHolder.context = context;
    }


    public static void showToast(final String s) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, s, Toast.LENGTH_LONG).show();
            }
        });
    }

    public abstract void onBindViewHolder(AxAdapter.ViewHolder holder, int position, AxCardItem axCardItem);

    /**
     * Override this to get the RefreshEvent.
     */
    public void refresh(){

    }
}
