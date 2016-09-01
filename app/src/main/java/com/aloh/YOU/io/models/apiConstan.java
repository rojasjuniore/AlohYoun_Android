package com.aloh.YOU.io.models;

/**
 * Created by develop on 15/06/16.
 */
public class apiConstan {


    public static final String URL_BASE = "http://bapps.tusitioenlared.com";
    public static final String PATH_API = "/apiRest/apps";
    public static final String KEY_FORMAT = "format";
    public static final String VALUE_FORMAT = "json";
    public static final String KEY_ID = "id"; /*id de la app*/
    public static final String VALUE_ID = "4"; /*ID APP*/

    /*URL ARMADA PARA EL REST*/

    public static final String URL_API = URL_BASE + PATH_API + "?" + KEY_FORMAT + "=" + VALUE_FORMAT + "&" + KEY_ID + "=" + VALUE_ID;




}
