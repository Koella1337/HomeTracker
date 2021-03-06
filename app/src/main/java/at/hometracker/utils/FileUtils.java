package at.hometracker.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    /**
     * sets the image of the ImageView to the image which is stored in the assets folder under the given filename
     */
    public static void setImageForImageView(ImageView imageView, Activity activity,String filename) throws IOException {
        setImageViewWithByteArray(imageView, getByteArrayForFile(activity,filename));
    }

    /**
     * sets the image of the ImageView to the image represented by the InputStream
     */
    public static void setImageForImageView(ImageView imageView, Activity activity, InputStream in) {
        setImageViewWithByteArray(imageView, toByteArray(in));
    }

    public static void setImageViewWithByteArray(ImageView imageView, byte[] data) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        imageView.setImageBitmap(bitmap);
    }

    public static byte[] getByteArrayForFile(Activity activity, String filename) throws IOException {
        return toByteArray(getInputStream(activity,filename));
    }

    private static InputStream getInputStream(Activity activity, String filename) throws IOException {
       return  activity.getAssets().open(filename);
    }

    public static byte[] toByteArray(InputStream in) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int len;
            // read bytes from the input stream and store them in buffer
            while ((len = in.read(buffer)) != -1) {
                // write bytes from the buffer into output stream
                os.write(buffer, 0, len);
            }
            return os.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

}
