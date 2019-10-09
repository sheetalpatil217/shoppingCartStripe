package com.example.userprofileapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.example.userprofileapp.model.BrainTreeToken;
import com.example.userprofileapp.model.BrainTreeTransaction;
import com.example.userprofileapp.pojo.Product;
import com.example.userprofileapp.pojo.User;
import com.example.userprofileapp.retrofit.IBrainTreeAPI;
import com.example.userprofileapp.retrofit.RetrofitClient;

import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class PlaymentActivityHome extends AppCompatActivity implements ProductFragment.OnFragmentInteractionListener, CheckoutDetailsFragment.OnFragmentInteractionListener {

    String token;
    String app_token;
    private static final int REQUEST_CODE=1234;

    Button payButton;
    TextView amount;
    ConstraintLayout paymentLayout;
    LinearLayout waitingLayout;
    Double amt;
    User user;

    IBrainTreeAPI myAPI;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playment_home);
        getSupportActionBar().setTitle("Payment");

        app_token=getIntent().getStringExtra("TOKEN");
        amt=getIntent().getDoubleExtra("TOTAL_AMOUNT",0.0);
        user = (User) getIntent().getSerializableExtra("USER");

        myAPI= RetrofitClient.getInstance(app_token).create(IBrainTreeAPI.class);

        paymentLayout= (ConstraintLayout) findViewById(R.id.payment_group);

        waitingLayout= (LinearLayout) findViewById(R.id.waiting_group);


        payButton = (Button) findViewById(R.id.payment_activity_pay_button);

        amount=(TextView) findViewById(R.id.payment_activity_amount_editText);
        amount.setText(amt.toString());

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitPayment();
            }
        });

        compositeDisposable.add(myAPI.getToken().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<BrainTreeToken>() {
            @Override
            public void accept(BrainTreeToken brainTreeToken) throws Exception {
                if(brainTreeToken.isSuccess()){
                    waitingLayout.setVisibility(View.INVISIBLE);
                    paymentLayout.setVisibility(View.VISIBLE);
                    token=brainTreeToken.getClientToken();
                    Log.d("pm_debug","Inside initial token check");
                    Log.d("pm_debug",token);
                }

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.d("pm_debug","Throwing error while fetching token");
                Toast.makeText(PlaymentActivityHome.this,""+throwable.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }));



    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();

    }

    private void submitPayment() {

        DropInRequest dropInRequest = new DropInRequest().clientToken(token);
        startActivityForResult(dropInRequest.getIntent(this),REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE){
            if(resultCode== RESULT_OK){
                DropInResult result= data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                PaymentMethodNonce nonce=result.getPaymentMethodNonce();

                // After having nonce, we just made a payment with API
                if(!TextUtils.isEmpty(amount.getText().toString())){
                    String amount_text = amount.getText().toString();
                    Log.d("sheetal","amount"+amount_text);
                    Log.d("chella","Nonce "+ nonce.getNonce());
                    compositeDisposable.add(myAPI
                                            .submitPayment(String.valueOf(Math.round(amt)),nonce.getNonce())
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new Consumer<BrainTreeTransaction>() {
                                                @Override
                                                public void accept(BrainTreeTransaction brainTreeTransaction) throws Exception {
                                                    if (brainTreeTransaction.isSuccess()){
                                                        Toast.makeText(PlaymentActivityHome.this,"" + brainTreeTransaction.getTransaction().getId(),Toast.LENGTH_SHORT).show();
                                                        ProductFragment.newInstance(app_token,"PAYMENT","null123");
                                                        getSupportFragmentManager().beginTransaction().add(R.id.container_payment,new ProductFragment(),"product").addToBackStack(null).commit();

                                                    }else{
                                                        Toast.makeText(PlaymentActivityHome.this,"Payment Failed",Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }, new Consumer<Throwable>() {
                                                @Override
                                                public void accept(Throwable throwable) throws Exception {
                                                    Toast.makeText(PlaymentActivityHome.this,""+throwable.getMessage(),Toast.LENGTH_SHORT).show();
                                                }
                                            }));


                }
            }
        }

    }

    @Override
    public void onFragmentInteraction(List<Product> products) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
