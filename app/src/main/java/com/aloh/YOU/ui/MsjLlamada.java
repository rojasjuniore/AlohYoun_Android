package com.aloh.YOU.ui;

import com.aloh.YOU.R;
import com.aloh.YOU.io.apiRetro;
import com.aloh.YOU.io.models.apiConstan;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MsjLlamada extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msj_llamada);

        String NOrigen = getIntent().getExtras().getString("NOrigen");
        String phone = getIntent().getExtras().getString("phone");
        Retrofit restAdapter = new Retrofit.Builder().baseUrl(apiConstan.URL_BASE).addConverterFactory(GsonConverterFactory.create()).build();
        apiRetro callback = restAdapter.create(apiRetro.class);
        Call<ResponseBody> call = callback.llamar(NOrigen.toString(),phone);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {

                    Toast.makeText(getApplicationContext(), response.body().string().toString(), Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

    }
}
