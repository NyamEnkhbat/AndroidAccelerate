package eu.isawsm.accelerate.ax.viewholders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import Shared.Car;
import Shared.Course;
import Shared.Lap;
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
    public Car car;
    LapAdapter lapAdapter0;
    LapAdapter lapAdapter1;
    LapAdapter lapAdapter2;
    Course currentCourse;

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
        listView1 = (ListView) v.findViewById(R.id.listView1);
        listView2 = (ListView) v.findViewById(R.id.listView2);

        detailsButton = (ImageButton) v.findViewById(R.id.detailsButton);
    }

    public void onBindViewHolder(AxAdapter.ViewHolder holder, int position, AxCardItem axCardItem) {
        car = (Car) axCardItem.get();
        if(!car.getLaps().isEmpty())
            currentCourse = car.getLaps().get(car.getLaps().size()-1).getCourse();

        tfCarName.setText(car.getName());
        tfClass.setText(car.getClazz().getName());
        String consistency;
        if (car.getConsistency(currentCourse) != -1) {
            consistency = car.getConsistency(currentCourse) + "%";
        } else {
            consistency = "-";
        }
        tfConsistencyValue.setText(consistency);
        tfRank.setText(car.getRank() +""); //if i pass an int it will search for a resource ID


        tfAvg.setText(car.getAvgTime(currentCourse) / 1000 + "");
        tfBest.setText(car.getBestTime(currentCourse) / 1000 + "");
        tfLaps.setText(car.getLapCount(currentCourse) + "");

        lapAdapter0 = new LapAdapter(context, R.layout.laplistrow);
        lapAdapter0.positionOffset = 0;
        listView0.setAdapter(lapAdapter0);

        lapAdapter1 = new LapAdapter(context, R.layout.laplistrow);
        lapAdapter1.positionOffset = 5;
        listView1.setAdapter(lapAdapter1);

        lapAdapter2 = new LapAdapter(context, R.layout.laplistrow);
        lapAdapter2.positionOffset = 10;
        listView2.setAdapter(lapAdapter2);


        //dont want to get a java.util.ConcurrentModificationException when a Lap is added while iterating.
        //todo this could lead to inconsistent data but who cares if that one lap is missing that happend in the same second where i opend up my app...
        final List<Lap> lapArrayList = car.getLaps();

        for (Lap l : lapArrayList) {
            addLap(l);
        }
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


    private static class ViewHolder {
        TextView index;
        TextView time;
        ImageView upArrow;
        ImageView downArrow;
        ImageView star;
        ImageView warn;
        ImageView equal;

    }

    public class LapAdapter extends ArrayAdapter<Lap> {

        int positionOffset = 1;
        private ViewHolder viewHolder;
        public LapAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Lap lap = getItem(position);


            if(convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.laplistrow, parent,false);
                viewHolder = new ViewHolder();

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.index = (TextView) convertView.findViewById(R.id.tfindex);
            viewHolder.time = (TextView) convertView.findViewById(R.id.textView);
            viewHolder.upArrow = (ImageView) convertView.findViewById(R.id.upArrow);
            viewHolder.downArrow = (ImageView) convertView.findViewById(R.id.downArrow);
            viewHolder.star = (ImageView) convertView.findViewById(R.id.star);
            viewHolder.warn = (ImageView) convertView.findViewById(R.id.warn);
            viewHolder.equal = (ImageView) convertView.findViewById(R.id.equal);


            viewHolder.index.setText(car.getLaps().indexOf(lap)+1 + ".");

            String lapTime = lap.getTime() / 1000d + "";
            while (lapTime.length() < 6) {
                lapTime += "0";
            }
            viewHolder.time.setText(lapTime);

            viewHolder.star.setVisibility(View.GONE);
            viewHolder.warn.setVisibility(View.GONE);
            viewHolder.downArrow.setVisibility(View.GONE);
            viewHolder.upArrow.setVisibility(View.GONE);
            viewHolder.equal.setVisibility(View.GONE);

            if (lap.getTime() < car.getBestTime(currentCourse)) {
                viewHolder.star.setVisibility(View.VISIBLE);
                return convertView;
            }
            if (car.getAvgTime(currentCourse) + 5000 < lap.getTime()) {
                viewHolder.warn.setVisibility(View.VISIBLE);
                return convertView;
            }

            if (car.getAvgTime(currentCourse)+500 < lap.getTime()) {
                viewHolder.downArrow.setVisibility(View.VISIBLE);
            } else if(car.getAvgTime(currentCourse)-500 > lap.getTime() ) {
                viewHolder.upArrow.setVisibility(View.VISIBLE);
            } else {
                viewHolder.equal.setVisibility(View.VISIBLE);
            }

            return convertView;
        }
    }
}
