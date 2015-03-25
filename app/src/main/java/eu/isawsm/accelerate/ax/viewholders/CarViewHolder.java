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
    public TextView tfRank;
    public TextView tfAvg;
    public TextView tfBest;
    public TextView tfLaps;
    public TextView tfClass;
    public ListView listView0;
    public ListView listView1;
    public ListView listView2;
    public ImageButton detailsButton;
    LapAdapter lapAdapter0;
    LapAdapter lapAdapter1;
    LapAdapter lapAdapter2;

    public CarViewHolder(View v, AxAdapter mDataset, MainActivity context) {
        super(v, mDataset, context);
        v.setTag(this);
        tfCarName = (TextView) v.findViewById(R.id.tfCarName);

        tfConsistencyValue = (TextView) v.findViewById(R.id.tfConsistencyValue);
        tfRank = (TextView) v.findViewById(R.id.tfRank);

        tfAvg = (TextView) v.findViewById(R.id.tfAvg);
        tfBest = (TextView) v.findViewById(R.id.tfBest);
        tfLaps = (TextView) v.findViewById(R.id.tfLaps);
        tfClass = (TextView)v.findViewById(R.id.tfClass);



        listView0 = (ListView) v.findViewById(R.id.listView0);
        listView1 = (ListView) v.findViewById(R.id.listView0);
        listView2 = (ListView) v.findViewById(R.id.listView0);

        detailsButton = (ImageButton) v.findViewById(R.id.detailsButton);
    }

    public void onBindViewHolder(AxAdapter.ViewHolder holder, int position, AxCardItem axCardItem) {
        Car car = (Car) axCardItem.get();

        tfCarName.setText(car.getFullName());
        tfClass.setText(car.getClazz().getName());

        tfConsistencyValue.setText(car.getConsitancy()+"%");
        tfRank.setText(car.getRank() +""); //if i pass an int it will search for a resource ID

        tfAvg.setText(car.getAvgTime() + "");
        tfBest.setText(car.getBestTime() + "");
        tfLaps.setText(car.getLapCount() + "");

        lapAdapter0 = new LapAdapter(context, R.layout.laplistrow);
        lapAdapter0.positionOffset = 0;
        listView0.setAdapter(lapAdapter0);

        lapAdapter1 = new LapAdapter(context, R.layout.laplistrow);
        lapAdapter1.positionOffset = 5;
        listView0.setAdapter(lapAdapter1);

        lapAdapter2 = new LapAdapter(context, R.layout.laplistrow);
        lapAdapter2.positionOffset = 10;
        listView0.setAdapter(lapAdapter2);


        //TODO Testdata
        addLap(new Lap(car, 29954, null));
        addLap(new Lap(car, 19933, null));
        addLap(new Lap(car, 29340, null));
        addLap(new Lap(car, 24990, null));
        addLap(new Lap(car, 42990, null));

        addLap(new Lap(car, 34990, null));
        addLap(new Lap(car, 24399, null));
        addLap(new Lap(car, 29980, null));
        addLap(new Lap(car, 29980, null));
        addLap(new Lap(car, 29980, null));

        addLap(new Lap(car, 29954, null));
        addLap(new Lap(car, 19933, null));
        addLap(new Lap(car, 29340, null));
        addLap(new Lap(car, 24990, null));
        addLap(new Lap(car, 42990, null));

        addLap(new Lap(car, 29954, null));
        addLap(new Lap(car, 19933, null));
        addLap(new Lap(car, 29340, null));
        addLap(new Lap(car, 24990, null));
        addLap(new Lap(car, 42990, null));
    }


    private void addLap(Lap lap) {
        lapAdapter0.insert(lap,0);
        if (lapAdapter0.getCount() > 5) {
            lapAdapter1.insert(lapAdapter0.getItem(5),0);
            lapAdapter0.remove(lapAdapter0.getItem(5));
            if(lapAdapter1.getCount() > 5){
                lapAdapter2.insert(lapAdapter1.getItem(5),0);
                lapAdapter1.remove(lapAdapter1.getItem(5));
                if(lapAdapter2.getCount() > 5 ){
                    lapAdapter2.remove(lapAdapter2.getItem(5));
                }
            }
        }
    }

    public class LapAdapter extends ArrayAdapter<Lap> {

        public LapAdapter(Context context, int resource) {
            super(context, resource);
        }

        int positionOffset = 1;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Lap lap = getItem(position);

            if(convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.laplistrow, parent,false);
            }

            TextView index = (TextView) convertView.findViewById(R.id.tfindex);
            TextView time = (TextView) convertView.findViewById(R.id.textView);
            ImageView upArrow = (ImageView) convertView.findViewById(R.id.upArrow);
            ImageView downArrow = (ImageView) convertView.findViewById(R.id.downArrow);
            ImageView star = (ImageView) convertView.findViewById(R.id.star);
            ImageView warn = (ImageView) convertView.findViewById(R.id.warn);

            index.setText((position+positionOffset+1)+".");

            time.setText(lap.getTime() / 1000d + "");

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
