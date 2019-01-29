package at.hometracker.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import at.hometracker.R;
import at.hometracker.database.datamodel.Group;
import at.hometracker.shared.Constants;
import at.hometracker.utils.FileUtils;

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

        LinearLayout layout = findViewById(R.id.table_shelf);
        LayoutInflater inflater = getLayoutInflater();

        for (int i = 0; i < 20; i++) {
            View row = inflater.inflate(R.layout.single_tablerow, layout, false);
            View amountLayout = row.findViewById(R.id.row_layout_amount);
            if (i%2 == 0) {
                row.setBackgroundResource(R.color.table_color_white);
                amountLayout.setBackgroundResource(R.color.table_color_white);
            }
            else {
                row.setBackgroundResource(R.color.table_color_gray);
                amountLayout.setBackgroundResource(R.color.table_color_gray);
            }

            layout.addView(row);
        }
    }
}
