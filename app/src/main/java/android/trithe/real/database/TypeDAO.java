package android.trithe.real.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.trithe.real.model.TypePet;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class TypeDAO {
    private final SQLiteDatabase db;
    public static final String TABLE_NAME = "Type";
    public static final String SQL_TYPEPET = "CREATE TABLE Type (id NCHAR(7) primary key, name NVARCHAR(50));";
    private static final String TAG = "TypeDAO";

    public TypeDAO(Context context) {
        Databasemanager databasemanager = new Databasemanager(context);
        db = databasemanager.getWritableDatabase();
    }

    public List<TypePet> getAllType() {
        List<TypePet> dsType = new ArrayList<>();
        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            TypePet ee = new TypePet();
            ee.setId(c.getString(0));
            ee.setName(c.getString(1));
                   dsType.add(ee);
            Log.d("//=====", ee.toString());
            c.moveToNext();
        }
        c.close();
        return dsType;
    }
    public void insertType(TypePet type) {
        ContentValues values = new ContentValues();
        values.put("id", type.getId());
        values.put("name", type.getName());
           try {
            db.insert(TABLE_NAME, null, values);
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        }
    }

}
