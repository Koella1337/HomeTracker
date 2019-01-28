package at.hometracker.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import at.hometracker.R;
import at.hometracker.shared.Constants;

public class TableActivity extends AppCompatActivity {

    private int group_id;
    private int shelf_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        shelf_id = getIntent().getIntExtra(Constants.INTENT_EXTRA_SHELF_ID, -1);
        group_id = getIntent().getIntExtra(Constants.INTENT_EXTRA_GROUP_ID, -1);
        if (shelf_id == -1 || group_id == -1) throw new RuntimeException("Invalid id on TableActivity creation!");
    }
}
