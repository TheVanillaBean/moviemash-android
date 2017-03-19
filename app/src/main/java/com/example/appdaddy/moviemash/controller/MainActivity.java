package com.example.appdaddy.moviemash.controller;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.appdaddy.moviemash.DataService.AuthService;
import com.example.appdaddy.moviemash.Model.User;
import com.example.appdaddy.moviemash.POJO.UserCastEvent;
import com.example.appdaddy.moviemash.R;
import com.example.appdaddy.moviemash.util.Constants;
import com.example.appdaddy.moviemash.util.Dialog;
import com.example.appdaddy.moviemash.util.L;
import com.example.appdaddy.moviemash.util.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.logo_imageview) ImageView background;

    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initBackgroundImage();

        if (Util.isGooglePlayServicesAvailable(MainActivity.this)){
            validateUserToken();
        }
    }

    private void initBackgroundImage() {
        Glide.with(this)
                .load(R.drawable.app_logo)
                .fitCenter()
                .into(background);
    }

    private void validateUserToken() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    User.castUser(user.getUid());
                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

            }
        };
    }

    private void navigateToPlayerActivity(){

        EventBus.getDefault().unregister(this);
        if (mAuthListener != null) {
            AuthService.getInstance().getAuthInstance().removeAuthStateListener(mAuthListener);
        }

        Intent intent = null;
        intent = new Intent(MainActivity.this, PlayerMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserCastCallBack(UserCastEvent event) {
        if (event.getError() == null){
            this.navigateToPlayerActivity();
        }else{
            Dialog.showDialog(MainActivity.this, "Authentication Error", event.getError(), "Okay");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        AuthService.getInstance().getAuthInstance().addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        if (mAuthListener != null) {
            AuthService.getInstance().getAuthInstance().removeAuthStateListener(mAuthListener);
        }
        super.onStop();
    }

}
