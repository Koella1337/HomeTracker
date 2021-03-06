package at.hometracker.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import at.hometracker.R;
import at.hometracker.database.DatabaseMethod;
import at.hometracker.database.DatabaseTask;
import at.hometracker.database.datamodel.Group;
import at.hometracker.shared.Constants;
import at.hometracker.utils.FileUtils;
import at.hometracker.utils.Utils;

import static at.hometracker.shared.Constants.PHP_ROW_SPLITTER;

public class GroupSelectionActivity extends AppCompatActivity {

    private int user_id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_selection);
        Toolbar myToolbar = findViewById(R.id.toolbar_groupselection);
        setSupportActionBar(myToolbar);

        int user_id = getIntent().getIntExtra(Constants.INTENT_EXTRA_USER_ID, -1);
        Log.v("misc", "Started GroupSelectionActivity with user_id: \"" + user_id + "\"");
        if (user_id == -1) throw new RuntimeException("Invalid user_id on GroupSelectionActivity creation!");

        this.user_id = user_id;
    }

    @Override
    protected void onStart() {
        super.onStart();
        clearAndFetchGroups();
    }

    private void clearAndFetchGroups() {
        ((LinearLayout) findViewById(R.id.layout_groups)).removeAllViews();

        DatabaseTask fetchGroups = new DatabaseTask(this, DatabaseMethod.SELECT_GROUPS_FOR_USER, (task, result) -> {
            if (result == null || result.isEmpty())
                return;
            List<Group> groups = new ArrayList<>();
            for (String row : result.split(PHP_ROW_SPLITTER)){
                groups.add(new Group(row));
            }
            addGroupViews(groups.toArray(new Group[0]));
        });
        fetchGroups.execute(user_id);
    }

    private void addGroupViews(Group... groups) {
        LinearLayout layout = findViewById(R.id.layout_groups);
        LayoutInflater inflater = getLayoutInflater();

        for (Group g : groups) {
            View group = inflater.inflate(R.layout.single_group, layout, false);
            group.setOnClickListener(view -> openGroupActivity(g.group_id));

            ((TextView) group.findViewById(R.id.single_group_text)).setText(g.name);
            FileUtils.setImageViewWithByteArray( group.findViewById(R.id.single_group_img), g.picture);
            layout.addView(group);
        }
    }

    private void createGroup(AlertDialog groupCreationDialog) {
        EditText textGroupName = groupCreationDialog.findViewById(R.id.create_group_name);
        if (!Utils.validateEditTexts(this, textGroupName))
            return;

        String groupName = textGroupName.getText().toString();
        try {
            new DatabaseTask(this, DatabaseMethod.INSERT_GROUP, (task, result) -> {
                if (result == null || result.startsWith(Constants.PHP_ERROR_PREFIX)){
                    Toast.makeText(this, R.string.toast_groupcreation_failed, Toast.LENGTH_LONG).show();
                }
                else {
                    clearAndFetchGroups();
                    Toast.makeText(this, getString(R.string.toast_groupcreation_success) + groupName, Toast.LENGTH_LONG).show();
                }
            }).execute(user_id, groupName, getAssets().open(Constants.DEFAULT_GROUP_PICTURE_NAME));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                AlertDialog alertDialog = Utils.buildAlertDialog(this, R.layout.dialog_create_group);
                Utils.setAlertDialogButtons(alertDialog,
                        getString(R.string.label_create_group), (dialog, id) -> createGroup(alertDialog),
                        getString(R.string.label_cancel), (dialog, id) -> Log.i("Groups", "User " + user_id + " cancelled group creation.")
                );
                alertDialog.setCancelable(false);
                alertDialog.show();
                return true;
            case R.id.action_profile:
                Log.i("menu clicked","action_profile");
                return true;
            case R.id.action_settings:
                Log.i("menu clicked","action_settings");
                return true;
            case R.id.action_logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences(Constants.PREFERENCES_FILE_NAME, MODE_PRIVATE);
        String email = prefs.getString(Constants.PREFNAME_EMAIL, null);
        String password = prefs.getString(Constants.PREFNAME_PASSWORD, null);

        Utils.setLoginPreferences(getApplicationContext(), null, null);
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.putExtra(Constants.INTENT_EXTRA_EMAIL, email);
        loginIntent.putExtra(Constants.INTENT_EXTRA_PASSWORD, password);

        startActivity(loginIntent);
        finish();
    }

    public void openGroupActivity(int group_id){
        Intent groupIntent = new Intent(this, GroupActivity.class);
        groupIntent.putExtra(Constants.INTENT_EXTRA_GROUP_ID, group_id);
        startActivity(groupIntent);
    }



}
