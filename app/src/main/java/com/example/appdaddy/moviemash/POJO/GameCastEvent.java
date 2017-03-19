package com.example.appdaddy.moviemash.POJO;

import android.support.annotation.Nullable;

import com.example.appdaddy.moviemash.Model.Game;
import com.example.appdaddy.moviemash.Model.User;

/**
 * Created by Alex on 1/26/2017.
 */

public class GameCastEvent {

    private final String error;

    private final Game game;

    public GameCastEvent(@Nullable String error, @Nullable Game game){
        this.error = error;
        this.game = game;
    }

    public String getError() {
        return error;
    }

    public Game getGame() {
        return game;
    }
}
