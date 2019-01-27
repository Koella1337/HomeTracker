package at.hometracker.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.renderscript.ScriptGroup;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static at.hometracker.app.Constants.REQUESTCODE_IMAGE_CAPTURE;

public class CameraUtil {

    public static void requestPicture(Activity sourceActivity) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(sourceActivity.getPackageManager()) != null) {
            sourceActivity.startActivityForResult(takePictureIntent, REQUESTCODE_IMAGE_CAPTURE);
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
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }




}
