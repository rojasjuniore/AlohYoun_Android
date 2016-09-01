package com.aloh.YOU.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ZVONIVRU";

    private static final int DATABASE_VERSION = 1;

    //TABLA USUARIO
    private static final String TblUser =
            "create table if not exists usuario("
                    + "user String not null," //usuario
                    + "clave String null" //clave
                    +");";
    //IVR NUMBER TABLE
    private static final String Table1 =
            "create table if not exists ivr("
                    + "cc String not null," //country code
                    + "number String null," //country
                    + "data1 integer null," // reserved
                    + "data2 integer null" // reserved
                    +");";

    //CALL RATES TABLE
    private static final String Table2 =
            "create table if not exists rates("
                + "cc String not null," //country code
                + "country String null," //country - not in use
                + "rate float null," // call rate
                + "data1 integer null," // reserved - not in use
                + "data2 integer null" // reserved - not in use
                +");";
    
    //REPORT TABLE
    private static final String Table3 =
            "create table if not exists report("
                + "time integer null," //time 
                + "description String null," //call from
                + "_from String null," //call from
                + "_to String null," //call to
                + "duration String null," //duration
                + "amount Float null," //duration
                + "cc String null," // country code
                + "type integer null" // operation type | 0 - неизвестно что 1 - пополнение баланса 2 - списание (не звонок) 3 - входящий звонок 4 - исходящий звонок 5 - отзвон 6 - неотвеченый вызов
                +");";
    
    
    //RENT TABLE
    private static final String Table4 =
            "create table if not exists rent("
                + "_id String null," //time 
                + "price String null," //call from
                + "number String null," //call from
                + "valid integer null," //call to
                + "time integer null" // operation type | 0 - неизвестно что 1 - пополнение баланса 2 - списание (не звонок) 3 - входящий звонок 4 - исходящий звонок 5 - отзвон 6 - неотвеченый вызов
                +");";
    /*  
    //COUNTRY CODE RENT TABLE
    private static final String Table5 =
            "create table if not exists rentcc("
                + "cc String null" //time 
                +");";
   */ 
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(Table1);
        database.execSQL(Table2);
        database.execSQL(Table3);
        database.execSQL(Table4);
        database.execSQL(TblUser);
    }

    // Method is called during an upgrade of the database, e.g. if you increase
    // the database version
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
            int newVersion) {
        //Log.w(DBHelper.class.getName(),"Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS rates");
        //database.execSQL("DROP TABLE IF EXISTS table2");

        onCreate(database);
    }


        public boolean deleteDatabase(Context context) {
            return context.deleteDatabase(DATABASE_NAME);
        }

}