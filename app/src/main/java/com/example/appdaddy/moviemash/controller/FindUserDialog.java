package com.example.appdaddy.moviemash.controller;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.appdaddy.moviemash.DataService.AuthService;
import com.example.appdaddy.moviemash.DataService.FBDataService;
import com.example.appdaddy.moviemash.Model.User;
import com.example.appdaddy.moviemash.POJO.UserCastEvent;
import com.example.appdaddy.moviemash.R;
import com.example.appdaddy.moviemash.util.Constants;
import com.example.appdaddy.moviemash.util.Dialog;
import com.example.appdaddy.moviemash.util.L;
import com.example.appdaddy.moviemash.util.Util;
import com.example.appdaddy.moviemash.widgets.CustomRecyclerView;
import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.parceler.Parcels;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by Alex on 3/3/2017.
 */

public class FindUserDialog extends DialogFragment {

    @BindView(R.id.search_view) EditText mSearchView;
    @BindView(R.id.recycler_view) CustomRecyclerView mRecyclerView;
    @BindView(R.id.empty_list) TextView mEmptyList;

    private User mCurrentUser;
    private FirebaseRecyclerAdapter mAdapter;

    public FindUserDialog() {
    }

    public static FindUserDialog newInstance() {
        return new FindUserDialog();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_find_player, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);

        if(AuthService.getInstance().getCurrentUser() != null){
            User.castUser(AuthService.getInstance().getCurrentUser().getUid());
        }else{
            Dialog.showDialog(getActivity(), "Authentication Error", "Could not find user...", "Okay");
        }
    }

    private void setupRecyclerView(){
        mRecyclerView.showIfEmpty(mEmptyList);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new FirebaseRecyclerAdapter<User, UserHolder>(User.class, R.layout.row_find_user, UserHolder.class,
                FBDataService.getInstance().usersRef()){

            @Override
            protected void populateViewHolder(UserHolder viewHolder, User user, int position) {
                viewHolder.setName(user.getFullName());
//                viewHolder.setStatus(user.get);
                viewHolder.setRank("52");
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        L.m("OnClick View Holder");
                    }
                });
            }
        };


        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @OnClick(R.id.cancel_btn)
    public void onCancelBtnPressed() {
        dismiss();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserCastCallBack(UserCastEvent event) {
        if (event.getError() == null){
            mCurrentUser = event.getUser();
            setupRecyclerView();
        }else{
            Dialog.showDialog(getActivity(), "Authentication Error", event.getError(), "Okay");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }

    public static class UserHolder extends CustomRecyclerView.ViewHolder {
        private final TextView mNameField;
        private final TextView mStatusField;
        private final TextView mRankField;
        private final ImageView mProfilePicImg;
        private View mView;

        public UserHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            mNameField = (TextView) itemView.findViewById(R.id.name_label);
            mRankField = (TextView) itemView.findViewById(R.id.all_time_rank_label);
            mStatusField = (TextView) itemView.findViewById(R.id.status_label);
            mProfilePicImg = (ImageView) itemView.findViewById(R.id.profile_image);
            mView = itemView;

        }

        View getView(){
            return mView;
        }

        void updateProfilePicture(Context context, String path){
            Glide.with(context)
                    .using(new FirebaseImageLoader())
                    .load(FBDataService.getInstance().profilePicsStorageRef().child(path))
                    .placeholder(R.drawable.profile_blue)
                    .bitmapTransform(new RoundedCornersTransformation(context, 48, 0))
                    .into(mProfilePicImg);
        }

        void setName(String name) {
            mNameField.setText(name);
        }

        void setRank(String rank) {
            mRankField.setText(rank);
        }

        void setStatus(String status){
            mStatusField.setText(status);
        }


    }

}


