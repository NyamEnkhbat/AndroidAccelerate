package eu.isawsm.accelerate.ax.viewholders;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.LinePoint;

import eu.isawsm.accelerate.Model.Car;
import eu.isawsm.accelerate.R;
import eu.isawsm.accelerate.ax.AxAdapter;


/**
 * Created by ofade_000 on 21.02.2015.
 */
public class CarViewHolder extends AxViewHolder {

    public CarViewHolder(View v, AxAdapter mDataset, Activity context) {
        super(v, mDataset, context);
        v.setTag(this);
    }

    @Override
    public void onBindViewHolder(AxAdapter.ViewHolder holder, int position) {
        TextView tfCarName = (TextView) holder.mView.findViewById(R.id.tfCarName);
        TextView tfConsistencyValue = (TextView) holder.mView.findViewById(R.id.tfConsistencyValue);

        TextView tfAvg = (TextView) holder.mView.findViewById(R.id.tfAvg);
        TextView tfBest = (TextView) holder.mView.findViewById(R.id.tfBest);
        TextView tfLaps = (TextView) holder.mView.findViewById(R.id.tfLaps);
        TextView tfClass = (TextView) holder.mView.findViewById(R.id.tfClass);
        LineGraph lgGraph = (LineGraph) holder.mView.findViewById(R.id.linegraph);

        fillLineGraph(lgGraph);

        Car car = axAdapter.getDataset().get(position).toCar();

        tfCarName.setText(car.getFullName());
        tfClass.setText(car.getClazz().getName());
    }

    private void fillLineGraph(LineGraph lgGraph) {
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

        lgGraph.setUsingDips(true);
        lgGraph.addLine(l);
        lgGraph.setRangeY(0, 10);
        lgGraph.setLineToFill(0);
    }
}
