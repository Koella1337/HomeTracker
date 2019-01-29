package at.hometracker.database.datamodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Item extends AbstractDatabaseObject {

    //Item
    public int item_id;
    public String name;
    public int barcode;
    public int group_id;

    //Drawer
    public int shelf_id;

    //_R_DrawerHasItem
    public int drawer_id;
    public int amount;
    public int drawerhasitem_id;

    /** use to assign keywords to this item, maps keyword.name to keyword */
    public Map<String, Keyword> assignedKeywords = new HashMap<>();

    public Item() {
        //empty constructor
    }

    public Item(String databaseRow) {
        super(databaseRow);
    }

    public Item(int item_id, String name, int barcode, int group_id, int shelf_id, int drawer_id, int amount, int drawerhasitem_id) {
        this.item_id = item_id;
        this.name = name;
        this.barcode = barcode;
        this.group_id = group_id;
        this.shelf_id = shelf_id;
        this.drawer_id = drawer_id;
        this.amount = amount;
        this.drawerhasitem_id = drawerhasitem_id;
    }

    @Override
    public String toString() {
        return "Item{" +
                "item_id=" + item_id +
                ", name='" + name + '\'' +
                ", barcode=" + barcode +
                ", group_id=" + group_id +
                ", shelf_id=" + shelf_id +
                ", drawer_id=" + drawer_id +
                ", amount=" + amount +
                ", drawerhasitem_id=" + drawerhasitem_id +
                ", assignedKeywords=" + Arrays.toString(assignedKeywords.keySet().toArray()) +
                '}';
    }
}
