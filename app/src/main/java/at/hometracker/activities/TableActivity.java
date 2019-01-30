package at.hometracker.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import at.hometracker.R;
import at.hometracker.database.DatabaseMethod;
import at.hometracker.database.DatabaseTask;
import at.hometracker.database.datamodel.Drawer;
import at.hometracker.database.datamodel.Item;
import at.hometracker.database.datamodel.Keyword;
import at.hometracker.database.datamodel.Shelf;
import at.hometracker.qrcode.GeneratorActivity;
import at.hometracker.shared.Constants;
import at.hometracker.utils.Utils;

import static at.hometracker.shared.Constants.MAX_ITEM_AMOUNT;
import static at.hometracker.shared.Constants.MIN_ITEM_AMOUNT;
import static at.hometracker.shared.Constants.PHP_ERROR_PREFIX;
import static at.hometracker.shared.Constants.PHP_ROW_SPLITTER;

public class TableActivity extends AppCompatActivity {

    private static double MAP_SIZE_CONSTANT_X = 1000.0;
    private static double MAP_SIZE_CONSTANT_Y = 1000.0;

    private Shelf shelf;
    private TableLayout tableLayout;

    private List<Item> unfilteredItems = new ArrayList<>();
    private List<Item> filteredItems = new ArrayList<>();

    private List<Keyword> keywords = new ArrayList<>();
    private LinkedHashSet<Keyword> keywordColumns = new LinkedHashSet<>();    //keeps insertion order

    private String sortedAscendingBy = "";
    private String searchString = "";

    private LinearLayout linearlayoutTableDrawer;
    private CustomImageView drawerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        Toolbar myToolbar = findViewById(R.id.toolbar_table);
        setSupportActionBar(myToolbar);
        myToolbar.setTitle("TableActivity");

        shelf = (Shelf) getIntent().getSerializableExtra(Constants.INTENT_EXTRA_SHELF);
        if (shelf == null) throw new RuntimeException("Invalid id on TableActivity creation!");

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int dw = (int) (displaymetrics.widthPixels);
        int dh = displaymetrics.heightPixels;

        drawerView = new CustomImageView(this, shelf.picture, (int) dw, (int) 700);
        new DatabaseTask(this, DatabaseMethod.SELECT_DRAWERS_FOR_SHELF, (task, result) -> {
            if (result == null || result.isEmpty()) {
                return;
            }

            List<Drawer> drawerList = new ArrayList<>();

            String[] results = result.split(Constants.PHP_ROW_SPLITTER);
            for (String res : results) {
                drawerList.add(new Drawer(res));
            }
            initDrawableRectListWithDrawers(drawerList);
        }).execute(shelf.shelf_id);

