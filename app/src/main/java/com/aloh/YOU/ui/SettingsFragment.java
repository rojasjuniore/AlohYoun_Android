package com.aloh.YOU.ui;


import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import com.aloh.YOU.R;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener, OnPreferenceClickListener  {


	SharedPreferences pref;
	EditTextPreference call_prefix;
	SharedPreferences.Editor mEditor;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        
        //PREFERENCES FROM XML
        addPreferencesFromResource(R.xml.preferences);
        
    
        //GET & SET EXPIRATION TIME FROM PREFERENCE
		pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		mEditor = pref.edit();
		refreshSummary();
		
		///*
		//IVR REFRESH 
        PreferenceScreen updateIVR = (PreferenceScreen) findPreference("updateIVR");
        updateIVR.setOnPreferenceClickListener(new OnPreferenceClickListener() {
       
    
            public boolean onPreferenceClick(Preference preference) {


                return true;
            }

        });
        //*/
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
    
    private void refreshSummary(){
    	
     	EditTextPreference sms_gateway= (EditTextPreference) findPreference("sms_gateway");	        
    	//sms_gateway.setSummary(pref.getString("sms_gateway", C.sms_gateway)+" - "+getResources().getString(R.string.set4));
    	//sms_gateway.setDefaultValue(pref.getString("sms_gateway", C.sms_gateway));
 
        call_prefix = (EditTextPreference) findPreference("call_prefix");	
        call_prefix.setDefaultValue(pref.getString("IVR_"+ContactsListActivity.cc.toUpperCase(), ""));
        if(pref.getString("IVR_"+ContactsListActivity.cc.toUpperCase(), "").length()==0){
        	//call_prefix.setSummary(getResources().getString(R.string.set17));
        }else{
        	//call_prefix.setSummary(pref.getString("IVR_"+ContactsListActivity.cc.toUpperCase(), "")+" - "+getResources().getString(R.string.set6));
        	
        }
    	//call_prefix.setDefaultValue(pref.getString("IVR_"+ContactsListActivity.cc.toUpperCase(), C.sms_gateway));
    	call_prefix.setDefaultValue(pref.getString("IVR_"+ContactsListActivity.cc.toUpperCase(), ""));	
    	mEditor.putString("call_prefix", pref.getString("IVR_"+ContactsListActivity.cc.toUpperCase(), ""));
    	mEditor.commit();
    }
    
    long refreshBlock = 0l;
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    	
    	
    	if(key.equals("call_prefix")){

	    	//if(!sharedPreferences.getString("call_prefix", "").equals("")){
	    		
		    	mEditor.putString("IVR_"+ContactsListActivity.cc.toUpperCase(), sharedPreferences.getString("call_prefix", ""));
			    
		    	mEditor.commit();
	    	//}
    	}
    	
    	
    	
    	
    	//POSTPONE MSG 
    	if((System.currentTimeMillis()-refreshBlock)>1000){
    	
    		refreshBlock = System.currentTimeMillis();
    		//Toast.makeText(getActivity(), getResources().getString(R.string.applied), Toast.LENGTH_SHORT).show();    
    	}
    	refreshSummary();

    }

	@Override
	public boolean onPreferenceClick(Preference preference) {
		// TODO Auto-generated method stub
		return false;
	}

}