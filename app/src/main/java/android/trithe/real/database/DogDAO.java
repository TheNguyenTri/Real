package android.trithe.real.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.trithe.real.model.Dog;
import android.trithe.real.model.User;
import android.util.Log;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;


public class DogDAO {
    private final SQLiteDatabase db;
    public static final String TABLE_NAME = "Dog";
    public static final String SQL_DOG = "CREATE TABLE Dog (dogid NVARCHAR(50) primary key, name NVARCHAR(50), age INT , price FLOAT, image blob);";
    private static final String TAG = "DogDAO";

    public DogDAO(Context context) {
        Databasemanager databasemanager = new Databasemanager(context);
        db = databasemanager.getWritableDatabase();
    }

    public int inserDog(Dog dog) {
        ContentValues values = new ContentValues();
        values.put("dogid", dog.getDogid());
        values.put("name", dog.getName());
        values.put("age", dog.getAge());
        values.put("price", dog.getPrice());
        values.put("image", dog.getImage());
        try {
            if (db.insert(TABLE_NAME, null, values) == -1) {
                return -1;
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        }
        return 1;
    }

    public List<Dog> getAllDog() {
        List<Dog> dsDog = new ArrayList<>();
        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            Dog ee = new Dog();
            ee.setDogid(c.getString(0));
            ee.setName(c.getString(1));
            ee.setAge(Integer.parseInt(c.getString(2)));
            ee.setPrice(Float.parseFloat(c.getString(3)));
            ee.setImage(c.getBlob(4));
            dsDog.add(ee);
            Log.d("//=====", ee.toString());
            c.moveToNext();
        }
        c.close();
        return dsDog;
    }

    //delete
    public void deleteDogByID(String dogid) {
        db.delete(TABLE_NAME, "dogid=?", new String[]{dogid});
    }


    public int updateDog(String editusername, String name, String age, String price, byte[] image) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("age", age);
        values.put("price", price);
        values.put("image", String.valueOf(image));
        int result = db.update(TABLE_NAME, values, "dogid=?", new
                String[]{editusername});
        if (result == 0) {
            return -1;
        }
        return 1;
    }

    public int updateDog(Dog dog) {
        ContentValues values = new ContentValues();
        values.put("dogid", dog.getDogid());
        values.put("name", dog.getName());
        values.put("age", dog.getAge());
        values.put("price", dog.getPrice());
        values.put("image", dog.getImage());
        int result = db.update(TABLE_NAME, values, "dogid=?", new
                String[]{dog.getDogid()});
        if (result == 0) {
            return -1;
        }
        return 1;
    }
}
