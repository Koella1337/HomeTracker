package at.hometracker.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;
import static android.support.v4.app.ActivityCompat.startActivityForResult;
import static at.hometracker.shared.Constants.*;

public class Utils {

    public static void pickImageFromGallery(Activity originActivity) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent = Intent.createChooser(intent, "Select Picture");
        startActivityForResult(originActivity, intent, REQUESTCODE_PICK_IMAGE, null);
    }

    public static void setLoginPreferences(Context appContext, String email, String pw) {
        SharedPreferences.Editor editor = appContext.getSharedPreferences(PREFERENCES_FILE_NAME, MODE_PRIVATE).edit();
        editor.putString(PREFNAME_EMAIL, email);
        editor.putString(PREFNAME_PASSWORD, pw);
        editor.apply();
    }

}
