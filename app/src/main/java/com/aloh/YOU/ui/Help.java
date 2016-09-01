package com.aloh.YOU.ui;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.aloh.YOU.db.DataBaseAdapter;
import com.aloh.YOU.R;
public class Help extends Activity {

	Context context;
	SharedPreferences pref;
	DataBaseAdapter db = new DataBaseAdapter(this);
	ListView listView;
	HistoryAdapter adapter;
	IntentFilter filter = new IntentFilter();
	
	//private static String tag = "History";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_history);
		context = getApplicationContext();
		pref = PreferenceManager.getDefaultSharedPreferences(context);
		//listView = (ListView) findViewById(R.id.listView);
		//filter.addAction("Async");
	}
	
	
	@Override
	public void onResume(){
		
	   		super.onResume();
	   		//registerReceiver(receiver, filter);
	   		//prepareListView();    	
	}
	@Override
	protected void onPause() {
	        super.onPause();	        
	      
	}
	//CLICK LISTENERS FROM activity.xml
	public void action1(View v){
		
		//OPEN LINK 
		startActivity(new Intent(this, HelpBalance.class));
		//startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(C.server+getResources().getString(R.string.link_bal))));		  	
	}

	public void callSupport(final String number){
	    	
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setIcon(R.drawable.ic_alert);
			alert.setTitle(R.string.attention);
			alert.setMessage(getResources().getString(R.string.call));

			alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					
					ContactsListActivity.callNow=number;
					Intent it1 = new Intent(context, ContactsListActivity.class);
				  	it1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			  	  	startActivity(it1);
					finish();
			  		}});
			
			alert.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
			
					
					
				  	}});
			alert.show();
	}
	   
	//CREATE TOP RIGHT MENU +  REFRESH BUTTON
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.balance, menu);
		return true;
	}
	
	//TOP RIGHT MENU LISTENER
    @Override
	public boolean onOptionsItemSelected(MenuItem item){
		

    	    // Handle presses on the action bar items
    	    switch (item.getItemId()) {
    	    

    	        default:
    	        	
    	        	return true;
    	    }
    }
}
