package com.example.userprofileapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.userprofileapp.pojo.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardMultilineWidget;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PayementWithStripe extends AppCompatActivity {

    CardMultilineWidget cardMultilineWidget;
    String Usertoken;
    Double amount;
    String customerID;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    List<com.example.userprofileapp.pojo.Card> cards = new ArrayList<>();
    String cardURL = "http://192.168.118.2:3000/getCards";
    String chargeURL="http://192.168.118.2:3000/charge";
    JSONObject jsonObject;
    String obj,data;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payement_with_stripe);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Payment");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);



        Usertoken = getIntent().getStringExtra("TOKEN");
        amount = getIntent().getDoubleExtra("TOTAL_AMOUNT",0.0);
        customerID=getIntent().getStringExtra("CUSTOMER_ID");
        cardMultilineWidget = findViewById(R.id.card_input_widget);
        Button save =  findViewById(R.id.saveButton);
        recyclerView = findViewById(R.id.savedCardsList);


        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        Log.d("cards","in payment activity"+cards);
        adapter = new cardAdapter(cards,amount,customerID,Usertoken,getApplicationContext());
        recyclerView.setAdapter(adapter);

        new CardAPI(cards,cardURL,Usertoken,customerID,adapter,getApplicationContext()).execute();
        adapter.notifyDataSetChanged();

        if(cards.isEmpty()==false){

        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCard();
            }
        });

    }

    private void saveCard() {

        Card card =  cardMultilineWidget.getCard();
        if(card == null){
            Toast.makeText(getApplicationContext(),"Invalid card",Toast.LENGTH_SHORT).show();
        }else {
            if (!card.validateCard()) {
                Toast.makeText(getApplicationContext(), "Invalid card", Toast.LENGTH_SHORT).show();
            } else {
                CreateToken(card);
            }
        }
    }

    private void CreateToken( Card card) {
        Stripe stripe = new Stripe(getApplicationContext(), getString(R.string.publishablekey));
        stripe.createToken(
                card,
                new ApiResultCallback<Token>(){
                    public void onSuccess(Token token) {

                        // Send token to your server
                        Log.e("Stripe Token", token.getId());
                        Intent intent = new Intent();
                        intent.putExtra("card",token.getCard().getLast4());
                        intent.putExtra("stripe_token",token.getId());
                        intent.putExtra("cardtype",token.getCard().getBrand());
                        setResult(0077,intent);
                        Log.d("chella","Token is"+token.toString());

                        JsonObject jsonstring = new JsonObject();
                        jsonstring.addProperty("amount", amount);
                        jsonstring.addProperty("stripeToken", token.getId());
                        jsonstring.addProperty("customer_ID", customerID);

                        Gson gson = new Gson();
                        String param = gson.toJson(jsonstring);



                        OkHttpClient client = new OkHttpClient();

                        RequestBody body = RequestBody.create(JSON, param);

                        Request request = new Request.Builder()
                                .url(chargeURL)
                                .post(body)
                                .header("Authorization","Bearer "+Usertoken)
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
                                        Toast.makeText((getApplicationContext()), "Successfull", Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                        ProductFragment.newInstance(Usertoken,"PAYMENT",customerID);
                                        getSupportFragmentManager().beginTransaction().replace(R.id.container_payment,new ProductFragment(),"product").addToBackStack(null).commit();
                                    }else{
                                        Looper.prepare();
                                        Log.d("sheetal","in sucees else");
                                        Toast.makeText((getApplicationContext()), "User Not Found", Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }

                                } catch (Exception e) {
                                    Log.d("chella","Exception in parsing the JSON ");
                                }

                            }
                        });

                        finish();
                    }
                    public void onError(Exception error) {

                        // Show localized error message
                        Toast.makeText(getApplicationContext(),
                                error.getLocalizedMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }
}
