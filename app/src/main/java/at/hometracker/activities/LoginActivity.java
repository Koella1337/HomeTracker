package at.hometracker.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import at.hometracker.R;
import at.hometracker.database.DatabaseMethod;
import at.hometracker.database.DatabaseTask;
import at.hometracker.database.datamodel.User;
import at.hometracker.shared.Constants;
import at.hometracker.utils.PasswordUtils;
import at.hometracker.utils.SecurePassword;
import at.hometracker.utils.Utils;

public class LoginActivity extends AppCompatActivity {

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
        if (intent.hasExtra(Constants.INTENT_EXTRA_EMAIL) && intent.hasExtra(Constants.INTENT_EXTRA_PASSWORD)){
            //extras are found after user has logged out inside of the app
            textfield_email.setText(intent.getStringExtra(Constants.INTENT_EXTRA_EMAIL));
            textfield_password.setText(intent.getStringExtra(Constants.INTENT_EXTRA_PASSWORD));
        } else {
            //no extras = app was freshly started --> check for preferences
            SharedPreferences prefs = getSharedPreferences(Constants.PREFERENCES_FILE_NAME, MODE_PRIVATE);
            String email = prefs.getString(Constants.PREFNAME_EMAIL, null);
            String password = prefs.getString(Constants.PREFNAME_PASSWORD, null);

            if (email != null && password != null) {
                textfield_email.setText(email);
                textfield_password.setText(password);
                new DatabaseTask(this, DatabaseMethod.SELECT_USER, (task, result) -> login(new User(result))).execute(email);
            }
        }
    }

    private void setOnClickListeners() {
        findViewById(R.id.btn_forgotPw).setOnClickListener(view -> {
            //TODO
        });

        AlertDialog registerDialog = Utils.buildAlertDialog(this, R.layout.dialog_register);
        Utils.setAlertDialogButtons(registerDialog,
                getString(R.string.label_register), (dialog, id) -> {
                    EditText textEmail = registerDialog.findViewById(R.id.textfield_email);
                    EditText textUsername = registerDialog.findViewById(R.id.textfield_username);
                    EditText textPassword = registerDialog.findViewById(R.id.textfield_password);

                    if (Utils.validateEditTexts(this, textEmail, textUsername, textPassword))
                        register(textEmail.getText().toString(), textUsername.getText().toString(), textPassword.getText().toString());
                },
                getString(R.string.label_cancel), (dialog, id) -> Log.i("login", "User cancelled registration.")
        );

        findViewById(R.id.btn_register).setOnClickListener(view -> {
            registerDialog.show();

            EditText textEmail = registerDialog.findViewById(R.id.textfield_email);
            EditText textpassword = registerDialog.findViewById(R.id.textfield_password);

            textEmail.setText(LoginActivity.this.textfield_email.getText());
            textpassword.setText(LoginActivity.this.textfield_password.getText());
        });

        findViewById(R.id.btn_login).setOnClickListener(view -> {
            String email = textfield_email.getText().toString();
            if (Utils.validateEmailEditTexts(this, textfield_email))
                new DatabaseTask(this, DatabaseMethod.SELECT_USER, (task, result) -> login(new User(result))).execute(email);
        });
    }

    private void login(User user) {
        if (!Utils.validateEditTexts(this, textfield_password))
            return;

        Log.i("login", "Logging in user: " + user);
        String providedPw = textfield_password.getText().toString();
        SecurePassword databasePw = PasswordUtils.fixPassword(new SecurePassword(user.password_salt, user.password));

        if (PasswordUtils.validatePassword(providedPw, databasePw)) {
            if (checkBox_keepSignedIn.isChecked())
               Utils.setLoginPreferences(getApplicationContext(), user.e_mail, providedPw);

            Intent groupSelectionIntent = new Intent(this, GroupSelectionActivity.class);
            groupSelectionIntent.putExtra(Constants.INTENT_EXTRA_USER_ID, user.user_id);
            startActivity(groupSelectionIntent);
            finish();   //finish this activity so user can't return here
        }
        else {
            Toast.makeText(this, R.string.toast_login_failed, Toast.LENGTH_LONG).show();
        }
    }

    private void register(String email, String username, String password) {
        SecurePassword secPw = PasswordUtils.generateSecurePassword(password);
        try {
            new DatabaseTask(this, DatabaseMethod.INSERT_USER, (task, result) -> {
                if (result == null || result.startsWith(Constants.PHP_ERROR_PREFIX)){
                    Toast.makeText(this, R.string.toast_registration_failed, Toast.LENGTH_LONG).show();
                }
                else {
                    textfield_email.setText(email);
                    textfield_password.setText(password);
                    Toast.makeText(this, R.string.toast_registration_success, Toast.LENGTH_LONG).show();
                }
            }).execute(email, username, secPw.hashedPw, secPw.salt, getAssets().open(Constants.DEFAULT_PROFILE_PICTURE_NAME));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void forgotPw(String email) {
        //TODO
    }

}
