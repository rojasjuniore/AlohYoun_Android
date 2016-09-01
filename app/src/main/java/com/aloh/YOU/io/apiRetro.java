package com.aloh.YOU.io;

/**
 * Created by develop on 15/06/16.
 */



import com.aloh.YOU.io.models.AppDataSet;
import com.aloh.YOU.io.models.apiConstan;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface apiRetro {



    @GET(apiConstan.URL_API)
    Call<List<AppDataSet>> getApi();

    @GET("http://voip.tusitioenlared.com/api/consultarUsuario.php")
    Call<ResponseBody> getSaldo(@Query("login") String login, @Query("clave") String claven);

    @GET("http://voip.tusitioenlared.com/api/consultarNumero.php")
    Call<ResponseBody> getNumero(@Query("login") String login);

    @GET("http://voip.tusitioenlared.com/aloh/call.php")
    Call<ResponseBody> llamar(@Query("origen") String origen, @Query("destino") String destino);
}
