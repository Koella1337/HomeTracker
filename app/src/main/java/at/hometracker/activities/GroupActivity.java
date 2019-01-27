package at.hometracker.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import at.hometracker.R;
import at.hometracker.app.CustomGridAdapter;
import at.hometracker.app.ImageWithText;
import at.hometracker.utils.FileUtils;

public class GroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_group);
        setSupportActionBar(myToolbar);

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

        CustomGridAdapter adapter = new CustomGridAdapter(GroupActivity.this, imageList);

        mainGrid.setAdapter(adapter);

        mainGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(GroupActivity.this, "You Clicked at " +imageList.get(position).getName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(GroupActivity.this,ShelfActivity.class);
                intent.putExtra("info","This is activity from card item index  "+position);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.group_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_profile:
                Log.i("menu clicked","action_profile");
                return true;
            case R.id.action_settings:
                Log.i("menu clicked","action_settings");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
