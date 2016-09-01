package com.aloh.YOU.util;

import android.util.Log;

public class C {

	//SMS GATEWAY
	public static String sms_gateway = "+37125415609";
	//SERVER URL
	public static String server = "http://zvoni-v.ru";
	//EMAIL VERIFICATION REQUEST INTERVAL in milisecond
	public static int evt = 50000;//50 seconds
	//COUNTRY LIST SUPPORTED BY ZVONIVRU
	public static final String[] locales = {"af","al","aq","dz","as","ad","ao","ag","az","ar","au","at","bs","bh","bd","am","bb","be","bm","bt","bo","ba","bw","br","bz","io","sb","vg","bn","bg","mm","bi","by","kh","cm","ca","cv","ky","cf","lk","td","cl","cn","tw","co","km","yt","cg","ck","cr","hr","rs","cu","cy","cz","bj","dk","dm","do","ec","sv","gq","et","er","ee","fo","fk","fj","fi","fr","gf","pf","dj","ga","ge","gm","de","gh","gi","ki","gr","gl","gd","gp","gu","gt","gn","gy","ht","hn","hk","hu","is","in","id","ir","iq","ie","il","it","ci","jm","jp","kz","jo","ke","kp","kr","kw","kg","la","lb","ls","lv","lr","ly","li","lt","lu","mo","mg","mw","my","mv","ml","mt","mq","mr","mu","mx","mc","mn","md","ms","ma","mz","om","na","nr","np","nl","an","aw","nc","vu","nz","ni","ne","ng","nu","nf","no","mp","fm","mh","pk","pa","pg","py","pe","ph","pl","pt","gw","pr","qa","re","ro","ru","rw","sh","kn","ai","lc","pm","vc","sm","st","sa","sn","sc","sl","sg","sk","vn","si","so","za","zw","es","sd","sr","sz","se","ch","sy","tj","th","tg","tk","to","tt","ae","tn","tr","tm","tc","tv","ug","ua","mk","eg","gb","tz","us","vi","bf","uy","uz","ve","wf","ws","ye","zm"};
	//IVR SERVER CACHE TIME FOR EACH COUNTRY
	public static long ivrCacheTime = 3600l*24l*31l; //5 days and bulk ivr resync
	//BULK SERVER RATES CACHE TIME
	public static long ratesCacheTime = 3600l*24l*30l; //1 day and bulk ivr resync
	//BALANCE SERVER CACHE TIME
	public static long balCacheTime = 3600l*24l; //24 HOUR
	//HISTORY BACKWORDS TIME INTERVAL - REPORT
	public static long rentCacheTime = 3600l*24l*32l; //24 HOUR
	//HISTORY BACKWORDS TIME INTERVAL - REPORT
	public static long balOfsetTime = 1000l*3600l*24l*20l; //24 HOUR
	//REFRESH COUNTRY LIST AVAILABLE FOR RENT
	public static long rentCCCacheTime = 3600l*24l*33l; //20 days
	//CHANGES CACHE
	public static long chTime = 3600l*1l; //1 hour
	//CURRENT GMT TIMESTAMP
	public static long gmt = 0;
	
	public static void log(String tag, String log){
		
		//Log.i(tag, log);
	}
}
