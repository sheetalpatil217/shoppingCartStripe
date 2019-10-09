package com.example.userprofileapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.userprofileapp.pojo.User;

import java.io.IOException;


public class LoginFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    View myFragment;
    EditText email;
    EditText password;
    String LoginUserURL="http://192.168.118.2:3000/signin";
    String authToken;
    SharedPreferences sharedPreferences;
    //static User user;

    public LoginFragment() {
        // Required empty public constructor
        Log.d("chella","Login URL : "+LoginUserURL);
    }


    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(User user1) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        //user=user1;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        authToken = sharedPreferences.getString("TOKEN","Default String");
        Log.d("chella","Auth Token in Login :"+ authToken.isEmpty());
        Log.d("chella","In Login auth is :" + authToken.equalsIgnoreCase("Default String"));
        Log.d("chella","Token from Login :"+authToken);
        if(authToken.isEmpty() != false|| authToken.equalsIgnoreCase("Default String")==false)
        {
            Log.d("chella","Inside the if condition");
            ProductFragment.newInstance(authToken,"HOME","null123");
            getFragmentManager().beginTransaction().replace(R.id.container,new ProductFragment(),"product").addToBackStack(null).commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        myFragment=inflater.inflate(R.layout.fragment_login, container, false);
        return myFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        email= (EditText)myFragment.findViewById(R.id.editTextEmail);
        password=(EditText) myFragment.findViewById(R.id.editTextPassword);
        TextView signup = (TextView)myFragment.findViewById(R.id.textViewSignUpLink);
       // TextView forgetPassword = (TextView)myFragment.findViewById(R.id.textViewForgetPassword);
        Button login = (Button)myFragment.findViewById(R.id.buttonLogin);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.containerLogin, new SignUpFragment(), "tag_signupFrag").addToBackStack(null).commit();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validateFields()){
                    User user =new User();
                    user.setEmail(email.getText().toString());
                    user.setPassword(password.getText().toString());
                    try {

                        new LoginUser(LoginUserURL,getActivity(),user).execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        });





    }


    public boolean validateFields(){
        Boolean valid =false;
        if(email.getText().toString()==""){
            Toast.makeText(getActivity(), "Enter Email.",
                    Toast.LENGTH_SHORT).show();
            email.setError("Enter your email");
            valid =true;
        }else if(password.getText().toString()==""){
            Toast.makeText(getActivity(), "Enter Password.",
                    Toast.LENGTH_SHORT).show();
            password.setError("Enter your password");
            valid =true;
        }else if (email.getText().toString()=="" && password.getText().toString()==""){
            Toast.makeText(getActivity(), "Enter Email and Password to Login.",
                    Toast.LENGTH_SHORT).show();
            email.setError("Enter your email");
            password.setError("Enter your password");
            valid =true;
        }

        return valid;
    }





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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
