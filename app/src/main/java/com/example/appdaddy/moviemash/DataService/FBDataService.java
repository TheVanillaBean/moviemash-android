package com.example.appdaddy.moviemash.DataService;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.appdaddy.moviemash.Model.Game;
import com.example.appdaddy.moviemash.Model.User;
import com.example.appdaddy.moviemash.POJO.GameUpdateEvent;
import com.example.appdaddy.moviemash.POJO.MovieIDSRetrievedEvent;
import com.example.appdaddy.moviemash.POJO.RatingReceivedEvent;
import com.example.appdaddy.moviemash.POJO.UploadFileEvent;
import com.example.appdaddy.moviemash.POJO.UploadProgressEvent;
import com.example.appdaddy.moviemash.POJO.UserUpdateEvent;
import com.example.appdaddy.moviemash.util.Constants;
import com.example.appdaddy.moviemash.util.L;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alex on 1/25/2017.
 */

public class FBDataService {


    private static final FBDataService _instance = new FBDataService();
    private static final FirebaseDatabase  mDatabase = FirebaseDatabase.getInstance();
    private static final StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();

    public static FBDataService getInstance() {
        return _instance;
    }

    //-----------------Database References------------------//

    public DatabaseReference mainRef() {
        return mDatabase.getReference();
    }

    public DatabaseReference usersRef() {
        return mDatabase.getReference(Constants.FIR_CHILD_USERS);
    }

    public DatabaseReference gamesRef() {
        return mDatabase.getReference(Constants.FIR_CHILD_GAMES);
    }

    public DatabaseReference userGamesRef() {
        return mDatabase.getReference(Constants.FIR_CHILD_USER_GAMES);
    }

    public DatabaseReference moviesRef() {
        return mDatabase.getReference(Constants.FIR_CHILD_MOVIES);
    }

    public DatabaseReference allTimeRankRef() {
        return mDatabase.getReference(Constants.FIR_CHILD_ALL_TIME_LEADERBOARD);
    }

    public DatabaseReference movieRatingsRef() {
        return mDatabase.getReference(Constants.FIR_CHILD_MOVIE_RATINGS);
    }

    //-----------------End Database References------------------//

    //-----------------Storage References--------------------//

    public StorageReference mainStorageRef() {
        return mStorageReference;
    }

    public StorageReference profilePicsStorageRef() {
        return mStorageReference.child(Constants.FIR_STORAGE_CHILD_USER_PROFILE_PICS);
    }

    //-----------------End Storage References--------------------//


    public void saveUser(final User user){
        Map<String, Object> properties = user.toMap();
        usersRef().child(user.getUUID()).setValue(properties, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null){
                    EventBus.getDefault().post(new UserUpdateEvent(null));
                }else{
                    EventBus.getDefault().post(new UserUpdateEvent(databaseError.getMessage()));
                }
            }
        });
    }

    public void updateUser(final User user){
        Map<String, Object> properties = user.toMap();
        usersRef().child(user.getUUID()).updateChildren(properties, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null){
                    EventBus.getDefault().post(new UserUpdateEvent(null));
                }else{
                    EventBus.getDefault().post(new UserUpdateEvent(databaseError.getMessage()));
                }
            }
        });
    }

    public void updateGame(final Game game){
        Map<String, Object> properties = game.toMap();
        gamesRef().child(game.getUuid()).updateChildren(properties, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null){
                    EventBus.getDefault().post(new GameUpdateEvent(game, null));
                }else{
                    EventBus.getDefault().post(new GameUpdateEvent(null, databaseError.getMessage()));
                }
            }
        });
    }

    public void updateAllTimeRank(String ID, String rank){
        allTimeRankRef().child(ID).setValue(rank);
    }

    public void retrieveMovieIDS(int end){
        int start = end - 10;
        int temp = 0;
        if(start <= 0){
            temp = end;
            end = end + 10;
            start = temp;
        }
        moviesRef().startAt(start).endAt(end).orderByValue().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String movieID;
                List<String> movies = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    movieID = postSnapshot.getKey();
                    movies.add(movieID);
                }
                EventBus.getDefault().post(new MovieIDSRetrievedEvent(movies, null));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                EventBus.getDefault().post(new MovieIDSRetrievedEvent(null, databaseError.getMessage()));
            }
        });
    }

    public void retrieveMovieRating(String movieID){
        movieRatingsRef().child(movieID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null){
                    EventBus.getDefault().post(new RatingReceivedEvent( Integer.valueOf(dataSnapshot.getValue().toString()), null));
                }else{
                    EventBus.getDefault().post(new RatingReceivedEvent( 1 , null));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                L.m("hello");
                EventBus.getDefault().post(new RatingReceivedEvent(-1, databaseError.getMessage()));
            }
        });
    }


    public FBDataService(){
    }

}
