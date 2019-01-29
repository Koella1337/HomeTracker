package at.hometracker.activities;

import android.annotation.SuppressLint;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import at.hometracker.R;
import at.hometracker.database.DatabaseMethod;
import at.hometracker.database.DatabaseTask;
import at.hometracker.database.datamodel.Group;
import at.hometracker.database.datamodel.Item;
import at.hometracker.database.datamodel.Keyword;
import at.hometracker.database.datamodel.Shelf;
import at.hometracker.shared.Constants;

import static at.hometracker.shared.Constants.PHP_ROW_SPLITTER;

public class TableActivity extends AppCompatActivity {

    private Group group;
    private Shelf shelf;

    private TableLayout tableLayout;
    private List<Item> items = null;

    private List<Keyword> keywords = null;
    private LinkedHashSet<String> keywordColumns = null;    //keeps insertion order

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        Toolbar myToolbar = findViewById(R.id.toolbar_table);
        setSupportActionBar(myToolbar);
        myToolbar.setTitle("TableActivity");

        group = (Group) getIntent().getSerializableExtra(Constants.INTENT_EXTRA_GROUP);
        shelf = (Shelf) getIntent().getSerializableExtra(Constants.INTENT_EXTRA_SHELF);
        if (shelf == null || group == null) throw new RuntimeException("Invalid id on TableActivity creation!");

        this.tableLayout = findViewById(R.id.table_shelf);
    }

    @Override
    protected void onStart() {
        super.onStart();
        clearAndFetchFromDatabase();
    }

    private void clearAndFetchFromDatabase() {
        new DatabaseTask(this, DatabaseMethod.SELECT_ITEMS_FOR_SHELF, (item_task, item_result) -> {
            items = new ArrayList<>();
            for (String row : item_result.split(PHP_ROW_SPLITTER)){
                items.add(new Item(row));
            }
            new DatabaseTask(this, DatabaseMethod.SELECT_KEYWORDS_FOR_SHELF, (kw_task, kw_result) -> {
                keywords = new ArrayList<>();
                for (String row : kw_result.split(PHP_ROW_SPLITTER)){
                    keywords.add(new Keyword(row));
                }
                assignKeywordsToItems();
                clearAndFillTableWithItems();
            }).execute(shelf.shelf_id);
        }).execute(shelf.shelf_id);
    }

    /** Assumes items and keywords aren't null. */
    private void assignKeywordsToItems() {
        for (Item item : items) {
            for (Keyword kw : keywords) {
                if (item.drawerhasitem_id == kw.drawerhasitem_id){
                    item.assignedKeywords.put(kw.name, kw);
                }
            }
        }
    }

    /** Assumes items, keywords aren't null and that keywords have been assigned to items. */
    private void clearAndFillTableWithItems() {
        tableLayout.removeAllViews();
        LayoutInflater inflater = getLayoutInflater();

        addTableHeader();

        for (int i = 0; i < items.size(); i++){
            Item item = items.get(i);
            TableRow row = (TableRow) inflater.inflate(R.layout.single_tablerow, tableLayout, false).getRootView();
            LinearLayout amountLayout = row.findViewById(R.id.row_layout_amount);

            if (i%2 == 0) { //alternating row background color
                row.setBackgroundResource(R.color.table_color_white);
                amountLayout.setBackgroundResource(R.color.table_color_white);
            }
            else {
                row.setBackgroundResource(R.color.table_color_gray);
                amountLayout.setBackgroundResource(R.color.table_color_gray);
            }

            TextView txtName = row.findViewById(R.id.row_itemname_txt);
            txtName.setText(item.name);
            txtName.setOnClickListener(view -> {
                Log.v("misc", "Clicked: " + item);
            });

            EditText editTextAmount = row.findViewById(R.id.row_itemamount_edittext);
            editTextAmount.setText(String.valueOf(item.amount));
            editTextAmount.setOnClickListener(view -> {

            });

            for (String keywordColumn : keywordColumns) {
                row.addView(createKeywordValueTextView(item.assignedKeywords.get(keywordColumn)));
            }

            //finished constructing row: add id to table
            tableLayout.addView(row);
        }
    }

    /** Assumes keywords != null. */
    private void addTableHeader() {
        TableRow header = new TableRow(this);
        header.setBackgroundResource(R.color.table_color_dark_gray);

        TextView nameHeader = createTableHeaderTextView(getString(R.string.label_item));
        TextView amountHeader = createTableHeaderTextView(getString(R.string.label_amount));

        header.addView(nameHeader);
        header.addView(amountHeader);

        makeViewClickable(nameHeader);
        nameHeader.setOnClickListener(view -> {
            Collections.sort(items, (item1, item2) -> {
                return item1.name.compareTo(item2.name);
            });
            clearAndFillTableWithItems();
        });

        makeViewClickable(amountHeader);
        amountHeader.setOnClickListener(view -> {
            Collections.sort(items, (item1, item2) -> {
                return ((Integer) item1.amount).compareTo(item2.amount);
            });
            clearAndFillTableWithItems();
        });

        LinkedHashSet<String> createdKeywordColumns = new LinkedHashSet<>();
        for (Keyword kw : keywords) {
            if (createdKeywordColumns.contains(kw.name))
                continue;
            header.addView(createTableHeaderTextView(kw.name));
            createdKeywordColumns.add(kw.name);
        }
        this.keywordColumns = createdKeywordColumns;

        TableLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        tableLayout.addView(header, params);
    }

    public TextView createTableHeaderTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        textView.setPadding(12, 0, 12, 12);
        return textView;
    }

    public TextView createKeywordValueTextView(Keyword keyword) {
        String text = keyword == null ? "" : keyword.value;
        if (text == null) text = getString(R.string.label_no_value);

        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        textView.setPadding(12, 0, 12, 0);
        textView.setGravity(Gravity.CENTER_VERTICAL);

        TableRow.LayoutParams params = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(params);

        makeViewClickable(textView);
        textView.setOnClickListener(view -> {
            Log.v("misc", "Short clicked: " + keyword);
        });

        textView.setOnLongClickListener(view -> {
            Log.v("misc", "Long clicked: " + keyword);

            return true;
        });

        return textView;
    }

    public void makeViewClickable(View view) {
        view.setFocusable(true);
        view.setClickable(true);

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.selectableItemBackground, typedValue, true);
        view.setBackgroundResource(typedValue.resourceId);
    }

}
