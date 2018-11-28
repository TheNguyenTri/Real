package android.trithe.real.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class Databasemanager extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "7p";
    private static final int VERSION = 1;

    public Databasemanager(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TypeDAO.SQL_TYPEPET);
        db.execSQL(PetDAO.SQL_PET);
        db.execSQL(PlanssDAO.SQL_PLANSS);
        db.execSQL(HistoryDAO.SQL_HISTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop table if exists " + TypeDAO.TABLE_NAME);
        db.execSQL("Drop table if exists " + PetDAO.TABLE_NAME);
        db.execSQL("Drop table if exists " + PlanssDAO.TABLE_NAME);
        db.execSQL("Drop table if exists " + HistoryDAO.TABLE_NAME);
           onCreate(db);
    }
}
