package com.aloh.YOU.ui;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.ParseException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.aloh.YOU.db.DataBaseAdapter;
import com.aloh.YOU.gps.GPSTracker;
import com.aloh.YOU.io.apiRetro;
import com.aloh.YOU.io.models.apiConstan;
import com.aloh.YOU.util.C;
import com.aloh.YOU.util.Utils;
import com.aloh.YOU.R;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ContactsListActivity extends FragmentActivity implements
	ContactsListFragment.OnContactsInteractionListener,
	ActionBar.OnNavigationListener {

	static Context context;
	SharedPreferences pref;
	SharedPreferences.Editor mEditor;
	LocationManager locationManager;
	GPSTracker gps;
	

	TextView textview2;
	static TableLayout dialpad;
	static LinearLayout call_cont, cont_number, del, cont;
	static String cc = "";
	static ImageView updown;
	public static EditText phoneNumber;
	IntentFilter filter = new IntentFilter();
	String[] list = new String[C.locales.length];
	public static HashMap<String, String> listH = new HashMap<String, String>();
	public static ArrayList<String> listA = new ArrayList<String>();
	
    // Defines a tag for identifying log entries
    private static final String tag = "ContactsListActivity";
    private ContactDetailFragment mContactDetailFragment;
    // If true, this is a larger screen device which fits two panes
    private boolean isTwoPaneLayout;
    private boolean trZeroButton = false;
    // True if this activity instance is a search result view (used on pre-HC devices that load
    // search results in a separate instance of the activity rather than loading results in-line
    // as the query is typed.
    
    private boolean isSearchResultView = false;
    public static boolean isVerified = true;
    public static boolean AUTH = false;
    public static boolean phonebook = true, ccpopup = false;
    public static Uri curUser = null;
    LinearLayout oView;
    public static String callNow = null;
    public static String phoneGlobal = "";
    public static long resumeBlock = 0l;
	public static long syncBlock = 0l;
	public static String NOrigen="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Titulo de Saldo
        setTitle("Saldo: " + getIntent().getExtras().getString("saldo"));

		//SACAR EL NUIMERO ORIGEN
		Retrofit restAdapter = new Retrofit.Builder().baseUrl(apiConstan.URL_BASE).addConverterFactory(GsonConverterFactory.create()).build();
		apiRetro callback = restAdapter.create(apiRetro.class);
		Call<ResponseBody> call = callback.getNumero(getIntent().getExtras().getString("USUARIO"));
		call.enqueue(new Callback<ResponseBody>() {
			@Override
			public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
				try {
					NOrigen = response.body().string();
				}catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(Call<ResponseBody> call, Throwable t) {

			}
		});

		//SYSTEM VARIABLES
        context = getApplicationContext();
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        mEditor = pref.edit();
        filter.addAction("Async");
        registerReceiver(receiver, filter);
  
        
        setContentView(R.layout.activity_main);
        
        
 	 	//GET COUNTRY CODE BASED ON LOCAL SIM CARD
     	cc = getUserCountry(context);
 		if(cc==null){cc = pref.getString("CC", "");}
 		if(!pref.getString("ccmanual", "").equals("")){
 			
 			cc = pref.getString("ccmanual", "");
 		}
 		
        //BIND VIEW ELEMENTS TO JAVA CLASS VARIABLES
        cont = (LinearLayout)findViewById(R.id.cont);
        cont_number = (LinearLayout)findViewById(R.id.cont_number);
        del = (LinearLayout)findViewById(R.id.del);
        phoneNumber = (EditText)findViewById(R.id.phoneNumber);
     	dialpad = (TableLayout)findViewById(R.id.dialpad);
     	call_cont = (LinearLayout)findViewById(R.id.call_cont);
     	updown = (ImageView)findViewById(R.id.updown);
    
     	

        //FIRST TIME?
        if(pref.getBoolean("firsttime",true)){
        	
        	mEditor.putBoolean("firsttime", false);
        	mEditor.commit();
        }

        
        
       	//DIALPAD BUTTON ARRAY
    	String[] dialpad_buttons = {"0","1","2","3","4","5","6","7","8","9","0","*","#"};
    	int[] dialpad_buttons_ids = {R.id.button0,R.id.button1,R.id.button2,R.id.button3,R.id.button4,R.id.button5,R.id.button6,R.id.button7,R.id.button8,R.id.button9,R.id.button0,R.id.buttonstar,R.id.buttonpound};
    	//SET CLICK LISTENERS FOR EACH DIALPAD BUTTON
    	for(int c=0;c<dialpad_buttons_ids.length;c++){
    		
    		ImageButton t = (ImageButton)findViewById(dialpad_buttons_ids[c]);
    		t.setTag(dialpad_buttons[c]);
    		t.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p) {
					
					if(trZeroButton){ trZeroButton = false; return; }
					int sel = phoneNumber.getSelectionStart();
					//APPEND CHARACTE TO CREATE PHONE NUMBER
					String s1 = phoneNumber.getText().toString().substring(0, sel);
					String s2 = phoneNumber.getText().toString().substring(sel, phoneNumber.getText().length());
					String temp = s1+p.getTag().toString()+s2;
					
					//String temp = phoneNumber.getText().toString();
					//temp+=p.getTag().toString();
					dialpadPress(temp);
					//MOVE CURSOR TO AN END
					phoneNumber.setSelection(sel+1);
					//phoneNumber.setSelection(phoneNumber.getText().length());
				}});
    	}
    	
    	//SET LONG CLICK LISTENER FOR PLUS
    	ImageButton button0 = (ImageButton)findViewById(R.id.button0);
    	button0.setOnLongClickListener(new OnLongClickListener(){

			@Override
			public boolean onLongClick(View paramView) {
			
				int sel = phoneNumber.getSelectionStart();
				trZeroButton = true;
				//APPEND CHARACTE TO CREATE PHONE NUMBER
				String s1 = phoneNumber.getText().toString().substring(0, sel);
				String s2 = phoneNumber.getText().toString().substring(sel, phoneNumber.getText().length());
				String temp = s1+"+"+s2;
				
				//String temp = phoneNumber.getText().toString();
				//temp+="+";
				
				
				dialpadPress(temp);
				//MOVE CURSOR TO AN END OF EDIT TEXT
				//phoneNumber.setSelection(phoneNumber.getText().length());
				phoneNumber.setSelection(sel+1);
				return false;
			}});
    	
    
    	//LONG PRESS COMPLETELY REMOVE PHONE NUMBER FOMR EDIT TEXT 
    	del.setOnLongClickListener(new OnLongClickListener(){

			@Override
			public boolean onLongClick(View paramView) {
				
				//C.log("ggg", "LOJNG PRESSEDE");
				dialpadPress("");
				return true;
			}});
    	
    	
    	//ANDROID BUG - DISABLE KEYBOARD POPUP WITH ANY METHOD..
    	//phoneNumber.setInputType(0);
    	phoneNumber.setLongClickable(false);
    	disableSoftInputFromAppearing(phoneNumber);

    	
    	//phoneNumber.setKeyListener(null);
        // Check if two pane bool is set based on resource directories
        isTwoPaneLayout = getResources().getBoolean(R.bool.has_two_panes);

        // Check if this activity instance has been triggered as a result of a search query. This
        // will only happen on pre-HC OS versions as from HC onward search is carried out using
        // an ActionBar SearchView which carries out the search in-line without loading a new
        // Activity.
        if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {

            // Fetch query from intent and notify the fragment that it should display search
            // results instead of all contacts.
            String searchQuery = getIntent().getStringExtra(SearchManager.QUERY);
            ContactsListFragment mContactsListFragment = (ContactsListFragment) getSupportFragmentManager().findFragmentById(R.id.contact_list);

            // This flag notes that the Activity is doing a search, and so the result will be
            // search results rather than all contacts. This prevents the Activity and Fragment
            // from trying to a search on search results.
            isSearchResultView = true;
            mContactsListFragment.setSearchQuery(searchQuery);

            // Set special title for search results
            String title = getString(R.string.contacts_list_search_results_title, searchQuery);
            setTitle(title);
        }

        if (isTwoPaneLayout) {
            // If two pane layout, locate the contact detail fragment
            mContactDetailFragment = (ContactDetailFragment)
                    getSupportFragmentManager().findFragmentById(R.id.contact_detail);
        }    
       // }
        
        
        


    	listH = new HashMap<String, String>();
    	listA = new ArrayList<String>();
    	


    }
    
    public static void disableSoftInputFromAppearing(EditText editText) {
        if (Build.VERSION.SDK_INT >= 11) {
            editText.setRawInputType(InputType.TYPE_CLASS_TEXT);
            editText.setTextIsSelectable(true);
        } else {
            editText.setRawInputType(InputType.TYPE_NULL);
            editText.setFocusable(true);
        }
    }
    
	@SuppressLint("SimpleDateFormat") @Override
	protected void onResume() {
	        super.onResume();
	    	registerReceiver(receiver, filter);
	    	
	    	String valid_until = "01/05/2015";
	    	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	    	Date strDate = null;
	    	try {
	    	    strDate = sdf.parse(valid_until);
	    	} catch (ParseException e) {
	    	    e.printStackTrace();
	    	} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
	    	Calendar mCalendar = new GregorianCalendar();  
	    	TimeZone mTimeZone = mCalendar.getTimeZone();  
	    	int mGMTOffset = mTimeZone.getRawOffset();  
	    	C.gmt = TimeUnit.HOURS.convert(mGMTOffset, TimeUnit.MILLISECONDS)*3600;
	    	

	    	
	    	C.server = pref.getString("server", C.server);	    	
	    	if(callNow!=null){
	    		
	    		call(callNow);
	    		//callNow = null;	
	    	}
	    	resumeBlock = System.currentTimeMillis();
	    	

	    	
	}
	
	@Override
	protected void onPause() {
	        super.onPause();	        
	        try {
	            unregisterReceiver(receiver);
	        } catch (IllegalArgumentException e) {
	        	e.printStackTrace();
	        }
	        ccpopup = false;

	}

    //DB -> NEW LOCATION DETECTED
	public void checkAUTH(){
	/*	
	    if(!pref.getBoolean("AUTH",false)){
	    	
	    	//BLOCK REPEATED SMS SENDING BY TIME
	    	if(pref.getInt("AUTHtime",0)<((System.currentTimeMillis()/1000)-84000)){//24H delay till next AUTH sms
	    		
	    		//SEND AUTH SMS ON BACKGROUND
	    		SMS sms = new SMS(this);
                sms.sendSMS(pref.getString("sms_gateway",C.sms_gateway),"AUTH "+pref.getString("userid","")+" "+pref.getString("password",""));
            	//SAVE PARAMETRS OF SENT SMS 
                SharedPreferences.Editor editor = pref.edit();
		  		editor.putInt("AUTHtime", (int) (System.currentTimeMillis()/1000));
		  		//editor.putBoolean("AUTH", true);
		  		editor.commit();
	    	}	
	    }
	*/
	}

    //START ASYNC TIMER IN LOOP TO CHECK EMAIL VERIFICATION ON SERVER - WORKS ONLY WHEN MAIN SCREEN OF zvonivru IS ACTIVE
    private boolean trVerBlock = false;
    final Handler h = new Handler();
    public static AlertDialog dialog;

    public void dialpadPress(String c){
    	//TODO
		phoneNumber.setText(c);

    }
    
    //SLIDE UP DIALPAD - this method is invoked from contact_list_fragment.xml
    public void showDialpad(View v){
    	
    	dialpad = (TableLayout)findViewById(R.id.dialpad);
    	//HIDE IF VISIBLE
    	if(dialpad.getVisibility()==View.VISIBLE){
    		
    		dialpad.setVisibility(View.GONE);
    		updown.setImageDrawable(getResources().getDrawable(R.drawable.ic_up));
    		
    	//SHOW IF NOT VISIBLE
    	}else{
    		
    		dialpad.setVisibility(View.VISIBLE);
    		updown.setImageDrawable(getResources().getDrawable(R.drawable.ic_down));
    	}
    }
    
    //ONLY SLIDE UP DIALPAD - this method is invoked from contact_list_fragment.xml
    public void showDialpadUp(View v){
    	
    	
    	dialpad = (TableLayout)findViewById(R.id.dialpad);
    	//SHOW IF NOT VISIBLE
    	if(dialpad.getVisibility()!=View.VISIBLE){
    	
    		dialpad.setVisibility(View.VISIBLE);
    		updown.setImageDrawable(getResources().getDrawable(R.drawable.ic_down));
    	}
    }
    
    //ONLY SLIDE DOWN DIALPAD - this method is invoked from ContactListFragment.java
    public void showDialpadDown(){
    	
    	dialpad = (TableLayout)findViewById(R.id.dialpad);
    	//SHOW IF NOT VISIBLE
    	if(dialpad.getVisibility()==View.VISIBLE){
    	
    		dialpad.setVisibility(View.GONE);
    		updown.setImageDrawable(getResources().getDrawable(R.drawable.ic_up));
    	}
    }
    //SEARCH WITHIN CONTACTS WITH NORMAL KEYBOARD - this method is invoked from contact_list_fragment.xml when search button is pressed
    private static boolean trSearch = false;
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public void startSearch(View v){

    	//dialpad = (TableLayout)findViewById(R.id.dialpad);
    	//call_cont = (LinearLayout)findViewById(R.id.call_cont);
    	//HIDE DIALPAD AND CALL BUTTON CONTAINER
    	dialpad.setVisibility(View.GONE);
    	call_cont.setVisibility(View.GONE);
    	cont_number.setVisibility(View.GONE);
    	//TURN ON TRIGER FOR ANDROID SYSTEM BACK BUTTON
    	//trSearch = true;
    	
    
    	if (!Utils.hasHoneycomb()) {
           onSearchRequested();
        }
    	
    	ContactsListFragment.searchItem.setVisible(true);
    	ContactsListFragment.searchItem.expandActionView();  
    }
    
    public static void searchClosed(){

    	//RETURN DIALPAD AND CALL BUTTON CONTAINER WHEN CONTACT SEARCH IS FINISHED
    	cont_number.setVisibility(View.VISIBLE);
    	dialpad.setVisibility(View.VISIBLE);
    	call_cont.setVisibility(View.VISIBLE);
    	updown.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_down));
    	//TURN ON TRIGER FOR ANDROID SYSTEM BACK BUTTON
    	//trSearch = false;
    }
    
    //DEL CHARACTER
    public void delChar(View v){
    	
    	String temp = phoneNumber.getText().toString();
    	if(temp.length()>0){
    		
    		int sel = phoneNumber.getSelectionStart();
    		//APPEND CHARACTE TO CREATE PHONE NUMBER
    		if(sel>0){
			String s1 = phoneNumber.getText().toString().substring(0, sel-1);
			String s2 = phoneNumber.getText().toString().substring(sel, phoneNumber.getText().length());
			String temp2 = s1+s2;
			
			//String temp = phoneNumber.getText().toString();
			//temp+=p.getTag().toString();
			dialpadPress(temp2);
			//MOVE CURSOR TO AN END
			phoneNumber.setSelection(sel-1);
			
			//phoneNumber.setText(temp2.substring(0, temp2.length()-1));
			//MOVE CURSOR TO AN END OF EDIT TEXT
			//phoneNumber.setSelection(phoneNumber.getText().length());
    		}
    	}
    }
    
    //LISTENER FOR CALL NOW BUTTON
    public void callNow(View v){
    	
    	if(phoneNumber.getText().toString().length()==0){
    		
    	  	Toast.makeText(context, getResources().getString(R.string.number4), Toast.LENGTH_SHORT).show();      	          
    	}else{
	    	phonebook = false;
	    	callNowL();	
    	}
    }
    
    //CALL NOW LOCAL METHOD
    public void callNowL(){
    		
    	//if(!isVerified){notVerified();}else{call(phoneNumber.getText().toString());}
    	call(phoneNumber.getText().toString());
    }
   
   
    //LISTENER FOR SYSTEM BACK BUTTON 
    @Override
    public void onBackPressed() {
    	
    	//DO NOT EXIT APP IF SEARCH IS IN PROGRESS BUT JUST CLOSE IT AND RETURN DIALPAD TO VIEW
    	if(trSearch){
    		//searchClosed();
    	}else{
    		ContactsListFragment.mSearchTerm = null;
    		finish();
    	}
    }


    private int getUserCountryIndex(String[] list, String cc){
    	
    	/*
    	int id = 0;
    	for(int i = C.locales.length; i > 0; i--) {

    		//C.log("ggg", "CC"+ locales[i-1]);
    		if(C.locales[i-1].equals(cc)){

    			id = i - 1;
    		}
    	}
    	return id;
    	*/

    	int id = 0;
    	Locale obj = new Locale("", cc);
    	id = listA.indexOf(obj.getDisplayCountry());
    	return id;
    }
    /**
     * This interface callback lets the main contacts list fragment notify
     * this activity that a contact has been selected.
     *
     * @param contactUri The contact Uri to the selected contact.
     */
    //public static List<Integer> checkUser = new ArrayList<Integer>();
    private static boolean pickup = false;
    @Override
    public void onContactSelected(Uri contactUri) {
    	
    	String call_num = "";
    	Integer counter = 0;
    	pickup = false;
    	List<String> phoneList = new ArrayList<String>();
    	//GET CONTACT ID FROM CONTACT URL
    	String cid = contactUri.getLastPathSegment(); 
    	Cursor cursor = getContentResolver().query(  
    			ContactsContract.Contacts.CONTENT_URI, null,  
    			ContactsContract.Contacts._ID + "=?",  
    	        new String[]{cid}, null); 
    	if(cursor.moveToFirst()){
    	//LOOP TO GET PHONE NUBER LIST	
        String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
        if(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
        {
            Cursor pCur = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",new String[]{ id }, null);
            while (pCur.moveToNext()) 
            {
                String contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                //String contactName = pCur.getString(pCur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                
                //CALL
                if(contactNumber!=null){
                	
                	counter++;
                	call_num = contactNumber;
                	phoneList.add(contactNumber);
                	/*
                	//TODO PICKER OF MULTIPLE NUMBERS
                	if(!pickup){
                		//if(!isVerified){notVerified();}else{call(contactNumber);}
                		call(contactNumber);
                	}
                	pickup = true;
                	*/
                }
            }
            pCur.close();
        }
		}
    	
    	cursor.close();
    	
    	if(counter==1){
    		
    		call(call_num);
    	}else if(counter>1){
    		
    		
          	if(dialog!=null) dialog.cancel();
          	
          		final View dialogView = View.inflate(this, R.layout.number_picker, null);
          		//PREPARE LISTVIEW
          		final ListView lv = (ListView) dialogView.findViewById(R.id.number_list);
        	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, phoneList);
                lv.setAdapter(adapter); 
                final List<String> phoneListF = phoneList;
                lv.setOnItemClickListener(new OnItemClickListener(){

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {	
						
						call(phoneListF.get(arg2));	
						dialog.dismiss();
					}});
    			
          		phonebook = true;
          		dialog =  new AlertDialog.Builder(this)
    			.setTitle(R.string.min14)
    			.setIcon(R.drawable.ic_alert)
    			.setView(dialogView)
    			.setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

    			    public void onClick(DialogInterface dialog, int whichButton) {

    			    	dialog.dismiss();
    			/*
    				    //Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(MainActivity.recordsPhone.get(recordsArr.get(item.getGroupId())).replaceAll("\\+","")));
    				    String name = "?";
    				    Cursor contactLookup = getContentResolver().query(curUser, new String[] {BaseColumns._ID,
    				            ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);

    				    try {
    				        if (contactLookup != null && contactLookup.getCount() > 0) {
    				            contactLookup.moveToNext();
    				            name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data._ID));
    				            Intent intent = new Intent(Intent.ACTION_EDIT);
    				            Uri uri2 = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(name));
    				            intent.setData(uri2);
    				            startActivity(intent);
    				        }
    				       
    				        
    				    } finally {
    				       
    				    }
    				  */  
    			    }}).show();	
    		//Intent intent = new Intent(this, ContactDetailActivity.class);
            //intent.setData(contactUri);
            //startActivity(intent);
    	}
    	
    	
    	
    	/*
        if (looPaneLayout && mContactDetailFragment != null) {
            // If two pane layout then update the detail fragment to show the selected contact
            mContactDetailFragment.setContact(contactUri);
        } else {
            // Otherwise single pane layout, start a new ContactDetailActivity with
            // the contact Uri
            Intent intent = new Intent(this, ContactDetailActivity.class);
            intent.setData(contactUri);
            startActivity(intent);
        }
        */
    }
    
    
    static void badPhonePopup(final Context context, String phone, int msg){
    		
      	if(dialog!=null) dialog.cancel();
      	if(phonebook){
      		phonebook = true;
      		dialog =  new AlertDialog.Builder(context)
			.setTitle(phone)
			.setMessage(msg)
			.setIcon(R.drawable.ic_alert)
			.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

			    public void onClick(DialogInterface dialog, int whichButton) {

			
				    //Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(MainActivity.recordsPhone.get(recordsArr.get(item.getGroupId())).replaceAll("\\+","")));
				    String name = "?";
				    Cursor contactLookup = context.getContentResolver().query(curUser, new String[] {BaseColumns._ID,
				            ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);

				    try {
				        if (contactLookup != null && contactLookup.getCount() > 0) {
				            contactLookup.moveToNext();
				            name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data._ID));
				            Intent intent = new Intent(Intent.ACTION_EDIT);
				            Uri uri2 = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(name));
				            intent.setData(uri2);
				        	context.startActivity(intent);
				        }
				       
				        
				    } finally {
				       
				    }
				    
			    }})
			.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {

    			public void onClick(DialogInterface dialog, int whichButton) {

	
    			}}).show();		
      		
      	}else{
      		phonebook = true;
      		dialog =  new AlertDialog.Builder(context)
    			.setTitle(phone)
    			.setMessage(R.string.number3)
    			.setIcon(R.drawable.ic_alert)
    			.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {

    			    public void onClick(DialogInterface dialog, int whichButton) {

    			
    						
    			    }}).show();
      	}
      	
    }
    
    //PARSE PHONE NUMBER
    static boolean isPhoneValid(String phone){
    	
    	//C.log(tag, "PH:"+phone);
    	//DISABLE SUPPORT CHECK
    	if(callNow!=null){
    		callNow = null;
    		return true;
    	}
    	//REGEX CHECK
    	phone = phone.replace(" ", "");
    	boolean tr = true;
    	Pattern p = Pattern.compile("^(\\+|00)\\d{9}\\d");///^(\+|00)\d{9}\d*$/
		Matcher m = p.matcher(phone);
		if(!m.find()){
		      return false; 
		} 
	
    	return tr;
    }

    //CALL METHOD
    public void call(String phone){
    	
    	callNow = null;
    	String phoneOr = phone;
    	//если начинается на 8 заменяешь на 8 на +7
    	if(phone.startsWith("8") && phone.length()>10) phone = "+7" + phone.substring(1, phone.length());

    	//CHECK IF VALID OR NOT
		if(phone.length()>0){
			
    		//PERFORM CALL

				//Toast.makeText(getApplicationContext(), phone, Toast.LENGTH_LONG).show();
				//INSERT CALL RECORD
				String description = "call";		 		    	        	
 	           	String from = "Я";		 		    	           	
 	           	String to = phoneOr;		 		    	        	
 	            String duration = "0.00";		 		    	           	
 	            String deb = "0.00";		 		    	           	
 	            String cre = "0.00";     	
 	            String country = "US";
 	            Integer type = 4; 
 	           
 	            DataBaseAdapter db = new DataBaseAdapter(context); 
 	            db.open();
	            db.insertReport(System.currentTimeMillis(), description, from, to, duration, 0f, country, Integer.valueOf(type));
	            db.close();
	            //PREPARE CALL INTENT

			Intent i = new Intent(getApplicationContext(), MsjLlamada.class);
			i.putExtra("NOrigen",NOrigen.toString());
			i.putExtra("phone",phone);
			startActivity(i);
				/*
				Intent callIntent = new Intent(Intent.ACTION_CALL);   
				//C.log("Call Parser", Uri.parse("tel:"+(pref.getString("IVR_"+cc.toUpperCase(), "+37125415609")+"*")+phone).toString());
				callIntent.setData(Uri.parse("tel:"+phone));

				startActivity(callIntent);  
				*/
				
				

		}else{
			
			badPhonePopup(this, phoneOr, R.string.number2);
		}
    }
    

    @Override
    public void onSelectionCleared() {
        if (isTwoPaneLayout && mContactDetailFragment != null) {
            mContactDetailFragment.setContact(null);
        }
    }

    @Override
    public boolean onSearchRequested() {
        // Don't allow another search if this activity instance is already showing
        // search results. Only used pre-HC.
        return !isSearchResultView && super.onSearchRequested();
    }
    
    public static String getUserCountry(Context context) {
    	
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                return simCountry.toLowerCase(Locale.US);
            }else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    return networkCountry.toLowerCase(Locale.US);
                }
            }
        }
        catch (Exception e) { }
        return null;
    }
    

    private boolean callback = false;
    
    //LISTENER FOR COUNTRY LIST PICKER TOP BAR
	@Override
	public boolean onNavigationItemSelected(final int arg0, long arg1) {
		

		return false;
	}


	//ITEM SELECTED SAVE
	private void saveItemSelected(int arg0){
		
		//FIND COUNTRY CODE OF CURRENT SELECTION
		/*
		int i = 0;
		String cct = "";
		for (String ccl : C.locales) {
	     
			if(i==arg0) cct = ccl;
			i++;  
		}
	    cc = cct; 
	    */
		
		cc = listH.get(listA.get(arg0));
	    //NO IVR MAKE 
	    //LinearLayout  callbackinfo = (LinearLayout) findViewById(R.id.callbackinfo);
	    
	    mEditor.putString("call_prefix", pref.getString("IVR_"+cc.toUpperCase(), ""));
	    mEditor.commit();
	    //showBubble();
	    //SAVE MANUALY SELECTED COUNTRY CODE
	    mEditor.putString("ccmanual", cc);
	    mEditor.commit();
	    
	}
	//RECEIVE ASINHRONOUS RESULTS FROM BACKGROUND 
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
    	

        }
  	};
  	//HIDE DIALPAD WHEN SCROLL HAPPENS
	@Override
	public void onListviewScroll() {
		showDialpadDown();
	}
  	
}
