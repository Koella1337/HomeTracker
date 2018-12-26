package at.hometracker.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import javax.xml.datatype.Duration;

import at.hometracker.R;

public class GroupSelectionActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_selection);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
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

}
