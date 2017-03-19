package com.example.appdaddy.moviemash.controller;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.appdaddy.moviemash.DataService.FBDataService;
import com.example.appdaddy.moviemash.Model.Game;
import com.example.appdaddy.moviemash.Model.User;
import com.example.appdaddy.moviemash.POJO.GameUpdateEvent;
import com.example.appdaddy.moviemash.POJO.MovieIDSRetrievedEvent;
import com.example.appdaddy.moviemash.POJO.RatingReceivedEvent;
import com.example.appdaddy.moviemash.R;
import com.example.appdaddy.moviemash.util.Constants;
import com.example.appdaddy.moviemash.util.Dialog;
import com.example.appdaddy.moviemash.util.L;
import com.example.appdaddy.moviemash.util.Util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.IOException;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


/**
 * Created by Alex on 3/3/2017.
 */

public class GameActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.movie_image) ImageView mMovieImage;
    @BindView(R.id.movie_title) TextView mMovieTitleLabel;
    @BindView(R.id.movie_year) TextView mMovieYearLabel;
    @BindView(R.id.movie_actors) TextView mMovieActorsLabel;
    @BindView(R.id.movie_desc) TextView mMovieDescLabel;
    @BindView(R.id.movie_count_label) TextView mMovieCountLabel;
    @BindView(R.id.score_label) TextView mScoreLabel;
    @BindView(R.id.rating_field) EditText mRatingField;

    private Game mGame;
    private User mCurrentUser;

    private int mMovieCounter;
    private OkHttpClient mClient;
    private String mCurrentRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("New Game");
        }

        if(Parcels.unwrap(getIntent().getExtras().getParcelable(Constants.EXTRA_GAME_PARCEL)) != null){

            mClient = new OkHttpClient();
            mGame = Parcels.unwrap(getIntent().getExtras().getParcelable(Constants.EXTRA_GAME_PARCEL));
            mCurrentUser = Parcels.unwrap(getIntent().getExtras().getParcelable(Constants.EXTRA_USER_PARCEL));
            mRatingField.setEnabled(false);
            mMovieCounter = -1;

            if(mGame.getStatus().equals(Constants.STATUS_NEW)){
                populateGameWithRandomMovies();
            }else{
                mMovieCounter++;
                loadMovie();
            }

            mRatingField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    boolean handled = false;
                    if (actionId == EditorInfo.IME_ACTION_DONE) {

                        if (!mRatingField.getText().toString().equals("") && Integer.parseInt(mRatingField.getText().toString()) <= 100 && Integer.parseInt(mRatingField.getText().toString()) >= 0){
                            mRatingField.setEnabled(false);
                            mCurrentRating = mRatingField.getText().toString();
                            mRatingField.setText("");
                            FBDataService.getInstance().retrieveMovieRating(mGame.getMovieList().get(mMovieCounter));
                        }else{
                            Toast.makeText(GameActivity.this, "Guess must be between 0-100.", Toast.LENGTH_LONG).show();
                        }

                        handled = true;
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

                        if(imm.isAcceptingText()) {
                            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        }

                    }
                    return handled;
                }
            });
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRatingReceivedEvent(RatingReceivedEvent event) {
        if (event.getError() == null){
            int actualRating = event.getRating();
            int userRating = Integer.parseInt(mCurrentRating);
            int difference = Math.abs(actualRating - userRating);
            int currentTotal = Integer.valueOf(mGame.getUserScore());
            int newScore = currentTotal + difference;

            mGame.setUserScore(newScore + "");

            Toast.makeText(this, "You were off by " + difference + "\n Actual Rating: " + actualRating, Toast.LENGTH_SHORT).show();
            FBDataService.getInstance().updateGame(mGame);

        }else{
            Dialog.showDialog(this, "Ratings Error", event.getError(), "Okay");
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMovieIDSRetrieved(MovieIDSRetrievedEvent event) {
        if (event.getError() == null){
            mGame.setMovieList(event.getMovieIDS());
            mGame.setStatus(Constants.STATUS_EXISTING);
            FBDataService.getInstance().updateGame(mGame);

        }else{
            Dialog.showDialog(this, "Movie IDS Error", event.getError(), "Okay");
        }
    }

    private void populateGameWithRandomMovies(){
        int randomNum = getRandomNumber(1, Constants.TOTAL_MOVIE_IDS);
        FBDataService.getInstance().retrieveMovieIDS(randomNum);
    }

    public static int getRandomNumber(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGameUpdateEvent(GameUpdateEvent event) {
        if (event.getError() == null){

            mGame = event.getGame();

            if(mMovieCounter == 9){
                EventBus.getDefault().unregister(this);
                Toast.makeText(GameActivity.this, "Finished with total score " + mGame.getUserScore(), Toast.LENGTH_LONG).show();
                int currentAllTime = Integer.parseInt(mCurrentUser.getUserAllTimeRank());
                currentAllTime = currentAllTime + Integer.valueOf(mGame.getUserScore());
                mCurrentUser.setUserAllTimeRank(currentAllTime + "");
                mGame.setStatus(Constants.STATUS_FINISHED);
                FBDataService.getInstance().updateAllTimeRank(mCurrentUser.getUUID(), currentAllTime + "");
                FBDataService.getInstance().updateUser(mCurrentUser);
                FBDataService.getInstance().updateGame(mGame);
                finish();
            }

            if(mGame.getStatus().equals(Constants.STATUS_EXISTING) && mMovieCounter != 9){
                mMovieCounter++;
                loadMovie();
            }

        }else{
            Dialog.showDialog(this, "Game Update Error", event.getError(), "Okay");
        }
    }

    private void loadMovie(){
        mMovieCountLabel.setText(String.format("Movie %d out of 10", mMovieCounter + 1));
        mScoreLabel.setText(String.format("Score: %s", mGame.getUserScore()));
        String movieID = mGame.getMovieList().get(mMovieCounter);
        getMovieFromURL(movieID);
    }

    private void getMovieFromURL(final String movieID) {
        Request request = new Request.Builder()
                .url(Util.getCorrectURl(movieID))
                .build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                Toast.makeText(GameActivity.this, "Failed to retrieve movie information...", Toast.LENGTH_LONG).show();
            }

            @Override public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String responseData = "";
                            try {
                                responseData = response.body().string();
                            } catch (IOException e) {
                                Toast.makeText(GameActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                            JSONObject jsonObject = new JSONObject(responseData);
                            if (response.isSuccessful()) {

                                mMovieTitleLabel.setText(getTitle(jsonObject));
                                mMovieYearLabel.setText(getYear(jsonObject));
                                mMovieActorsLabel.setText(getActors(jsonObject));
                                mMovieDescLabel.setText(getPlot(jsonObject));
                                Glide.with(GameActivity.this)
                                        .load(getImageURL(jsonObject))
                                        .fitCenter()
                                        .into(mMovieImage);

                                mRatingField.setEnabled(true);

                            } else {
                                Toast.makeText(GameActivity.this, "Database Response Failed...", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(GameActivity.this, "Failed to retrieve movie information..." + e.getMessage(), Toast.LENGTH_LONG).show();
                        }


                    }
                });

            }
        });
    }

    private String getTitle (JSONObject jsonObject) throws JSONException {
        return jsonObject.getString("Title");
    }
    private String getYear( JSONObject jsonObject) throws JSONException {
        return jsonObject.getString("Year");
    }
    private String getActors (JSONObject jsonObject) throws JSONException {
        return jsonObject.getString("Actors");
    }
    private String getPlot( JSONObject jsonObject) throws JSONException {
        return jsonObject.getString("Plot");
    }
    private String getImageURL( JSONObject jsonObject) throws JSONException {
        return jsonObject.getString("Poster");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        if(mGame.getStatus().equals(Constants.STATUS_EXISTING)){
            mGame.setUserScore("0");
            FBDataService.getInstance().updateGame(mGame);
            Toast.makeText(GameActivity.this, "Closed out of Game. You can replay it though...", Toast.LENGTH_LONG).show();
            finish();
        }
        super.onPause();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}


