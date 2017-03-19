package com.example.appdaddy.moviemash.controller;

import android.content.Context;
import android.location.Address;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.example.appdaddy.moviemash.DataService.AuthService;
import com.example.appdaddy.moviemash.DataService.FBDataService;
import com.example.appdaddy.moviemash.Model.User;
import com.example.appdaddy.moviemash.POJO.UserCastEvent;
import com.example.appdaddy.moviemash.R;
import com.example.appdaddy.moviemash.util.Dialog;
import com.example.appdaddy.moviemash.widgets.CustomRecyclerView;
import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.parceler.Parcels;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


public class ProfileFragment extends Fragment {

    @BindView(R.id.user_image) ImageView mProfileImage;
    @BindView(R.id.name_label) TextView mNameLabel;
    @BindView(R.id.email_label) TextView mEmailLabel;
    @BindView(R.id.all_time_rank_label) TextView mAllTimeRank;

    private User mCurrentUser;
    private TextDrawable mTextDrawable;
    private ColorGenerator generator;
    private int randomColor;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(AuthService.getInstance().getCurrentUser() != null){
            generator = ColorGenerator.MATERIAL;
            User.castUser(AuthService.getInstance().getCurrentUser().getUid());
        }else{
            Dialog.showDialog(getActivity(), "Authentication Error", "Could not find user...", "Okay");
        }
      }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserCastCallBack(UserCastEvent event) {
        if (event.getError() == null){
            mCurrentUser = event.getUser();
            mNameLabel.setText(mCurrentUser.getFullName());
            mEmailLabel.setText(mCurrentUser.getEmail());
            mAllTimeRank.setText("All Time Rank: " + mCurrentUser.getUserAllTimeRank());
            randomColor = generator.getRandomColor();
            mTextDrawable = TextDrawable.builder()
                    .buildRound(mCurrentUser.getFullName().substring(0,2), randomColor);
            mProfileImage.setImageDrawable(mTextDrawable);
        }else{
            Dialog.showDialog(getActivity(), "Authentication Error", event.getError(), "Okay");
        }
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

}
