package at.hometracker.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import at.hometracker.R;
import at.hometracker.utils.FileUtils;
import at.lukle.clickableareasimage.ClickableArea;
import at.lukle.clickableareasimage.ClickableAreasImage;
import at.lukle.clickableareasimage.OnClickableAreaClickedListener;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ClickableImageTestActivity extends AppCompatActivity  implements OnClickableAreaClickedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clickable_image_test);

        ImageView imageView = (ImageView) findViewById(R.id.testClickableAreaImageView);
        try {
            FileUtils.setImageForImageView(imageView,this,"Homura_BG2.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create your image
        ClickableAreasImage clickableAreasImage = new ClickableAreasImage(new PhotoViewAttacher(imageView), this);

        // Initialize your clickable area list
        List<ClickableArea> clickableAreas = new ArrayList<>();

        // Define your clickable areas
        // parameter values (pixels): (x coordinate, y coordinate, width, height) and assign an object to it
        clickableAreas.add(new ClickableArea(10, 0, 200, 200, "item 1"));
        clickableAreas.add(new ClickableArea(300, 0, 200, 200,"item 2"));

        // Set your clickable areas to the image
        clickableAreasImage.setClickableAreas(clickableAreas);
    }

    @Override
    public void onClickableAreaTouched(Object item) {
        if (item instanceof String) {
            String text = ((String) item);
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        }
    }
}
