package com.aloh.YOU.ui;


import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.aloh.YOU.db.DataBaseAdapter;
import com.aloh.YOU.util.ContactParser;
import com.aloh.YOU.util.HttpUtils;
import com.aloh.YOU.R;
public class History extends Activity {

	Context context;
	SharedPreferences pref;
	DataBaseAdapter db = new DataBaseAdapter(this);
	ListView listView;
	HistoryAdapter adapter;
	IntentFilter filter = new IntentFilter();
	
	private static String tag = "History";
	public static ArrayList<String> desc = new ArrayList<String>();
	public static ArrayList<String> duration = new ArrayList<String>();
	public static ArrayList<String> amount = new ArrayList<String>();
	public static ArrayList<Integer> type = new ArrayList<Integer>();
	public static ArrayList<String> from = new ArrayList<String>();
	public static ArrayList<String> to = new ArrayList<String>();
	public static ArrayList<Long> time = new ArrayList<Long>();	
	public static ArrayList<String> name = new ArrayList<String>();	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_history);
		context = getApplicationContext();
		pref = PreferenceManager.getDefaultSharedPreferences(context);
		listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(new OnItemClickListener(){

			//DISPLAY DETAILED INFO ABOUT PHONE CALL
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				
			   	 //SharedPreferences.Editor editor = pref.edit();
			  	 //editor.putString("call_now_number", IVR);
			  	 ///editor.commit();
				 ContactsListActivity.callNow = from.get(pos);
				 finish();
				 //showDetailed(pos);
			}});
		filter.addAction("Async");
		if(HttpUtils.isNetAvailable(context) && pref.getLong("reportCacheTime", 0l)==0l){
    		
		  	Toast.makeText(context, getResources().getString(R.string.refresh), Toast.LENGTH_SHORT).show();
    		//HttpUtils.getReport(context, db, pref, "-", tag);	
    	}
	}
	

	@Override
	public void onResume(){
		
	   		super.onResume();
	   		registerReceiver(receiver, filter);
	   		prepareListView();    	
	}
	@Override
	protected void onPause() {
		
	        super.onPause();	        
	        try {
	            unregisterReceiver(receiver);
	        } catch (IllegalArgumentException e) {
	        	e.printStackTrace();
	        }    
	}
	
	private void prepareListView(){
		     

	//	/*
		db.open();
		Cursor c = db.getCalls();
		//REASSIGN ARRAYS
		desc = new ArrayList<String>();
		amount = new ArrayList<String>();
		duration = new ArrayList<String>();
		type = new ArrayList<Integer>();
		name = new ArrayList<String>();
		from = new ArrayList<String>();
		to = new ArrayList<String>();
		time = new ArrayList<Long>();
		////////////////////////
		
		/*
		//EXAMPLE LOG WHEN NO DATA IN DB 
		name.add(ContactParser.getContactDisplayNameByNumber("+37122104847", context));
    	desc.add("000");
    	amount.add("0.5");
    	duration.add("12:99");
    	from.add("+37122104847");
    	to.add("+17122104847");
    	type.add(4);
    	time.add(1234567890l);
    	name.add(ContactParser.getContactDisplayNameByNumber("+37126443318", context));
    	desc.add("000");
    	amount.add("2.5");
    	duration.add("12:99");
    	from.add("+37122104847");
    	to.add("+17122104847");
    	type.add(5);
    	time.add(1234567890l);
    	name.add(ContactParser.getContactDisplayNameByNumber("+37126443318", context));
    	desc.add("000");
    	amount.add("2.4");
    	duration.add("12:99");
    	from.add("+37122104847");
    	to.add("+17122104847");
    	type.add(6);
    	time.add(1234567890l);
    	*/
    	int count = 0;
    	//c.moveToLast();
	    while(c.moveToNext()){
 
	    	String fromt = "";
	     	if(c.getInt(c.getColumnIndexOrThrow("type"))==4 || c.getInt(c.getColumnIndexOrThrow("type"))==5){
    			
		    	fromt = c.getString(c.getColumnIndexOrThrow("_to"));
	    		to.add(c.getString(c.getColumnIndexOrThrow("_from")));
	    		
	    		String t = ContactParser.getContactDisplayNameByNumber("+"+c.getString(c.getColumnIndexOrThrow("_to")), context);
	    		if(t.length()<1)
	    			t = c.getString(c.getColumnIndexOrThrow("_to"));
	    		name.add(t);
	    	}else{
	    	
	    		
	        	fromt = c.getString(c.getColumnIndexOrThrow("_from"));
		    	to.add(c.getString(c.getColumnIndexOrThrow("_to")));
		    	
		    	String t = ContactParser.getContactDisplayNameByNumber("+"+c.getString(c.getColumnIndexOrThrow("_from")), context);
	    		if(t.length()<1)
	    			t = c.getString(c.getColumnIndexOrThrow("_from"));
				name.add(t); 
	    	}
	     	from.add(fromt);
	     	
	    	//Log.e(tag, "---sdfghjklgfjkl");
	    	
	    	desc.add(c.getString(c.getColumnIndexOrThrow("description")));
	    	amount.add(c.getString(c.getColumnIndexOrThrow("amount")));
	    	duration.add(c.getString(c.getColumnIndexOrThrow("duration")));
	   
	    	type.add(c.getInt(c.getColumnIndexOrThrow("type")));
	    	time.add(c.getLong(c.getColumnIndexOrThrow("time")));
	    	count++;
        }
	    
	    //NOTIFY IF CALL LOG IS EMPTY
	    if(count==0 && pref.getLong("reportCacheTime", 0l)!=0l)
	    	Toast.makeText(context, getResources().getString(R.string.empty), Toast.LENGTH_LONG).show();      
		
	    c.close();
		db.close();
		//GET RATES FROM SERVER
		//if(HttpUtils.isNetAvailable(context) && (pref.getLong("ratesCacheTime",0l)<((System.currentTimeMillis()/1000) - C.ratesCacheTime)))
		//HttpUtils.getRates(context, db, pref, "ALL", tag);
		//BIND DATA WITH ADAPTER
		adapter = new HistoryAdapter (this, R.layout.activity_history_row, from, to);
        listView.setAdapter(adapter);
        
        
        //getSyncTime("reportCacheTime");
     //   */
	}
	
	//RECEIVE ASINHRONOUS RESULTS FROM BACKGROUND 
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
    	

      }
  	};
  	

	
	public void clear_log(){
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setIcon(R.drawable.ic_alert);
		alert.setTitle(R.string.attention);
		alert.setMessage(getResources().getString(R.string.min13));
		alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				
				db.open();
				db.cleanReport();
				db.close();
				desc = new ArrayList<String>();
				amount = new ArrayList<String>();
				duration = new ArrayList<String>();
				type = new ArrayList<Integer>();
				name = new ArrayList<String>();
				from = new ArrayList<String>();
				to = new ArrayList<String>();
				time = new ArrayList<Long>();
				adapter = new HistoryAdapter (context, R.layout.activity_history_row, from, to);
		        listView.setAdapter(adapter);
		        Toast.makeText(getApplicationContext(), getResources().getString(R.string.min15), Toast.LENGTH_SHORT).show();
		        
				//listView.notify();
				
		  		}});
		alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
		
				
				
			  	}});
		alert.show();
	
	}
	
	//CREATE TOP RIGHT MENU +  REFRESH BUTTON
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.history, menu);
		return true;
	}
	
	//TOP RIGHT MENU LISTENER
    @Override
	public boolean onOptionsItemSelected(MenuItem item){
		

    	    // Handle presses on the action bar items
    	    switch (item.getItemId()) {
    	    
    	        case R.id.trash:
    	    	
    	        	clear_log();
    	        	//refresh();
    	            return true;
  
    	        default:
    	        	
    	        	return true;
    
    	    }
    }
}
