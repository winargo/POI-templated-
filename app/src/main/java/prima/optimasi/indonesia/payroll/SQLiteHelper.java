package prima.optimasi.indonesia.payroll;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import prima.optimasi.indonesia.payroll.objects.company;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String LOG = "DatabaseHelper";
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "primahrd";

    // Table Names
    private static final String TABLE_COMPANY = "company";


    // account

    private static final String KEY_COMPANY_ID = "company_id";
    private static final String KEY_COMPANY_NAME = "company_name";
    private static final String KEY_COMPANY_IP = "company_ip";
    private static final String KEY_COMPANY_PORT = "company_port";


    private static final String CREATE_TABLE_COMPANY= "CREATE TABLE "
            + TABLE_COMPANY + "(" + KEY_COMPANY_ID + " TEXT," + KEY_COMPANY_NAME + " TEXT," +  KEY_COMPANY_IP + " TEXT," + KEY_COMPANY_PORT + " INTEGER )";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_COMPANY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPANY);

        onCreate(db);
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    // ------------------------ "account" table methods ----------------//
    public long createAccount(company comp, String user) {
        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues values = new ContentValues();
        values.put(KEY_COMPANY_ID, comp.getCompanyid());
        values.put(KEY_COMPANY_NAME, comp.getCompanyname());
        values.put(KEY_COMPANY_IP, comp.getCompanyip());
        values.put(KEY_COMPANY_PORT, comp.getCompanyport());

        // insert row
        long todo_id = db.insert(TABLE_COMPANY, null, values);

        closeDB();

        return todo_id;
    }


    public List<company> getAllcompany() {
        List<company> todos = new ArrayList<company>();
        String selectQuery = "SELECT  * FROM " + TABLE_COMPANY + " ORDER BY " + KEY_COMPANY_NAME + " ASC";

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        try {
            if (c.moveToFirst()) {
                do {
                    company td = new company();
                    td.setCompanyid(c.getString(c.getColumnIndex(KEY_COMPANY_ID)));
                    td.setCompanyname(c.getString(c.getColumnIndex(KEY_COMPANY_NAME)));
                    td.setCompanyip(c.getString(c.getColumnIndex(KEY_COMPANY_IP)));
                    td.setCompanyport(c.getInt(c.getColumnIndex(KEY_COMPANY_PORT)));
                    // adding to todo list
                    todos.add(td);
                } while (c.moveToNext());
            }
            closeDB();
            return todos;
        } catch (Exception e) {
            closeDB();
            return null;
        }

    }

    public void deleteaccount(String compid) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_COMPANY, KEY_COMPANY_ID + " = ?",
                new String[]{compid});
    }
}