package at.hometracker.qrcode;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import at.hometracker.R;
import at.hometracker.activities.GroupActivity;
import at.hometracker.activities.ShelfActivity;
import at.hometracker.activities.TableActivity;
import at.hometracker.database.DatabaseMethod;
import at.hometracker.database.DatabaseTask;
import at.hometracker.database.datamodel.Shelf;
import at.hometracker.shared.Constants;
import at.hometracker.utils.FileUtils;

public class ReaderActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        final Activity activity = this;
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        String qrCodeDecoded = result.getContents();
        if(qrCodeDecoded != null){
            Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();

            String shelfIdentifier = "shelf_";
            if(qrCodeDecoded.startsWith(shelfIdentifier)){
                int shelf_id = Integer.parseInt("0"+qrCodeDecoded.replace(shelfIdentifier,""));
                new DatabaseTask(this, DatabaseMethod.SELECT_SHELF, (task, taskResult) -> {
                    if (taskResult == null || taskResult.isEmpty())
                        return;
                    Shelf shelf = new Shelf(taskResult);

                    Intent shelfIntent = new Intent(this, TableActivity.class);
                    shelfIntent.putExtra(Constants.INTENT_EXTRA_SHELF,shelf);
                    startActivity(shelfIntent);
                    finish();
                }).execute(shelf_id);
            }else{
                finish();
            }
        }else{
            finish();
        }

    }
}
