package at.hometracker.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import at.hometracker.R;
import at.hometracker.shared.ShelfGridAdapter;
import at.hometracker.database.DatabaseMethod;
import at.hometracker.database.DatabaseTask;
import at.hometracker.database.datamodel.Group;
import at.hometracker.database.datamodel.Shelf;
import at.hometracker.qrcode.ReaderActivity;
import at.hometracker.shared.Constants;
import at.hometracker.utils.CameraUtils;

import static at.hometracker.shared.Constants.PHP_ERROR_PREFIX;
import static at.hometracker.shared.Constants.PHP_ROW_SPLITTER;


public class GroupActivity extends AppCompatActivity {

    private int group_id;
    private Group group;

    private ShelfGridAdapter shelfGridAdapter;
    private List<Shelf> shelves = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_group);
        setSupportActionBar(myToolbar);

        group_id = getIntent().getIntExtra(Constants.INTENT_EXTRA_GROUP_ID, -1);
        Log.v("misc", "Started GroupActivity with group_id: \"" + group_id + "\"");
        if (group_id == -1)
            throw new RuntimeException("Invalid group_id on GroupActivity creation!");

        new DatabaseTask(this, DatabaseMethod.SELECT_GROUP_WITHOUT_PICTURE, (task, result) -> {
            if (result == null || result.isEmpty())
                return;
            Group group = new Group(result);
            this.group = group;
            myToolbar.setTitle(group.name);
        }).execute(group_id);
    }

    @Override
    protected void onStart() {
        super.onStart();
        clearAndFetchShelves();
    }

    private void clearAndFetchShelves() {
        GridView grid = findViewById(R.id.shelf_grid);
        grid.setOnItemClickListener((parent, view, position, id) -> {
            openShelfActivity(shelves.get(position));
        });
        shelves = new ArrayList<>();

        new DatabaseTask(this, DatabaseMethod.SELECT_SHELVES_FOR_GROUP, (task, result) -> {
            if (result == null || result.isEmpty())
                return;
            for (String row : result.split(PHP_ROW_SPLITTER)) {
                shelves.add(new Shelf(row));
            }
            shelfGridAdapter = new ShelfGridAdapter(GroupActivity.this, shelves);
            grid.setAdapter(shelfGridAdapter);
            shelfGridAdapter.notifyDataSetChanged();
        }).execute(group_id);
    }

    private void openShelfActivity(Shelf shelf) {
        Intent shelfIntent = new Intent(this, TableActivity.class);
        shelfIntent.putExtra(Constants.INTENT_EXTRA_SHELF, shelf);
        shelfIntent.putExtra(Constants.INTENT_EXTRA_GROUP, group);
        startActivity(shelfIntent);
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
            case R.id.action_camera:
                Intent scanIntent = new Intent(this, ReaderActivity.class);
                startActivity(scanIntent);
                return true;
            case R.id.action_map:
                Intent mapIntent = new Intent(this, MapActivity.class);
                mapIntent.putExtra(Constants.INTENT_EXTRA_GROUP_ID, group_id);
                this.startActivity(mapIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




}
