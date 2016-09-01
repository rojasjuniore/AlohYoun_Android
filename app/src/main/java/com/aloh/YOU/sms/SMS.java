package com.aloh.YOU.sms;

import java.util.ArrayList;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class SMS {

	private Context context;
	public SMS(Context c){
		
		context = c;
	}
	
	public void sendSMS(String phoneNumber, String message){
		
	    ArrayList<PendingIntent> sentPendingIntents = new ArrayList<PendingIntent>();
	    ArrayList<PendingIntent> deliveredPendingIntents = new ArrayList<PendingIntent>();
	    PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(context, SmsSentReceiver.class), 0);
	    PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0, new Intent(context, SmsDeliveredReceiver.class), 0);
	    try {
	        SmsManager sms = SmsManager.getDefault();
	        ArrayList<String> mSMSMessage = sms.divideMessage(message);
	        for (int i = 0; i < mSMSMessage.size(); i++) {
	        	
	            sentPendingIntents.add(i, sentPI);
	            deliveredPendingIntents.add(i, deliveredPI);
	        }
	        sms.sendMultipartTextMessage(phoneNumber, null, mSMSMessage, sentPendingIntents, deliveredPendingIntents);
	        Toast.makeText(context, "SMS sent...",Toast.LENGTH_SHORT).show();
	        Log.i("tel"+phoneNumber, "sms"+message);
	    } catch (Exception e) {

	        e.printStackTrace();
	        Toast.makeText(context, "SMS sending failed...",Toast.LENGTH_SHORT).show();
	    }
	}	
}