        this.tableLayout = findViewById(R.id.table_shelf);
        this.linearlayoutTableDrawer = findViewById(R.id.linearlayout_table_drawer);
        this.linearlayoutTableDrawer.addView(drawerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.shelf_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_search:
                showSearchDialog();
                return true;
            case R.id.action_new_item:
                showAddItemDialog();
                return true;
            case R.id.action_generate_qr:
                String qrCodeString = "shelf_" + shelf.shelf_id;

                Intent qrGeneratorIntent = new Intent(this, GeneratorActivity.class);
                qrGeneratorIntent.putExtra(Constants.INTENT_EXTRA_QR_STRING, qrCodeString);
                startActivity(qrGeneratorIntent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSearchDialog() {
        AlertDialog dialog = Utils.buildAlertDialog(this, R.layout.dialog_one_edittext);
        Utils.setAlertDialogButtons(dialog,
                getString(R.string.label_search), (view, id) -> {
                    EditText textfield_search = dialog.findViewById(R.id.dialog_one_edittext_textfield);
                    searchString = textfield_search.getText().toString();
                    clearAndFillTableWithItems();
                },
                getString(R.string.label_cancel), (view, id) -> {
                    dialog.dismiss();
                }
        );
        dialog.setCancelable(false);
        dialog.show();

        TextView titleView = dialog.findViewById(R.id.dialog_one_edittext_title);
        titleView.setText(R.string.label_search);

        EditText textfield_search = dialog.findViewById(R.id.dialog_one_edittext_textfield);
        textfield_search.setHint("");
    }

    private void showAddItemDialog() {
        if (getSelectedDrawers().size() != 1) {
            Toast.makeText(this, R.string.toast_invalid_drawer_selection, Toast.LENGTH_SHORT).show();
            return;
        }
        int selectedDrawerID = getSelectedDrawers().get(0).drawer_id;

        AlertDialog dialog = Utils.buildAlertDialog(this, R.layout.dialog_create_item);
        Utils.setAlertDialogButtons(dialog,
                getString(R.string.label_create_item), (view, id) -> {
                    EditText textfield_itemname = dialog.findViewById(R.id.edittext_new_item_name);
                    EditText textfield_itemamount = dialog.findViewById(R.id.edittext_new_item_amount);

                    if (!Utils.validateEditTexts(this, textfield_itemname))
                        return;

                    String name = textfield_itemname.getText().toString();
                    int amount = readAmountEditText(textfield_itemamount, 0);

                    new DatabaseTask(this, DatabaseMethod.INSERT_ITEM, (task, result) -> {
                        if (result == null || result.isEmpty() || result.startsWith(PHP_ERROR_PREFIX))
                            Toast.makeText(this, R.string.toast_itemcreation_failed, Toast.LENGTH_SHORT).show();
                        else
                            clearAndFetchFromDatabase();
                    }).execute(name, amount, shelf.group_id, selectedDrawerID);
                },
                getString(R.string.label_cancel), (view, id) -> {
                    dialog.dismiss();
                }
        );
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        clearAndFetchFromDatabase();
    }

    private void clearAndFetchFromDatabase() {
        new DatabaseTask(this, DatabaseMethod.SELECT_ITEMS_FOR_SHELF, (item_task, item_result) -> {
            if (item_result == null || item_result.isEmpty() || item_result.startsWith(PHP_ERROR_PREFIX))
                return;

            unfilteredItems.clear();
            for (String row : item_result.split(PHP_ROW_SPLITTER)){
                unfilteredItems.add(new Item(row));
            }
            new DatabaseTask(this, DatabaseMethod.SELECT_KEYWORDS_FOR_SHELF, (kw_task, kw_result) -> {
                if (kw_result == null || kw_result.isEmpty() || kw_result.startsWith(PHP_ERROR_PREFIX))
                    return;

                keywords.clear();
                for (String row : kw_result.split(PHP_ROW_SPLITTER)) {
                    keywords.add(new Keyword(row));
                }
                assignKeywordsToUnfilteredItems();
                clearAndFillTableWithItems();
            }).execute(shelf.shelf_id);
        }).execute(shelf.shelf_id);
    }

    /** Assumes filteredItems and keywords aren't null. */
    private void assignKeywordsToUnfilteredItems() {
        for (Item item : unfilteredItems) {
            for (Keyword kw : keywords) {
                if (item.drawerhasitem_id == kw.drawerhasitem_id) {
                    item.assignedKeywords.put(kw.name, kw);
                }
            }
        }
    }

    private void filterItems() {
        filteredItems.clear();
        List<Drawer> selectedDrawers = getSelectedDrawers();
        searchString = searchString.toLowerCase();

        for (Item unfItem : unfilteredItems) {
            boolean containsSearchString = false;
            if (unfItem.name.toLowerCase().contains(searchString))
                containsSearchString = true;
            else {
                for (Keyword kw : unfItem.assignedKeywords.values()) {
                    if (kw.value != null && kw.value.toLowerCase().contains(searchString)) {
                        containsSearchString = true;
                        break;
                    }
                }
            }

            boolean isInSelectedDrawer = false;
            if (containsSearchString) {
                if (selectedDrawers.size() == 0) {
                    isInSelectedDrawer = true;  //no drawers selected --> no filtering
                }
                else {
                    for (Drawer d : selectedDrawers) {
                        if (d.drawer_id == unfItem.drawer_id) {
                            isInSelectedDrawer = true;
                            break;
                        }
                    }
                }
            }

            if (containsSearchString && isInSelectedDrawer)
                filteredItems.add(unfItem);
        }
    }

    /** Assumes filteredItems, keywords aren't null and that keywords have been assigned to filteredItems. */
    private void clearAndFillTableWithItems() {
        tableLayout.removeAllViews();
        LayoutInflater inflater = getLayoutInflater();

        addTableHeader();
        filterItems();

        for (int i = 0; i < filteredItems.size(); i++){
            Item item = filteredItems.get(i);
            TableRow row = (TableRow) inflater.inflate(R.layout.single_tablerow, tableLayout, false).getRootView();
            LinearLayout amountLayout = row.findViewById(R.id.row_layout_amount);

            if (i % 2 == 0) { //alternating row background color
                row.setBackgroundResource(R.color.table_color_white);
                amountLayout.setBackgroundResource(R.color.table_color_white);
            } else {
                row.setBackgroundResource(R.color.table_color_gray);
                amountLayout.setBackgroundResource(R.color.table_color_gray);
            }

            TextView txtName = row.findViewById(R.id.row_itemname_txt);
            txtName.setText(item.name);
            makeViewClickable(txtName);
            txtName.setOnClickListener(view -> {
                setPrimaryDrawer(item.drawer_id);
            });

            EditText editTextAmount = row.findViewById(R.id.row_itemamount_edittext);
            editTextAmount.setText(String.valueOf(item.amount));

            editTextAmount.setOnEditorActionListener((textView, actionId, keyEvent) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) textView.getContext().getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);

                    int newAmount = readAmountEditText(editTextAmount, 0);
                    new DatabaseTask(TableActivity.this, DatabaseMethod.UPDATE_ITEM_AMOUNT, (task, result) -> {
                        if (result == null || result.isEmpty() || result.startsWith(PHP_ERROR_PREFIX))
                            return;
                        item.amount = newAmount;
                        editTextAmount.setText(String.valueOf(newAmount));
                    }).execute(newAmount, item.drawerhasitem_id);
                    return true;
                }
                return false;
            });

