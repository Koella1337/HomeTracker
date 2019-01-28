package at.hometracker.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

import at.hometracker.R;
import at.hometracker.database.DatabaseMethod;
import at.hometracker.database.DatabaseTask;
import at.hometracker.database.datamodel.User;
import at.hometracker.shared.Constants;
import at.hometracker.utils.PasswordUtils;
import at.hometracker.utils.SecurePassword;
import at.hometracker.utils.Utils;

public class DatabaseActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        this.textView = findViewById(R.id.databaseTextView);
    }

    public void readFromDatabase(View view) {
        new DatabaseTask(this, DatabaseMethod.SELECT_ALL_USERS, (task, result) -> {
            StringBuilder textBuilder = new StringBuilder();
            for (String row : result.split(Constants.PHP_ROW_SPLITTER)){
                User u = new User(row);
                textBuilder.append(String.format("%d: %s (%s)%n", u.user_id, u.name, u.e_mail));
            }
            textView.setText(textBuilder.toString());
        }).execute();
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
                        DatabaseTask dbTask = new DatabaseTask(this, DatabaseMethod.INSERT_USER, null);

                        dbTask.execute("test@mail.com", "testUser", secPw.hashedPw, secPw.salt, fileStream);
                    }
                    else {
                        Log.i("dbResult", "File too large for upload!");
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
