package eu.isawsm.accelerate.ax.viewholders;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import eu.isawsm.accelerate.Model.Club;
import eu.isawsm.accelerate.R;
import eu.isawsm.accelerate.ax.AxAdapter;
import eu.isawsm.accelerate.ax.AxCardItem;
import eu.isawsm.accelerate.ax.Util.RemoteFetch;

/**
 * Created by ofade_000 on 21.02.2015.
 */
public class ClubViewHolder extends  AxViewHolder implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private TextView tfClubName;
    private TextView tfCondition;
    private TextView tfAditionalInfo;
    private TextView tfTemperature;
    private ImageView ivCondition;


    public ClubViewHolder(View v, AxAdapter mDataset, Activity context) {
        super(v, mDataset, context);
    }

    @Override
    public void onBindViewHolder(AxAdapter.ViewHolder holder, int position) {
        TextView tfClubName = (TextView) holder.mView.findViewById(R.id.tfClubName);
        TextView tfCondition = (TextView) holder.mView.findViewById(R.id.tfCondition);
        TextView tfAditionalInfo = (TextView) holder.mView.findViewById(R.id.tfAditionalInfo);
        TextView tfTemperature = (TextView) holder.mView.findViewById(R.id.tfTemperature);
        ImageView ivCondition = (ImageView) holder.mView.findViewById(R.id.ivCondition);


        Club club = axAdapter.getDataset().get(position).toClub();

        //TODO: Replace Test Data:
        buildGoogleApiClient();
//            tfClubName.setText(club.getName());
//            tfCondition.setText("Sunny");
//            tfAditionalInfo.setText("Wind 11km/h Precip 0%");
//            tfTemperature.setText("9°");
//            ivCondition.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_fair));
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(this.getClass().getName(), "Google API Connected");
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null)
            updateWeatherData(mLastLocation.getLatitude(), mLastLocation.getLongitude());
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(this.getClass().getName(),"Google API Connection Suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(this.getClass().getName(),"Google API Connection Failed " + connectionResult.toString());
    }

    private void updateWeatherData(final double lat, final double lon){
        final Handler handler = new Handler();
        new Thread(){
            public void run(){
                final JSONObject json = RemoteFetch.getJSON(context, lat, lon);
                if(json == null){
                    handler.post(new Runnable(){
                        public void run(){
                            Toast.makeText(context,
                                    context.getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable(){
                        public void run(){
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }
    private void renderWeather(JSONObject json){
        try {
            tfClubName.setText(json.getString("name").toUpperCase(Locale.US) +
                    ", " +
                    json.getJSONObject("sys").getString("country"));


            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            tfAditionalInfo.setText(
                    details.getString("description").toUpperCase(Locale.US) +
                            "\n" + "Humidity: " + main.getString("humidity") + "%" +
                            "\n" + "Pressure: " + main.getString("pressure") + " hPa");

            tfTemperature.setText(
                    String.format("%.2f", main.getDouble("temp"))+ " ℃");

            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(json.getLong("dt")*1000));
            //updatedField.setText("Last update: " + updatedOn);

            setWeatherIcon(details.getInt("id"));

        }catch(Exception e){
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }
    private void setWeatherIcon(int actualId){
        int id = actualId / 100;
        Drawable icon = null;

        switch(id) {
            case 2 : icon = context.getResources().getDrawable(R.drawable.ic_thunderstorms);
                break;
            case 3 : icon = context.getResources().getDrawable(R.drawable.ic_rain); //drizzle
                break;
            case 7 : icon =context.getResources().getDrawable(R.drawable.ic_fog);
                break;
            case 8 : icon = context.getResources().getDrawable(R.drawable.ic_cloudy);
                break;
            case 6 : icon = context.getResources().getDrawable(R.drawable.ic_snow);
                break;
            case 5 : icon =context.getResources().getDrawable(R.drawable.ic_rain);
                break;
        }

        ivCondition.setImageDrawable(icon);
    }
}
