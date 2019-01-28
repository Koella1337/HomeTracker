package at.hometracker.activities;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatImageView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import java.util.ArrayList;
import java.util.List;

import at.hometracker.R;

public class MapActivity extends AppCompatActivity {





    private List<DrawableRect> drawableRectList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        ConstraintLayout constraintLayout = findViewById(R.id.touchDrawLayout);

        CustomImageView view = new CustomImageView(this);
        view.setBackgroundColor(Color.CYAN);

        constraintLayout.addView(view);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int dw = displaymetrics.widthPixels;
        int dh = displaymetrics.heightPixels;
        Log.i("display size", String.format("width: %s, height: %s", dw, dh));
        drawableRectList.add(new DrawableRect(500, 600, 600, 710));

    }

public void startShelfDraw(View view){

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

    private class CustomImageView extends AppCompatImageView implements OnTouchListener {
        Paint paint;

        int downx = 0, downy = 0, upx = 0, upy = 0;

        private Canvas canvas;

        public CustomImageView(Context context) {
            super(context);
            Bitmap bitmap = Bitmap.createBitmap((int) 1000, (int) 1500,
                    Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bitmap);
            paint = new Paint();
            paint.setColor(Color.GRAY);
            this.setImageBitmap(bitmap);
            this.setOnTouchListener(this);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
        }



        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.i("onTouch", "onTouch");
            int action = event.getAction();
            handleSingleTouch(event, action);
            return true;
        }

        private void updateCanvas() {
            Log.v("method called", "updateCanvas");
            canvas.drawColor(Color.LTGRAY);


            for (DrawableRect d : drawableRectList) {
                canvas.drawRect(d.x, d.y, d.x2, d.y2, paint);
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
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:

                    Log.i("touch action", "ACTION_UP");
                    upx = (int) event.getX();
                    upy = (int) event.getY();
                    drawableRectList.add(new DrawableRect(downx, downy ,upx, upy));
                    updateCanvas();
                    invalidate();

                    break;
                case MotionEvent.ACTION_CANCEL:
                    break;
                default:
                    break;
            }
        }
    }
}


