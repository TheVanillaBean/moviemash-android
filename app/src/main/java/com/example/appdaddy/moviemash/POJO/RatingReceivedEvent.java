package com.example.appdaddy.moviemash.POJO;

import android.support.annotation.Nullable;

import com.example.appdaddy.moviemash.Model.Game;

/**
 * Created by Alex on 1/21/2017.
 */

public class RatingReceivedEvent {

    private final String error;
    private final int rating;

    public RatingReceivedEvent(int rating, @Nullable String error){
        this.error = error;
        this.rating = rating;
    }

    public String getError() {
        return error;
    }

    public int getRating() {
        return rating;
    }

}
