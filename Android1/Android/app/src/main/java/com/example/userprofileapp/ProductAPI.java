package com.example.userprofileapp;


import android.media.Image;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.userprofileapp.pojo.Product;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProductAPI {
    String productURL;
    FragmentActivity context;
    Product product;
    String jsonData;
    List<Product> products = new ArrayList<>();
    List<Product> prod_list;
    private RecyclerView.Adapter prodAdapters;
    String bearerToken;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public ProductAPI(String URL,FragmentActivity con,RecyclerView.Adapter prodAdapter, List<Product> prods,String token) throws IOException {
        productURL=URL;
        context=con;
        product= new Product();
        prodAdapters=prodAdapter;
        prod_list=prods;
        bearerToken =token;
    }

 //   public List<Product> execute() {
 public void execute() {

     Log.d("sheetal", "hit the product execute");
       // Gson gson = new Gson();
       // String param = gson.toJson(product);

        OkHttpClient client = new OkHttpClient();

      //  RequestBody body = RequestBody.create(JSON, param);

        Request request = new Request.Builder()
                .url(productURL)
                .get()
                .header("Authorization","Bearer "+bearerToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("sheetal", "call fail"+e.getMessage());
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                jsonData = response.body().string();
                Log.d("sheetal", "jsondata" + jsonData);
                JSONObject jsonObject=null;
                try{
                     jsonObject = new JSONObject(jsonData);
                   // Log.d("sheetal",e.getMessage());
                if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        Log.d("sheetal","jsonArray"+ jsonArray);
                        for(int i =0; i<jsonArray.length();i++) {
                            JSONObject prod = jsonArray.getJSONObject(i);
                            Product prods = new Product();
                            prods.setDiscount(prod.getInt("discount"));
                            prods.setProductName(prod.getString("name"));
                            prods.setImageLink(prod.getString("photo"));
                           // Picasso.get().load(prod.getString("photo")).into(prods.getIm);
                            Log.d("chella","Product Image :"+prods.getProductImage());
                            prods.setProductPrice(prod.getDouble("price"));
                            prods.setCategory(prod.getString("region"));
                            products.add(prods);
                            //Log.d("chella","item in prods: "+prods.getProductImage());

                        }


                    Handler handler = new Handler(Looper.getMainLooper()) {
                        @Override
                        public void handleMessage(Message msg) {
                            // Any UI task, example
                            prod_list.addAll(products);
                            //Log.d("chella","Product List on parsing the JSON "+prod_list);
                            prodAdapters.notifyDataSetChanged();
                        }
                    };
                    handler.sendEmptyMessage(1);

                        Log.d("sheetal","products list"+products.size());
                } else {
                    Log.d("chella","Error in retrieving the JSONDATA");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }});
     //   return products;
    }

}
