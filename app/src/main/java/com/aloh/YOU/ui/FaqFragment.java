package com.aloh.YOU.ui;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;

import com.aloh.YOU.R;

public class FaqFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener, OnPreferenceClickListener  {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //PREFERENCES FROM XML
        addPreferencesFromResource(R.xml.faq);

        //APPLY ON CLICK LISTENERS
		Preference preference = null;
		for(int i=0;i<9;i++){
			
			preference = findPreference("faqa"+i);
			preference.setOnPreferenceClickListener(this);  
		}
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

	@Override
	public boolean onPreferenceClick(Preference preference) {
	
		
		
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setIcon(R.drawable.ic_launcher);
		alert.setTitle(R.string.faqanws);
		alert.setMessage(getResources().getString(getActivity().getResources().getIdentifier(preference.getKey().toString(), "string", getActivity().getPackageName())));

		alert.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				
		
		  		}});

		alert.show();
		
		//Log.i("jj", preference.getKey());
		//if(preference.getKey().toString().equals("pref1")){
			
		//	startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(C.server+getResources().getString(R.string.link_bal))));
		//}
		return false;
	}

}