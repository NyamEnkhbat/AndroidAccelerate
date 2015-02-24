package eu.isawsm.accelerate.ax;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;

import eu.isawsm.accelerate.Model.Car;
import eu.isawsm.accelerate.Model.Club;
import eu.isawsm.accelerate.R;
import eu.isawsm.accelerate.ax.viewholders.AxViewHolder;
import eu.isawsm.accelerate.ax.viewholders.CarSettingsViewHolder;
import eu.isawsm.accelerate.ax.viewholders.CarViewHolder;
import eu.isawsm.accelerate.ax.viewholders.ClubViewHolder;
import eu.isawsm.accelerate.ax.viewholders.ConnectionViewHolder;
import eu.isawsm.accelerate.ax.viewholders.FriendsViewHolder;
import eu.isawsm.accelerate.ax.viewholders.RecentLapsViewHolder;
import eu.isawsm.accelerate.ax.viewmodel.CarSetup;
import eu.isawsm.accelerate.ax.viewmodel.ConnectionSetup;
import eu.isawsm.accelerate.ax.viewmodel.Friends;
import eu.isawsm.accelerate.ax.viewmodel.RecentLaps;

public class AxAdapter extends RecyclerView.Adapter<AxViewHolder> {
    private ArrayList<AxCardItem> mDataset;

    private Activity context;
    private int lastPosition = -1;


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
    public int getItemViewType(int position) {
        if (mDataset.get(position).get() instanceof Car)
            return R.layout.ax_car_cardview;
        else if (mDataset.get(position).get() instanceof ConnectionSetup)
            return R.layout.ax_connection_cardview;
        else if (mDataset.get(position).get() instanceof Club) {
            return R.layout.ax_club_cardview;
        } else if (mDataset.get(position).get() instanceof CarSetup) {
            return R.layout.ax_car_settings_cardview;
        } else if (mDataset.get(position).get() instanceof RecentLaps) {
            return R.layout.ax_recent_laps_cardview;
        } else if(mDataset.get(position).get() instanceof Friends) {
            return R.layout.ax_friends_cardview;
        } else {
            return -1;
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AxViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        if(viewType == R.layout.ax_car_cardview) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ax_car_cardview, parent, false);
            return new CarViewHolder(v, this, context);

        } else if(viewType == R.layout.ax_connection_cardview) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ax_connection_cardview, parent, false);
            return new ConnectionViewHolder(v, this, context);

        } else if(viewType == R.layout.ax_club_cardview) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ax_club_cardview, parent, false);
            return new ClubViewHolder(v, this, context);

        } else if(viewType == R.layout.ax_car_settings_cardview){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ax_car_settings_cardview, parent, false);
            return new CarSettingsViewHolder(v, this, context);

        } else if(viewType == R.layout.ax_recent_laps_cardview){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ax_recent_laps_cardview, parent, false);
            return new RecentLapsViewHolder(v, this, context);

        } else if(viewType == R.layout.ax_friends_cardview){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ax_friends_cardview, parent, false);
            return new FriendsViewHolder(v, this, context);

        } else {
            throw new RuntimeException("Cardview not supported");
        }

    }

    @Override
    public void onBindViewHolder(AxViewHolder holder, int position) {
        setAnimation(holder.mView, position);
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
        AxViewHolder.viewHolders.remove(position);
        mDataset.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mDataset.size());
    }
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if(lastPosition == -1) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        } else if (position > lastPosition){
//            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_up);
//            viewToAnimate.startAnimation(animation);
//            lastPosition = position;
        }

    }
}