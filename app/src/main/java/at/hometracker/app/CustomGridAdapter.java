package at.hometracker.app;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import at.hometracker.R;
import at.hometracker.utils.FileUtils;

public class CustomGridAdapter extends BaseAdapter {
        private Context mContext;
       private List<ImageWithText> imageList;

        public CustomGridAdapter(Context mContext, List<ImageWithText> imageList) {
            this.mContext = mContext;
            this.imageList = imageList;
        }

        @Override
        public int getCount() {
            return imageList.size();
        }

        @Override
        public Object getItem(int position) {
            return imageList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View grid;
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                grid = new View(mContext);
                grid = inflater.inflate(R.layout.activity_gridview_singleitem, null);
                TextView textView = (TextView) grid.findViewById(R.id.txtInfo);
                ImageView imageView = (ImageView)grid.findViewById(R.id.thumbnailImage);
                textView.setText(imageList.get(position).getName());
                FileUtils.setImageViewWithByteArray(imageView,imageList.get(position).getImageData());
            } else {
                grid = (View) convertView;
            }

            return grid;
        }
}
