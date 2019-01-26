package at.hometracker.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import at.hometracker.database.DatabaseActivity;
import at.hometracker.qrcode.QRCodeMainActivity;
import at.hometracker.R;

public class GroupSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_selection);
        Toolbar myToolbar = findViewById(R.id.toolbar_groupselection);
        setSupportActionBar(myToolbar);

        setOnClickListeners();
        createTestingGroups();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.group_selection_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_add_group:
                Log.i("menu clicked","action_add_group");

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

                LayoutInflater inflater = this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_create_group, null);
                dialogBuilder.setView(dialogView);

                dialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO
                    }
                    });

                dialogBuilder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO
                        }
                });
                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();


                return true;
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

    public void openGroupActivity(View view){
        Intent intent = new Intent(GroupSelectionActivity.this, GroupActivity.class);
        startActivity(intent);
    }

    public void openDatabaseActivity(View view){
        Intent intent = new Intent(GroupSelectionActivity.this, DatabaseActivity.class);
        startActivity(intent);
    }

    public void openQRCodeActivity(View view){
        Intent intent = new Intent(GroupSelectionActivity.this, QRCodeMainActivity.class);
        startActivity(intent);
    }

    public void openGridActivity(View view){
        Intent intent = new Intent(GroupSelectionActivity.this, ShelfSelectionActivity.class);
        startActivity(intent);
    }

    public void openClickableAreaActivity(View view){
        Intent intent = new Intent(GroupSelectionActivity.this, ClickableImageTestActivity.class);
        startActivity(intent);
    }

    private void setOnClickListeners() {
        //TODO

    }

    public void createTestingGroups(){
        LinearLayout layout = findViewById(R.id.layout_groups);
        LayoutInflater inflater = getLayoutInflater();

        for (int i = 1; i <= 20; i++) {
            View group = inflater.inflate(R.layout.single_group, layout, false);
            group.setOnClickListener(this::openGroupActivity);
            ((TextView) group.findViewById(R.id.single_group_text)).setText("Group " + i);
            layout.addView(group);
        }

    }
}
