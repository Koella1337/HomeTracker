package at.hometracker.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import at.hometracker.shared.Constants;
import at.hometracker.shared.HometrackerFileProvider;


public class CameraUtils {

    public static void requestPicture(Activity sourceActivity) {
        File file = new File(sourceActivity.getFilesDir() + "/hometracker_shelf_picture.png");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Uri imageUri = HometrackerFileProvider.getUriForFile(sourceActivity, "com.at.hometracker.provider", file);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        sourceActivity.startActivityForResult(intent, Constants.REQUESTCODE_IMAGE_CAPTURE);
    }

    public static InputStream getPictureAsInputStream(Activity sourceActivity){
        return new ByteArrayInputStream(getPictureAsByteArray(sourceActivity));
    }

    public static byte[] getPictureAsByteArray(Activity sourceActivity) {
        File file = new File(sourceActivity.getFilesDir() + "/hometracker_shelf_picture.png");
        Uri imageUri = HometrackerFileProvider.getUriForFile(sourceActivity, "com.at.hometracker.provider", file);

        try {
            Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(sourceActivity.getContentResolver(), imageUri);
            return getByteArrayForBitmap(imageBitmap);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] getByteArrayForBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (bitmap.getByteCount() > Constants.FILE_MAX_SIZE){
            bitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 0.2), (int) (bitmap.getHeight() * 0.2), false);
        }
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

}
