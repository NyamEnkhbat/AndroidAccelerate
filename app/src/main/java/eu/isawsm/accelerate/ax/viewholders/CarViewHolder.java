package eu.isawsm.accelerate.ax.viewholders;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import eu.isawsm.accelerate.Model.Car;
import eu.isawsm.accelerate.Model.Lap;
import eu.isawsm.accelerate.R;
import eu.isawsm.accelerate.ax.AxAdapter;
import eu.isawsm.accelerate.ax.AxCardItem;
import eu.isawsm.accelerate.ax.MainActivity;


public class CarViewHolder extends AxViewHolder {

    public TextView tfCarName;
    public TextView tfConsistencyValue;

    public TextView tfAvg;
    public TextView tfBest;
    public TextView tfLaps;
    public TextView tfClass;
    public ListView listView;
    public ImageButton detailsButton;

    public CarViewHolder(View v, AxAdapter mDataset, MainActivity context) {
        super(v, mDataset, context);
        v.setTag(this);
        tfCarName = (TextView) v.findViewById(R.id.tfCarName);
        tfConsistencyValue = (TextView) v.findViewById(R.id.tfConsistencyValue);

        tfAvg = (TextView) v.findViewById(R.id.tfAvg);
        tfBest = (TextView) v.findViewById(R.id.tfBest);
        tfLaps = (TextView) v.findViewById(R.id.tfLaps);
        tfClass = (TextView)v.findViewById(R.id.tfClass);
        listView = (ListView) v.findViewById(R.id.listView);
        detailsButton = (ImageButton) v.findViewById(R.id.detailsButton);
    }

    public void onBindViewHolder(AxAdapter.ViewHolder holder, int position, AxCardItem axCardItem) {
        Car car = (Car) axCardItem.get();

        tfCarName.setText(car.getFullName());
        tfClass.setText(car.getClazz().getName());
        LapAdapter lapAdapter = new LapAdapter(context, R.layout.laplistrow);
        listView.setAdapter(lapAdapter);

        lapAdapter.add(new Lap(car, 2990, null));
        lapAdapter.add(new Lap(car, 1990, null));
        lapAdapter.add(new Lap(car, 2990, null));
        lapAdapter.add(new Lap(car, 2990, null));
        lapAdapter.add(new Lap(car, 2990, null));
        lapAdapter.add(new Lap(car, 2990, null));
        lapAdapter.add(new Lap(car, 2990, null));
        lapAdapter.add(new Lap(car, 2990, null));

    }


    public class LapAdapter extends ArrayAdapter<Lap> {

        public LapAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Lap lap = getItem(position);

            if(convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.laplistrow, parent,false);
            }

            TextView time = (TextView) convertView.findViewById(R.id.textView);
            ImageView upArrow = (ImageView) convertView.findViewById(R.id.upArrow);
            ImageView downArrow = (ImageView) convertView.findViewById(R.id.downArrow);
            ImageView star = (ImageView) convertView.findViewById(R.id.star);
            ImageView warn = (ImageView) convertView.findViewById(R.id.warn);

            time.setText(lap.getTime() / 1000 + "");

            if (lap.getTime() < lap.getCar().getBestTime()){
                star.setVisibility(View.VISIBLE);
                return convertView;
            }
            if(lap.getCar().getAvgTime() +5000 < lap.getTime()) {
                warn.setVisibility(View.VISIBLE);
                return convertView;
            }

            if(lap.getCar().getAvgTime() < lap.getTime()){
                downArrow.setVisibility(View.VISIBLE);
            } else {
                upArrow.setVisibility(View.VISIBLE);
            }

            return convertView;
        }
    }
}
