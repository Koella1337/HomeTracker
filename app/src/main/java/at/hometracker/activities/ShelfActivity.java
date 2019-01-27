package at.hometracker.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import at.hometracker.R;

public class ShelfActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelf);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_shelf);
        setSupportActionBar(myToolbar);

        if(getIntent() != null)
        {
            String info = getIntent().getStringExtra("info");
            Toast.makeText(ShelfActivity.this, info, Toast.LENGTH_SHORT).show();
        }
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
            case R.id.action_settings:
                Log.i("menu clicked","action_settings");
                return true;
            case R.id.action_logout:
                Log.i("menu clicked","action_logout");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
