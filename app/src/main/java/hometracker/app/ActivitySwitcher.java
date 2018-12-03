package hometracker.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Sallaberger on 03.12.2018.
 */

public class ActivitySwitcher {

    public static void openGroupActivity(Context current){
        current.startActivity(new Intent(current,GroupActivity.class));
    }

    public static void openItemListActivity(Context current){
        current.startActivity(new Intent(current,ItemListActivity.class));
    }

    public static void openScanActivity(Context current){
        current.startActivity(new Intent(current,FullscreenActivity.class));
    }
}
