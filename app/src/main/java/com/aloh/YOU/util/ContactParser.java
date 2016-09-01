package com.aloh.YOU.util;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.aloh.YOU.R;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.ContactsContract;

public class ContactParser {

    //EXTRACT CONTACT
	public Map<String, String> QRparser(String[] titles, String scan_data_clean_in, char delim){
	    
		Map<String, String> result = new HashMap<String, String>();
	    for(String title : titles) {
	
	        int start = scan_data_clean_in.indexOf(title);
	        if(start!=-1){
	        for (int i=start;i<scan_data_clean_in.length();i++)
	        {
	            //SEARCH TILL FIRST OCCURANCE OF SEPARATOR
	            if (i>0 && scan_data_clean_in.charAt(i)==delim)
	            {
	                //JUST IN CASE SEPARATOR WAS ESCAPED | TRIGER BETWEEN SEPARETORS ';' 'r'
	                if (((i>0 && scan_data_clean_in.charAt(i-1)!='\\') && delim==';') || ((i>0 && (int)scan_data_clean_in.charAt(i-1)!=13) && delim==10))
	                {
	                	String value = scan_data_clean_in.substring(start+title.length(), i);
	                	result.put(title,value);
	                    break;
	                //IN CASE TWO END CHARACTERS TO BE REMOVED /r/n
	                }else if(i>1 && delim==10 && (int)scan_data_clean_in.charAt(i-1)==13){
	                    
	                    String value = scan_data_clean_in.substring(start+title.length(), (i-1));
	                    result.put(title,value);
	                    break;
	                }
	            }
	        }
	        }
	        
	      }
	      return result;
	}
	//GET CONTACT BY PHONE NUMBER
	public static String getContactDisplayNameByNumber(String number, Context context){
		
		    Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
		    String name = "";
	
		    //ContentResolver contentResolver = getContentResolver();
		    Cursor contactLookup = context.getContentResolver().query(uri, new String[] {BaseColumns._ID,
		            ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);
	
		    try {
		        if (contactLookup != null && contactLookup.getCount() > 0) {
		            contactLookup.moveToNext();
		            name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
		            //String contactId = contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
		        }
		    } finally {
		        if (contactLookup != null) {
		            contactLookup.close();
		        }
		    }
	
		    return name;
	}
	//GET CONTACT BY PHONE BOOK ID
	public static String getContactNameById(Integer _id, Context context){
		
			//Map<String, String> result = new HashMap<String, String>();

	    	String cid = _id.toString(); 
		 	//String contactNumber = "";
		 	String contactName = "";
	    	Cursor cursor = context.getContentResolver().query(  
	    			ContactsContract.Contacts.CONTENT_URI, null,  
	    			ContactsContract.Contacts._ID + "=?",  
	    	        new String[]{cid}, null); 
	    	
	    	//Log.e("PARSER", "1");
	    	if(cursor.moveToFirst()){
	        //String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
	
	        //if(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
	        //{
		
		
	    	//Log.e("PARSER", "2");
	    	
	    	contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
	    	//contactName = cursor.getString(ContactsQuery.DISPLAY_NAME);
            Cursor pCur = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",new String[]{ _id.toString() }, null);
            while (pCur.moveToNext()){
            	
            	
               // contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
               // contactName = pCur.getString(pCur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                
                //Log.e("PARSER"+contactNumber, "Name"+contactName);
                //result.put(contactNumber, contactName);
            }         
            pCur.close();
	        //}
	        
			}
	    	cursor.close();
            return contactName;
	}
	//GET NUMBER BY PHONE BOOK ID
	public static String getPhoneById(String cid, Context context){
		

		 	String contactNumber = "";
		 	//String contactName = "";
	    	Cursor cursor = context.getContentResolver().query(  
	    			ContactsContract.Contacts.CONTENT_URI, null,  
	    			ContactsContract.Contacts._ID + "=?",  
	    	        new String[]{cid}, null); 
	    	
	    	//Log.e("PARSER", "1");
	    	if(cursor.moveToFirst()){
	
	    	//contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

            Cursor pCur = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",new String[]{ cid }, null);
            while (pCur.moveToNext()){
            	
            	
                contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                contactNumber = contactNumber.replaceAll(" ", "");
                // contactName = pCur.getString(pCur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                //Log.e("PARSER"+contactNumber, "Name"+contactNumber);
                //result.put(contactNumber, contactName);
            }         
            pCur.close();
	        //}
	        
			}
	    	cursor.close();
            return contactNumber;
	}
	//PARSE TIMESTAMP TO NICE DATE
	public static String date(Long time, Context context){
		
        //FORMAT TIME
		String res = "";
        Long tsLong = System.currentTimeMillis()/1000;	
       	long dv = Long.valueOf(time)*1000;// its need to be in milisecond
    	Date df = new java.util.Date(dv);
    	//LESS THAN 24
    	if((tsLong-time)<86400){
    		DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);
    		res = timeFormat.format(df);
    	}else{
    		DateFormat dateFormat = android.text.format.DateFormat.getMediumDateFormat(context);
    		res = dateFormat.format(df);
    	}	
    	if(time.equals(0l)){
    		
    		res = context.getResources().getString(R.string.never);
    	}
    	return res;
	}
	//CREATE DIRECTORY IF NOT EXISTS
	public static Boolean dir(String str){
		
		//File dir_root = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/kenzap");
		//if (!dir_root.exists()){dir_root.mkdir();Log.e("DIRECROY", "MAKING_ROOT");}
		
	    File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + str);
	    if (!dir.exists()){
	    	dir.mkdirs();
	    	//Log.e("DIRECTORY", "MAKING");
	    	}

		//Log.e("DIRECROY", "YES");
//Log.e("DIRECROY", "NO");
		return dir.exists();
	}
	
	public static String html_decode(String t){
		
		
		t = t.replaceAll("&quot;", "\"");
		return t;
	}
	
	public static String sign(Context context, String amount){

		if(amount.equals("0")){amount = "0.00";}
		if(amount.contains("-")){
			//amount = "-"+context.getResources().getString(R.string.sign)+amount.replace("-","");
		}else{	
			//amount = context.getResources().getString(R.string.sign)+amount;
		}
		return amount;
	}
	
}
