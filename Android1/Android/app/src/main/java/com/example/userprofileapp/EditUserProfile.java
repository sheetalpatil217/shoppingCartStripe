package com.example.userprofileapp;

import android.util.Log;

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

public class EditUserProfile {
    String editUserProfileURL;
    FragmentActivity context;

    User User;
    String jsonData;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public EditUserProfile(String URL,FragmentActivity con,User user) throws IOException {
        editUserProfileURL=URL;
        context=con;
        User=user;
    }

    public void execute() throws IllegalAccessException {
        Gson gson = new Gson();
        String param = gson.toJson(User);

        OkHttpClient client = new OkHttpClient();

        Log.d("chella","Param in EditUserProfile "+param);

        RequestBody body = RequestBody.create(JSON, param);

        Log.d("chella","JSON Body "+body.toString());

        Request request = new Request.Builder()
                .url(editUserProfileURL)
                .put(body)
                .header("Authorization","Bearer "+User.getToken())
                .build();



        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("sheetal","call failure"+e.getMessage());
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                jsonData = response.body().string();

                Log.d("sheetal","jsondata"+jsonData);
                if(jsonData.equalsIgnoreCase("success")){
                    Log.d("chella","On Success  User Details Updated");

                }else{
                    Log.d("chella","On Failure");
                }
            }
        });
    }
}
