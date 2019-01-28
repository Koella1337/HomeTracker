package at.hometracker.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import at.hometracker.R;
import at.hometracker.database.DatabaseMethod;
import at.hometracker.database.DatabaseTask;
import at.hometracker.database.datamodel.Shelf;
import at.hometracker.qrcode.GeneratorActivity;
import at.hometracker.shared.Constants;
import at.hometracker.utils.FileUtils;

public class ShelfActivity extends AppCompatActivity {

    private int shelf_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelf);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_shelf);
        setSupportActionBar(myToolbar);

        shelf_id = getIntent().getIntExtra(Constants.INTENT_EXTRANAME_SHELF_ID, -1);
        Log.v("misc", "Started ShelfActivity with shelf_id: \"" + shelf_id + "\"");
        if (shelf_id == -1) throw new RuntimeException("Invalid shelf_id on ShelfActivity creation!");

        ImageView imageView = findViewById(R.id.shelf_img);
        new DatabaseTask(this, DatabaseMethod.SELECT_SHELF, (task, result) -> {
            Shelf shelf = new Shelf(result.split(Constants.PHP_ROW_SPLITTER)[0]);
            Log.v("misc", "Entered shelf: " + shelf);
            FileUtils.setImageViewWithByteArray(imageView, shelf.picture);
        }).execute(shelf_id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.shelf_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_generate_qr:
                Log.i("menu clicked","action_generate_qr");

                String qrCodeString = "shelf_"+shelf_id;

                Intent shelfIntent = new Intent(this, GeneratorActivity.class);
                shelfIntent.putExtra(Constants.INTENT_EXTRANAME_QR_STRING, qrCodeString);
                startActivity(shelfIntent);

                return true;
            case R.id.action_logout:
                Log.i("menu clicked","action_logout");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
