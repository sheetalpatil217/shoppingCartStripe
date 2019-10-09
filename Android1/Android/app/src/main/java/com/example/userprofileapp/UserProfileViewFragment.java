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

import com.example.userprofileapp.pojo.User;

import java.io.IOException;

public class UserProfileViewFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    View MyFragment;
    TextView fname;
    TextView lname;
    TextView age;
    TextView weight;
    TextView address;
    static User user;
    String userProfileURL = "http://ec2-3-89-187-121.compute-1.amazonaws.com:3000/userprofile/";

    public UserProfileViewFragment() {
        // Required empty public constructor
    }

    public static UserProfileViewFragment newInstance(User user1) {
        UserProfileViewFragment fragment = new UserProfileViewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        //user = user1;
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
        MyFragment =inflater.inflate(R.layout.fragment_user_profile_view, container, false);
//        Bundle bundle = getArguments();
//        user = (User)bundle.getSerializable("user");
        return MyFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((AppCompatActivity)getActivity()).getSupportActionBar().show();

        fname = (TextView)MyFragment.findViewById(R.id.fname_friend);
        lname = (TextView)MyFragment.findViewById(R.id.lname_friend);
        age = (TextView) MyFragment.findViewById(R.id.age_firend);
        weight= (TextView)MyFragment.findViewById(R.id.weight_friend);
        address= (TextView)MyFragment.findViewById(R.id.address_friend);
        Button Edit = (Button)MyFragment.findViewById(R.id.back_button_friends);
        try {
           user = new  UserProfile(userProfileURL,getActivity(),user).execute();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }









        //call async method to set the text for the view


        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditUserProfileFragment.newInstance(user);
                getFragmentManager().beginTransaction().replace(R.id.container,new EditUserProfileFragment(),"tag_EditFrag").addToBackStack(null).commit();

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
