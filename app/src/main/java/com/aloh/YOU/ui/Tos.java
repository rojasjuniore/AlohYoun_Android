package com.aloh.YOU.ui;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;

public class Tos extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//IN APP BROWSER
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
	    }
        WebView webview = new WebView(this);
        setContentView(webview);
        webview.loadUrl("http://zvoni-v.ru/tos/");

	}

	//SHOW TOP RIGHT MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	
    	MenuInflater inflater = getMenuInflater();
    	//inflater.inflate(R.menu.instruct, menu);
    	return super.onCreateOptionsMenu(menu);    
    }
 
	//Back to home screen	
	public boolean onOptionsItemSelected(MenuItem item){
		
		 switch (item.getItemId()) {
	        default:
	        	this.finish();
	            return super.onOptionsItemSelected(item);
	    }

	}
}