package org.ecaib.marvelapi;

import android.graphics.Movie;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.Arrays;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by carlesgm on 10/12/15.
 */
public class MarvelClient {
    public final String BASE_URL = "http://gateway.marvel.com/v1/public/";
    public final String API_KEY = "64e32d3f22a03e3e095961b007b922ad";
    public final String SECRET_KEY = "f4da3ea5ecc5c0388364a4a7782a97d590af0897";

    public final int TS = 1;
    private final MarvelAPI apiService;

    public interface MarvelAPI {
        // Request method and URL specified in the annotation
        // Callback for the parsed response is the last parameter

        @GET("characters")
        Call<JsonElement> getCharacters(
                @Query("limit") int limit
        );
    }

    public MarvelClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofit.client().interceptors().add(new Interceptor() {
            @Override
            public com.squareup.okhttp.Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                HttpUrl originalUrl = originalRequest.httpUrl();

                HttpUrl newUrl = originalUrl.newBuilder()
                        .addQueryParameter("apikey", API_KEY)
                        .addQueryParameter("ts", String.valueOf(TS))
                        .addQueryParameter("hash", Utils.MD5(TS + SECRET_KEY + API_KEY))
                        .build();

                Request newRequest = originalRequest.newBuilder().url(newUrl).build();

                return chain.proceed(newRequest);
            }
        });
        apiService = retrofit.create(MarvelAPI.class);
    }

    public void getCharacters(){
        Call<JsonElement> call = apiService.getCharacters(1);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Response<JsonElement> response, Retrofit retrofit) {
                try {
                    if (response.isSuccess()){

                    } else {
                        Log.e("XXXXX", response.errorBody().string());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });

    }
}
