package at.hometracker.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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
import at.hometracker.qrcode.GeneratorActivity;
import at.hometracker.shared.Constants;
import at.hometracker.utils.Utils;

import static at.hometracker.shared.Constants.MAX_ITEM_AMOUNT;
import static at.hometracker.shared.Constants.MIN_ITEM_AMOUNT;
import static at.hometracker.shared.Constants.PHP_ERROR_PREFIX;
import static at.hometracker.shared.Constants.PHP_ROW_SPLITTER;

public class TableActivity extends AppCompatActivity {

    private Shelf shelf;

    private TableLayout tableLayout;
    private List<Item> items = new ArrayList<>();

    private List<Keyword> keywords = new ArrayList<>();
    private LinkedHashSet<String> keywordColumns = new LinkedHashSet<>();    //keeps insertion order

    private String sortedAscendingBy = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        Toolbar myToolbar = findViewById(R.id.toolbar_table);
        setSupportActionBar(myToolbar);
        myToolbar.setTitle("TableActivity");

        shelf = (Shelf) getIntent().getSerializableExtra(Constants.INTENT_EXTRA_SHELF);
        if (shelf == null) throw new RuntimeException("Invalid id on TableActivity creation!");

        this.tableLayout = findViewById(R.id.table_shelf);
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
            case R.id.action_new_item:
                addItem();
                return true;
            case R.id.action_generate_qr:
                String qrCodeString = "shelf_"+ shelf.shelf_id;

                Intent qrGeneratorIntent = new Intent(this, GeneratorActivity.class);
                qrGeneratorIntent.putExtra(Constants.INTENT_EXTRA_QR_STRING, qrCodeString);
                startActivity(qrGeneratorIntent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addItem() {
        if (getSelectedDrawers().length != 1) {
            Toast.makeText(this, R.string.toast_invalid_drawer_selection, Toast.LENGTH_SHORT).show();
            return;
        }
        int selectedDrawerID = getSelectedDrawers()[0];

        AlertDialog dialog = Utils.buildAlertDialog(this, R.layout.dialog_create_item);
        Utils.setAlertDialogButtons(dialog,
                getString(R.string.label_create_item), (view, id) -> {
                    EditText textfield_itemname = dialog.findViewById(R.id.edittext_new_item_name);
                    EditText textfield_itemamount = dialog.findViewById(R.id.edittext_new_item_amount);

                    if (!Utils.validateEditTexts(this, textfield_itemname))
                        return;

                    String name = textfield_itemname.getText().toString();
                    int amount = clampAmountEditText(textfield_itemamount);

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
        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        clearAndFetchFromDatabase();
    }

    private void clearAndFetchFromDatabase() {
        new DatabaseTask(this, DatabaseMethod.SELECT_ITEMS_FOR_SHELF, (item_task, item_result) -> {
            items.clear();
            for (String row : item_result.split(PHP_ROW_SPLITTER)){
                items.add(new Item(row));
            }
            new DatabaseTask(this, DatabaseMethod.SELECT_KEYWORDS_FOR_SHELF, (kw_task, kw_result) -> {
                keywords.clear();
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

    private void filterItems() {
        //TODO
    }

    /** Assumes items, keywords aren't null and that keywords have been assigned to items. */
    private void clearAndFillTableWithItems() {
        tableLayout.removeAllViews();
        LayoutInflater inflater = getLayoutInflater();

        addTableHeader();
        filterItems();

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
            makeViewClickable(txtName);
            txtName.setOnClickListener(view -> {
                Log.v("misc", "Clicked: " + item);
            });

            EditText editTextAmount = row.findViewById(R.id.row_itemamount_edittext);
            editTextAmount.setText(String.valueOf(item.amount));

            TextView btnPlus = amountLayout.findViewById(R.id.row_btn_plus);
            TextView btnMinus = amountLayout.findViewById(R.id.row_btn_minus);

            btnPlus.setOnClickListener(view -> {
                int newAmount = clampAmountEditText(editTextAmount) + 1;
                new DatabaseTask(this, DatabaseMethod.UPDATE_ITEM_AMOUNT, (task, result) -> {
                    if (result == null || result.isEmpty() || result.startsWith(PHP_ERROR_PREFIX))
                        return;
                    item.amount = newAmount;
                    editTextAmount.setText(String.valueOf(newAmount));
                }).execute(newAmount, item.drawerhasitem_id);
            });
            btnMinus.setOnClickListener(view -> {
                int newAmount = clampAmountEditText(editTextAmount) - 1;
                new DatabaseTask(this, DatabaseMethod.UPDATE_ITEM_AMOUNT, (task, result) -> {
                    if (result == null || result.isEmpty() || result.startsWith(PHP_ERROR_PREFIX))
                        return;
                    item.amount = newAmount;
                    editTextAmount.setText(String.valueOf(newAmount));
                }).execute(newAmount, item.drawerhasitem_id);
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

        makeViewClickable(nameHeader);
        nameHeader.setOnClickListener(view -> {
            String viewText = nameHeader.getText().toString();
            boolean sortDescending = viewText.equals(sortedAscendingBy);
            if (sortDescending)
                sortedAscendingBy = "";
            else
                sortedAscendingBy = viewText;

            Collections.sort(items, (item1, item2) -> {
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

            Collections.sort(items, (item1, item2) -> {
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
            if (keywordColumns.contains(kw.name))
                continue;
            header.addView(createTableHeaderTextView(kw.name));
            keywordColumns.add(kw.name);
        }

        TableLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        tableLayout.addView(header, params);
    }

    private TextView createTableHeaderTextView(String keywordName) {
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

            Collections.sort(items, (item1, item2) -> {
                Keyword kw1 = item1.assignedKeywords.get(keywordName);
                Keyword kw2 = item2.assignedKeywords.get(keywordName);

                if (kw1 == null) {
                    if (kw2 == null)
                        return 0;
                    else
                        return 1;   //null > non-null
                }
                else if (kw2 == null) {
                    return -1;      //non-null < null
                }
                else if (kw1.value == null) {
                    if (kw2.value == null)
                        return 0;
                    else
                        return 1;   //null > non-null
                }
                else if (kw2.value == null) {
                    return -1;      //non-null < null
                }
                else {
                    int ascendingRetVal = kw1.value.toLowerCase().compareTo(kw2.value.toLowerCase());
                    return sortDescending ? ascendingRetVal * -1 : ascendingRetVal;
                }
            });
            clearAndFillTableWithItems();
        });

        return textView;
    }

    private TextView createKeywordValueTextView(Keyword keyword) {
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

    private void makeViewClickable(View view) {
        view.setFocusable(true);
        view.setClickable(true);

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.selectableItemBackground, typedValue, true);
        view.setBackgroundResource(typedValue.resourceId);
    }

    private int clampAmountEditText(EditText amountEditText) {
        String amountString = amountEditText.getText().toString();
        int amount;
        try {
            amount = Integer.parseInt(amountString);
        } catch (NumberFormatException e) {
            return MIN_ITEM_AMOUNT;
        }

        if (amount < MIN_ITEM_AMOUNT){
            amount = MIN_ITEM_AMOUNT;
            amountEditText.setText(String.valueOf(amount));
        }
        else if (amount > MAX_ITEM_AMOUNT){
            amount = MAX_ITEM_AMOUNT;
            amountEditText.setText(String.valueOf(amount));
        }
        return amount;
    }

    public int[] getSelectedDrawers() {
        return new int[] {1};   //TODO: Omas !!
    }

}
