package at.hometracker.database.datamodel;

import android.support.annotation.NonNull;

public class Keyword extends AbstractDatabaseObject {

    //Keyword
    public int keyword_id;
    public String name;
    public int group_id;
    public String type;

    //_R_DrawerItemHasKeyword
    public String value;
    public int drawerhasitem_id;

    //Drawer
    public int shelf_id;
    public int drawer_id;

    public Keyword() {
        //empty constructor
    }

    public Keyword(String databaseRow) {
        super(databaseRow);
    }

    public Keyword(int keyword_id, String name, int group_id, String type, int shelf_id, int drawer_id, int drawerhasitem_id) {
        this.keyword_id = keyword_id;
        this.name = name;
        this.group_id = group_id;
        this.type = type;
        this.shelf_id = shelf_id;
        this.drawer_id = drawer_id;
        this.drawerhasitem_id = drawerhasitem_id;
    }

    @Override
    public String toString() {
        return "Keyword{" +
                "keyword_id=" + keyword_id +
                ", name='" + name + '\'' +
                ", group_id=" + group_id +
                ", type='" + type + '\'' +
                ", value='" + value + '\'' +
                ", drawerhasitem_id=" + drawerhasitem_id +
                ", shelf_id=" + shelf_id +
                ", drawer_id=" + drawer_id +
                '}';
    }
}
