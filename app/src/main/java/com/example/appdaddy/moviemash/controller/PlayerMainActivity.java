package com.example.appdaddy.moviemash.controller;

import android.content.Context;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.appdaddy.moviemash.POJO.UploadFileEvent;
import com.example.appdaddy.moviemash.POJO.UploadProgressEvent;
import com.example.appdaddy.moviemash.POJO.UserUpdateEvent;
import com.example.appdaddy.moviemash.R;
import com.ncapdevi.fragnav.FragNavController;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PlayerMainActivity extends AppCompatActivity implements FragNavController.TransactionListener, FragNavController. RootFragmentListener{

    Toolbar mToolbar;

    private BottomBar mBottomBar;
    private FragNavController mNavController;

    private final int INDEX_DASHBOARD = FragNavController.TAB1;
    private final int INDEX_PROFILE = FragNavController.TAB2;
    private final int INDEX_LEADERBOARD = FragNavController.TAB3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("MovieMash");

        mBottomBar = (BottomBar) findViewById(R.id.bottomBar);
        mBottomBar.selectTabAtPosition(INDEX_DASHBOARD);

        mNavController =
                new FragNavController(savedInstanceState, getSupportFragmentManager(), R.id.container, this, 3, INDEX_DASHBOARD);
        mNavController.setTransactionListener(this);

        mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.bb_menu_dashboard:
                        mNavController.switchTab(INDEX_DASHBOARD);
                        break;
                    case R.id.bb_menu_profile:
                        mNavController.switchTab(INDEX_PROFILE);
                        break;
                    case R.id.bb_menu_leaderboard:
                        mNavController.switchTab(INDEX_LEADERBOARD);
                        break;
                }
            }
        });

        mBottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId){
                mNavController.clearStack();
            }
        });
    }

    @Override
    public void onBackPressed() {
       super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mNavController != null) {
            mNavController.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onTabTransaction(Fragment fragment, int index) {

    }

    @Override
    public void onFragmentTransaction(Fragment fragment) {

    }

    @Override
    public Fragment getRootFragment(int index) {
        switch (index) {
            case INDEX_DASHBOARD:
                return DashboardFragment.newInstance();
            case INDEX_PROFILE:
                return ProfileFragment.newInstance();
            case INDEX_LEADERBOARD:
                return LeaderboardFragment.newInstance();

        }
        throw new IllegalStateException("Illegal Index");
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
