package com.aloh.YOU.ui;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;


public class Settings extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
           
        }	
        
    	SettingsFragment prefFragment = new SettingsFragment();
    	FragmentManager fragmentManager = getFragmentManager();
    	FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    	fragmentTransaction.replace(android.R.id.content, prefFragment);
    	fragmentTransaction.commit();
    	
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		SharedPreferences.Editor editor = pref.edit();
		editor.putBoolean("changes", true);
        editor.commit();
    }
    @Override
    public void onBackPressed() {
    	
    	this.finish();
        return;
    }
	//Back to home screen + TOP RIGHT MENU LISTENER
    @Override
	public boolean onOptionsItemSelected(MenuItem item){
		

    	    // Handle presses on the action bar items
    	    switch (item.getItemId()) {
  
    	        default:
    	        	this.finish();
    	            return super.onOptionsItemSelected(item);
    	    }
    }
}
