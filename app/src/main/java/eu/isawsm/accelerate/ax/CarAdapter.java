package eu.isawsm.accelerate.ax;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.LinePoint;

import org.w3c.dom.Text;

import java.util.ArrayList;

import eu.isawsm.accelerate.Model.Car;
import eu.isawsm.accelerate.R;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.ViewHolder> {
    private ArrayList<Car> mDataset;

    private Context context;

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
    public CarAdapter(ArrayList<Car> myDataset, Context context) {
        mDataset = myDataset;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CarAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ax_cardview, parent, false);
        // set the view's size, margins, paddings and layout parameters
        //...
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CarAdapter.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        TextView tfCarName = (TextView) holder.mView.findViewById(R.id.tfCarName);
        TextView tfConsistencyValue = (TextView) holder.mView.findViewById(R.id.tfConsistencyValue);

        TextView tfAvg = (TextView) holder.mView.findViewById(R.id.tfAvg);
        TextView tfBest = (TextView) holder.mView.findViewById(R.id.tfBest);
        TextView tfLaps = (TextView) holder.mView.findViewById(R.id.tfLaps);


        Line l = new Line();
        l.setUsingDips(true);
        LinePoint p = new LinePoint();
        p.setX(0);
        p.setY(5);
        p.setColor(context.getResources().getColor(R.color.colorPrimary));
        l.addPoint(p);
        p = new LinePoint();
        p.setX(8);
        p.setY(8);
        p.setColor(context.getResources().getColor(R.color.colorPrimary));
        l.addPoint(p);
        p = new LinePoint();
        p.setX(10);
        p.setY(4);
        p.setColor(context.getResources().getColor(R.color.colorPrimary));
        l.addPoint(p);

        l.setColor(context.getResources().getColor(R.color.colorPrimary));

        LineGraph li = (LineGraph) holder.mView.findViewById(R.id.linegraph);

        li.setUsingDips(true);
        li.addLine(l);
        li.setRangeY(0, 10);
        li.setLineToFill(0);



        Car car = mDataset.get(position);

        tfCarName.setText(car.getFullName());



        //SET ALL THE VALUES TO ALL THE VIEWS
        //SET EVENTS ON CARDS
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}