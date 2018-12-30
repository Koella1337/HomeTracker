package at.hometracker.database;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

import at.hometracker.R;

public class DatabaseActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        this.textView = findViewById(R.id.databaseTextView);
    }

    public void readFromDatabase(View view) {
        try {
            String result = new DatabaseTask(DatabaseMethod.SELECT_USERS).execute().get();
            Log.i("db", DatabaseMethod.SELECT_USERS.name() + " result = " + result);

            if(result != null && result.trim().length() > 0) {
                //remove last "|" from result, then replace all "|" with "\n"
                result = result.substring(0, result.length() -1).replace('|', '\n');
                this.textView.setText(result);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void insertIntoDatabase(View view) throws ExecutionException, InterruptedException {
        String result = new DatabaseTask(DatabaseMethod.INSERT_USER).execute("testUser", "testPW").get();
        Log.i("db", DatabaseMethod.INSERT_USER.name() + " result = " + result);
    }

}
