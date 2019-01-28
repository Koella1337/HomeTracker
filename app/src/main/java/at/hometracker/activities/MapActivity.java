package at.hometracker.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.camera2.params.MeteringRectangle;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.hometracker.R;
import at.hometracker.database.DatabaseMethod;
import at.hometracker.database.DatabaseTask;
import at.hometracker.database.datamodel.Shelf;
import at.hometracker.shared.Constants;
import at.hometracker.utils.CameraUtils;
import at.hometracker.utils.FileUtils;
import at.hometracker.utils.Utils;

public class MapActivity extends AppCompatActivity {

    private static final int RASTER_SIZE = 50;


    private List<DrawableRect> drawableRectList = new ArrayList<>();
    private CustomImageView mapView;

    private Button newShelfButton;
    private Button okButton;
    private Button cancelbutton;

    private DrawableRect created;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int dw = displaymetrics.widthPixels;
        int dh = displaymetrics.heightPixels;
        Log.i("MapActivity", String.format("display size width: %s, height: %s", dw, dh));

        newShelfButton = findViewById(R.id.button_map_create_shelf);
        okButton = findViewById(R.id.button_map_create_ok);
        cancelbutton = findViewById(R.id.button_map_create_cancel);
        displayNewShelfButtonAndMakeOKandCancelInvisible();

        new DatabaseTask(this, DatabaseMethod.SELECT_SHELVES_FOR_GROUP, (task, result) -> {
            if (result == null || result.isEmpty()) {
                return;
            }

            String[] results = result.split(Constants.PHP_ROW_SPLITTER);
            for (String res : results) {
                new Shelf(res);
            }
        }).execute(3213);


        ConstraintLayout constraintLayout = findViewById(R.id.touchDrawLayout);

        mapView = new CustomImageView(this, dw - 100, dh - 400);

        setMarginsForMap();
        constraintLayout.addView(mapView);
        mapView.updateCanvas();
        mapView.invalidate();
    }


    public void initDrawableRectListWithShelfs(List<Shelf> shelfList) {
        for (Shelf s : shelfList) {
            DrawableRect rect = new DrawableRect(s.posX, s.posY, s.posX + s.sizeX, s.posY + s.sizeY);
            rect.setName(s.name);
        }
    }

    private void setMarginsForMap() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(50, 50, 50, 50);
        mapView.setLayoutParams(layoutParams);
    }

    public void startShelfDraw(View view) {
        displayOKandCancelButtonAndMakeNewShelfButtonInvisible();
        this.mapView.startShelfDraw();
    }

    public void finishShelfDraw(View view) {
        displayNewShelfButtonAndMakeOKandCancelInvisible();
        AlertDialog alertDialog = Utils.buildAlertDialog(this, R.layout.dialog_create_shelf);
        Utils.setAlertDialogButtons(alertDialog,
                getString(R.string.label_create_shelf), (dialog, id) -> createShelf(view, alertDialog),
                getString(R.string.label_cancel), (dialog, id) -> cancelShelfDraw(view)
        );
        alertDialog.setCancelable(false);
        alertDialog.show();
    }


    public void createShelf(View view, AlertDialog shelfCreationDialog) {
        Log.i("MapActivity", "createShelf");

        EditText textShelfName = shelfCreationDialog.findViewById(R.id.shelf_name_edittext);
        if (!Utils.validateEditTexts(this, textShelfName)) {
            cancelShelfDraw(view);
            return;
        }

        CameraUtils.requestPicture(this);

        created = this.mapView.finishShelfDraw();
        String shelfName = textShelfName.getText().toString();
        created.setName(shelfName);

        mapView.refresh();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUESTCODE_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            byte[] imageData = CameraUtils.getPictureAsByteArray(this);     //TODO: OMAX FIX THIS !!!
            Log.i("onActivityResult ok", "requestCode " + requestCode + " data length: " + imageData.length);

            created.setImageData(imageData);
            created = null;
        } else {
            Log.i("onActivityResult not ok", "requestCode " + requestCode);
        }

        mapView.refresh();
    }

    public void cancelShelfDraw(View view) {
        this.newShelfButton.setVisibility(View.VISIBLE);
        this.okButton.setVisibility(View.INVISIBLE);
        this.cancelbutton.setVisibility(View.INVISIBLE);
        this.mapView.cancelShelfDraw();
    }

    private void displayNewShelfButtonAndMakeOKandCancelInvisible() {
        this.newShelfButton.setVisibility(View.VISIBLE);
        this.okButton.setVisibility(View.INVISIBLE);
        this.cancelbutton.setVisibility(View.INVISIBLE);
    }

    private void displayOKandCancelButtonAndMakeNewShelfButtonInvisible() {
        this.newShelfButton.setVisibility(View.INVISIBLE);
        this.okButton.setVisibility(View.VISIBLE);
        this.cancelbutton.setVisibility(View.VISIBLE);
    }

    private class DrawableRect {
        private byte[] imageData;
        private Rect rect;
        private String name;

        public DrawableRect(int x, int y, int x2, int y2) {
            this.rect = createRectForPoints(x, y, x2, y2);
            this.name = name;
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
    }


    private class CustomImageView extends AppCompatImageView implements OnTouchListener {
        int downx = 0, downy = 0, upx = 0, upy = 0;
        private Canvas canvas;
        private Paint paint;

        private boolean currentlyCreating = false;

        public CustomImageView(Context context, int width, int height) {
            super(context);
            Bitmap bitmap = Bitmap.createBitmap((int) width, (int) height,
                    Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bitmap);
            paint = new Paint();
            paint.setColor(Color.GRAY);
            this.setImageBitmap(bitmap);
            this.setOnTouchListener(this);
        }

        public void refresh() {
            this.updateCanvas();
            this.invalidate();
        }

        public void startShelfDraw() {
            this.currentlyCreating = true;
        }

        public DrawableRect finishShelfDraw() {
            this.currentlyCreating = false;

            DrawableRect created = new DrawableRect(downx, downy, upx, upy);
            drawableRectList.add(created);
            refresh();
            return created;
        }

        public void cancelShelfDraw() {
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

            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.DKGRAY);
            paint.setStrokeWidth(3);
            paint.setTextSize(50);

            for (DrawableRect d : drawableRectList) {
                canvas.drawRect(d.rect.left, d.rect.top, d.rect.right, d.rect.bottom, paint);

                Paint textPaint = new Paint();
                textPaint.setStyle(Paint.Style.FILL);
                textPaint.setColor(Color.DKGRAY);
                textPaint.setStrokeWidth(3);
                textPaint.setTextSize(50);

                canvas.drawText(d.getName(), d.rect.left + 50, (d.rect.top + d.rect.bottom) / 2, textPaint);

                if (d.imageData != null) { // TODO remove, used for debugging
                    Bitmap bitmap = BitmapFactory.decodeByteArray(d.imageData, 0, d.imageData.length);
                    canvas.drawBitmap(bitmap, d.rect.left, d.rect.top, null);
                }

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

                        boolean isAlreadyCoverd = false;
                        for (DrawableRect rect : drawableRectList) {
                            if (rect.intersects(downx, downy, mx, my)) {
                                isAlreadyCoverd = true;
                                break;
                            }
                        }
                        if (isAlreadyCoverd) {
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
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private int round(int n) {
        return (int) (RASTER_SIZE * (Math.round(n / RASTER_SIZE)));
    }

}


