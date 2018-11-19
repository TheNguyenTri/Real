package android.trithe.real.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.trithe.real.model.Pet;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class PetDAO {
    private SQLiteDatabase db;
    private final Databasemanager databasemanager;
    public static final String TABLE_NAME = "PET";
    private static final String Health = "health";
    public static final String SQL_PET = "CREATE TABLE PET (id NCHAR(5) primary key, name NVARCHAR(10), giongloai NCHAR(7), age INT, health NVARCHAR(50), weight float, gender NVARCHAR(50), image Blob);";
    private static final String TAG = "PetDAO";

    public PetDAO(Context context) {
        databasemanager = new Databasemanager(context);
        db = databasemanager.getWritableDatabase();
    }


    public List<Pet> getAllPet() {
        List<Pet> dsType = new ArrayList<>();
        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            Pet ee = new Pet();
            ee.setId(c.getString(0));
            ee.setName(c.getString(1));
            ee.setGiongloai(c.getString(2));
            ee.setAge(c.getInt(3));
            ee.setHealth(c.getString(4));
            ee.setWeight(c.getFloat(5));
            ee.setGender(c.getString(6));
            ee.setImage(c.getBlob(7));
            dsType.add(ee);
            Log.d("//=====", ee.toString());
            c.moveToNext();
        }
        c.close();
        return dsType;
    }

    public int getKhoe() {
        db = databasemanager.getReadableDatabase();
        String selectQuery = "SELECT  *  FROM " + TABLE_NAME + " where " + Health + " like 'S%'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        int count = cursor.getCount();
       cursor.close();
        Log.d("abc", count+"");
        return count;
    }

    public int getYeu() {
        db = databasemanager.getReadableDatabase();
        String selectQuery = "SELECT  *  FROM " + TABLE_NAME + " where " + Health + " like 'W%'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int getBT() {
        db = databasemanager.getReadableDatabase();
        String selectQuery = "SELECT  *  FROM " + TABLE_NAME + " where " + Health + " like 'N%'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    //    delete
    public void deleteTypeByID(String id) {
        db.delete(TABLE_NAME, "id=?", new String[]{id});
    }

    //
//
    public int updatePet(String editusername, String name, String giongloai, int age, float weight, String health, String gender, byte[] image) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("giongloai", giongloai);
        values.put("age", age);
        values.put("weight", weight);
        values.put("health", health);
        values.put("gender", gender);
        values.put("image", image);
        int result = db.update(TABLE_NAME, values, "id=?", new
                String[]{editusername});
        if (result == 0) {
            return -1;
        }
        return 1;
    }
//
//    public int updatePet(Pet type) {
//        ContentValues values = new ContentValues();
//        values.put("name", type.getName());
//        values.put("giongloai", type.getGiongloai());
//        values.put("age", type.getAge());
//        values.put("weight", type.getWeight());
//        values.put("health", type.getHealth());
//        values.put("gender", type.getGender());
//        values.put("image", type.getImage());
//        int result = db.update(TABLE_NAME, values, "id=?", new
//                String[]{type.getId()});
//        if (result == 0) {
//            return -1;
//        }
//        return 1;
//    }

    public int insertPet(Pet type) {
        ContentValues values = new ContentValues();
        values.put("id", type.getId());
        values.put("name", type.getName());
        values.put("giongloai", type.getGiongloai());
        values.put("age", type.getAge());
        values.put("weight", type.getWeight());
        values.put("health", type.getHealth());
        values.put("gender", type.getGender());
        values.put("image", type.getImage());
        try {
            if (db.insert(TABLE_NAME, null, values) == -1) {
                return -1;
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        }
        return 1;
    }
}