            TextView btnPlus = amountLayout.findViewById(R.id.row_btn_plus);
            TextView btnMinus = amountLayout.findViewById(R.id.row_btn_minus);

            btnPlus.setOnClickListener(view -> {
                int newAmount = readAmountEditText(editTextAmount, 1);
                new DatabaseTask(this, DatabaseMethod.UPDATE_ITEM_AMOUNT, (task, result) -> {
                    if (result == null || result.isEmpty() || result.startsWith(PHP_ERROR_PREFIX))
                        return;
                    item.amount = newAmount;
                    editTextAmount.setText(String.valueOf(newAmount));
                }).execute(newAmount, item.drawerhasitem_id);
            });
            btnMinus.setOnClickListener(view -> {
                int newAmount = readAmountEditText(editTextAmount, -1);
                new DatabaseTask(this, DatabaseMethod.UPDATE_ITEM_AMOUNT, (task, result) -> {
                    if (result == null || result.isEmpty() || result.startsWith(PHP_ERROR_PREFIX))
                        return;
                    item.amount = newAmount;
                    editTextAmount.setText(String.valueOf(newAmount));
                }).execute(newAmount, item.drawerhasitem_id);
            });

            for (Keyword keywordColumn : keywordColumns) {
                Keyword keyword = item.assignedKeywords.get(keywordColumn.name);
                row.addView(createKeywordValueTextView(keyword, keywordColumn, item));
            }

