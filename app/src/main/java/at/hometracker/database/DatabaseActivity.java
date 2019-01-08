package at.hometracker.database;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

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
        new DatabaseTask(DatabaseMethod.SELECT_USERS, this.textView).execute();
    }

    public void insertIntoDatabase(View view) throws IOException {
        SecurePassword secPw = PasswordUtils.generateSecurePassword("testPW");

        DatabaseTask dbTask = new DatabaseTask(DatabaseMethod.INSERT_USER, null);
        dbTask.execute("test@mail.com", "1000", "testUser", secPw.hashedPw, secPw.salt, getAssets().open("grimma.png"));
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
