package com.example.userprofileapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import com.example.userprofileapp.pojo.Product;

import java.util.List;

import static androidx.core.content.ContextCompat.getSystemService;

public class MainActivity extends AppCompatActivity implements
   CheckoutDetailsFragment.OnFragmentInteractionListener,ProductFragment.OnFragmentInteractionListener,LoginFragment.OnFragmentInteractionListener,SignUpFragment.OnFragmentInteractionListener,UserProfileViewFragment.OnFragmentInteractionListener,EditUserProfileFragment.OnFragmentInteractionListener {

    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }

        //Login Frag
        getSupportFragmentManager().beginTransaction().add(R.id.container,new LoginFragment(),"tag_LoginFrag").addToBackStack(null).commit();
        /*Intent intent = new Intent(this,PlaymentActivityHome.class);
        startActivity(intent);*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.logout){
            sharedPreferences.edit().remove("TOKEN").commit();
            getSupportFragmentManager().beginTransaction().add(R.id.container,new LoginFragment(),"tag_LoginFrag").addToBackStack(null).commit();
            return true;
        }
        //if(id==R.id.userprofile){
            //UserProfileViewFragment.newInstance(user);
            //getSupportFragmentManager().beginTransaction().add(R.id.container,new UserProfileViewFragment(),"userprofile").addToBackStack(null).commit();
        //}

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }

    @Override
    public void onFragmentInteraction(List<Product> products) {

    }
}
