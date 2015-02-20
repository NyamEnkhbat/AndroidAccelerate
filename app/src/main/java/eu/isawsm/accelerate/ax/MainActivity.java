package eu.isawsm.accelerate.ax;



import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.util.ArrayList;

import eu.isawsm.accelerate.Model.Car;
import eu.isawsm.accelerate.Model.Clazz;
import eu.isawsm.accelerate.Model.Driver;
import eu.isawsm.accelerate.Model.Manufacturer;
import eu.isawsm.accelerate.Model.Model;
import eu.isawsm.accelerate.R;

public class MainActivity extends ActionBarActivity {

    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;
    private CardView carCardView;



    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ax_recycler);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.ax_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setAdapter(new AxAdapter(getTestCarList(), this));
        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        int viewWidth = mRecyclerView.getMeasuredWidth();
                        float cardViewWidth = getResources().getDimension(R.dimen.cardview_layout_width);
                        int newSpanCount = (int) Math.floor(viewWidth / cardViewWidth);
                        mLayoutManager.setSpanCount(newSpanCount);
                        mLayoutManager.requestLayout();
                    }
                });

    }


    private ArrayList<Car> getTestCarList(){
        ArrayList<Car> retVal = new ArrayList<>();

        Driver driver = new Driver("Oliver", "Faderbauer", "Awli", null, null);
        Manufacturer manufacturer = new Manufacturer("Xray", null);
        Model model = new Model(manufacturer,"T4'15", "4WD", "17.5", "Touring Car", "1:10");
        Clazz clazz = new Clazz("17.5 Blinky", "Stock Class");
        long transponderID = 123456789;
        Bitmap picture = null;


        retVal.add(new Car(driver, model, clazz, transponderID, picture));
        retVal.add(new Car(driver, model, clazz, transponderID, picture));
        retVal.add(new Car(driver, model, clazz, transponderID, picture));
        retVal.add(new Car(driver, model, clazz, transponderID, picture));
        retVal.add(new Car(driver, model, clazz, transponderID, picture));
        retVal.add(new Car(driver, model, clazz, transponderID, picture));
        retVal.add(new Car(driver, model, clazz, transponderID, picture));
        retVal.add(new Car(driver, model, clazz, transponderID, picture));

        return retVal;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
