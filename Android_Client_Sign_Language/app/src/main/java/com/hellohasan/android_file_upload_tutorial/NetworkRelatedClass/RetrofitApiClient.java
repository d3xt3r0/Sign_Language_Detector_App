package com.hellohasan.android_file_upload_tutorial.NetworkRelatedClass;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hellohasan.android_file_upload_tutorial.MainActivity;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitApiClient {

    private static String BASE_URL = "http://192.168.43.52/";

    private static Retrofit retrofit = null;


    private static Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    private RetrofitApiClient() {} // So that nobody can create an object with constructor

    public static synchronized Retrofit getClient() {


        //BASE_URL =  "http://" + MainActivity.getData() + "/";


        if (retrofit==null) {

            int timeOut = 5 * 60;
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(timeOut, TimeUnit.SECONDS)
                    .writeTimeout(timeOut, TimeUnit.SECONDS)
                    .readTimeout(timeOut, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build();
        }
        return retrofit;
    }

}
