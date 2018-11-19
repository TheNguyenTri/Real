package android.trithe.real.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.trithe.real.model.History;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class HistoryDAO {
    private final SQLiteDatabase db;
    public static final String TABLE_NAME = "Historys";
    private static final String Date1 = "day";
    private static final String Time1 = "gio";
    public static final String SQL_HISTORY = "CREATE TABLE Historys (id NCHAR(5) primary key, name NVARCHAR(50), idpet NCHAR(5), day date , gio String);";
    private static final String TAG = "HistoryDAO";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


    public HistoryDAO(Context context) {
        Databasemanager databasemanager = new Databasemanager(context);
        db = databasemanager.getWritableDatabase();
    }

    public List<History> getAllHistory() {
        List<History> dsPlanss = new ArrayList<>();
        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            History ee = new History();
            ee.setId(c.getString(0));
            ee.setName(c.getString(1));
            ee.setIdpet(c.getString(2));
            try {
                ee.setDay(sdf.parse(c.getString(3)));
                ee.setTime(c.getString(4));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dsPlanss.add(ee);
            Log.d("//=====", ee.toString());
            c.moveToNext();
        }
        c.close();
        return dsPlanss;
    }

    public List<History> getAllHistoryAsc() {
        List<History> dsPlanss = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " ORDER BY " + Date1 + " ASC, " + Time1 + " ASC ";
        Cursor c = db.rawQuery(selectQuery, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            History ee = new History();
            ee.setId(c.getString(0));
            ee.setName(c.getString(1));
            ee.setIdpet(c.getString(2));
            try {
                ee.setDay(sdf.parse(c.getString(3)));
                ee.setTime(c.getString(4));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dsPlanss.add(ee);
            Log.d("//=====", ee.toString());
            c.moveToNext();
        }
        c.close();
        return dsPlanss;
    }

    public int insertHistory(History planss) {
        ContentValues values = new ContentValues();
        values.put("id", planss.getId());
        values.put("name", planss.getName());
        values.put("idpet", planss.getIdpet());
        values.put("day", sdf.format(planss.getDay()));
        values.put("gio", planss.getTime());
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
