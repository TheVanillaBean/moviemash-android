package com.example.appdaddy.moviemash.POJO;

import android.support.annotation.Nullable;

import com.example.appdaddy.moviemash.Model.User;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Alex on 1/26/2017.
 */

public class UserCastEvent {

    private final String error;

    private final User user;

    public UserCastEvent(@Nullable String error, @Nullable User user){
        this.error = error;
        this.user = user;
    }

    public String getError() {
        return error;
    }

    public User getUser() {
        return user;
    }
}
