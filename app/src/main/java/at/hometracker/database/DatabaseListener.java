package at.hometracker.database;

public interface DatabaseListener {

    void receiveDatabaseResult(DatabaseMethod method, String result);

}
