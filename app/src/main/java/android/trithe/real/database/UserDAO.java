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
    public static final String SQL_USER = "CREATE TABLE User (username NVARCHAR(50) primary key, password NVARCHAR(50),  name NVARCHAR(50), image blob, phone NCHAR(10));";
    private static final String TAG = "UserDAO";

    public UserDAO(Context context) {
        Databasemanager databasemanager = new Databasemanager(context);
        db = databasemanager.getWritableDatabase();
    }

    public int inserUser(User user) {
        ContentValues values = new ContentValues();
        values.put("username", user.getUserName());
        values.put("password", user.getPassword());
        values.put("name", user.getName());
        values.put("image", user.getImage());
        values.put("phone", user.getPhone());
        try {
            if (db.insert(TABLE_NAME, null, values) == -1) {
                return -1;
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        }
        return 1;
    }

    //update
//    public int updateUser(User user) {
//        ContentValues values = new ContentValues();
//        values.put("username", user.getUserName());
//        values.put("password", user.getPassword());
//        values.put("name", user.getName());
//        values.put("phone", user.getPhone());
//        int result = db.update(TABLE_NAME, values, "username=?", new String[]{user.getUserName()});
//        if (result == 0) {
//            return -1;
//        }
//        return 1;
//    }

    public List<User> getAllUser() {
        List<User> dsUser = new ArrayList<>();
        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            User ee = new User();
            ee.setUserName(c.getString(0));
            ee.setPassword(c.getString(1));
            ee.setName(c.getString(2));
            ee.setImage(c.getBlob(3));
            ee.setPhone(c.getString(4));
            dsUser.add(ee);
            Log.d("//=====", ee.toString());
            c.moveToNext();
        }
        c.close();
        return dsUser;
    }

    public String login(String username) {
        String query = "SELECT username, password from " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        String a;
        String b;
        b = "Not found";
        if (cursor.moveToFirst()) {
            do {
                a = cursor.getString(0);
                if (a.equals(username)) {
                    b = cursor.getString(1);
                    break;
                }
            }
            while (cursor.moveToNext());
        }
        return b;
    }


    public int updateUser(String editusername, String name, String phone) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("phone", phone);
        int result = db.update(TABLE_NAME, values, "username=?", new
                String[]{editusername});
        if (result == 0) {
            return -1;
        }
        return 1;
    }

//    //check login
//    public int checkLogin(String username, String password) {
//        int result = db.delete(TABLE_NAME, "username=? AND password=?", new String[]{username, password});
//        if (result == 0)
//            return -1;
//        return 1;
//    }

    public int changePasswordNguoiDung(User nd) {
        ContentValues values = new ContentValues();
        values.put("username", nd.getUserName());
        values.put("password", nd.getPassword());
        int result = db.update(TABLE_NAME, values, "username=?", new
                String[]{nd.getUserName()});
        if (result == 0) {
            return -1;
        }
        return 1;
    }
}
