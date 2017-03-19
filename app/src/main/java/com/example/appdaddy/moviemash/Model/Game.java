package com.example.appdaddy.moviemash.Model;

import com.example.appdaddy.moviemash.DataService.FBDataService;
import com.example.appdaddy.moviemash.POJO.GameCastEvent;
import com.example.appdaddy.moviemash.POJO.UserCastEvent;
import com.example.appdaddy.moviemash.util.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by AppDaddy on 3/11/17.
 */


@Parcel /* Variables are not private because of the Parcel Dependency - Reflection */
@IgnoreExtraProperties
public class Game {

    String uuid;
    String userID;
    String userName;
    List<String> movieList = new ArrayList<>();
    String score;
    String status;
    HashMap<String, Object> timestamp;

    public String getStatus() {
        return (status == null) ? "" : status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserScore() {
        return (score == null) ? "0" : score;
    }

    public void setUserScore(String score) {
        this.score = score;
    }

    public String getUuid() {
        return (uuid == null) ? "" : uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUserID() {
        return (userID == null) ? "" : userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return (userName == null) ? "" : userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<String> getMovieList() {
        return movieList;
    }

    public void setMovieList(List<String> movieList) {
        this.movieList = movieList;
    }

    public HashMap<String, Object> getTimestamp() {
        return timestamp;
    }

    public void setTimestamp() {
        HashMap<String, Object> timestampHash = new HashMap<>();
        timestampHash.put(Constants.TIMESTAMP, ServerValue.TIMESTAMP);
        this.timestamp = timestampHash;
    }

    public Game() {
    }


    public Game(String uuid, String userID, String userName, String status) {
        this.uuid = uuid;
        this.userID = userID;
        this.userName = userName;
        this.status = status;
        HashMap<String, Object> timestampHash = new HashMap<>();
        timestampHash.put(Constants.TIMESTAMP, ServerValue.TIMESTAMP);
        this.timestamp = timestampHash;    }

    @Exclude
    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();
        result.put(Constants.UUID, getUuid());
        result.put(Constants.USER_ID, getUserID());
        result.put(Constants.USER_NAME, getUserName());
        result.put(Constants.SCORE, getUserScore());
        result.put(Constants.MOVIE_LIST, getMovieList());
        result.put(Constants.STATUS, getStatus());
        result.put(Constants.TIMESTAMP, getTimestamp());
        return result;
    }

    public static void castGame(String uuid) {

        FBDataService.getInstance().gamesRef().child(uuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) return;

                Game game = dataSnapshot.getValue(Game.class);

                EventBus.getDefault().post(new GameCastEvent(null, game));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                EventBus.getDefault().post(new GameCastEvent(databaseError.getMessage(), null));
            }
        });

    }
}
