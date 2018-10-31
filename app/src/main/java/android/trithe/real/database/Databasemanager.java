package android.trithe.real.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class Databasemanager extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "p3";
    private static final int VERSION = 1;

    public Databasemanager(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UserDAO.SQL_USER);
        db.execSQL(DogDAO.SQL_DOG);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop table if exists " + UserDAO.TABLE_NAME);
        db.execSQL("Drop table if exists " + DogDAO.TABLE_NAME);
        onCreate(db);
    }
}
