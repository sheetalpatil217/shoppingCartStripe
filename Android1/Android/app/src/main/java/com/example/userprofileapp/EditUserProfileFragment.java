package com.example.userprofileapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

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

public class EditUserProfileFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    View MyFragment;
    EditText fname;
    EditText lname;
    EditText age;
    EditText weight;
    EditText address;
    static User userDetail;
    String editUserProfileURL = "http://ec2-3-89-187-121.compute-1.amazonaws.com:3000/userprofile";

    public EditUserProfileFragment() {
        // Required empty public constructor
    }

    public static EditUserProfileFragment newInstance(User user) {
        EditUserProfileFragment fragment = new EditUserProfileFragment();
        Bundle args = new Bundle();
        userDetail=user;
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
        MyFragment =inflater.inflate(R.layout.fragment_edit_user_profile, container, false);
        return MyFragment;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        Log.d("sheetal","userdetail"+userDetail);
        fname = (EditText)getActivity().findViewById(R.id.edit_profile_fname);
        lname = (EditText) getActivity().findViewById(R.id.edit_profile_last_name_edittext);
        age = (EditText) getActivity().findViewById(R.id.edit_profile_age_edit_text);
        weight= (EditText)getActivity().findViewById(R.id.edit_profile_weight_edit_text);
        address= (EditText)getActivity().findViewById(R.id.editText_address);
        Button save = (Button)getActivity().findViewById(R.id.edit_profile_save_button);

        fname.setText(userDetail.getFname());
        lname.setText(userDetail.getLname());

        if(userDetail.getAge().toString().isEmpty() || userDetail.getAge().toString().equalsIgnoreCase("0") ){
            //age.setText("No information to display");
        }else{
            age.setText(userDetail.getAge().toString());
        }

        if(userDetail.getWeight().toString().isEmpty() || userDetail.getWeight().toString().equalsIgnoreCase("0.0")){
           // weight.setText("No information to display");
        }else{
            weight.setText(userDetail.getWeight().toString());
        }

        if(userDetail.getAddress().isEmpty()){
            //address.setText("No information to display");
        }else{
            address.setText(userDetail.getAddress());
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get all details back



                userDetail.setFname(fname.getText().toString());
                userDetail.setLname(lname.getText().toString());
                userDetail.setAge(Integer.parseInt(age.getText().toString()));
                userDetail.setWeight(Double.valueOf(weight.getText().toString()));
                userDetail.setAddress(address.getText().toString());

                try {
                    new EditUserProfile(editUserProfileURL,getActivity(),userDetail).execute();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getActivity(), "Saved Profile Successfully",
                        Toast.LENGTH_SHORT).show();
                getFragmentManager().popBackStack();

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


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
