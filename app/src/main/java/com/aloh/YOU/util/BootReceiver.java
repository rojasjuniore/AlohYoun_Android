package com.aloh.YOU.util;

import com.aloh.YOU.ui.ContactsListFragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
//IS INVOKED WHEN DEVICE TRUNS ON
public class BootReceiver extends BroadcastReceiver {   

    @Override
    public void onReceive(Context context, Intent intent) {

    	//WE ASSUME THAT PROBABLY SIM WAS CHANGED
    	//Log.e("zvonivru", "BOOT");
    	//SET SIM REGISTER TRIGER
      	SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
      	SharedPreferences.Editor mEditor = pref.edit();
      	mEditor.putBoolean("simregistered", false);
      	mEditor.putString("ccmanual", "");
      	mEditor.commit();
      	
      	ContactsListFragment.resetSimRegistered(context);
    
    }
}