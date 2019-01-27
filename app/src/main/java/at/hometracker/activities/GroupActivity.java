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
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import at.hometracker.R;
import at.hometracker.app.CustomGridAdapter;
import at.hometracker.app.Thumbnail;
import at.hometracker.shared.Constants;
import at.hometracker.utils.CameraUtils;
import at.hometracker.utils.FileUtils;


public class GroupActivity extends AppCompatActivity {

    private CustomGridAdapter shelfGridAdapter;
    private List<Thumbnail> thumbnailList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_group);
        setSupportActionBar(myToolbar);

        GridView mainGrid = (GridView) findViewById(R.id.mainGrid);

        initTestImageListIfNecessary();

        shelfGridAdapter = new CustomGridAdapter(GroupActivity.this, thumbnailList);
        mainGrid.setAdapter(shelfGridAdapter);

        mainGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(GroupActivity.this, "You Clicked at " + thumbnailList.get(position).getName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(GroupActivity.this, ShelfActivity.class);
                intent.putExtra("info", "This is activity from card item index  " + position);
                startActivity(intent);
            }
        });
    }

    private void initTestImageListIfNecessary(){
        if(this.thumbnailList != null){
            return;
        }
        thumbnailList = new ArrayList<>();
        try {
            thumbnailList.add(new Thumbnail(FileUtils.getByteArrayForFile(this, "max-the-magician.jpg"), "max1"));
            thumbnailList.add(new Thumbnail(FileUtils.getByteArrayForFile(this, "grimma.png"), "grimma"));
            thumbnailList.add(new Thumbnail(FileUtils.getByteArrayForFile(this, "Homura_BG2.jpg"), "Homura_BG2"));
            thumbnailList.add(new Thumbnail(FileUtils.getByteArrayForFile(this, "max-the-magician.jpg"), "max2"));
            thumbnailList.add(new Thumbnail(FileUtils.getByteArrayForFile(this, "max-the-magician.jpg"), "max3"));
            thumbnailList.add(new Thumbnail(FileUtils.getByteArrayForFile(this, "max-the-magician.jpg"), "max4"));
            thumbnailList.add(new Thumbnail(FileUtils.getByteArrayForFile(this, "max-the-magician.jpg"), "max5"));
            thumbnailList.add(new Thumbnail(FileUtils.getByteArrayForFile(this, "max-the-magician.jpg"), "max6"));
            thumbnailList.add(new Thumbnail(FileUtils.getByteArrayForFile(this, "max-the-magician.jpg"), "max7"));
            thumbnailList.add(new Thumbnail(FileUtils.getByteArrayForFile(this, "max-the-magician.jpg"), "max8"));
            thumbnailList.add(new Thumbnail(FileUtils.getByteArrayForFile(this, "max-the-magician.jpg"), "max9"));
            thumbnailList.add(new Thumbnail(FileUtils.getByteArrayForFile(this, "max-the-magician.jpg"), "max10"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("initTestImageList", "list initialized");
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
            case R.id.action_create_shelf:
                Log.i("menu clicked", "action_create_shelf");
                CameraUtils.requestPicture(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUESTCODE_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            byte[] imageData = CameraUtils.getPictureByteArrayFromCameraResponse(data);
            Log.i("onActivityResult ok", "requestCode " + requestCode+" data length: "+imageData.length);
            thumbnailList.get(0).setImageData(imageData);
        }else{
            Log.i("onActivityResult not ok", "requestCode " + requestCode);
        }

    }


}
