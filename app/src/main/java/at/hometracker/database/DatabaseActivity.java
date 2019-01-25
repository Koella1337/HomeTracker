package at.hometracker.database;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

import at.hometracker.R;
import at.hometracker.shared.Constants;
import at.hometracker.shared.PasswordUtils;
import at.hometracker.shared.SecurePassword;
import at.hometracker.shared.Utils;

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

    public void insertIntoDatabase(View view) {
        Utils.pickImageFromGallery(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            System.out.println("Non-OK resultCode! (" + resultCode + ")");
            return;
        }

        switch (requestCode) {
            case Constants.REQUESTCODE_PICK_IMAGE:
                try {
                    InputStream fileStream = getContentResolver().openInputStream(data.getData());
                    if (fileStream.available() < Constants.FILE_MAX_SIZE) {
                        SecurePassword secPw = PasswordUtils.generateSecurePassword("testPW");
                        DatabaseTask dbTask = new DatabaseTask(DatabaseMethod.INSERT_USER, null);

                        dbTask.execute("test@mail.com", "1000", "testUser", secPw.hashedPw, secPw.salt, fileStream);
                    }
                    else {
                        Log.e("db", "File too large for upload!");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                System.out.println("Unrecognized requestcode: " + requestCode);
                break;
        }
    }
}
