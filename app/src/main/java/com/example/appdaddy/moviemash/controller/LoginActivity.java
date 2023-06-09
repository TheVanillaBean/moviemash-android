package com.example.appdaddy.moviemash.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.example.appdaddy.moviemash.DataService.AuthService;
import com.example.appdaddy.moviemash.Model.User;
import com.example.appdaddy.moviemash.POJO.AuthEvent;
import com.example.appdaddy.moviemash.POJO.UserCastEvent;
import com.example.appdaddy.moviemash.R;
import com.example.appdaddy.moviemash.util.Constants;
import com.example.appdaddy.moviemash.util.Dialog;
import com.example.appdaddy.moviemash.util.Util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by AppDaddy on 12/28/16.
 */

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.logo_imageview) ImageView background;
    @BindView(R.id.email_input) EditText emailField;
    @BindView(R.id.password_input) EditText passwordField;
    @BindView(R.id.login_btn) Button loginBtn;
    @BindView(R.id.sign_up_btn) TextView signUpBtn;
    @BindView(R.id.forgot_password_btn) TextView forgotPasswordBtn;

    private MaterialDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initBackgroundImage();
        progressDialog = Dialog.showProgressIndeterminateDialog(LoginActivity.this, "Loading...", "Signing in...", false);
    }

    private void initBackgroundImage() {
        Glide.with(this)
                .load(R.drawable.logo)
                .fitCenter()
                .into(background);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    private boolean validateInputFields(){
        return !(emailField.getText().toString().equals("") || passwordField.getText().toString().equals(""));
    }

    private void navigateToTabBarActivity(User user){
        Intent intent = null;
        intent = new Intent(LoginActivity.this, PlayerMainActivity.class);
        intent.putExtra(Constants.EXTRA_USER_ID, user.getUUID());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSignInCallBack(AuthEvent event) {
        progressDialog.dismiss();
        if (event.getError() == null){
            User.castUser(event.getUser().getUid());
        }else{
            Dialog.showDialog(LoginActivity.this, "Authentication Error", event.getError(), "Okay");
            loginBtn.setEnabled(true);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserCastCallBack(UserCastEvent event) {
        loginBtn.setEnabled(true);
        if (event.getError() == null){
            this.navigateToTabBarActivity(event.getUser());
        }else{
            Dialog.showDialog(LoginActivity.this, "Authentication Error", event.getError(), "Okay");
        }
    }

    @OnClick(R.id.forgot_password_btn)
    public void onForgotPasswordBtnPressed() {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.sign_up_btn)
    public void onSignUpBtnPressed() {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.login_btn)
    public void onLoginBtnPressed() {
        if (validateInputFields()){

            loginBtn.setEnabled(false);

            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();

            if (Util.isGooglePlayServicesAvailable(LoginActivity.this)){
                progressDialog.show();
                AuthService.getInstance().login(email, password);
            }else{
                Toast.makeText(this, "Google Play Unavailable..", Toast.LENGTH_LONG).show();
                loginBtn.setEnabled(true);
            }

        }else{
            Dialog.showDialog(LoginActivity.this, "Invalid", "Please Enter an Email and Password", "Okay");
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
