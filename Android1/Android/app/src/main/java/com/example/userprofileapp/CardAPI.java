package com.example.userprofileapp;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.example.userprofileapp.pojo.Card;
import com.example.userprofileapp.pojo.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CardAPI {
    String cardURL;
    String userToken;
    String customerID;
    List<Card> cardList = new ArrayList<>();
    List<Card> cardListinAdapter;
    RecyclerView.Adapter cAdapter;
    Context context;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public CardAPI(List<Card> clist, String cardURL, String userToken, String customerID, RecyclerView.Adapter adapter, Context con) {
        this.cardURL = cardURL;
        this.userToken = userToken;
        this.customerID=customerID;
        cAdapter=adapter;
        cardListinAdapter=clist;
        context=con;
    }


    public void execute(){
        OkHttpClient client = new OkHttpClient();

        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("customer_ID", customerID);

        Gson gson = new Gson();
        String param = gson.toJson(jsonObj);

        RequestBody body = RequestBody.create(JSON, param);

        Request request = new Request.Builder()
                .url(cardURL)
                .post(body)
                .header("Authorization","Bearer "+userToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.d("chella","Response result "+result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONObject jsonObject1 =jsonObject.getJSONObject("data");
                    JSONArray cards = jsonObject1.getJSONArray("data");
                    Log.d("cards",jsonObject1.toString());
                    Log.d("cards",cards.toString());
                    for(int i =0; i<cards.length();i++) {
                        JSONObject cardsJSONObject = cards.getJSONObject(i);
                        Card card = new Card();
                        card.setId(cardsJSONObject.getString("id"));
                        card.setBrand(cardsJSONObject.getString("brand"));
                        card.setNumber(cardsJSONObject.getString("last4"));
                        cardList.add(card);



                    }

                    Handler handler = new Handler(Looper.getMainLooper()) {
                        @Override
                        public void handleMessage(Message msg) {
                            // Any UI task, example
                            cardListinAdapter.addAll(cardList);
                            //Log.d("chella","Product List on parsing the JSON "+prod_list);
                            cAdapter.notifyDataSetChanged();
                        }
                    };
                    handler.sendEmptyMessage(1);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
        Log.d("cards","this is a caredlist"+cardList.toString());
       // return cardList;
    }
}
