package eu.isawsm.accelerate.ax;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.LinePoint;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.BreakIterator;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import eu.isawsm.accelerate.Model.Car;
import eu.isawsm.accelerate.Model.Club;
import eu.isawsm.accelerate.R;
import eu.isawsm.accelerate.ax.Util.RemoteFetch;
import eu.isawsm.accelerate.ax.viewholders.AxViewHolder;
import eu.isawsm.accelerate.ax.viewholders.CarSettingsViewHolder;
import eu.isawsm.accelerate.ax.viewholders.CarViewHolder;
import eu.isawsm.accelerate.ax.viewholders.ClubViewHolder;
import eu.isawsm.accelerate.ax.viewholders.ConnectionViewHolder;
import eu.isawsm.accelerate.ax.viewmodel.CarSetup;
import eu.isawsm.accelerate.ax.viewmodel.ConnectionSetup;

public class AxAdapter extends RecyclerView.Adapter<AxViewHolder> {
    private ArrayList<AxCardItem> mDataset;

    private Activity context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View mView;
        public ViewHolder(View v) {
            super(v);
            mView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AxAdapter(ArrayList<AxCardItem> myDataset, Activity context) {
        mDataset = myDataset;
        this.context = context;


    }

    @Override
    public int getItemViewType(int position){
        if(mDataset.get(position).get() instanceof Car)
            return R.layout.ax_car_cardview;
        else if (mDataset.get(position).get()instanceof ConnectionSetup)
            return R.layout.ax_connection_cardview;
        else if(mDataset.get(position).get () instanceof Club) {
            return R.layout.ax_club_cardview;
        } else if(mDataset.get(position).get() instanceof CarSetup) {
            return R.layout.ax_car_settings_cardview;
        } else
            return -1;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AxViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        if(viewType == R.layout.ax_car_cardview) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ax_car_cardview, parent, false);
            // set the view's size, margins, paddings and layout parameters

            return new CarViewHolder(v, this, context);
        } else if(viewType == R.layout.ax_connection_cardview) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ax_connection_cardview, parent, false);
            // set the view's size, margins, paddings and layout parameters

            return new ConnectionViewHolder(v, this, context);

        } else if(viewType == R.layout.ax_club_cardview) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ax_club_cardview, parent, false);

            return new ClubViewHolder(v, this, context);

        } else if(viewType == R.layout.ax_car_settings_cardview){
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ax_car_settings_cardview, parent, false);

            return new CarSettingsViewHolder(v, this, context);
        } else {
            throw new RuntimeException("Cardview not supported");
        }

    }

    @Override
    public void onBindViewHolder(AxViewHolder holder, int position) {
        holder.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public ArrayList<AxCardItem> getDataset(){
        return mDataset;
    }

    public void removeCard(final int position) {
        if(position == -1) return; //Card is not already removed
        mDataset.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mDataset.size());
    }
}