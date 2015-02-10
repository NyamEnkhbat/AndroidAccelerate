package eu.isawsm.accelerate.adapters;

import eu.isawsm.accelerate.Model.Lap;
import eu.isawsm.accelerate.R;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LapListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Lap> laps;


    public LapListAdapter(Activity activity, List<Lap> laps) {
        this.activity = activity;
        this.laps = laps;
    }

    @Override
    public int getCount() {
        return laps.size();
    }

    @Override
    public Object getItem(int location) {
        return laps.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.lap_list_row, null);

        TextView driverName = (TextView) convertView.findViewById(R.id.driverName);
        TextView time = (TextView) convertView.findViewById(R.id.time);

        TextView avgTime = (TextView) convertView.findViewById(R.id.avgTime);
        TextView bestTime = (TextView) convertView.findViewById(R.id.bestTime);
        TextView lapsCounter = (TextView) convertView.findViewById(R.id.lapsCounter);

        TextView car = (TextView) convertView.findViewById(R.id.car);
        TextView calzz = (TextView) convertView.findViewById(R.id.clazz);

        // getting movie data for the row
        Lap l = laps.get(position);

        driverName.setText(l.getCar().getDriver().getFirstname());
        time.setText(""+(l.getTime()/1000));

        avgTime.setText(l.getCar().getAvgTime()+"");
        bestTime.setText(l.getCar().getBestTime()+"");
        lapsCounter.setText(l.getCar().getLapCount()+"");

        car.setText(l.getCar().getFullName());
        calzz.setText(l.getCar().getClazz().getName());

        return convertView;

    }

}