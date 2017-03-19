package com.example.appdaddy.moviemash.controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.appdaddy.moviemash.DataService.FBDataService;
import com.example.appdaddy.moviemash.Model.User;
import com.example.appdaddy.moviemash.R;
import com.example.appdaddy.moviemash.widgets.CustomRecyclerView;
import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LeaderboardFragment extends Fragment {

    @BindView(R.id.recycler_view) CustomRecyclerView mRecyclerView;
    @BindView(R.id.empty_list) TextView mEmptyList;

    private FirebaseRecyclerAdapter mAdapter;

    public LeaderboardFragment() {
    }

    public static LeaderboardFragment newInstance() {
        return new LeaderboardFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_leaderboards, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
            setupRecyclerView();
      }


    private void setupRecyclerView(){
        mRecyclerView.showIfEmpty(mEmptyList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new FirebaseIndexRecyclerAdapter<User, LeaderboardFragment.UserHolder>(User.class, R.layout.leaderboard_row, LeaderboardFragment.UserHolder.class,
                FBDataService.getInstance().allTimeRankRef(), FBDataService.getInstance().usersRef()) {
            @Override
            public void populateViewHolder(final LeaderboardFragment.UserHolder viewHolder, final User user, int position) {
                viewHolder.setName(user.getFullName());
                viewHolder.setRank(user.getUserAllTimeRank());
                viewHolder.updateProfileLetter(user.getFullName());

            }
        };

        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mAdapter != null){
            mAdapter.cleanup();
        }
    }

    public static class UserHolder extends CustomRecyclerView.ViewHolder {
        private final TextView mNameField;
        private final TextView mStatusField;
        private final TextView mRankField;
        private final ImageView mProfilePicImg;
        private View mView;
        private TextDrawable mTextDrawable;
        private ColorGenerator generator;
        private int randomColor;


        public UserHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            generator = ColorGenerator.MATERIAL;
            mNameField = (TextView) itemView.findViewById(R.id.name_label);
            mRankField = (TextView) itemView.findViewById(R.id.all_time_rank_label);
            mStatusField = (TextView) itemView.findViewById(R.id.status_label);
            mProfilePicImg = (ImageView) itemView.findViewById(R.id.profile_image);
            mView = itemView;

        }

        View getView(){
            return mView;
        }

        void updateProfileLetter(String name){
            randomColor = generator.getRandomColor();
            mTextDrawable = TextDrawable.builder()
                    .buildRound(name.substring(0,2), randomColor);
            mProfilePicImg.setImageDrawable(mTextDrawable);
        }

        void setName(String name) {
            mNameField.setText(name);
        }

        void setRank(String rank) {
            mRankField.setText(String.format("All Time Rank: %s", rank));
        }


    }

}
