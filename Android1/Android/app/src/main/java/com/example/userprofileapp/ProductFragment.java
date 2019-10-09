package com.example.userprofileapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.userprofileapp.pojo.Product;
import com.example.userprofileapp.pojo.User;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
//implements ProductAdapter.prodInterface
public class ProductFragment extends Fragment implements ProductAdapter.prodInterface  {

    View root;
    static String fragment_tag;
    Product product;
    TextView cart_count;
    private RecyclerView prodRecyclerView;
    private RecyclerView.Adapter prodAdapter;
    private RecyclerView.LayoutManager layoutManager;
    List<Product> productList = new ArrayList<>();
    List<Product> selectedProducts = new ArrayList<>();
    String productURL = "http://192.168.118.2:3000/products";
    static String token;
    static String customer_id;
    int product_added =0;
    //static User User;
    SharedPreferences sharedPreferences;
//    SharedPreferences.Editor editor;
//    Gson gson = new Gson();
//    String jsonProd;

    private OnFragmentInteractionListener mListener;

    public ProductFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ProductFragment newInstance(String t, String tag,String ID) {
        ProductFragment fragment = new ProductFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        token=t;
        fragment_tag=tag;
        customer_id=ID;
        //User = user;
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Products");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_product, container, false);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        //editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        ImageButton cart = root.findViewById(R.id.cart);
        cart_count = root.findViewById(R.id.cart_count);
        TextView usernameText = root.findViewById(R.id.usernameText);
        usernameText.setText("Hello "+sharedPreferences.getString("FNAME","null"));
        if(fragment_tag == "PAYMENT"){
            product_added=0;
            Log.d("chella","Coming after payment: "+product_added);
        }
        if(product_added>0){
            //sharedPreferences.getInt("COUNTER",0);
            cart_count.setText(Integer.toString(product_added));
        }

        prodRecyclerView = (RecyclerView)root.findViewById(R.id.products_list);
        prodRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        prodRecyclerView.setLayoutManager(layoutManager);
        prodAdapter = new ProductAdapter(productList,selectedProducts,this,fragment_tag);
        prodRecyclerView.setAdapter(prodAdapter);
        //prodAdapter.notifyDataSetChanged();

        try {
            new ProductAPI(productURL,getActivity(),prodAdapter,productList,token).execute();
         //   prodAdapter.notifyDataSetChanged();
        } catch (IOException e) {
            e.printStackTrace();
        }


//        if(!productList.isEmpty()) {
//        }else{
//            Log.d("sheetal","Product List is empty");
//        }
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //jsonProd = gson.toJson(selectedProducts);
                //editor.putString("ADDEDPROD",jsonProd);
                //editor.apply();
                CheckoutDetailsFragment.newInstance(token,selectedProducts,customer_id);
                if(fragment_tag == "HOME") {
                    getFragmentManager().beginTransaction().replace(R.id.container, new CheckoutDetailsFragment()).addToBackStack(null).commit();
                }
                else if(fragment_tag == "PAYMENT"){
                    getFragmentManager().beginTransaction().replace(R.id.container_payment, new CheckoutDetailsFragment()).addToBackStack(null).commit();
                }
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(selectedProducts);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void setCounter(int counter) {

        Log.d("chella","Counter value in Fragment "+counter);
        product_added=counter;
        //editor.putInt("COUNTER",product_added);
        cart_count.setText(Integer.toString(product_added));
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(List<Product> products);
    }
}
