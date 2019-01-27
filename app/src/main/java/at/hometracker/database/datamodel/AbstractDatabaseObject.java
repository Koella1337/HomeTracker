package at.hometracker.database.datamodel;

import android.util.Log;

import java.lang.reflect.Field;

import at.hometracker.shared.Constants;

public abstract class AbstractDatabaseObject {

    public AbstractDatabaseObject() {
        //empty constructor
    }

    public AbstractDatabaseObject(String databaseRow) {
        if (databaseRow == null || databaseRow.isEmpty())
            return;

        String[] keyValuePairs = databaseRow.split(Constants.PHP_COLUMN_SPLITTER);

        for (int i = 0; i < keyValuePairs.length; i++) {
            String[] keyThenValue = keyValuePairs[i].split(Constants.PHP_KEYVALUE_SPLITTER);
            String colName = keyThenValue[0];
            String value = keyThenValue.length < 2 ? null : keyThenValue[1];

            try {
                Field field = getClass().getField(colName);

                switch(field.getType().getSimpleName()){
                    case "String":
                        field.set(this, value);
                        break;
                    case "int":
                        field.setInt(this, Integer.parseInt(value));
                        break;
                    default:
                        Log.e("db", String.format("Illegal field type (%s) in %s", field.getType().getSimpleName(), this.getClass().getSimpleName()));
                        break;
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

}