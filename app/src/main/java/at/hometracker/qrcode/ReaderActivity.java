package at.hometracker.qrcode;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import at.hometracker.R;
import at.hometracker.activities.GroupActivity;
import at.hometracker.activities.ShelfActivity;
import at.hometracker.shared.Constants;

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
        Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
        Intent shelfIntent = new Intent(this, ShelfActivity.class);

        shelfIntent.putExtra(Constants.INTENT_EXTRANAME_SHELF_ID, Integer.parseInt("0"+qrCodeDecoded.replace("shelf_","")));
        startActivity(shelfIntent);
        finish();
    }
}
