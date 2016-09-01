package com.aloh.YOU.db;

import java.io.File;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


public class DataBaseAdapter {

    private Context context;
    private SQLiteDatabase database;
    private DBHelper dbHelper;
    public DataBaseAdapter(Context context) {
        this.context = context;
    }

    public DataBaseAdapter open() throws SQLException {
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void deleteTable(String tablename){
        database.execSQL("drop table if exists "+tablename+';');
    }
    
    public void createIndividualTable(String query){
        database.execSQL(query);
    }

    public void vanish(){
    	
        database.delete("ivr", null, null);
        database.delete("rates", null, null);
        database.delete("report", null, null);
        database.delete("rent", null, null);
        database.delete("usuario", null, null);
    }

    //RATES - CLEAN TABLE
    public void cleanRates(){

        database.delete("rates", null, null);
    }




    //RATES - INSERT RECORD
    public void insertRate(String cc, String country, Float rate){

        ContentValues initialValues = new ContentValues();
        initialValues.put("cc", cc);
        initialValues.put("country", country);
        initialValues.put("rate", rate);
        database.insert("rates", null, initialValues);
    }

    //RATES - usuario TABLE
    public void cleanUsuario(){

        database.delete("usuario", null, null);
    }

    //Usuario - INSERT RECORD
    public void insertUsuario(String user, String clave){

        ContentValues initialValues = new ContentValues();
        initialValues.put("user", user);
        initialValues.put("clave",clave);
        database.insert("usuario", null, initialValues);
    }


    //usuario - GET ALL RECORDS
    public Cursor getUsuario(){

        String q = "SELECT * FROM usuario";
        return database.rawQuery(q, null);
    }

    //RATES - GET RECORD
    public Cursor getRate(String cc){
    	
    	String q = "SELECT * FROM rates WHERE cc = '" + cc + "' limit 1" ;
    	return database.rawQuery(q, null);
    }
    
    //RATES - GET ALL RECORDS
    public Cursor getRates(){
    	
    	String q = "SELECT * FROM rates" ;
    	return database.rawQuery(q, null);
    }
    
    //Rent TABLE
    private static final String Table4 =
            "create table if not exists rent("
                + "_id String null," //time 
                + "price String null," //call from
                + "number String null," //call from
                + "valid integer null," //call to
                + "time integer null" // operation type | 0 - неизвестно что 1 - пополнение баланса 2 - списание (не звонок) 3 - входящий звонок 4 - исходящий звонок 5 - отзвон 6 - неотвеченый вызов
                +");";
    //REPORT - CLEAN TABLE
    public void cleanReport(){
    	
    	//database.execSQL("DROP TABLE report");
    	//database.execSQL(Table4);
    	database.delete("report", null, null);
    }
    
    //REPORT - INSERT RECORD
    public void insertReport(Long time, String description, String from, String to, String duration, Float amount, String cc, int type){
    	
    	   ContentValues initialValues = new ContentValues();
           initialValues.put("time", time);
           if(description!=null && description.length()>0)
           initialValues.put("description", description);
           if(from!=null && from.length()>0)
           initialValues.put("_from", from);
           if(to!=null && to.length()>0)
           initialValues.put("_to", to);
           if(duration!=null && duration.length()>0)
           initialValues.put("duration", duration);
           //if(amount!=null && amount.length()>0)
           initialValues.put("amount", amount);
           if(cc!=null && cc.length()>0)
           initialValues.put("cc", cc);
           initialValues.put("type", type);
           //Log.e("TYPe:", "kk"+type);
           database.insert("report", null, initialValues);
    }

    
    //RATES - GET ALL RECORDS
    public Cursor getReports(){
    	
    	String q = "SELECT * FROM report order by time DESC" ;
    	return database.rawQuery(q, null);
    }
    
    //RATES - GET ONLY CALL RECORDS
    public Cursor getCalls(){
    	
    	String q = "SELECT * FROM report where type > '2' AND type < '7' order by time DESC limit 100" ;
    	return database.rawQuery(q, null);
    }
    
    
    
    //REPORT - INSERT RECORD
    public void insertRent(String _id, String price, String number, int valid, Long time){

    	   ContentValues initialValues = new ContentValues();
           initialValues.put("_id", _id);
           if(price!=null)
           initialValues.put("price", price);
           if(number!=null)
           initialValues.put("number", number);
           initialValues.put("valid", valid);
           if(time!=null)
           initialValues.put("time", time);
           database.insert("rent", null, initialValues);
    }
    
    
    //RENT - GET ALL RECORDS
    public Cursor getRent(){
    	
    	String q = "SELECT * FROM rent" ;
    	return database.rawQuery(q, null);
    }
    
    //RENTT - CLEAN TABLE
    public void cleanRent(){
    	
    	database.delete("rent", null, null);
    }
    
    void DeleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                DeleteRecursive(child);

        fileOrDirectory.delete();
    }
    
    public ContentValues createContentValues(String category, String summary,
            String description) {
        ContentValues values = new ContentValues();

        return values;
    }
}
