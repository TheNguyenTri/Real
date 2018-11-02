package android.trithe.real.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.trithe.real.model.User;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class UserDAO {
    private final SQLiteDatabase db;
    public static final String TABLE_NAME = "User";
    public static final String SQL_USER = "CREATE TABLE User (username NVARCHAR(50) primary key, name NVARCHAR(50), age INTEGER , phone NCHAR(10) , gmail NVARCHAR(50), image blob);";
    private static final String TAG = "UserDAO";

    public UserDAO(Context context) {
        Databasemanager databasemanager = new Databasemanager(context);
        db = databasemanager.getWritableDatabase();
    }

    public int inserUser(User user) {
        ContentValues values = new ContentValues();
        values.put("username", user.getUserName());
        values.put("name", user.getName());
        values.put("age", user.getAge());
        values.put("phone", user.getPhone());
        values.put("gmail", user.getGmail());
        values.put("image", user.getImage());
        try {
            if (db.insert(TABLE_NAME, null, values) == -1) {
                return -1;
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        }
        return 1;
    }

    public int updateUser(User user) {
        ContentValues values = new ContentValues();
        values.put("username", user.getUserName());
        values.put("name", user.getName());
        values.put("age", user.getAge());
        values.put("phone", user.getPhone());
        values.put("gmail", user.getGmail());
        values.put("image", user.getImage());
        int result = db.update(TABLE_NAME, values, "username=?", new String[]{user.getUserName()});
        if (result == 0) {
            return -1;
        }
        return 1;
    }

    public List<User> getAllUser() {
        List<User> dsUser = new ArrayList<>();
        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            User ee = new User();
            ee.setUserName(c.getString(0));
            ee.setName(c.getString(1));
            ee.setAge(c.getInt(2));
            ee.setPhone(c.getString(3));
            ee.setGmail(c.getString(4));
            ee.setImage(c.getBlob(5));
            dsUser.add(ee);
            Log.d("//=====", ee.toString());
            c.moveToNext();
        }
        c.close();
        return dsUser;
    }

    //delete
    public void deleteUserByID(String username) {
        db.delete(TABLE_NAME, "username=?", new String[]{username});
    }

}
