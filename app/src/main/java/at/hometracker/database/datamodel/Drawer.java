package at.hometracker.database.datamodel;

public class Drawer extends AbstractDatabaseObject {

    public int drawer_id;
    public String description;
    public int shelf_id;
    public int posX, posY, sizeX, sizeY;


    public Drawer() {
        //empty constructor
    }

    public Drawer(int drawer_id, String description,int shelf_id, int posX, int posY, int sizeX, int sizeY) {
        this.drawer_id = drawer_id;
        this.description = description;
        this.shelf_id = shelf_id;
        this.posX = posX;
        this.posY = posY;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    public Drawer(String databaseRow){
        super(databaseRow);
    }

    @Override
    public String toString() {
        return "Drawer{" +
                "drawer_id=" + drawer_id +
                ", description='" + description + '\'' +
                ", shelf_id=" + shelf_id +
                ", posX=" + posX +
                ", posY=" + posY +
                ", sizeX=" + sizeX +
                ", sizeY=" + sizeY +
                '}';
    }
}
