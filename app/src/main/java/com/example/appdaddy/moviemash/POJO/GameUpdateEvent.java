package com.example.appdaddy.moviemash.POJO;

import android.support.annotation.Nullable;

import com.example.appdaddy.moviemash.Model.Game;

/**
 * Created by Alex on 1/21/2017.
 */

public class GameUpdateEvent {

    private final String error;
    private final Game game;

    public GameUpdateEvent(Game game, @Nullable String error){
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
