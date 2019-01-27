package at.hometracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TouchDrawActivity extends AppCompatActivity implements OnTouchListener {

    int offsetX = 300, offsetY = 200;

    ImageView imageView;
    Bitmap bitmap;
    Canvas canvas;
    Paint paint;
    int downx = 0, downy = 0, upx = 0, upy = 0;

    private List<DrawableRect> drawableRectList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch_draw);

        imageView = (ImageView) this.findViewById(R.id.imageView1);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int dw = displaymetrics.widthPixels;
        int dh = displaymetrics.heightPixels;
        Log.i("display size", String.format("width: %s, height: %s", dw, dh));

        bitmap = Bitmap.createBitmap((int) dw, (int) dh,
                Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        paint.setColor(Color.GRAY);
        imageView.setImageBitmap(bitmap);

        imageView.setOnTouchListener(this);

        drawableRectList.add(new DrawableRect(500, 600, 600, 710));

        updateCanvas();
    }


    private void updateCanvas() {
        Log.v("method called", "updateCanvas");
        canvas.drawColor(Color.LTGRAY);

        canvas.translate(-offsetX, -offsetY);
        for (DrawableRect d : drawableRectList) {
            canvas.drawRect(d.x, d.y, d.x2, d.y2, paint);
        }
        canvas.translate(offsetX, offsetY);
    }

    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getActionMasked();

        boolean multiTouch = (event.getPointerCount() > 1);

        if (multiTouch) {
            handleMultiTouch(event,action);
        } else {
            handleSingleTouch(event, action);
        }

        return true;
    }


    Integer lastX, lastY;
    boolean multitouch = false;

    private void handleMultiTouch(MotionEvent event, int action) {
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                multitouch = true;
                //Log.i("touch action", "ACTION_DOWN");
                lastX = (int) event.getX();
                lastY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                multitouch = true;
                Log.i("touch action", "ACTION_MOVE "+lastX);

                if(lastX != null && event.getX() > lastX){
                    offsetX-=20;
                }else if(lastX != null && event.getX() < lastX){
                    offsetX+=20;
                }
                if(lastY != null && event.getY() > lastY){
                    offsetY-=20;
                }else if(lastY != null && event.getY() < lastY){
                    offsetY+=20;
                }

                lastX = (int) event.getX();
                lastY = (int) event.getY();
                updateCanvas();
                imageView.invalidate();
                break;
        }
    }

    private void handleSingleTouch(MotionEvent event, int action) {
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.i("touch action", "ACTION_DOWN");
                downx = (int) event.getX();
                downy = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                updateCanvas();
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                paint.setColor(Color.BLUE);
                canvas.drawRect(downx, downy, event.getX(), event.getY(), paint);
                imageView.invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if(!multitouch){
                    Log.i("touch action", "ACTION_UP");
                    upx = (int) event.getX();
                    upy = (int) event.getY();
                    drawableRectList.add(new DrawableRect(downx + offsetX, downy + offsetY, upx + offsetX, upy + offsetY));
                    updateCanvas();
                    imageView.invalidate();
                }else{
                    multitouch = false;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }
    }

    private class DrawableRect {
        int x, y, x2, y2;

        public DrawableRect(int x, int y, int x2, int y2) {
            this.x = x;
            this.y = y;
            this.x2 = x2;
            this.y2 = y2;
        }
    }
}