            //finished constructing row: add id to table
            tableLayout.addView(row);
        }
    }

    /**
     * Assumes keywords != null.
     */
    private void addTableHeader() {
        TableRow header = new TableRow(this);
        header.setBackgroundResource(R.color.table_color_dark_gray);

        TextView nameHeader = createKeywordHeaderTextView(getString(R.string.label_item));
        TextView amountHeader = createKeywordHeaderTextView(getString(R.string.label_amount));

        makeViewClickable(nameHeader);
        nameHeader.setOnClickListener(view -> {
            String viewText = nameHeader.getText().toString();
            boolean sortDescending = viewText.equals(sortedAscendingBy);
            if (sortDescending)
                sortedAscendingBy = "";
            else
                sortedAscendingBy = viewText;

            Log.v("misc", "Sorting by: "+ viewText + ", desc: " + sortDescending);

            Collections.sort(unfilteredItems, (item1, item2) -> {
                if (sortDescending)
                    return item2.name.toLowerCase().compareTo(item1.name.toLowerCase());
                else  //ascending
                    return item1.name.toLowerCase().compareTo(item2.name.toLowerCase());
            });
            clearAndFillTableWithItems();
        });

        makeViewClickable(amountHeader);
        amountHeader.setOnClickListener(view -> {
            String viewText = amountHeader.getText().toString();
            boolean sortDescending = viewText.equals(sortedAscendingBy);
            if (sortDescending)
                sortedAscendingBy = "";
            else
                sortedAscendingBy = viewText;

            Collections.sort(unfilteredItems, (item1, item2) -> {
                if (sortDescending)
                    return Integer.compare(item2.amount, item1.amount);
                else  //ascending
                    return Integer.compare(item1.amount, item2.amount);
            });
            clearAndFillTableWithItems();
        });

        header.addView(nameHeader);
        header.addView(amountHeader);

        keywordColumns.clear();
        for (Keyword kw : keywords) {
            if (keywordColumns.contains(kw))
                continue;
            header.addView(createKeywordHeaderTextView(kw.name));
            keywordColumns.add(kw);
        }

        TableLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        tableLayout.addView(header, params);
    }

    private TextView createKeywordHeaderTextView(String keywordName) {
        TextView textView = new TextView(this);
        textView.setText(keywordName);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        textView.setPadding(12, 20, 12, 32);

        makeViewClickable(textView);
        textView.setOnClickListener(view -> {
            String viewText = textView.getText().toString();
            boolean sortDescending = viewText.equals(sortedAscendingBy);
            if (sortDescending)
                sortedAscendingBy = "";
            else
                sortedAscendingBy = viewText;

            Collections.sort(unfilteredItems, (item1, item2) -> {
                Keyword kw1 = item1.assignedKeywords.get(keywordName);
                Keyword kw2 = item2.assignedKeywords.get(keywordName);

                if (kw1 == null) {
                    if (kw2 == null)
                        return 0;
                    else
                        return 1;   //null > non-null
                } else if (kw2 == null) {
                    return -1;      //non-null < null
                } else if (kw1.value == null) {
                    if (kw2.value == null)
                        return 0;
                    else
                        return 1;   //null > non-null
                } else if (kw2.value == null) {
                    return -1;      //non-null < null
                } else {
                    int ascendingRetVal = kw1.value.toLowerCase().compareTo(kw2.value.toLowerCase());
                    return sortDescending ? ascendingRetVal * -1 : ascendingRetVal;
                }
            });
            clearAndFillTableWithItems();
        });

        return textView;
    }

    private TextView createKeywordValueTextView(Keyword keyword, Keyword keywordColumn, Item item) {
        final String noKeywordValue = getString(R.string.label_no_value);
        final String keywordValue = keyword == null ? "" : keyword.value == null ? noKeywordValue : keyword.value;

        TextView textView = new TextView(this);
        textView.setText(keywordValue);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        textView.setPadding(12, 0, 12, 0);
        textView.setGravity(Gravity.CENTER_VERTICAL);

        TableRow.LayoutParams params = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(params);

        makeViewClickable(textView);
        textView.setOnClickListener(view -> {
            setPrimaryDrawer(item.drawer_id);
        });

        textView.setOnLongClickListener(btnView -> {
            AlertDialog dialog = Utils.buildAlertDialog(this, R.layout.dialog_one_edittext);
            Utils.setAlertDialogButtons(dialog,
                    getString(R.string.label_update_keyword), (view, id) -> {
                        EditText textfield_value = dialog.findViewById(R.id.dialog_one_edittext_textfield);

                        if (!Utils.validateEditTexts(this, textfield_value))
                            return;
                        String newValue = textfield_value.getText().toString();
                        DatabaseMethod dbMethod = keyword == null ? DatabaseMethod.INSERT_KEYWORD_FOR_ITEM : DatabaseMethod.UPDATE_KEYWORD_VALUE;

                        new DatabaseTask(this, dbMethod, (task, result) -> {
                            if (result == null || result.isEmpty() || result.startsWith(PHP_ERROR_PREFIX))
                                Toast.makeText(this, R.string.toast_update_keyword_failed, Toast.LENGTH_SHORT).show();
                            else {
                                if (dbMethod == DatabaseMethod.INSERT_KEYWORD_FOR_ITEM)
                                    clearAndFetchFromDatabase();
                                else {
                                    keyword.value = newValue;
                                    textView.setText(keyword.value);
                                }
                            }
                        }).execute(item.drawerhasitem_id, keywordColumn.keyword_id, newValue);
                    },
                    getString(R.string.label_cancel), (view, id) -> {
                        dialog.dismiss();
                    }
            );
            dialog.setCancelable(false);
            dialog.show();

            TextView titleView = dialog.findViewById(R.id.dialog_one_edittext_title);
            titleView.setText(String.format(getString(R.string.format_update_keyword), keywordColumn.name));

            EditText textfield_value = dialog.findViewById(R.id.dialog_one_edittext_textfield);
            textfield_value.setText(keywordValue.equals(noKeywordValue) ? "" : keywordValue);

            return true;
        });

        return textView;
    }

    private void makeViewClickable(View view) {
        view.setFocusable(true);
        view.setClickable(true);

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.selectableItemBackground, typedValue, true);
        view.setBackgroundResource(typedValue.resourceId);
    }

    private int readAmountEditText(EditText amountEditText, int amountToAdd) {
        String amountString = amountEditText.getText().toString();
        int amount;
        try {
            amount = Integer.parseInt(amountString) + amountToAdd;
        } catch (NumberFormatException e) {
            amount = MIN_ITEM_AMOUNT;
            amountEditText.setText(String.valueOf(amount));
            return amount;
        }

        if (amount < MIN_ITEM_AMOUNT) {
            amount = MIN_ITEM_AMOUNT;
            amountEditText.setText(String.valueOf(amount));
        } else if (amount > MAX_ITEM_AMOUNT) {
            amount = MAX_ITEM_AMOUNT;
            amountEditText.setText(String.valueOf(amount));
        }
        return amount;
    }

    private int getPrimaryDrawer() {
        //TODO: Omas !!
        return 1;
    }

    private void setPrimaryDrawer(int drawer_id) {
        //TODO: Omas !!
    }

    private List<Drawer> getSelectedDrawers() {
        List<Drawer> selectedDrawersFromView = new ArrayList<>();
        for(DrawableDrawerRect d : drawerView.selectedDrawers){
            selectedDrawersFromView.add(d.drawer);
        }
        return selectedDrawersFromView;
    }

    private class DrawableDrawerRect {
        private Drawer drawer;
        private Rect rect;
        private String name;
        private Paint paint;

        public DrawableDrawerRect(Drawer drawer, int x, int y, int x2, int y2) {
            this.rect = createRectForPoints(x, y, x2, y2);
            this.name = name;
            this.drawer = drawer;
            this.paint = new Paint();
            this.paint.setStyle(Paint.Style.FILL_AND_STROKE);
            this.paint.setColor(Color.argb(150, 150, 150, 150));
            this.paint.setStrokeWidth(3);
            this.paint.setTextSize(50);
        }

        public boolean contains(int x, int y) {
            return rect.contains(x, y);
        }

        private Rect createRectForPoints(int x, int y, int x2, int y2) {
            int topleftX = Math.min(x, x2);
            int topleftY = Math.min(y, y2);
            int bottomRightX = Math.max(x, x2);
            int bottomRightY = Math.max(y, y2);
            return new Rect(topleftX, topleftY, bottomRightX, bottomRightY);
        }

        public String getName() {
            return name == null ? "" : name;
        }

        public void setName(String name) {
            this.name = name;
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

        @Override
        public String toString() {
            return "DrawableDrawerRect{" +
                    "drawer=" + drawer +
                    ", rect=" + rect +
                    ", name='" + name + '\'' +
                    ", paint=" + paint +
                    '}';
        }
    }


    public void initDrawableRectListWithDrawers(List<Drawer> drawerList) {
        Log.i("TableActivity", "initDrawableRectListWithDrawers drawerList.size: " + drawerList.size());
        for (Drawer d : drawerList) {
            if (d.sizeX != 0 && d.sizeY != 0) {
                Rect convertedRect = convertRelativeRectToDrawableRect(new Rect(d.posX, d.posY, d.posX + d.sizeX, d.posY + d.sizeY));
                TableActivity.DrawableDrawerRect rect = new TableActivity.DrawableDrawerRect(d, convertedRect.left, convertedRect.top, convertedRect.right, convertedRect.bottom);
                rect.setName("");
                drawerView.drawableRectList.add(rect);
            }
        }
        drawerView.refresh();
    }

    public Rect convertRelativeRectToDrawableRect(Rect drawableRect) {
        int canvasWidth = this.drawerView.canvas.getWidth();
        int canvasHeight = this.drawerView.canvas.getHeight();
        int newX = (int) ((drawableRect.left / MAP_SIZE_CONSTANT_X) * canvasWidth);
        int newX2 = (int) ((drawableRect.right / MAP_SIZE_CONSTANT_X) * canvasWidth);
        int newY = (int) ((drawableRect.top / MAP_SIZE_CONSTANT_Y) * canvasHeight);
        int newY2 = (int) ((drawableRect.bottom / MAP_SIZE_CONSTANT_Y) * canvasHeight);
        Rect after = new Rect(newX, newY, newX2, newY2);
        return after;
    }

    private class CustomImageView extends AppCompatImageView implements View.OnTouchListener {

        private List<DrawableDrawerRect> drawableRectList = new ArrayList<>();
        private Canvas canvas;
        private Paint paint;

        private Bitmap drawableBitmap;
        private Bitmap backgroundBitmap;

        private Set<DrawableDrawerRect> selectedDrawers = new HashSet<>();


        public CustomImageView(Context context, byte[] image, int width, int height) {
            super(context);
            drawableBitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            drawableBitmap = Bitmap.createScaledBitmap(drawableBitmap, width, height, false);

            backgroundBitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            backgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, width, height, false);

            canvas = new Canvas(drawableBitmap);
            paint = new Paint();
            paint.setColor(Color.argb(150, 150, 150, 150));

            this.setImageBitmap(drawableBitmap);
            this.setOnTouchListener(this);
        }

        public void refresh() {
            this.updateCanvas();
            this.invalidate();
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //Log.i("onTouch", "onTouch");
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:

                    int downx = ((int) event.getX());
                    int downy = ((int) event.getY());
                    Log.i("TableActivity", "TOUCH ACTION_DOWN x:" + downx + ", y:" + downy);

                    for (DrawableDrawerRect drawableDrawerRect : drawableRectList) {
                        Log.i("DrawableDrawerRect", "x:" + drawableDrawerRect.rect.left + ", y:" + drawableDrawerRect.rect.top);
                        if (drawableDrawerRect.contains(downx, downy)) {
                            if (selectedDrawers.contains(drawableDrawerRect)) {
                                Log.i("misc", "selected: " + drawableDrawerRect);
                                selectedDrawers.remove(drawableDrawerRect);
                            } else {
                                Log.i("misc", "deselected: " + drawableDrawerRect);
                                selectedDrawers.add(drawableDrawerRect);
                            }
                        }
                    }
                    clearAndFillTableWithItems();
                    refresh();
                    break;
                default:
                    break;
            }
            return true;
        }

        private void updateCanvas() {
            //Log.i("method called", "updateCanvas");
            canvas.drawColor(Color.LTGRAY);
            canvas.drawBitmap(backgroundBitmap, 0, 0, null);

            for (TableActivity.DrawableDrawerRect d : drawableRectList) {
                if (selectedDrawers.contains(d)) {
                    d.paint.setColor(Color.argb(150, 200, 50, 50));
                } else {
                    d.paint.setColor(Color.argb(150, 150, 150, 150));
                }
                canvas.drawRect(d.rect.left, d.rect.top, d.rect.right, d.rect.bottom, d.paint);
            }
        }
    }

}
