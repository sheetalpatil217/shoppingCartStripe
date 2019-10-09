package com.example.userprofileapp.retrofit;

import android.os.Bundle;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit instance;

    public static Retrofit getInstance(String token){
        if (instance==null){
            instance= new Retrofit.Builder().baseUrl("http://192.168.48.2:3000/")
                    .client(provideOkHttpClient(token))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return instance;
    }
    private static OkHttpClient provideOkHttpClient(String token) {
        final String token2=token;
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request newRequest  = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + token2)
                        .build();
                return chain.proceed(newRequest);
            }
        }).build();
        return client;
    }
}
