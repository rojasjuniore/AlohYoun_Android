package com.aloh.YOU.ui;



import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class HelpBalance extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_settings);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	            // Show the Up button in the action bar.
	            getActionBar().setDisplayHomeAsUpEnabled(true);     
	    }	
    	HelpBalanceFragment prefFragment = new HelpBalanceFragment();
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
	
    //SHOW TOP RIGHT MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	
    	MenuInflater inflater = getMenuInflater();
    	//inflater.inflate(R.menu.instruct, menu);
    	return super.onCreateOptionsMenu(menu);    
    }
    
	
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
