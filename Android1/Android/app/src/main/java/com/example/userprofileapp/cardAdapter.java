package com.example.userprofileapp;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.userprofileapp.pojo.Card;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class cardAdapter extends RecyclerView.Adapter<cardAdapter.ViewHolder> {

    List<Card> cardList = new ArrayList<>();
    Card card = new Card();
    String chargePreviousURL = "http://192.168.118.2:3000/chargePrevious";
    Double amount;
    String customerID,userToken;
    Integer Index;
    JSONObject jsonObject;
    String obj,data;
    Context context;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public cardAdapter(List<Card> cardList, Double amount, String customerID, String UserToken, Context con) {
        this.cardList = cardList;
        this.amount = amount;
        this.customerID=customerID;
        userToken =UserToken;
        context=con;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_cards,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d("cards","in adapter"+cardList.toString());
        card = cardList.get(position);
        holder.cardNumber.setText(card.getNumber());

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Index=position;
                OkHttpClient client = new OkHttpClient();

                JsonObject jsonObj = new JsonObject();
                jsonObj.addProperty("amount", amount);
                jsonObj.addProperty("card_id",cardList.get(Index).getId());
                jsonObj.addProperty("customer_ID", customerID);
                Gson gson = new Gson();
                String param = gson.toJson(jsonObj);
                Log.d("cards","number"+cardList.get(Index).getNumber()+"card id in adapter"+param);


                RequestBody body = RequestBody.create(JSON, param);

                Request request = new Request.Builder()
                        .url(chargePreviousURL)
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
                        try {
                            jsonObject = new JSONObject(response.body().string());
                            obj = jsonObject.getString("status");
                            data=jsonObject.getString("data");

                            if(obj.equalsIgnoreCase("Success")){
                                Looper.prepare();
                                Log.d("sheetal","in sucees else");
                                Toast.makeText((context), "Successfull", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                                ProductFragment.newInstance(userToken,"PAYMENT",customerID);
                                     ((PayementWithStripe)context).getSupportFragmentManager().beginTransaction().replace(R.id.container_payment,new ProductFragment(),"product").addToBackStack(null).commit();
                            }else{
                                Looper.prepare();
                                Log.d("sheetal","in sucees else");
                                Toast.makeText((context), "User Not Found", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }

                        } catch (JSONException e) {
                            Log.d("chella","Exception in parsing the JSON ");
                        }
                    }
                });
            }
        });
    }


    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
    TextView cardNumber;
    ConstraintLayout parentLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardNumber = itemView.findViewById(R.id.cardNumber);
            parentLayout=itemView.findViewById(R.id.linearLayout2);
        }

    }
}
