package at.hometracker.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Arrays;

import at.hometracker.R;
import at.hometracker.database.DatabaseListener;
import at.hometracker.database.DatabaseMethod;
import at.hometracker.database.DatabaseTask;
import at.hometracker.database.datamodel.User;
import at.hometracker.shared.Constants;
import at.hometracker.utils.PasswordUtils;
import at.hometracker.utils.SecurePassword;
import at.hometracker.utils.Utils;

public class LoginActivity extends AppCompatActivity implements DatabaseListener {

    private EditText textfield_email, textfield_password;
    private CheckBox checkBox_keepSignedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textfield_email = findViewById(R.id.textfield_email);
        textfield_password = findViewById(R.id.textfield_password);
        checkBox_keepSignedIn = findViewById(R.id.checkbox_keepSignedIn);

        setOnClickListeners();

        Intent intent = getIntent();
        if (intent.hasExtra(Constants.INTENT_EXTRANAME_EMAIL) && intent.hasExtra(Constants.INTENT_EXTRANAME_PASSWORD)){
            //extras are found after user has logged out inside of the app
            textfield_email.setText(intent.getStringExtra(Constants.INTENT_EXTRANAME_EMAIL));
            textfield_password.setText(intent.getStringExtra(Constants.INTENT_EXTRANAME_PASSWORD));
        } else {
            //no extras = app was freshly started --> check for preferences
            SharedPreferences prefs = getSharedPreferences(Constants.PREFERENCES_FILE_NAME, MODE_PRIVATE);
            String email = prefs.getString(Constants.PREFNAME_EMAIL, null);
            String password = prefs.getString(Constants.PREFNAME_PASSWORD, null);

            if (email != null && password != null) {
                textfield_email.setText(email);
                textfield_password.setText(password);
                new DatabaseTask(this, DatabaseMethod.SELECT_USER, this).execute(email);
            }
        }
    }

    private void setOnClickListeners() {
        findViewById(R.id.btn_forgotPw).setOnClickListener(view -> {
            //TODO
        });

        findViewById(R.id.btn_register).setOnClickListener(view -> {
            //TODO
        });

        findViewById(R.id.btn_login).setOnClickListener(view -> {
            String email = textfield_email.getText().toString();
            new DatabaseTask(this, DatabaseMethod.SELECT_USER, this).execute(email);
        });
    }

    @Override
    public void receiveDatabaseResult(DatabaseMethod method, String result) {
        switch(method) {
            case SELECT_USER:
                login(new User(result));
                break;
            default:
                break;
        }
    }

    private void login(User user) {
        Log.i("login", "Logging in user: " + user);

        String providedPw = textfield_password.getText().toString();
        SecurePassword databasePw = PasswordUtils.fixPassword(new SecurePassword(user.password_salt, user.password));

        if (PasswordUtils.validatePassword(providedPw, databasePw)) {
            if (checkBox_keepSignedIn.isChecked())
               Utils.setLoginPreferences(getApplicationContext(), user.e_mail, providedPw);

            Intent groupSelectionIntent = new Intent(this, GroupSelectionActivity.class);
            groupSelectionIntent.putExtra(Constants.INTENT_EXTRANAME_USER_ID, user.user_id);
            startActivity(groupSelectionIntent);
            finish();   //finish this activity so user can't return here
        }
        else {
            Toast.makeText(this, R.string.toast_login_failed, Toast.LENGTH_LONG).show();
        }
    }

    private void forgotPw(String email) {
        //TODO
    }

    private void register(String email, String username, String password) {
        //TODO
    }


}
