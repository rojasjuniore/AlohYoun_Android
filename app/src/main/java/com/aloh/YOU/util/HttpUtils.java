package com.aloh.YOU.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.aloh.YOU.db.DataBaseAdapter;
import com.aloh.YOU.ui.ContactsListActivity;


@SuppressLint("SimpleDateFormat")
public class HttpUtils {

	//private static String tag = "";
	
	//IF NET AVAILABLE
	public static boolean isNetAvailable(Context context){
		  
		    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	protected static void fatalError(String e){
		
		//RESET ALL BLOCKS
		getRatesBlock = false;
		getBalanceBlock = false;
		getReportBlock = false;
		getRentBlock = false;
		getRentCCBlock = false;
		setRentBlock = false;
		getChangesBlock = false;
		resetPassBlock = false;
		
		C.log("HttpUtils", "Caught FatalError: "+e);

	}
	
	//MESSAGE EXTRACT FROM XML BODY TAG
	public static String getBody(String temp, String tag){
		
		Pattern p = Pattern.compile("<"+tag+">(.*?)</"+tag+">", Pattern.MULTILINE | Pattern.DOTALL);
		Matcher m = p.matcher(temp);
		String res = "";
		while (m.find()){res = m.group(1);}
		return res;
	}
	
	//MD5 CONVERTER
	public static String convertPassMd5(String pass) {
		
		  String password = null;
		  MessageDigest mdEnc;
		  try {
		    mdEnc = MessageDigest.getInstance("MD5");
		    mdEnc.update(pass.getBytes(), 0, pass.length());
		    pass = new BigInteger(1, mdEnc.digest()).toString(16);
		    while (pass.length() < 32) {
		      pass = "0" + pass;
		    }
		    password = pass;
		  } catch (NoSuchAlgorithmException e1) {
		    e1.printStackTrace();
		  }
		  return password;
	}
	
	//CONVERT DATE TO UNIX TIMESTAMP
	public static long getTimestamp(String date){
		
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date parsedDate = null;
		try {
			parsedDate = dateFormat.parse(date.substring(0, date.length()-4));
			//parsedDate = dateFormat.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
        return parsedDate.getTime()/1000; 
	}
	
	//NOTIFY FRONTEND ABOUT REQUEST RESULTS
	public static void sendAsyncResponse(Context context, int success, String tag){
		
		Intent i = new Intent("Async"); 
		i.putExtra("tag", tag);
	    i.putExtra("success", success);
	 	context.sendBroadcast(i);
	}
	
	//REGISTRATION ON TELEPONT SERVER
	public static boolean register(final Context context, final String email, final String password, final String tag){
		  
			//START BACKGROUND PROCESS
	    	Runnable runnable = new Runnable() {
	        @Override
	        public void run() {
	        	
	        		//INITIALIZE HTTP REQUEST VARIABLES
	        	  	HttpClient httpclient = new DefaultHttpClient();
	        	  	HttpPost httppost = new HttpPost(C.server+"/xml/auth/");
	        	  	
	        	  	try {
	        	  		
	        	  		//CREATE MD5 PASSWORD HASH
	        	  		final String pass = convertPassMd5(password);
	        	    	//SET POST VARIABLES
	        	  		String request = "<telepont><email>"+email+"</email><pass>"+pass+"</pass></telepont>";
	        	  		C.log("request", request);
		    	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		    	        nameValuePairs.add(new BasicNameValuePair("request", request));
		    	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
		    	        HttpResponse response = httpclient.execute(httppost);
		    	        
		    	        //PARSE RESPONSE IF DATA EXISTS
		    	        if(response.getEntity().getContentLength()>0){
		    	        	
		    	        	//CONVERT RESPONSE TO STRING
		    	        	StringBuilder sb = new StringBuilder();
		    	        	try {
		    	        	    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 65728);
		    	        	    String line = null;
	
		    	        	    while ((line = reader.readLine()) != null) {
		    	        	        sb.append(line);
		    	        	    }
		    	        
			    	        	String data = sb.toString();
				  			    C.log("RESPONSE", data);
				  			    
				  			    //TRY TO EXTRACT XML DATA
				  			    if(data.startsWith("<telepont")){
				  				  
				  			    	//GET SUCCESS FLAG
				  			    	int s = Integer.valueOf(getBody(data, "success"));
				  			    	String userid = getBody(data, "userid");
				  			    	if(userid.length()==0) 
				  			    		fatalError("Telepont Server responded with empty userid");
				  			    	
				  			    	//SAVE RESULTS TO LOCAL STORAGE 
				  			    	if(s==1 || s==2){
				  			    		
				  			    		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
				  				    	SharedPreferences.Editor editor = pref.edit();
				  			  			editor.putString("userid", userid);
				  			  			editor.putString("password", pass);//md5 hash
				  			  			editor.putString("passclean", password);//plain password
				  			  			editor.putString("email", email);
				  			  			editor.putBoolean("newPass", false);
				  			  			//WE'LL CHECK IT IN NEXT ACTIVITY
				  			  			editor.putBoolean("emailverified", false);
				  			  			editor.putBoolean("simregistered", false);
				  			  			editor.commit();
				  			  			
				  						isVerified(context, userid, pass, tag, false);
				  						SystemClock.sleep(100);
				  			    	}
				  			    	
				  			    	switch(s){
				  			    	
				  			    	//USER REGISTERED SUCCESSFULLY
				  			    	case 1:
				  			    		sendAsyncResponse(context, s, tag);
				  			    	break;
				  			    	//USER SIGNED IN SUCCESSFULLY
				  			    	case 2:
				  			    		sendAsyncResponse(context, s, tag);
				  			    	break;
				  			    	//USER SIGNED IN WITH INCORRECT PASSWORD
				  			    	case 0:
				  			    		sendAsyncResponse(context, s, tag);
				  			    	break;
				  			    	}
				  			
				  			    	
				  			    }else{

				  					//SUCCESS = 100 is error
				  			    	sendAsyncResponse(context, 100, tag);
				  			    	fatalError("Telepont Server responded with errors");
				  			    }
			  			    
		    	        	}
		    	        	catch (IOException e) { e.printStackTrace(); }
		    	        	catch (Exception e) { e.printStackTrace(); }   
		    	        }
	    	            
	          	  } catch (ClientProtocolException e) {
	          		  
	          		  fatalError(e.toString());
		    	  } catch (IOException e) {
		    		  
		    		  fatalError(e.toString());
					e.printStackTrace();
				  }
	        }
	    	};
	    	new Thread(runnable).start(); 
			return true;
	}
	
	//REGISTRATION ON SERVER
	public static boolean isVerified(final Context context, final String userid, final String pass, final String tag, final boolean broadcast){
		  
			//START BACKGROUND PROCESS
	    	Runnable runnable = new Runnable() {
	        @Override
	        public void run() {
	        	
	        		//INITIALIZE HTTP REQUEST VARIABLES
	        	  	HttpClient httpclient = new DefaultHttpClient();
	        	  	HttpPost httppost = new HttpPost(C.server+"/xml/confirm/");
	        	  	
	        	  	try {
	        	  		
	        	  		//CREATE MD5 PASSWORD HASH
	        	  		//final String pass = convertPassMd5(password);
	        	    	//SET POST VARIABLES

	        	  		String request = "<telepont><userid>"+userid+"</userid><pass>"+pass+"</pass></telepont>";
	        	  		C.log("request", request);
		    	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		    	        nameValuePairs.add(new BasicNameValuePair("request", request));
		    	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
		    	        HttpResponse response = httpclient.execute(httppost);
		    	        
		    	        //PARSE RESPONSE IF DATA EXISTS
		    	        if(response.getEntity().getContentLength()>0){
		    	        	
		    	        	//CONVERT RESPONSE TO STRING
		    	        	StringBuilder sb = new StringBuilder();
		    	        	try {
		    	        	    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 65728);
		    	        	    String line = null;
	
		    	        	    while ((line = reader.readLine()) != null) {
		    	        	        sb.append(line);
		    	        	    }
		    	        
			    	        	String data = sb.toString();
				  			    C.log("VERIFIED RESPONSE", data);
				  			    
				  			    //TRY TO EXTRACT XML DATA
				  			    if(data.startsWith("<telepont")){
				  				  
				  			    	//GET SUCCESS FLAG
				  			    	int s = Integer.valueOf(getBody(data, "confirmed"));
				  			    	//String userid = getBody(data, "userid");
				  			    	//if(userid.length()==0) 
				  			    	//fatalError("Telepont Server responded with empty userid");
				  			    	
				  			    	if(broadcast){
				  			    	switch(s){
				  			    	
				  			    	//USER REGISTERED SUCCESSFULLY
				  			    	case 1:
				  			    		sendAsyncResponse(context, 10, tag);
				  			    	break;
				  			    	//USER SIGNED IN WITH INCORRECT PASSWORD
				  			    	case 0:
				  			    		sendAsyncResponse(context, s, tag);
				  			    	break;
				  			    	}
				  			    	}
				  			    	//SAVE RESULTS TO LOCAL STORAGE 
				  			    	if(s==1){
				  			    		
				  			    		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
				  				    	SharedPreferences.Editor editor = pref.edit();
				  				    	editor.putBoolean("emailverified", true);
				  				    	ContactsListActivity.isVerified=true;
				  			  			
				  			  			editor.commit();
				  			    	}
				  			    	
				  			    }else{
				  			    	sendAsyncResponse(context, 0, tag);
				  			    	fatalError("Telepont Server responded with errors");
				  			    }
			  			    
		    	        	}
		    	        	catch (IOException e) { e.printStackTrace(); }
		    	        	catch (Exception e) { e.printStackTrace(); }   
		    	        }
	    	            
	          	  } catch (ClientProtocolException e) {
	          		  
	          		  fatalError(e.toString());
		    	  } catch (IOException e) {
		    		  
		    		  fatalError(e.toString());
					e.printStackTrace();
				  }
	        }
	    	};
	    	new Thread(runnable).start(); 
			return true;
	}
	
