package com.example.userprofileapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.userprofileapp.pojo.Product;
import com.example.userprofileapp.pojo.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class CheckoutDetailsFragment extends Fragment {

    View root;
    private RecyclerView checkoutRecyclerView;
    private RecyclerView.Adapter checkoutAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    static List<Product> addedProducts;
    static  String token;
    double totalCost=0.0;
    static String customer_ID;
    //static User user;
    SharedPreferences sharedPreferences;
    List<Product> addedProd = new ArrayList<>();

    private OnFragmentInteractionListener mListener;

    public CheckoutDetailsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CheckoutDetailsFragment newInstance(String t, List<Product> products,String ID) {
        CheckoutDetailsFragment fragment = new CheckoutDetailsFragment();
        Bundle args = new Bundle();
        addedProducts=products;
        fragment.setArguments(args);
        token=t;
        customer_ID=ID;
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Checkout");
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
        root =  inflater.inflate(R.layout.fragment_checkout_details, container, false);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //addedProducts = getSharedPreference();
        TextView total = root.findViewById(R.id.total_value);
        Button pay = root.findViewById(R.id.payButton);
        //if(addedProducts!=null){
            for(Product product: addedProducts){
                totalCost+=product.getProductPrice();
            }
        //}
        total.setText(String.valueOf(totalCost));



        checkoutRecyclerView = (RecyclerView)root.findViewById(R.id.productCheckoutList);
        checkoutRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        checkoutRecyclerView.setLayoutManager(mLayoutManager);
        Log.d("chella","Added products before adapter "+addedProducts);
        checkoutAdapter= new CartAdapter(addedProducts);
        checkoutRecyclerView.setAdapter(checkoutAdapter);
        checkoutAdapter.notifyDataSetChanged();


        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),PayementWithStripe.class);
                intent.putExtra("TOKEN",token);
                intent.putExtra("TOTAL_AMOUNT",totalCost);
                intent.putExtra("CUSTOMER_ID",customer_ID);
                getActivity().startActivity(intent);
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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

   /* public List<Product> getSharedPreference(){
        Gson gson = new Gson();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String jsonPreferences = sharedPreferences.getString("ADDEDPROD", "null list");
        Type type = new TypeToken<List<Product>>() {}.getType();
        addedProd = gson.fromJson(jsonPreferences, type);
        Log.d("chella","Added Products in Checkout: "+addedProd);
        Log.d("chella","Condition check: "+addedProd.isEmpty());
        return addedProd;
    }*/
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
