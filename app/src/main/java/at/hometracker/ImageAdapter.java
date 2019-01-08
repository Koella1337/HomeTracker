package at.hometracker;
 
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
public class ImageAdapter extends BaseAdapter {
    private Context context;
    private final int item_image[];
    private final String item_text[];
 
    public ImageAdapter(Context context, int item_image[], String[] item_text) {
        this.context = context;
        this.item_image = item_image;
        this.item_text = item_text;
    }
 
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView;
 
        if (convertView == null) {
            gridView = new View(context);
            gridView = inflater.inflate(R.layout.thumbnail_gridview, null);

            ImageView image = (ImageView) gridView.findViewById(R.id.item_image);
            image.setImageResource(item_image[position]);
          //  image.setImageIcon(null); TODO set icon

            TextView text = (TextView) gridView.findViewById(R.id.item_text);
            text.setText(item_text[position]);
        } else {
            gridView = (View) convertView;
        }
        return gridView;
    }
 
    @Override
    public int getCount() {
        return item_text.length;
    }
 
    @Override
    public Object getItem(int position) {
        return null;
    }
 
    @Override
    public long getItemId(int position) {
        return 0;
    }
}