package at.hometracker.activities;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import at.hometracker.R;
import at.hometracker.database.DatabaseMethod;
import at.hometracker.database.DatabaseTask;
import at.hometracker.database.datamodel.Drawer;
import at.hometracker.shared.Constants;

public class CreateDrawerActivity extends AppCompatActivity {

    private static double MAP_SIZE_CONSTANT_X;
    private static double MAP_SIZE_CONSTANT_Y;

    private static final int RASTER_SIZE = 50;

    private List<DrawableRect> drawableRectList = new ArrayList<>();
    private DrawerImageView mapView;
    private Button newDrawerButton;
    private Button okButton;
    private Button cancelbutton;
    private DrawableRect created;

    private int shelf_id;

    private int mapWidth;
    private int mapHeight;

    byte[] backgroundImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);


        shelf_id = getIntent().getIntExtra(Constants.INTENT_EXTRA_SHELF_ID, -1);

        backgroundImage = getIntent().getByteArrayExtra(Constants.INTENT_EXTRA_IMAGE);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int dw = displaymetrics.widthPixels;
        int dh = displaymetrics.heightPixels;
        Log.i("MapActivity", String.format("display size width: %s, height: %s", dw, dh));

        newDrawerButton = findViewById(R.id.button_map_create_drawer);
        okButton = findViewById(R.id.button_map_create_ok);
        cancelbutton = findViewById(R.id.button_map_create_cancel);
        displayNewDrawerButtonAndMakeOKandCancelInvisible();


        ConstraintLayout constraintLayout = findViewById(R.id.touchDrawLayout);

        Resources r = getResources();
        int buttonBarHeight = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                80,
                r.getDisplayMetrics()
        );

        mapWidth = dw;
        mapHeight = dh - buttonBarHeight;


        new DatabaseTask(this, DatabaseMethod.SELECT_DRAWERS_FOR_SHELF, (task, result) -> {
            if (result == null || result.isEmpty()) {
                return;
            }

            List<Drawer> drawerList = new ArrayList<>();

            String[] results = result.split(Constants.PHP_ROW_SPLITTER);
            for (String res : results) {
                drawerList.add(new Drawer(res));
            }
            initDrawableRectListWithDrawers(drawerList, mapWidth, mapHeight);
        }).execute(shelf_id);


        mapView = new DrawerImageView(this, backgroundImage, mapWidth, mapHeight);
        constraintLayout.addView(mapView);
        mapView.updateCanvas();
        mapView.invalidate();
    }

    public void initDrawableRectListWithDrawers(List<Drawer> drawerList, int mapWidth, int mapHeight) {
        Log.i("MapActivity", "initDrawableRectListWithDrawers drawerList.size: " + drawerList.size());
        for (Drawer s : drawerList) {
            if (s.sizeX != 0 && s.sizeY != 0) {
                Rect convertedRect = convertRelativeRectToDrawableRect(new Rect(s.posX, s.posY, s.posX + s.sizeX, s.posY + s.sizeY));
                DrawableRect rect = new DrawableRect(convertedRect.left, convertedRect.top, convertedRect.right, convertedRect.bottom);
                rect.setName("");
                drawableRectList.add(rect);
            }
        }
        mapView.refresh();
    }

    public Rect convertDrawableRectToRelativeRect(Rect drawableRect) {
       // Log.i("convert drawable to relative first ",drawableRect);
        int canvasWidth = this.mapView.canvas.getWidth();
        int canvasHeight = this.mapView.canvas.getHeight();
        int newX = (int) ((drawableRect.left / (double) canvasWidth) * MAP_SIZE_CONSTANT_X);
        int newX2 = (int) ((drawableRect.right / (double) canvasWidth) * MAP_SIZE_CONSTANT_X);
        int newY = (int) ((drawableRect.top / (double) canvasHeight) * MAP_SIZE_CONSTANT_Y);
        int newY2 = (int) ((drawableRect.bottom / (double) canvasHeight) * MAP_SIZE_CONSTANT_Y);
        Rect after = new Rect(newX, newY, newX2, newY2);
      //  Log.i("convert drawable to relative ",after);
        return after;
    }

    public Rect convertRelativeRectToDrawableRect(Rect drawableRect) {
        int canvasWidth = this.mapView.canvas.getWidth();
        int canvasHeight = this.mapView.canvas.getHeight();
        int newX = (int) ((drawableRect.left / MAP_SIZE_CONSTANT_X) * canvasWidth);
        int newX2 = (int) ((drawableRect.right / MAP_SIZE_CONSTANT_X) * canvasWidth);
        int newY = (int) ((drawableRect.top / MAP_SIZE_CONSTANT_Y) * canvasHeight);
        int newY2 = (int) ((drawableRect.bottom / MAP_SIZE_CONSTANT_Y) * canvasHeight);
        Rect after = new Rect(newX, newY, newX2, newY2);
        return after;
    }

    public void saveDrawerCreation(View view) {
        for (DrawableRect drawableRect : drawableRectList) {

            Rect convertedDrawerRect = convertDrawableRectToRelativeRect(drawableRect.rect);
            new DatabaseTask(this, DatabaseMethod.INSERT_DRAWER, (task, result) -> {
                Log.i("DatabaseTask", "inserted drawer");
            }).execute("testdescription", shelf_id, convertedDrawerRect.left, convertedDrawerRect.top, convertedDrawerRect.right - convertedDrawerRect.left, convertedDrawerRect.bottom - convertedDrawerRect.top);
        }
        finish();
    }


    public void startDrawerDraw(View view) {
        displayOKandCancelButtonAndMakeNewDrawerButtonInvisible();
        this.mapView.startShelfDraw();
    }

    public void finishDrawerDraw(View view) {
        displayNewDrawerButtonAndMakeOKandCancelInvisible();
        created = this.mapView.finishDrawerDraw();
        mapView.refresh();
    }


    public void cancelDrawerDraw(View view) {
        this.newDrawerButton.setVisibility(View.VISIBLE);
        this.okButton.setVisibility(View.INVISIBLE);
        this.cancelbutton.setVisibility(View.INVISIBLE);
        this.mapView.cancelDrawerDraw();
    }

    private void displayNewDrawerButtonAndMakeOKandCancelInvisible() {
        this.newDrawerButton.setVisibility(View.VISIBLE);
        this.okButton.setVisibility(View.INVISIBLE);
        this.cancelbutton.setVisibility(View.INVISIBLE);
    }

    private void displayOKandCancelButtonAndMakeNewDrawerButtonInvisible() {
        this.newDrawerButton.setVisibility(View.INVISIBLE);
        this.okButton.setVisibility(View.VISIBLE);
        this.cancelbutton.setVisibility(View.VISIBLE);
    }

    private class DrawableRect {
        private byte[] imageData;
        private Rect rect;
        private String name;
        private Paint paint;

        public DrawableRect(int x, int y, int x2, int y2) {
            this.rect = createRectForPoints(x, y, x2, y2);
            this.name = name;

            this.paint = new Paint();
            this.paint.setStyle(Paint.Style.FILL_AND_STROKE);
            this.paint.setColor(Color.argb(150, 150, 150, 150));
            this.paint.setStrokeWidth(3);
            this.paint.setTextSize(50);
        }

        public boolean intersects(int x, int y, int x2, int y2) {
            return Rect.intersects(this.rect, createRectForPoints(x, y, x2, y2));
        }

        private Rect createRectForPoints(int x, int y, int x2, int y2) {
            int topleftX = round(Math.min(x, x2));
            int topleftY = round(Math.min(y, y2));
            int bottomRightX = round(Math.max(x, x2));
            int bottomRightY = round(Math.max(y, y2));
            return new Rect(topleftX, topleftY, bottomRightX, bottomRightY);
        }

        public String getName() {
            return name == null ? "" : name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public byte[] getImageData() {
            return imageData;
        }

        public void setImageData(byte[] imageData) {
            this.imageData = imageData;
        }

        public int getSizeX() {
            return this.rect.right - this.rect.left;
        }

        public int getSizeY() {
            return this.rect.bottom - this.rect.top;
        }

        public Paint getPaint() {
            return paint;
        }

        public void setPaint(Paint paint) {
            this.paint = paint;
        }
    }


    private class DrawerImageView extends AppCompatImageView implements OnTouchListener {
        int downx = 0, downy = 0, upx = 0, upy = 0;
        private Canvas canvas;
        private Paint paint;

        private boolean currentlyCreating = false;
        private Bitmap drawableBitmap;
        private Bitmap backgroundBitmap;

        public DrawerImageView(Context context, byte[] image, int width, int height) {
            super(context);

            drawableBitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            drawableBitmap = Bitmap.createScaledBitmap(drawableBitmap, width, height, false);

            backgroundBitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            backgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, width, height, false);

            canvas = new Canvas(drawableBitmap);
            paint = new Paint();
            paint.setColor(Color.GRAY);

            this.setImageBitmap(drawableBitmap);
            this.setOnTouchListener(this);
        }

        public void refresh() {
            this.updateCanvas();
            this.invalidate();
        }

        public void startShelfDraw() {
            this.currentlyCreating = true;
        }

        public DrawableRect finishDrawerDraw() {
            this.currentlyCreating = false;

            DrawableRect created = new DrawableRect(downx, downy, upx, upy);
            drawableRectList.add(created);
            refresh();
            return created;
        }

        public void cancelDrawerDraw() {
            this.currentlyCreating = false;
            refresh();
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //Log.i("onTouch", "onTouch");
            int action = event.getAction();
            handleSingleTouch(event, action);
            return true;
        }

        private void updateCanvas() {
            //Log.i("method called", "updateCanvas");

            canvas.drawColor(Color.LTGRAY);
            canvas.drawBitmap(backgroundBitmap, 0, 0, null);

            for (DrawableRect d : drawableRectList) {
                canvas.drawRect(d.rect.left, d.rect.top, d.rect.right, d.rect.bottom, d.paint);

                Paint textPaint = new Paint();
                textPaint.setStyle(Paint.Style.FILL);
                textPaint.setColor(Color.DKGRAY);
                textPaint.setStrokeWidth(3);
                textPaint.setTextSize(35);

                canvas.drawText(d.getName(), d.rect.left + 10, (d.rect.top + d.rect.bottom) / 2, textPaint);
            }
        }

        private void handleSingleTouch(MotionEvent event, int action) {
            if (currentlyCreating) {
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        Log.i("MapActivity", "TOUCH ACTION_DOWN..........");
                        downx = round((int) event.getX());
                        downy = round((int) event.getY());
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //Log.i("touch action", "ACTION_MOVE");
                        updateCanvas(); //remove previous drawing
                        paint.setStyle(Paint.Style.FILL_AND_STROKE);

                        int mx = round((int) event.getX());
                        int my = round((int) event.getY());

                        if (isAlreadyCovered(downx, downy, mx, my)) {
                            paint.setColor(Color.RED);
                        } else {
                            paint.setColor(Color.GREEN);
                        }

                        canvas.drawRect(downx, downy, mx, my, paint);
                        invalidate();
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.i("MapActivity", "TOUCH ACTION_UP..........");

                        upx = round((int) event.getX());
                        upy = round((int) event.getY());

                        if (isAlreadyCovered(downx, downy, upx, upy)) {
                            CreateDrawerActivity.this.cancelDrawerDraw(this);
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        break;
                    default:
                        break;
                }
            }
        }


        private boolean isAlreadyCovered(int x, int y, int x2, int y2) {
            boolean isAlreadyCoverd = false;
            for (DrawableRect rect : drawableRectList) {
                if (rect.intersects(x, y, x2, y2)) {
                    isAlreadyCoverd = true;
                    break;
                }
            }
            if (isAlreadyCoverd) {
                paint.setColor(Color.RED);
            } else {
                paint.setColor(Color.GREEN);
            }
            return isAlreadyCoverd;
        }
    }


    private int round(int n) {
        return (int) (RASTER_SIZE * (Math.round(n / RASTER_SIZE)));
    }

}