	//GET IVR NUMBERS .. LV - Latvia | RU - Russia | ALL - Every Country | +371 - Latvia | +791636443318 - Russia
	public static boolean getIVR(final Context context, final SharedPreferences pref, final String cc, final String tag){
		  
			//START BACKGROUND PROCESS
	    	Runnable runnable = new Runnable() {
	        @Override
	        public void run() {
	        	
	        		//INITIALIZE HTTP REQUEST VARIABLES
	        	  	HttpClient httpclient = new DefaultHttpClient();
	        	  	HttpPost httppost = new HttpPost(C.server+"/xml/ivr/");
	        	  	
	        	  	try {

	        	    	//SET POST VARIABLES
	        	  		String request = "<telepont><userid>"+pref.getString("userid","")+"</userid><pass>"+pref.getString("password","")+"</pass><country>"+cc+"</country></telepont>";
	        	  		C.log("request IVR", request);
		    	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		    	        nameValuePairs.add(new BasicNameValuePair("request", request));
		    	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
		    	        HttpResponse response = httpclient.execute(httppost);
		    	        
		    	        //PARSE RESPONSE IF DATA EXISTS
		    	        if(response.getEntity().getContentLength()>0){
		    	        	
		    	        	//CONVERT RESPONSE TO STRING
		    	        	StringBuilder sb = new StringBuilder();
		    	        	try {
		    	        	    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 65728);
		    	        	    String line = null;
	
		    	        	    while ((line = reader.readLine()) != null) {
		    	        	        sb.append(line);
		    	        	    }
		    	        
			    	        	String data = sb.toString();
				  			    C.log("RESPONSE IVR", data);
				  			    
				  			    //TRY TO EXTRACT XML DATA
				  			    if(data.startsWith("<telepont")){
				  				  
				  			    	//GET SUCCESS FLAG
				  			    	int s = Integer.valueOf(getBody(data, "success"));
				  			    	SharedPreferences.Editor editor = pref.edit();
				  			    	
				  			        //CLEAR PREV RECORDS
		 		    	            for (String ccl : C.locales) {
		 		    	            	
		 		    	            	if(!pref.getString("IVRB_"+ccl.toUpperCase(),"").equals("")){ // && !pref.getString("IVR_"+ccl.toUpperCase(),"").equals("")
		 		    	            	editor.putString("IVR_"+ccl.toUpperCase(), "");
			 		    		    	editor.putString("IVRB_"+ccl.toUpperCase(), "");
			 		    	            editor.putLong("IVR_T"+ccl.toUpperCase(), 0l);
		 		    	            	}
		 		    	            }
		 		    	        
		 		    	            //editor.commit();
				  			    	if(s==1){
				  			    		
				  			    		 for (int i = -1; (i = data.indexOf("<country>", i + 1)) != -1; ) {
				 		    	            //EXTRACT COUNTRY CODE
				 		    	            char[] buffer = new char[2];
				 		    	            data.getChars(i+9, i+11, buffer, 0);
				 		    	            //EXTRACT COUNTRY CODE
				 		    	            String number = data.substring(data.indexOf("<number>", i + 1)+8, data.indexOf("</number>", i + 10));
				 		    	            C.log(String.valueOf(buffer), number);
				 		    	        
				 		    	            //DB -> SAVE LOCALLY
				 		    	            editor.putString("IVR_"+String.valueOf(buffer), number);
				 		    		    	editor.putString("IVRB_"+String.valueOf(buffer), number);
				 		    	            editor.putLong("IVR_T"+String.valueOf(buffer), (System.currentTimeMillis()/1000));
				 		    	        }
				  			    		//COMMIT CHANGES
				  			    		editor.putLong("ivrCacheTime",(System.currentTimeMillis()/1000));
				  			    		editor.putString("call_prefix", "");
				  			  			editor.commit();
				  			    	}
				  			    	//SystemClock.sleep(1000);
				  			    }else{
				  			    	//sendAsyncResponse(context, 0, tag);
				  			    	fatalError("Telepont Server responded with errors | IVR SYNC");
				  			    }
			  			    
		    	        	}
		    	        	catch (IOException e) { e.printStackTrace(); }
		    	        	catch (Exception e) { e.printStackTrace(); }   
		    	        }
	    	            
	          	  } catch (ClientProtocolException e) {
	          		  
	          		  fatalError(e.toString());
		    	  } catch (IOException e) {
		    		  
		    		  fatalError(e.toString());
					e.printStackTrace();
				  }
	        }
	    	};
	    	new Thread(runnable).start(); 
			return true;
	}
	//GET RATES
	private static boolean getRatesBlock = false;
	public static boolean getRates(final Context context, final DataBaseAdapter db, final SharedPreferences pref, final String cc, final String tag){
		  
			if(getRatesBlock) return false;
			getRatesBlock = true;
			//START BACKGROUND PROCESS
	    	Runnable runnable = new Runnable() {
	        @Override
	        public void run() {
	        	
	        		//INITIALIZE HTTP REQUEST VARIABLES
	        	  	HttpClient httpclient = new DefaultHttpClient();
	        	  	HttpPost httppost = new HttpPost(C.server+"/xml/rates/");
	        	  	
	        	  	try {

	        	  		
	        	    	//SET POST VARIABLES
	        	  		String request = "<telepont><country>"+cc+"</country></telepont>";
	        	  		C.log("request getRates", request);
		    	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		    	        nameValuePairs.add(new BasicNameValuePair("request", request));
		    	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
		    	        HttpResponse response = httpclient.execute(httppost);
		    	        
		    	        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		    	        String body = "", res="";
		    	        while ((body = rd.readLine()) != null){
		    	        	res+=body;
		    	            C.log("HttpResponse", body);
		    	        }
		    	        if(res.length()>5){
		    	        	
			    	        if(cc.equals("ALL")){
				    	        //OPEN DB
				    	        db.open();
				    	        //CLEAN PREVIOUS RECORDS
				    	        db.cleanRates();       
				    	        for (int i = -1; (i = res.indexOf("<code>", i + 1)) != -1; ) {

				    	            char[] buffer = new char[2];
				    	            res.getChars(i+6, i+8, buffer, 0);
				    	            String rate = res.substring(res.indexOf("<rate>", i + 1)+6, res.indexOf("</rate>", i + 10));
		 		    	            //C.log(String.valueOf(buffer), rate);
		 		    	           

				    	            db.insertRate(String.valueOf(buffer), "", Float.valueOf(rate));
				    	        }
				    	        db.close();
				    	        getRatesBlock = false;
				    	        //SAVE RATES SYNC TIME
				    	        SharedPreferences.Editor mEditor = pref.edit();
								mEditor.putLong("ratesCacheTime", (System.currentTimeMillis()/1000));
				    	        mEditor.commit();
				    	        //NOTIFY FRONTEND
				    	        sendAsyncResponse(context, 1, tag);
			    	        }else{
			    	        	
			    	        	getRatesBlock = false;
			    	        	SharedPreferences.Editor mEditor = pref.edit();
			    	        	mEditor.putString("currate", "");
			    	        	mEditor.putString("curop", "");
			    	        	mEditor.putString("curcc", "");
			    	        	mEditor.commit();
			    	            for (int i = -1; (i = res.indexOf("<success>1", i + 1)) != -1; ) {
				    	            //System.out.println(i);
				    	            //char[] buffer = new char[2];
				    	            //res.getChars(i+6, i+8, buffer, 0);
				    	            String country = res.substring(res.indexOf("<country>", i + 1)+9, res.indexOf("</country>", i + 10));
				    	            String provider = res.substring(res.indexOf("<provider>", i + 1)+10, res.indexOf("</provider>", i + 10));					    	          
				    	            String rate = res.substring(res.indexOf("<rate>", i + 1)+6, res.indexOf("</rate>", i + 10));
			 		    	          
				    	            C.log("country"+country, rate);
				    	            mEditor.putString("curcc", country.toLowerCase());
		 		    	            mEditor.putString("curop", provider);
		 		    	            mEditor.putString("currate", rate);
						    	    mEditor.commit();
						    	    sendAsyncResponse(context, 5, tag);
				    	        }
			    	        	
			    	        	
			    	        }
		    	        }
		    	        
	          	  } catch (ClientProtocolException e) {
	          		  
	          		  fatalError(e.toString());
		    	  } catch (IOException e) {
		    		  
		    		  fatalError(e.toString());
					e.printStackTrace();
				  }
	        }
	    	};
	    	new Thread(runnable).start(); 
			return true;
	}
	
	//GET REPORT, BALANCE, CALL HISTORY
	private static boolean getReportBlock = false;
	public static boolean getReport(final Context context, final DataBaseAdapter db, final SharedPreferences pref, final String from, final String tag){
		  
			if(getReportBlock) return false;
			getReportBlock = true;
			//START BACKGROUND PROCESS
	    	Runnable runnable = new Runnable() {
	        @Override
	        public void run() {
	        	
	        		//INITIALIZE HTTP REQUEST VARIABLES
	        	  	HttpClient httpclient = new DefaultHttpClient();
	        	  	HttpPost httppost = new HttpPost(C.server+"/xml/report/");
	        	  	
	        	  	try {

	        	  		
	        	  		Date dt = new Date();
	        			dt.setTime(System.currentTimeMillis()-C.balOfsetTime);
	        	  		Date de = new Date();
	        	  		de.setTime(System.currentTimeMillis()+1000000000l);
	        	  		String st = new SimpleDateFormat("dd.MM.yyyy").format(de);
	        	  		String en = new SimpleDateFormat("dd.MM.yyyy").format(dt);
	        	    	
	        	  		//SET POST VARIABLES
	        	  		String request = "<telepont><userid>"+pref.getString("userid","")+"</userid><pass>"+pref.getString("password","")+"</pass><startdate>"+en+"</startdate><enddate>"+st+"</enddate><unanswered>1</unanswered><answered>1</answered><payments>1</payments><rent>1</rent></telepont>";
	        	  		C.log("request", request);
		    	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		    	        nameValuePairs.add(new BasicNameValuePair("request", request));
		    	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
		    	        HttpResponse response = httpclient.execute(httppost);
		    	        
		    	        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		    	        String body = "", res="";
		    	        while ((body = rd.readLine()) != null){
		    	        	res+=body;
		    	            //C.log("HttpResponse", body);
		    	        }
		    	        if(res.length()>5){
		    	        	
			    	        //if(cc.equals("ALL")){
				    	        //OPEN DB
				    	        db.open();
				    	        //CLEAN PREVIOUS RECORDS
				    	        db.cleanReport();       
				    	        for (int i = -1; (i = res.indexOf("<record>", i + 1)) != -1; ) {
				    	            //System.out.println(i);
				    	            char[] buffer = new char[2];
				    	            res.getChars(i+6, i+8, buffer, 0);
				    	            String date = res.substring(res.indexOf("<date>", i + 1)+6, res.indexOf("</date>", i + 10));
				    	            int gmt = Integer.valueOf(date.substring(date.length()-2, date.length()));
				    	            String gmts = String.valueOf(date.substring(date.length()-3, date.length()-2));
				    	            //NORMALIZE TIME TO TIMESTAMP
				    	            long time = 0;
				    	            if(gmts.equals("-")){
				    	            	time = getTimestamp(date)+(gmt*3600);
				    	            }else{
				    	            	time = getTimestamp(date)-(gmt*3600);
				    	            }
		 		    	        	String description = res.substring(res.indexOf("<description>", i + 1)+13, res.indexOf("</description>", i + 8));		 		    	        	
		 		    	           	String from = res.substring(res.indexOf("<from>", i + 1)+6, res.indexOf("</from>", i + 10));		 		    	           	
		 		    	           	String to = res.substring(res.indexOf("<to>", i + 1)+4, res.indexOf("</to>", i + 8));		 		    	        	
		 		    	            String duration = res.substring(res.indexOf("<duration>", i + 1)+10, res.indexOf("</duration>", i + 8));		 		    	           	
		 		    	            String deb = res.substring(res.indexOf("<db>", i + 1)+4, res.indexOf("</db>", i + 8));		 		    	           	
		 		    	            String cre = res.substring(res.indexOf("<cr>", i + 1)+4, res.indexOf("</cr>", i + 8));     	
		 		    	            String country = res.substring(res.indexOf("<country>", i + 1)+9, res.indexOf("</country>", i + 8));
		 		    	            String type = res.substring(res.indexOf("<type>", i + 1)+6, res.indexOf("</type>", i + 8)); 
		 		    	            
		 		    	            
		 		    	            // C.log("from", " "+from);
		 		    	            // C.log("to"," "+to);
		 		    	            // C.log("date"," "+date);
		 		    	            // C.log("duration"," "+duration);
		 		    	            // C.log("deb"," "+deb);
		 		    	            // C.log("cre"," "+cre);
		 		    	            // C.log("country"," "+country);
		 		    	            // C.log("type"," "+type);
		 		    	 
		 		    	           	
		 		    	           	Float amount;
		 		    	            if(!deb.equals("0.00")){
		 		    	            	amount = Float.valueOf(deb);
		 		    	            }else{
		 		    	            	amount = -Float.valueOf(cre);
		 		    	            }
				    	            db.insertReport(time, description, from, to, duration, amount, country, Integer.valueOf(type));
				    	        }
				    	        db.close();
				    	        getBalance(context, db, pref, tag);
				    	        SystemClock.sleep(1000);
				    	        getReportBlock = false;
				    	        
				    	        //SAVE RATES SYNC TIME
				    	        SharedPreferences.Editor mEditor = pref.edit();
							mEditor.putLong("reportCacheTime", (System.currentTimeMillis()/1000));
				    	        mEditor.commit();
				    	        
				    	        
				    	        //NOTIFY FRONTEND
				    	        sendAsyncResponse(context, 1, tag);
			    	        //}
		    	        }
		    	        
	          	  } catch (ClientProtocolException e) {
	          		  
	          		  fatalError(e.toString());
		    	  } catch (IOException e) {
		    		  
		    		  fatalError(e.toString());
		    		  e.printStackTrace();
				  }
	        }
	    	};
	    	new Thread(runnable).start(); 
			return true;
	}
	
	private static boolean getBalanceBlock = false;
	public static boolean getBalance(final Context context, final DataBaseAdapter db, final SharedPreferences pref, final String tag){
		  
			if(getBalanceBlock) return false;
			getBalanceBlock = true;
			//START BACKGROUND PROCESS
	    	Runnable runnable = new Runnable() {
	        @Override
	        public void run() {
	        	
	        		//INITIALIZE HTTP REQUEST VARIABLES
	        	  	HttpClient httpclient = new DefaultHttpClient();
	        	  	HttpPost httppost = new HttpPost(C.server+"/xml/getbalance/");
	        	  	
	        	  	try {
        	  		
	        	    	//SET POST VARIABLES
	        	  		String request = "<telepont><userid>"+pref.getString("userid","")+"</userid><pass>"+pref.getString("password","")+"</pass></telepont>";
	        	  		C.log("getBalance", request);
		    	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		    	        nameValuePairs.add(new BasicNameValuePair("request", request));
		    	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
		    	        HttpResponse response = httpclient.execute(httppost);
		    	        
		    	        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		    	        String body = "", res="";
		    	        while ((body = rd.readLine()) != null){
		    	        	res+=body;
		    	            C.log("HttpResponse", body);
		    	        }
		    	        if(res.length()>5){
		    	        		
			    	        	getBalanceBlock = false;
			    	        	SharedPreferences.Editor mEditor = pref.edit();
			    	        	//mEditor.putString("currate", "");
			    	        	//mEditor.commit();
			    	            for (int i = -1; (i = res.indexOf("<telepont>", i + 1)) != -1; ) {
				    	            //System.out.println(i);
				    	            char[] buffer = new char[2];
				    	            res.getChars(i+6, i+8, buffer, 0);
				    	            String balance = res.substring(res.indexOf("<balance>", i + 1)+9, res.indexOf("</balance>", i + 10));
		 		    	            //C.log(String.valueOf(buffer), balance);
		 		    	            mEditor.putLong("balCacheTime", (System.currentTimeMillis()/1000));
		 		    	            mEditor.putString("balance", balance);
						    	    mEditor.commit();
						    	    //sendAsyncResponse(context, 5, tag);
				    	        }
		    	        }
		    	        
	          	  } catch (ClientProtocolException e) {
	          		  
	          		  fatalError(e.toString());
		    	  } catch (IOException e) {
		    		  
		    		  fatalError(e.toString());
					e.printStackTrace();
				  }
	        }
	    	};
	    	new Thread(runnable).start(); 
			return true;
	}
	
	private static boolean resetPassBlock = false;
	public static boolean resetPass(final Context context, final SharedPreferences pref, final String newPass, final String tag){
		  
			if(resetPassBlock) return false;
			resetPassBlock = true;
			//START BACKGROUND PROCESS
	    	Runnable runnable = new Runnable() {
	        @Override
	        public void run() {
	        	
	        		//INITIALIZE HTTP REQUEST VARIABLES
	        	  	HttpClient httpclient = new DefaultHttpClient();
	        	  	HttpPost httppost = new HttpPost(C.server+"/xml/resetpass/");
	        	  	
	        	  	final String pass = convertPassMd5(newPass);
	        	  	try {
        	  		
	        	    	//SET POST VARIABLES
	        	  		String request = "<telepont><userid>"+pref.getString("userid","")+"</userid><pass>"+pref.getString("password","")+"</pass><newpass>"+pass+"</newpass></telepont>";
	        	  		C.log("resetPass"+newPass, request);
		    	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		    	        nameValuePairs.add(new BasicNameValuePair("request", request));
		    	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
		    	        HttpResponse response = httpclient.execute(httppost);
		    	        
		    	        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		    	        String body = "", res="";
		    	        while ((body = rd.readLine()) != null){
		    	        	res+=body;
		    	            C.log("HttpResponse", body);
		    	        }
		    	        resetPassBlock = false;
		    	        if(res.length()>5){
		    	        		
		    	        		resetPassBlock = false;
			    	        	SharedPreferences.Editor mEditor = pref.edit();
			    	            if(res.indexOf("<telepont><success>1", 0) != -1) {
				    	          
			    	            	mEditor.putBoolean("newPass", true);
		 		    	            mEditor.putString("password", pass);
						    	    mEditor.commit();
						    	    sendAsyncResponse(context, 1, tag);
						    	    
				    	        }else if(res.indexOf("<telepont><success>0", 0) != -1) {
				    	        	
				    	        	 sendAsyncResponse(context, 0, tag);
				    	        }
		    	        }
		    	        
	          	  } catch (ClientProtocolException e) {
	          		  
	          		  fatalError(e.toString());
		    	  } catch (IOException e) {
		    		  
		    		  fatalError(e.toString());
					e.printStackTrace();
				  }
	        }
	    	};
	    	new Thread(runnable).start(); 
			return true;
	}
	
	//GET LIST OF PHONE NUMBERS IN RENT
	private static boolean getRentBlock = false;
	public static boolean getRent(final Context context, final DataBaseAdapter db, final SharedPreferences pref, final String tag){
		  
			if(getRentBlock) return false;
			getRentBlock = true;
			//START BACKGROUND PROCESS
	    	Runnable runnable = new Runnable() {
	        @Override
	        public void run() {
	        	
	        		//INITIALIZE HTTP REQUEST VARIABLES
	        	  	HttpClient httpclient = new DefaultHttpClient();
	        	  	HttpPost httppost = new HttpPost(C.server+"/xml/getinfo/");
	        	  	
	        	  	try {
        	  		
	        	    	//SET POST VARIABLES
	        	  		String request = "<telepont><userid>"+pref.getString("userid","")+"</userid><pass>"+pref.getString("password","")+"</pass></telepont>";
	        	  		C.log("request", request);
		    	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		    	        nameValuePairs.add(new BasicNameValuePair("request", request));
		    	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
		    	        HttpResponse response = httpclient.execute(httppost);
		    	        
		    	        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		    	        String body = "", res="";
		    	        while ((body = rd.readLine()) != null){
		    	        	res+=body;
		    	            C.log("HttpResponse", body);
		    	        }
		    	        
		    	        if(res.length()>5 && res.startsWith("<telepont>")){
		    	        	
		    	            	db.open();
		    	            	//CLEAN PREVIOUS RECORDS
		    	            	db.cleanRent(); 
			    	        
		    	        		getRentBlock = false;
			    	        	SharedPreferences.Editor mEditor = pref.edit();
			    	        	//mEditor.putString("currate", "");
			    	        	//mEditor.commit();
			    	            for (int i = -1; (i = res.indexOf("<in>", i + 1)) != -1; ) {
				    	            //System.out.println(i);
				    	            //char[] buffer = new char[2];
				    	            //res.getChars(i+6, i+8, buffer, 0);
				    	            String _id = res.substring(res.indexOf("<inid>", i + 1)+6, res.indexOf("</inid>", i+6));
		 		    	            String price = res.substring(res.indexOf("<price>", i + 1)+7, res.indexOf("</price>", i+7));
		 		    	            String date = res.substring(res.indexOf("<date>", i + 1)+6, res.indexOf("</date>", i+6));
		 		    	            String enabled = res.substring(res.indexOf("<enabled>", i + 1)+9, res.indexOf("</enabled>", i+9));
		 		    	            String number = res.substring(res.indexOf("<number>", i + 1)+8, res.indexOf("</number>", i + 8));
		 		    	            
		 		    	            //NORMALIZE TIME TO TIMESTAMP
		 		    	            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		 		    	            Date parsedDate = dateFormat.parse(date.substring(0, date.length()-4));
		 		    	            long time = parsedDate.getTime()/1000;  
		 		    	            
				    	            C.log(tag, number);
		 		    	            db.insertRent(_id, price, number, Integer.valueOf(enabled), time);
		 		    	            
		 		    	  
				    	        }
			    	            getRentCC(context, db, pref, tag); 
			    	            
			    	            db.close();
			    	            mEditor.putLong("rentCacheTime", (System.currentTimeMillis()/1000));
	 		    	            //mEditor.putString("balance", balance);
					    	    mEditor.commit();
					    	    SystemClock.sleep(1000);
					    	    sendAsyncResponse(context, 1, tag);
		    	        }
		    	        
	          	  } catch (ClientProtocolException e) {
	          		 
	          		  fatalError(e.toString());
	          		  e.printStackTrace();
		    	  } catch (IOException e) {
		    		  
		    		  fatalError(e.toString());
		    		  e.printStackTrace();
				  } catch (ParseException e) {
					  
					  fatalError(e.toString());
					  e.printStackTrace();
				  }
	        }
	    	};
	    	new Thread(runnable).start(); 
			return true;
	}
	
	//GET LIST OF COUNTRIES AVAILABLE FOR RENT 
	private static boolean getRentCCBlock = false;
	public static boolean getRentCC(final Context context, final DataBaseAdapter db, final SharedPreferences pref, final String tag){
		  
			if(getRentCCBlock) return false;
			getRentCCBlock = true;
			//START BACKGROUND PROCESS
	    	Runnable runnable = new Runnable() {
	        @Override
	        public void run() {
	        	
	        		//INITIALIZE HTTP REQUEST VARIABLES
	        	  	HttpClient httpclient = new DefaultHttpClient();
	        	  	HttpPost httppost = new HttpPost(C.server+"/xml/getcountries/");
	        	  	
	        	  	try {
        	  		
	        	    	//SET POST VARIABLES
	        	  		String request = "<telepont><userid>"+pref.getString("userid","")+"</userid><pass>"+pref.getString("password","")+"</pass></telepont>";
	        	  		C.log("request", request);
		    	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		    	        nameValuePairs.add(new BasicNameValuePair("request", request));
		    	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
		    	        HttpResponse response = httpclient.execute(httppost);
		    	        
		    	        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		    	        String body = "", res="";
		    	        while ((body = rd.readLine()) != null){
		    	        	res+=body;
		    	            C.log("HttpResponse", body);
		    	        }
		    	        
		    	        if(res.length()>5 && res.startsWith("<telepont>")){
		    	        	
		    	            	getRentCCBlock = false;
			    	        	SharedPreferences.Editor mEditor = pref.edit();
			    	        	//Set<String> hs1 = new HashSet<String>();
			    	        	//Set<String> hs2 = new HashSet<String>();
			    	        	//Set<String> hs3 = new HashSet<String>();
			    	        	Set<String> all = new HashSet<String>();
			    	            for (int i = -1; (i = res.indexOf("<country>", i + 1)) != -1; ){
				    	            
				    	            String cc = res.substring(res.indexOf("<code>", i - 1)+6, res.indexOf("</code>", i-1));
				    	            String price = res.substring(res.indexOf("<price>", i - 1)+7, res.indexOf("</price>", i-1));
				    	            String interval = res.substring(res.indexOf("<interval>", i - 1)+10, res.indexOf("</interval>", i-1));
				    	            
				    	            //hs1.add(cc.toLowerCase());
				    	            //hs2.add(price);
				    	           // hs3.add(interval);
				    	            all.add(cc+"|"+price+"|"+interval);
				    	            //C.log(tag, cc+"|"+price+"|"+interval);
		 		    	   
				    	        }
			    	      
			    	            mEditor.putLong("rentCCCacheTime", (System.currentTimeMillis()/1000));
			    	           
	 		    	            //mEditor.putStringSet("rentcc", hs1);
	 		    	            //mEditor.putStringSet("rentprice", hs2);
	 		    	            //mEditor.putStringSet("rentinterval", hs3); 
	 		    	            mEditor.putStringSet("rentall", all);
					    	    mEditor.commit();
					    	    //sendAsyncResponse(context, 1, tag);
		    	        }
		    	        
	          	  } catch (ClientProtocolException e) {
	          		 
	          		  fatalError(e.toString());
	          		  e.printStackTrace();
		    	  } catch (IOException e) {
		    		  
		    		  fatalError(e.toString());
		    		  e.printStackTrace();
				  } 
	        }
	    	};
	    	new Thread(runnable).start(); 
			return true;
	}
	
	//GET LIST OF COUNTRIES AVAILABLE FOR RENT 
	private static boolean setRentBlock = false;
	public static boolean setRent(final Context context, final DataBaseAdapter db, final SharedPreferences pref, final String CC, final String type, final String tag){
		  
			if(setRentBlock) return false;
			setRentBlock = true;
			//START BACKGROUND PROCESS
	    	Runnable runnable = new Runnable() {
	        @Override
	        public void run() {
	        	
	        		//INITIALIZE HTTP REQUEST VARIABLES
	        	  	HttpClient httpclient = new DefaultHttpClient();
	        	  	HttpPost httppost = new HttpPost(C.server+"/xml/setin/");
	        	  	
	        	  	try {
        	  		
	        	    	//SET POST VARIABLES
	        	  		String request = "<telepont><userid>"+pref.getString("userid","")+"</userid><pass>"+pref.getString("password","")+"</pass><action>"+ type +"</action>"+((type.equals("enable")||type.equals("delete"))?"<inid>"+CC+"</inid>":"<country>"+ CC +"</country>")+"</telepont>";
	        	  		C.log("request", request);
		    	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		    	        nameValuePairs.add(new BasicNameValuePair("request", request));
		    	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
		    	        HttpResponse response = httpclient.execute(httppost);
		    	        
		    	        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		    	        String body = "", res="";
		    	        while ((body = rd.readLine()) != null){
		    	        	res+=body;
		    	            C.log("HttpResponse", body);
		    	        }
		    	        
		    	       
		    	        	
		    	        if(res.length()>5 && res.startsWith("<telepont>")){
		    	        	
		    	        	if(type.equals("add")){
		    	        	if(res.indexOf("<success>1</success>", 9)!=-1){
		    	        	db.open();
		    	            for (int i = -1; (i = res.indexOf("<in>", i + 1)) != -1; ) {
			    	          
		    	            	//EXTRACT TAG VALUES
			    	            String _id = res.substring(res.indexOf("<inid>", i + 1)+6, res.indexOf("</inid>", i+6));
	 		    	            String price = res.substring(res.indexOf("<price>", i + 1)+7, res.indexOf("</price>", i+7));
	 		    	            String date = res.substring(res.indexOf("<date>", i + 1)+6, res.indexOf("</date>", i+6));
	 		    	            String enabled = "1";
	 		    	            String number = res.substring(res.indexOf("<number>", i + 1)+8, res.indexOf("</number>", i + 8));
	 		    	            
	 		    	            //NORMALIZE TIME TO TIMESTAMP
	 		    	            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	 		    	            Date parsedDate = dateFormat.parse(date.substring(0, date.length()-4));
	 		    	            long time = parsedDate.getTime()/1000;  
	 		    	            
	 		    	            
	 		    	            //INSERT TO DB
			    	            //C.log(tag, number);
	 		    	            db.insertRent(_id, price, number, Integer.valueOf(enabled), time);
	 		    	            sendAsyncResponse(context, 2, tag);
			    	        }
		    	            db.close();
		    	        	}else{
		    	        		if(res.indexOf("<success>0</success>", 9)!=-1){
		    	        			sendAsyncResponse(context, 3, tag);
		    	        		}
		    	        		if(res.indexOf("<success>2</success>", 9)!=-1){
		    	        			//C.log(tag, "UUUUUUUU");
		    	        			sendAsyncResponse(context, 4, tag);
		    	        		}
		    	        	}
		    	        	//mEditor.putLong("rentCCCacheTime", (System.currentTimeMillis()/1000));
	 		    	       	//mEditor.putStringSet("rentcc", hs);
		    	        	//mEditor.commit();
					    	//sendAsyncResponse(context, 3, tag);
		    	        	}else if(type.equals("delete")){

		    	        		if(res.indexOf("<success>1</success>", 9)!=-1){
		    	        			sendAsyncResponse(context, 5, tag);
		    	        		}
		    	        		if(res.indexOf("<success>0</success>", 9)!=-1){
		    	        			sendAsyncResponse(context, 3, tag);
		    	        		}
		    	        		if(res.indexOf("<success>2</success>", 9)!=-1){
		    	        			//C.log(tag, "UUUUUUUU");
		    	        			sendAsyncResponse(context, 4, tag);
		    	        		}
		    	        	}else if(type.equals("enable")){

		    	        		if(res.indexOf("<success>1</success>", 9)!=-1){
		    	        			sendAsyncResponse(context, 6, tag);
		    	        		}
		    	        		if(res.indexOf("<success>0</success>", 9)!=-1){
		    	        			sendAsyncResponse(context, 3, tag);
		    	        		}
		    	        		if(res.indexOf("<success>2</success>", 9)!=-1){
		    	        			//C.log(tag, "UUUUUUUU");
		    	        			sendAsyncResponse(context, 4, tag);
		    	        		}
		    	        	}
		    	        }
		    	        setRentBlock = false;
		    	        
	          	  } catch (ClientProtocolException e) {
	          		 
	          		  fatalError(e.toString());
	          		  e.printStackTrace();
		    	  } catch (IOException e) {
		    		  
		    		  fatalError(e.toString());
		    		  e.printStackTrace();
				  } catch (ParseException e) {
					  fatalError(e.toString());
					  e.printStackTrace();
				} 
	        }
	    	};
	    	new Thread(runnable).start(); 
			return true;
	}
	
	//GET SMS NUMBER
	public static boolean getSMS(final Context context, final SharedPreferences pref, final String tag){
		  
	
		//START BACKGROUND PROCESS
    	Runnable runnable = new Runnable() {
        @Override
        public void run() {
        	
        		//INITIALIZE HTTP REQUEST VARIABLES
        	  	HttpClient httpclient = new DefaultHttpClient();
        	  	HttpPost httppost = new HttpPost(C.server+"/xml/getsms/");
        	  	
        	  	try {
    	  		
        	    	//SET POST VARIABLES
        	  		String request = "<telepont><userid>"+pref.getString("userid","")+"</userid><pass>"+pref.getString("password","")+"</pass></telepont>";
        	  		C.log("request", request);
	    	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	    	        nameValuePairs.add(new BasicNameValuePair("request", request));
	    	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
	    	        HttpResponse response = httpclient.execute(httppost);
	    	        
	    	        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	    	        String body = "", res="";
	    	        while ((body = rd.readLine()) != null){
	    	        	res+=body;
	    	            C.log("HttpResponseSMS", body);
	    	        }
	    	        if(res.length()>5){
	    	        		
		    	        	SharedPreferences.Editor mEditor = pref.edit();
		    	            for (int i = -1; (i = res.indexOf("<telepont>", i + 1)) != -1; ) {
		    	            	 
			    	            String sms_gateway = res.substring(res.indexOf("<number>", i + 1)+8, res.indexOf("</number>", i + 10));
	 		    	            if(sms_gateway.length()>4){
	 		    	            	mEditor.putString("sms_gateway", sms_gateway);
	 		    	            	mEditor.commit();
	 		    	            }
			    	        }
	    	        }
	    	        
          	  } catch (ClientProtocolException e) {
          		  
          		  fatalError(e.toString());
	    	  } catch (IOException e) {
	    		  
	    		  fatalError(e.toString());
				e.printStackTrace();
			  }
        }
    	};
    	new Thread(runnable).start(); 
		return true;
	}

	//GET SYSTEM SUPPORT NUMBERS	
	public static boolean getSysNumbers(final Context context, final SharedPreferences pref, final String tag){
		  
		
		//START BACKGROUND PROCESS
    	Runnable runnable = new Runnable() {
        @Override
        public void run() {
        	
        		//INITIALIZE HTTP REQUEST VARIABLES
        	  	HttpClient httpclient = new DefaultHttpClient();
        	  	HttpPost httppost = new HttpPost(C.server+"/xml/getphonebook/");
        	  	
        	  	try {
    	  		
        	    	//SET POST VARIABLES
        	  		String request = "<telepont><userid>"+pref.getString("userid","")+"</userid><pass>"+pref.getString("password","")+"</pass></telepont>";
        	  		C.log("request", request);
	    	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	    	        nameValuePairs.add(new BasicNameValuePair("request", request));
	    	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
	    	        HttpResponse response = httpclient.execute(httppost);
	    	        
	    	        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	    	        String body = "", res="";
	    	        while ((body = rd.readLine()) != null){
	    	        	res+=body;
	    	            C.log("HttpResponseSMS", body);
	    	        }
	    	        
	    	        Set<String> sys1 = new HashSet<String>();
    	        	Set<String> sys2 = new HashSet<String>();
    	        	
	    	        if(res.length()>5){
	    	        		
		    	        	SharedPreferences.Editor mEditor = pref.edit();
		    	        	//GET SYSTEM TAG
		    	        	String  system = res.substring(res.indexOf("<system>", 0), res.indexOf("</system>", 0));
		    	        	//ITERATE NUMBERS
		    	            for (int i = -1; (i = system.indexOf("<number>", i + 1)) != -1; ){
		    	            	 
			    	            String sys_number = res.substring(res.indexOf("<short>", i + 1)+8, res.indexOf("</short>", i + 10));
			    	            String sys_number_type = res.substring(res.indexOf("<description>", i + 1)+8, res.indexOf("</description>", i + 10));
			    	            
			    	            sys1.add(sys_number);
			    	            sys2.add(sys_number_type);
			    	            
			    	            Log.d(tag, sys_number);
			    	            //if(sms_gateway.length()>4){
	 		    	            // 	mEditor.putString("sms_gateway", sms_gateway);
	 		    	            // 	mEditor.commit();
	 		    	            // }
			    	        }
		    	            //SAVE LOCALLY
		    	            mEditor.putStringSet("sys_number", sys1);
		    	            mEditor.putStringSet("sys_desc", sys2);
		    	            mEditor.commit();
	    	        }
	    	        
          	  } catch (ClientProtocolException e) {
          		  
          		  fatalError(e.toString());
	    	  } catch (IOException e) {
	    		  
	    		  fatalError(e.toString());
				e.printStackTrace();
			  }
        }
    	};
    	new Thread(runnable).start(); 
		return true;
	}

	//CHECK IF CHANGES OCCURED
	private static boolean getChangesBlock = false;
	public static boolean getChanges(final Context context, final SharedPreferences pref, final String tag){
		  
			if(getChangesBlock) return false;
			getChangesBlock = true;
			//START BACKGROUND PROCESS
	    	Runnable runnable = new Runnable() {
		        @Override
		        public void run() {
		        	
		        		//INITIALIZE HTTP REQUEST VARIABLES
		        	  	HttpClient httpclient = new DefaultHttpClient();
		        	  	HttpPost httppost = new HttpPost(C.server+"/xml/changes/");
		        	  	
		        	  	try {
	        	  		
		        	    	//SET POST VARIABLES
		        	  		String request = "<telepont><userid>"+pref.getString("userid","")+"</userid><pass>"+pref.getString("password","")+"</pass></telepont>";
		        	  		C.log("request", request);
			    	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			    	        nameValuePairs.add(new BasicNameValuePair("request", request));
			    	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			    	        HttpResponse response = httpclient.execute(httppost);
			    	        
			    	        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			    	        String body = "", res="";
			    	        while ((body = rd.readLine()) != null){
			    	        	res+=body;
			    	            C.log("HttpResponse", body);
			    	        }
			    	        SharedPreferences.Editor mEditor = pref.edit();
			    	        if(res.equals("0")){
			    	        	
			    	        	mEditor.putBoolean("newPass", true);
			    	        	mEditor.commit();
			    	        }
			    	        if(res.length()>5){
			    	        		
			    	        		DataBaseAdapter db = new DataBaseAdapter(context);  
			    	        		getChangesBlock = false;
				    	        	
				    	        	//mEditor.putString("currate", "");
				    	        	//mEditor.commit();
				    	            for (int i = -1; (i = res.indexOf("<telepont>", i + 1)) != -1; ) {
					    	     
					    	            String rates = res.substring(res.indexOf("<rates>", i + 1)+7, res.indexOf("</rates>", i + 7));
					    	            if(!rates.equals(pref.getString("ratesd", ""))){	
					    	            	getRates(context, db, pref, "ALL", tag);
					    	            	mEditor.putString("ratesd", rates);
					    	            }
					    	            
					    	            String ivr = res.substring(res.indexOf("<ivr>", i + 1)+5, res.indexOf("</ivr>", i + 5));							    	      
					    	            if(!ivr.equals(pref.getString("ivrsd", ""))){	
					    	            	getIVR(context, pref, "ALL", tag);
					    	            	mEditor.putString("ivrsd", ivr);
					    	            }
					    	           
						    	        String sms = res.substring(res.indexOf("<sms>", i + 1)+5, res.indexOf("</sms>", i + 5));
						    	        if(!sms.equals(pref.getString("smssd", ""))){	
					    	            	getSMS(context, pref, tag);
					    	            	mEditor.putString("smssd", sms);
					    	            }
						    	        						    
						    	        String sysnumbers = res.substring(res.indexOf("<sysnumbers>", i + 1)+12, res.indexOf("</sysnumbers>", i + 5));							    	       
						    	        if(!sysnumbers.equals(pref.getString("sysnumbers", ""))){	
						    	        	getSysNumbers(context, pref, tag);
						    	        	mEditor.putString("sysnumbers", sysnumbers);
						    	        }
						    	        	
						    	        String getcountries = res.substring(res.indexOf("<getcountries>", i + 1)+9, res.indexOf("</getcountries>", i + 5)); 						    	      
					    	            if(!getcountries.equals(pref.getString("getcountriessd", ""))){	
					    	            	getRentCC(context, db, pref, tag); 
					    	            	mEditor.putString("getcountriessd", getcountries);
					    	            }
						    	        
						    	        String balance = res.substring(res.indexOf("<balance>", i + 1)+9, res.indexOf("</balance>", i + 5));
						    	        if(!balance.equals(pref.getString("balancesd", ""))){	
						    	        	getBalance(context, db, pref, tag);
						    	        	mEditor.putLong("balCacheTime", (System.currentTimeMillis()/1000));
				 		    	            mEditor.putString("balance", balance);
					    	            }
		
					    	            //C.log(tag, rates);
					   
					    	        }
				    	            
				    	            mEditor.putLong("chTime", System.currentTimeMillis()/100);
						    	    mEditor.commit();
						    	    //sendAsyncResponse(context, 5, tag);
			    	        }
			    	        
		          	  } catch (ClientProtocolException e) {
		          		  
		          		  fatalError(e.toString());
			    	  } catch (IOException e) {
			    		  
			    		  fatalError(e.toString());
						e.printStackTrace();
					  }
		        }
		    	};
		    	new Thread(runnable).start(); 
				return true;
	}
		
}
