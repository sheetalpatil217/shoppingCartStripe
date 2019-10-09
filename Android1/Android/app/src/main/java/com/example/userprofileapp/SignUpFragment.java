package com.example.userprofileapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.userprofileapp.pojo.User;

import java.io.IOException;
import java.lang.reflect.Parameter;


public class SignUpFragment extends Fragment {


    private OnFragmentInteractionListener mListener;
    View MyFragment;
    EditText editTextEmail;
    EditText password ;
    EditText confirmPassword;
    EditText firstName;
    EditText lastName;
    String registerUserURL= "http://192.168.118.2:3000/signup";




    public SignUpFragment() {
        // Required empty public constructor
    }


    public static SignUpFragment newInstance(String param1, String param2) {
        SignUpFragment fragment = new SignUpFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
        MyFragment=inflater.inflate(R.layout.fragment_sign_up, container, false);
        return MyFragment;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        super.onActivityCreated(savedInstanceState);
         editTextEmail = (EditText)getActivity().findViewById(R.id.editTextEmailSignup);
         password = (EditText) getActivity().findViewById(R.id.editTextPasswordSignup);
         confirmPassword = (EditText) getActivity().findViewById(R.id.sign_up_email_confirm_password_edit_text);
         firstName= (EditText)getActivity().findViewById(R.id.sign_up_first_name_edit_text);
         lastName= (EditText)getActivity().findViewById(R.id.sign_up_last_name_edit_text);
        Button signup = (Button)getActivity().findViewById(R.id.buttonSignup);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("debug","Inside signup");
                if(editTextEmail.getText().toString().isEmpty() || password.getText().toString().isEmpty() || confirmPassword.getText().toString().isEmpty() || firstName.getText().toString().isEmpty()|| lastName.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "Fill in the details", Toast.LENGTH_SHORT).show();
                    Log.d("debug", "Fill details");
                }else{
                    Log.d("debug", "in else part");
                    Log.d("debug", "p"+password.getText().toString());
                    Log.d("debug", "cp"+confirmPassword.getText().toString());

                    Boolean match =(password.getText().toString().equals(confirmPassword.getText().toString()));
                    Log.d("debug", "cp"+match);
                    if (match) {
                        Log.d("debug", "password is equal");
                        //call the post for user to signup
                        User user =new User();
                        user.setFname(firstName.getText().toString());
                        user.setLname(lastName.getText().toString());
                        user.setEmail(editTextEmail.getText().toString());
                        user.setPassword(password.getText().toString());
                        try {
                            new RegisterUser(registerUserURL,getActivity(),user).execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        });
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
