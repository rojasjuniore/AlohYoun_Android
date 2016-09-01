package com.aloh.YOU.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.aloh.YOU.R;
import com.aloh.YOU.db.DataBaseAdapter;
import com.aloh.YOU.io.apiRetro;
import com.aloh.YOU.io.models.apiConstan;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Main2Activity extends Activity {
    final Context context= Main2Activity.this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

    }

    public void entrar(View view) {
        if(checkConnectivity()) {
             /*RESTADAPTER DE RETROFIT PARA APIGET*/
            final EditText usuario = (EditText) findViewById(R.id.usuario);
            final EditText clave = (EditText) findViewById(R.id.clave);

            Retrofit restAdapter = new Retrofit.Builder().baseUrl(apiConstan.URL_BASE).addConverterFactory(GsonConverterFactory.create()).build();
            apiRetro callback = restAdapter.create(apiRetro.class);
            Call<ResponseBody> call = callback.getSaldo(usuario.getText().toString(),clave.getText().toString());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String saldo = response.body().string();
                        if(saldo.equals("s")){
                            Toast.makeText(getApplicationContext(),"Usuario o Cleve incorrectos!!!",Toast.LENGTH_LONG).show();
                            usuario.setText("");
                            clave.setText("");
                        }else{

                            DataBaseAdapter db = new DataBaseAdapter(context);
                            db.open();
                            //db.cleanUsuario();
                            db.insertUsuario(usuario.getText().toString(),clave.getText().toString());
                            db.close();
                            Intent i = new Intent(getApplicationContext(),ContactsListActivity.class);
                            i.putExtra("saldo",saldo);
                            i.putExtra("USUARIO",usuario.getText().toString());
                            startActivity(i);
                            finish();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }

    }



    private boolean checkConnectivity()
    {
        boolean enabled = true;

        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();

        if ((info == null || !info.isConnected() || !info.isAvailable()))
        {
            enabled = false;
        }
        return enabled;
    }

}