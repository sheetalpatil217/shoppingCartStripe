package com.example.userprofileapp;

import android.util.Log;
import android.widget.TextView;

import com.example.userprofileapp.pojo.User;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import androidx.fragment.app.FragmentActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserProfile {
    String userProfileURL;
    FragmentActivity context;
    User User;
    String jsonData;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public UserProfile(String URL,FragmentActivity con,User user) throws IOException {
        userProfileURL=URL;
        context=con;
        User=user;
    }


    public User execute() throws IllegalAccessException {
        Gson gson = new Gson();
        String param = gson.toJson(User);

        OkHttpClient client = new OkHttpClient();
        Log.d("sheetal","user token before calling the api"+ User.getToken());

        Log.d("chella","Email "+User.getEmail());

          HttpUrl.Builder httpBuider = HttpUrl.parse(userProfileURL).newBuilder();

        httpBuider .addQueryParameter("email",User.getEmail());


        Request request = new Request.Builder()
                .url(httpBuider.build())
                .get()
                .header("Authorization","Bearer "+User.getToken())
                .build();


        Log.d("chella","Token in UserProfile "+ User.getToken());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
               jsonData = response.body().string();
               Log.d("chella","jsonData in UserProfile "+jsonData);

                try {
                    JSONObject jsonObject = new JSONObject(jsonData);
                    User.setStatus(jsonObject.getString("status"));
                    JSONObject jsondataObject = jsonObject.getJSONObject("data");
                    User.setAge(jsondataObject.getInt("age"));
                    User.setFname(jsondataObject.getString("first_name"));
                    User.setLname(jsondataObject.getString("last_name"));
                    User.setEmail(jsondataObject.getString("_id"));
                    User.setAddress(jsondataObject.getString("address"));
                    User.setWeight(jsondataObject.getDouble("weight"));
                    Log.d("chella","Inside the try block "+User);

                    ( (TextView)context.findViewById(R.id.fname_friend)).setText(User.getFname());
                    ( (TextView)context.findViewById(R.id.lname_friend)).setText(User.getLname());

                    if(User.getAge().toString().isEmpty() || User.getAge().toString().equalsIgnoreCase("0") ){
                        ( (TextView)context.findViewById(R.id.age_firend)).setText("No information to display");
                    }else{
                        ( (TextView)context.findViewById(R.id.age_firend)).setText(User.getAge().toString());
                    }


                    if(User.getWeight().toString().isEmpty() || User.getWeight().toString().equalsIgnoreCase("0")){
                        ( (TextView)context.findViewById(R.id.weight_friend)).setText("No information to display");
                    }else{
                        ( (TextView)context.findViewById(R.id.weight_friend)).setText(User.getWeight().toString());
                    }

                    if(User.getAddress().isEmpty()){
                        ( (TextView)context.findViewById(R.id.address_friend)).setText("No information to display");
                    }else{
                        ( (TextView)context.findViewById(R.id.address_friend)).setText(User.getAddress());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
        return User;
    }

}
