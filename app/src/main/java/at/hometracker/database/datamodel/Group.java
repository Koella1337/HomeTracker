package at.hometracker.database.datamodel;

public class Group extends AbstractDatabaseObject {

    public int group_id;
    public String name;
    public String password;
    public String password_salt;

    public Group(String databaseResult) {
        super(databaseResult);
    }

    public Group(int group_id, String name, String password, String password_salt) {
        this.group_id = group_id;
        this.name = name;
        this.password = password;
        this.password_salt = password_salt;
    }

    @Override
    public String toString() {
        return "Group{" +
                "group_id=" + group_id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", password_salt='" + password_salt + '\'' +
                '}';
    }
}
