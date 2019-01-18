package at.hometracker.shared;

import android.app.Activity;
import android.content.Intent;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

public class Utils {

    public static void pickImageFromGallery(Activity originActivity) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent = Intent.createChooser(intent, "Select Picture");
        startActivityForResult(originActivity, intent, Constants.REQUESTCODE_PICK_IMAGE, null);
    }

}
