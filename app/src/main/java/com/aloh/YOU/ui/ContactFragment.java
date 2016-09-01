package com.aloh.YOU.ui;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;

import com.aloh.YOU.R;

public class ContactFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener, OnPreferenceClickListener  {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //PREFERENCES FROM XML
        addPreferencesFromResource(R.xml.contact);
        Preference preference = null;
		preference = findPreference("pref1");
        preference.setOnPreferenceClickListener(this);
        preference = findPreference("pref2");
        preference.setOnPreferenceClickListener(this);
        preference = findPreference("pref3");
        preference.setOnPreferenceClickListener(this);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }
    
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		//INITIALIZE DATABASE CLASS
/*	   	
        db.open();
        if(key.equals("pref1")){
        	
        	Boolean res = sharedPreferences.getBoolean(key, false);
        	Log.e("sharedpreference",String.valueOf(res));
        	
        	db.saveSetting(10, String.valueOf(res));
        	
     
        	//String res = sharedPreferences.getString(key, "ddd");
        	//Log.e("sharedpreference",res);
        }else if(key.equals("pref2")){
        	
        	
        }else if(key.equals("pref3")){
        	
        	
        	
        }
        db.close();
 */       
        //Log.i("settings", "preference changed: " + key);

    }

    public void callSupport(final String number){
    	
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setIcon(R.drawable.ic_alert);
		alert.setTitle(R.string.attention);
		alert.setMessage(getResources().getString(R.string.call));

		alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				
				ContactsListActivity.callNow=number;
				Intent it1 = new Intent(getActivity(), ContactsListActivity.class);
			  	it1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		  	  	startActivity(it1);
				getActivity().finish();
		  		}});
		alert.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
		
				
				
			  	}});
		alert.show();
    }
    
	@Override
	public boolean onPreferenceClick(Preference preference) {

        /*
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		if(preference.getKey().toString().equals("pref1")){
			
			Iterator iterator = pref.getStringSet("sys_number", null).iterator();
			String number = iterator.next().toString();
			callSupport(number);
		
		}
		
		if(preference.getKey().toString().equals("pref2")){
			
			//startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(C.server+getResources().getString(R.string.link_bal))));
			Iterator iterator = pref.getStringSet("sys_number", null).iterator();
			iterator.next();
			String number = iterator.next().toString();
			callSupport(number);
		}
		
		if(preference.getKey().toString().equals("pref3")){
	
			startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(C.server+getResources().getString(R.string.link_help))));		  	
		}
		*/
		
		return false;
	}

}