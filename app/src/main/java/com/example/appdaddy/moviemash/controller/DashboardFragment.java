package com.example.appdaddy.moviemash.controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.appdaddy.moviemash.DataService.AuthService;
import com.example.appdaddy.moviemash.DataService.FBDataService;
import com.example.appdaddy.moviemash.Model.Game;
import com.example.appdaddy.moviemash.Model.User;
import com.example.appdaddy.moviemash.POJO.UserCastEvent;
import com.example.appdaddy.moviemash.R;
import com.example.appdaddy.moviemash.util.Constants;
import com.example.appdaddy.moviemash.util.Dialog;
import com.example.appdaddy.moviemash.widgets.CustomRecyclerView;
import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class DashboardFragment extends Fragment {

    @BindView(R.id.new_game_btn) Button mNewGameBtn;
    @BindView(R.id.recycler_view) CustomRecyclerView mRecyclerView;
    @BindView(R.id.empty_list) TextView mEmptyList;

    private User mCurrentUser;
    private FirebaseRecyclerAdapter mAdapter;

    public DashboardFragment() {
    }

    public static DashboardFragment newInstance() {
        return new DashboardFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mNewGameBtn.setEnabled(false);
        if(AuthService.getInstance().getCurrentUser() != null){
            User.castUser(AuthService.getInstance().getCurrentUser().getUid());
        }else{
            Dialog.showDialog(getActivity(), "Authentication Error", "Could not find user...", "Okay");
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserCastCallBack(UserCastEvent event) {
        if (event.getError() == null){
            mNewGameBtn.setEnabled(true);
            mCurrentUser = event.getUser();
            setupRecyclerView();
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
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mAdapter != null){
            mAdapter.cleanup();
        }
    }

    @OnClick(R.id.new_game_btn)
    public void onNewGameBtnPressed() {
        showNewGameDialog();
    }

    public void showNewGameDialog() {

        new MaterialDialog.Builder(getActivity())
                .title("New Game")
                .content("Do you want to start a new game?")
                .positiveText("Yes")
                .negativeText("No")
                .autoDismiss(true)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String key = FBDataService.getInstance().gamesRef().push().getKey();
                        Game game = new Game(key, mCurrentUser.getUUID(), mCurrentUser.getFullName(), Constants.STATUS_NEW);
                        FBDataService.getInstance().gamesRef().child(key).setValue(game);
                        FBDataService.getInstance().userGamesRef().child(mCurrentUser.getUUID()).child(key).setValue(true);
                        Toast.makeText(getActivity(), "New Game!", Toast.LENGTH_LONG).show();
                        showGameActivity(game);
                    }
                })
                .typeface("Roboto-Regular.ttf", "Roboto-Light.ttf")
                .show();

    }

    public void showGameActivity(Game game) {

        Bundle bundle = new Bundle();
        Parcelable wrappedGame = Parcels.wrap(game);
        Parcelable wrappedUser = Parcels.wrap(mCurrentUser);
        bundle.putParcelable(Constants.EXTRA_GAME_PARCEL, wrappedGame);
        bundle.putParcelable(Constants.EXTRA_USER_PARCEL, wrappedUser);


        Intent intent = new Intent(getActivity(), GameActivity.class);
        intent.putExtras(bundle);
        getActivity().startActivity(intent);

    }

    private void setupRecyclerView(){
        mRecyclerView.showIfEmpty(mEmptyList);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new FirebaseIndexRecyclerAdapter<Game, DashboardFragment.GameHolder>(Game.class, R.layout.row_dashboard, DashboardFragment.GameHolder.class,
                FBDataService.getInstance().userGamesRef().child(mCurrentUser.getUUID()), FBDataService.getInstance().gamesRef()) {
            @Override
            public void populateViewHolder(final DashboardFragment.GameHolder gameHolder, final Game game, int position) {
                gameHolder.setName(game);
                gameHolder.setStatus(game.getStatus(), game.getUserScore());
                gameHolder.updateProfileLetter(game.getUserName());

            };
        };

        mRecyclerView.setAdapter(mAdapter);
    }


    public static class GameHolder extends CustomRecyclerView.ViewHolder {
        private final TextView mNameField;
        private final TextView mStatusField;
        private final ImageView mProfilePicImg;
        private View mView;
        private TextDrawable mTextDrawable;
        private ColorGenerator generator;
        private int randomColor;

        public GameHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            itemView.setClickable(true);
            generator = ColorGenerator.MATERIAL;
            mNameField = (TextView) itemView.findViewById(R.id.name_label);
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

        void setName(Game game) {
           mNameField.setText(game.getUserName());
        }

        void setStatus(String status, String userScore){
            switch (status) {
                case Constants.STATUS_NEW:
                    mStatusField.setText("New Game");
                    break;
                case Constants.STATUS_EXISTING:
                    mStatusField.setText("Continue Playing this game!");
                    break;
                case Constants.STATUS_FINISHED:
                    mStatusField.setText("You already finished this game! \nScore: " + userScore);
                    break;
            }
        }

    }
}
