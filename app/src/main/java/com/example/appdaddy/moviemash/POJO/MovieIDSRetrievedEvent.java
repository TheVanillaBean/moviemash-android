package com.example.appdaddy.moviemash.POJO;

import android.support.annotation.Nullable;

import com.example.appdaddy.moviemash.Model.Game;

import java.util.List;

/**
 * Created by Alex on 1/21/2017.
 */

public class MovieIDSRetrievedEvent {

    private final String error;
    private final List<String> movieIDS;

    public MovieIDSRetrievedEvent(List<String> movies, @Nullable String error){
        this.error = error;
        this.movieIDS = movies;
    }

    public String getError() {
        return error;
    }

    public List<String> getMovieIDS() {
        return movieIDS;
    }

}
