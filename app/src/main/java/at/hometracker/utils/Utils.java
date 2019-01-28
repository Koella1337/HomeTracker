package at.hometracker.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.EditText;
import android.widget.Toast;

import at.hometracker.R;

import static android.content.Context.MODE_PRIVATE;
import static android.support.v4.app.ActivityCompat.startActivityForResult;
import static at.hometracker.shared.Constants.*;

public class Utils {

    public static boolean validateEditTexts(AppCompatActivity originActivity, EditText... textfields) {
        for (EditText txt : textfields) {
            if (txt.getInputType() == (InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS | InputType.TYPE_CLASS_TEXT)) {
                //e-mail edittext: use email validation function
                if (validateEmailEditTexts(originActivity, txt))
                    continue;
                else
                    return false;
            }

            String str = txt.getText().toString();
            if (str.length() < 4 || str.length() > 64 || !str.matches("[a-zA-Z_0-9@ \\-]*")) {
                Toast.makeText(originActivity, R.string.toast_edittext_invalid, Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    public static boolean validateEmailEditTexts(AppCompatActivity originActivity, EditText... textfields) {
        for (EditText txt : textfields) {
            String str = txt.getText().toString();
            if (str.length() < 4 || str.length() > 64 || !Patterns.EMAIL_ADDRESS.matcher(str).matches()) {
                Toast.makeText(originActivity, R.string.toast_email_invalid, Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    public static void pickImageFromGallery(AppCompatActivity originActivity) {
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

    /**
     * Buids and returns an AlertDialog with the specified parameters.
     */
    public static AlertDialog buildAlertDialog(AppCompatActivity originActivity, int resource) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(originActivity);

        LayoutInflater inflater = originActivity.getLayoutInflater();
        View dialogView = inflater.inflate(resource, null);
        dialogView.setPadding(20, 10, 20, 10);
        dialogBuilder.setView(dialogView);

        return dialogBuilder.create();
    }

    /**
     * Sets positive and/or negative buttons for the specified dialog.
     * If posBtnText and/or posBtnAction are "null" then no positive button will be set for the dialogue.<br>
     * Analogous for the negative button.
     */
    public static void setAlertDialogButtons(AlertDialog dialog, String posBtnText, OnClickListener posBtnAction, String negBtnText, OnClickListener negBtnAction) {
        if (!(posBtnText == null || posBtnAction == null))
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, posBtnText, posBtnAction);
        if (!(negBtnText == null || negBtnAction == null))
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, negBtnText, negBtnAction);
    }

    /**
     * Buids and returns an AlertDialog with the specified parameters.<br>
     * If posBtnText and/or posBtnAction are "null" then no positive button will be set for the dialogue.<br>
     * Analogous for the negative button.
     */
    public static AlertDialog buildAlertDialog(AppCompatActivity originActivity, int resource, String posBtnText, OnClickListener posBtnAction, String negBtnText, OnClickListener negBtnAction) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(originActivity);

        LayoutInflater inflater = originActivity.getLayoutInflater();
        View dialogView = inflater.inflate(resource, null);
        dialogView.setPadding(20, 10, 20, 10);
        dialogBuilder.setView(dialogView);

        if (!(posBtnText == null || posBtnAction == null))
            dialogBuilder.setPositiveButton(posBtnText, posBtnAction);
        if (!(negBtnText == null || negBtnAction == null))
            dialogBuilder.setNegativeButton(negBtnText, negBtnAction);

        return dialogBuilder.create();
    }

}
