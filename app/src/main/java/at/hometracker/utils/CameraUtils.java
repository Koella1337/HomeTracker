package at.hometracker.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import at.hometracker.shared.Constants;


public class CameraUtils {

    public static void requestPicture(Activity sourceActivity) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(sourceActivity.getPackageManager()) != null) {
            sourceActivity.startActivityForResult(takePictureIntent, Constants.REQUESTCODE_IMAGE_CAPTURE);
        }
    }

    public static InputStream getPictureAsInputStream(Intent data){
        return new ByteArrayInputStream(getPictureByteArrayFromCameraResponse(data));
    }

    public static byte[] getPictureByteArrayFromCameraResponse(Intent data) {
        Bundle extras = data.getExtras();
        Bitmap imageBitmap = (Bitmap) extras.get("data");
        return getByteArrayForBitmap(imageBitmap);
    }

    private static byte[] getByteArrayForBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if(bitmap.getByteCount() > Constants.FILE_MAX_SIZE/2){
            bitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth()*0.4),(int) (bitmap.getHeight()*0.4),false);
        }
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }




}
