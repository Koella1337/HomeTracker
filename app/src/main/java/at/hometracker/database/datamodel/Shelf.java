package at.hometracker.database.datamodel;

public class Shelf extends AbstractDatabaseObject {

    public int shelf_id;
    public String name;
    public int group_id;
    public int posX, posY, sizeX, sizeY;
    public byte[] picture;

    public Shelf(int shelf_id, String name, int group_id, int posX, int posY, int sizeX, int sizeY, byte[] picture) {
        this.shelf_id = shelf_id;
        this.name = name;
        this.group_id = group_id;
        this.posX = posX;
        this.posY = posY;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.picture = picture;
    }

    public Shelf(String databaseResult){
        super(databaseResult);
    }

    @Override
    public String toString() {
        return "Shelf{" +
                "shelf_id=" + shelf_id +
                ", name='" + name + '\'' +
                ", group_id=" + group_id +
                ", posX=" + posX +
                ", posY=" + posY +
                ", sizeX=" + sizeX +
                ", sizeY=" + sizeY +
                '}';
    }
}
