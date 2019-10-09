package com.example.userprofileapp;

import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.userprofileapp.pojo.User;
import com.google.gson.Gson;

import java.io.IOException;

import androidx.fragment.app.FragmentActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterUser {

    String registerUserURL;
    FragmentActivity context;
    User User;
    String jsonData;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public RegisterUser(String URL,FragmentActivity con,User user) throws IOException {
        registerUserURL=URL;
        context=con;
        User=user;
    }

    public void execute() {
        Gson gson = new Gson();
        String param = gson.toJson(User);

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(JSON, param);

        Request request = new Request.Builder()
                .url(registerUserURL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
              //  Log.d("sheetal",response.body().string());
                //result[0] =response.body().string();
               // Log.v(TAG, response.body().string());
                 jsonData = response.body().string();

                Log.d("sheetal","jsondata"+jsonData);
                if(jsonData.equalsIgnoreCase("success")){
                    Looper.prepare();
                    Toast.makeText(context.getBaseContext(), "User Registered", Toast.LENGTH_SHORT).show();
                    //LoginFragment.newInstance(User);
                    context.getSupportFragmentManager().beginTransaction().replace(R.id.container,new LoginFragment(),"tag_LoginFrag").addToBackStack(null).commit();
                    Looper.loop();

                }else{
                    Looper.prepare();
                    Toast.makeText(context, "Something Went Wrong, Try Again", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        });



    }




}

