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

    public void testPw(View view) {
        String pw = "X";

        SecurePassword secPw = PasswordUtils.generateSecurePassword(pw);

        System.out.printf("Salt Len: %d --> %s%n", secPw.salt.length(), secPw.salt);
        System.out.printf("PW Len: %d --> %s%n", secPw.hashedPw.length(), secPw.hashedPw);

        System.out.println();

        System.out.printf("Validating \"%s\": %b%n", pw, PasswordUtils.validatePassword(pw, secPw));

        System.out.println();

        System.out.printf("Validating \"%s\": %b%n", "test123_lo", PasswordUtils.validatePassword("test123_lo", secPw));
        System.out.printf("Validating \"%s\": %b%n", "test123-lol", PasswordUtils.validatePassword("test123-lol", secPw));
        System.out.printf("Validating \"%s\": %b%n", "test123_loL", PasswordUtils.validatePassword("test123_loL", secPw));
        System.out.printf("Validating \"%s\": %b%n", "x", PasswordUtils.validatePassword("x", secPw));
    }

}
