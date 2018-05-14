package net.prahasiwi.laporankeuangan.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by PRAHASIWI on 19/03/2018.
 */

public class ServiceGenerator {

    private Retrofit retrofit;

    public Retrofit getClient(String URL){
       // String url = db.getUrlServer();
        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}

