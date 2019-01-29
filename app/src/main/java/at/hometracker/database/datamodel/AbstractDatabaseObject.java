package at.hometracker.database.datamodel;

import android.util.Base64;
import android.util.Log;

import java.io.Serializable;
import java.lang.reflect.Field;

import at.hometracker.shared.Constants;

public abstract class AbstractDatabaseObject implements Serializable {

    public AbstractDatabaseObject() {
        //empty constructor
    }

    /** Sets class fields via Reflection using a database result row. */
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

                if (colName.equals("picture")) {
                    field.set(this, Base64.decode(value, 0));
                }
                else {
                    switch(field.getType().getSimpleName()){
                        case "String":
                            field.set(this, value);
                            break;
                        case "int":
                            if (value == null || value.isEmpty())
                                field.setInt(this, 0);
                            else
                                field.setInt(this, Integer.parseInt(value));
                            break;
                        default:
                            Log.e("db", String.format("Illegal field type (%s) in %s", field.getType().getSimpleName(), this.getClass().getSimpleName()));
                            break;
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

}
