package at.hometracker.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
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

    public static InputStream getPictureAsInputStream(Activity sourceActivity) {
        return new ByteArrayInputStream(getPictureAsByteArray(sourceActivity));
    }

    public static byte[] getPictureAsByteArray(Activity sourceActivity) {
        File file = new File(sourceActivity.getFilesDir() + "/hometracker_shelf_picture.png");
        Uri imageUri = HometrackerFileProvider.getUriForFile(sourceActivity, "com.at.hometracker.provider", file);

        try {
            Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(sourceActivity.getContentResolver(), imageUri);
            System.out.println("imageBitmap: " + imageBitmap.getByteCount());

            imageBitmap = rotateImageIfRequired(imageBitmap, Uri.fromFile(file));

            System.out.println("imageBitmap rotated: " + imageBitmap.getByteCount());
            return getByteArrayForBitmap(imageBitmap);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] getByteArrayForBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        if (bitmap.getByteCount() > Constants.FILE_MAX_SIZE / 5) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        } else {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        }
        return stream.toByteArray();
    }

    /**
     * Rotate an image if required.
     *
     * @param img           The image bitmap
     * @param selectedImage Image URI
     * @return The resulted Bitmap after manipulation
     */
    private static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {

        ExifInterface ei = new ExifInterface(selectedImage.getPath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

}
