package eu.isawsm.accelerate;

import eu.isawsm.accelerate.Model.Lap;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class LapListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Lap> movieItems;


    public LapListAdapter(Activity activity, List<Lap> movieItems) {
        this.activity = activity;
        this.movieItems = movieItems;
    }

    @Override
    public int getCount() {
        return movieItems.size();
    }

    @Override
    public Object getItem(int location) {
        return movieItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

//        if (inflater == null)
//            inflater = (LayoutInflater) activity
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        if (convertView == null)
//            convertView = inflater.inflate(R.layout.list_row, null);
//
//        if (imageLoader == null)
//            imageLoader = AppController.getInstance().getImageLoader();
//        NetworkImageView thumbNail = (NetworkImageView) convertView
//                .findViewById(R.id.thumbnail);
//        TextView title = (TextView) convertView.findViewById(R.id.title);
//        TextView rating = (TextView) convertView.findViewById(R.id.rating);
//        TextView genre = (TextView) convertView.findViewById(R.id.genre);
//        TextView year = (TextView) convertView.findViewById(R.id.releaseYear);
//
//        // getting movie data for the row
//        Lap m = movieItems.get(position);
//
//        // thumbnail image
//        thumbNail.setImageUrl(m.getThumbnailUrl(), imageLoader);
//
//        // title
//        title.setText(m.getTitle());
//
//        // rating
//        rating.setText("Rating: " + String.valueOf(m.getRating()));
//
//        // genre
//        String genreStr = "";
//        for (String str : m.getGenre()) {
//            genreStr += str + ", ";
//        }
//        genreStr = genreStr.length() > 0 ? genreStr.substring(0,
//                genreStr.length() - 2) : genreStr;
//        genre.setText(genreStr);
//
//        // release year
//        year.setText(String.valueOf(m.getYear()));
//
//        return convertView;
        return null;
    }

}