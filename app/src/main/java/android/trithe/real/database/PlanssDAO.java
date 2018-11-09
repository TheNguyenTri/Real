package android.trithe.real.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.trithe.real.model.Pet;
import android.trithe.real.model.Planss;
import android.util.Log;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class PlanssDAO {
    private final SQLiteDatabase db;
    public static final String TABLE_NAME = "Planss";
    public static final String SQL_PLANSS = "CREATE TABLE Planss (id NCHAR(5) primary key, name NVARCHAR(50), idpet NCHAR(5), day date , gio String);";
    private static final String TAG = "PlanssDAO";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


    public PlanssDAO(Context context) {
        Databasemanager databasemanager = new Databasemanager(context);
        db = databasemanager.getWritableDatabase();
    }

    public List<Planss> getAllPlanss() {
        List<Planss> dsPlanss = new ArrayList<>();
        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            Planss ee = new Planss();
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

    //    delete
    public void deletePlanssByID(String id) {
        db.delete(TABLE_NAME, "id=?", new String[]{id});
    }

    //
//
    public int updatePlanss(String editusername, String name, String idpet, Date day, String gio) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("idpet", idpet);
        values.put("day", sdf.format(day));
        values.put("gio", gio);
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

    public int insertPlanss(Planss planss) {
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
