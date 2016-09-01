package com.aloh.YOU.ui;



import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class Contact extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_settings);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	            // Show the Up button in the action bar.
	            getActionBar().setDisplayHomeAsUpEnabled(true);     
	    }	
    	ContactFragment prefFragment = new ContactFragment();
    	FragmentManager fragmentManager = getFragmentManager();
    	FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    	fragmentTransaction.replace(android.R.id.content, prefFragment);
    	fragmentTransaction.commit();
    	
	}
	@Override
	public void onBackPressed() {
	    	
	    	this.finish();
	        return;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
	
    @Override
	public boolean onOptionsItemSelected(MenuItem item){
		

    	    // Handle presses on the action bar items
    	    switch (item.getItemId()) {           
    	        //case R.id.help:
    	            //showHelp();
    	        //    return true;
    	        default:
    	        	this.finish();
    	            return super.onOptionsItemSelected(item);
    	    }
    }
}
