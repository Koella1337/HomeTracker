package at.hometracker.database.datamodel;

public class Drawer extends AbstractDatabaseObject {

    public int drawer_id;
    public String description;
    public int posX, posY, sizeX, sizeY;


    public Drawer() {
        //empty constructor
    }

    public Drawer(int drawer_id, String description, int posX, int posY, int sizeX, int sizeY) {
        this.drawer_id = drawer_id;
        this.description = description;
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
                ", posX=" + posX +
                ", posY=" + posY +
                ", sizeX=" + sizeX +
                ", sizeY=" + sizeY +
                '}';
    }
}
