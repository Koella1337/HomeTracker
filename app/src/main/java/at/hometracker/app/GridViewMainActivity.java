package at.hometracker.app;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import at.hometracker.R;
import at.hometracker.utils.FileUtils;

public class GridViewMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gridview_main);

        GridView mainGrid = (GridView) findViewById(R.id.mainGrid);
        ImageView imageView = (ImageView) findViewById(R.id.testImageView);

        final List<ImageWithText> imageList = new ArrayList<>();

        try {
            imageList.add(new ImageWithText(FileUtils.getByteArrayForFile(this,"max-the-magician.jpg"),"max1"));
            imageList.add(new ImageWithText(FileUtils.getByteArrayForFile(this,"grimma.png"),"grimma"));
            imageList.add(new ImageWithText(FileUtils.getByteArrayForFile(this,"Homura_BG2.jpg"),"Homura_BG2"));
            imageList.add(new ImageWithText(FileUtils.getByteArrayForFile(this,"max-the-magician.jpg"),"max2"));
            imageList.add(new ImageWithText(FileUtils.getByteArrayForFile(this,"max-the-magician.jpg"),"max3"));
            imageList.add(new ImageWithText(FileUtils.getByteArrayForFile(this,"max-the-magician.jpg"),"max4"));
            imageList.add(new ImageWithText(FileUtils.getByteArrayForFile(this,"max-the-magician.jpg"),"max5"));
            imageList.add(new ImageWithText(FileUtils.getByteArrayForFile(this,"max-the-magician.jpg"),"max6"));
            imageList.add(new ImageWithText(FileUtils.getByteArrayForFile(this,"max-the-magician.jpg"),"max7"));
            imageList.add(new ImageWithText(FileUtils.getByteArrayForFile(this,"max-the-magician.jpg"),"max8"));
            imageList.add(new ImageWithText(FileUtils.getByteArrayForFile(this,"max-the-magician.jpg"),"max9"));
            imageList.add(new ImageWithText(FileUtils.getByteArrayForFile(this,"max-the-magician.jpg"),"max10"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        CustomGridAdapter adapter = new CustomGridAdapter(GridViewMainActivity.this, imageList);

        mainGrid.setAdapter(adapter);
        mainGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(GridViewMainActivity.this, "You Clicked at " +imageList.get(position).getName(), Toast.LENGTH_SHORT).show();

            }
        });

        //Set Event
        //setToggleEvent(mainGrid);
    }

    private void setToggleEvent(GridLayout mainGrid) {
        //Loop all child item of Main Grid
        for (int i = 0; i < mainGrid.getChildCount(); i++) {
            //You can see , all child item is CardView , so we just cast object to CardView
            final CardView cardView = (CardView) mainGrid.getChildAt(i);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cardView.getCardBackgroundColor().getDefaultColor() == -1) {
                        //Change background color
                        cardView.setCardBackgroundColor(Color.parseColor("#FF6F00"));
                        Toast.makeText(GridViewMainActivity.this, "State : True", Toast.LENGTH_SHORT).show();

                    } else {
                        //Change background color
                        cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                        Toast.makeText(GridViewMainActivity.this, "State : False", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void setSingleEvent(GridLayout mainGrid) {
        //Loop all child item of Main Grid
        for (int i = 0; i < mainGrid.getChildCount(); i++) {
            //You can see , all child item is CardView , so we just cast object to CardView
            CardView cardView = (CardView) mainGrid.getChildAt(i);
            final int finalI = i;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(GridViewMainActivity.this,GridViewOneSingleItemActivity.class);
                    intent.putExtra("info","This is activity from card item index  "+finalI);
                    startActivity(intent);

                }
            });
        }
    }
}
