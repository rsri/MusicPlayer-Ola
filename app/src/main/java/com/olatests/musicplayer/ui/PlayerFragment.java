package com.olatests.musicplayer.ui;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.koushikdutta.ion.Ion;
import com.olatests.musicplayer.MusicPlayerApp;
import com.olatests.musicplayer.R;
import com.olatests.musicplayer.audio.IAudio;
import com.olatests.musicplayer.controller.AudioController;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerFragment extends Fragment implements OnCompletionListener {


    private static final long REFRESH_DURATION = 500;

    public PlayerFragment() {
        // Required empty public constructor
    }

    private ImageView mCoverPic;
    private TextView mTitleView;
    private TextView mArtistView;
    private ImageView mPlayPauseBtn;
    private ProgressBar mProgressBar;
    private ProgressBar mPlayPercentageBar;

    private Handler mHandler;
    private AudioController mAudioController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCoverPic = view.findViewById(R.id.audio_cover_pic);
        mTitleView = view.findViewById(R.id.audio_title);
        mArtistView = view.findViewById(R.id.audio_artists);
        mPlayPauseBtn = view.findViewById(R.id.play_options_ib);
        mProgressBar = view.findViewById(R.id.play_progress_bar);
        mPlayPercentageBar = view.findViewById(R.id.play_percentage_bar);

        mHandler = new Handler();
        mAudioController = MusicPlayerApp.getApp(getContext()).getAudioController();
        mAudioController.addOnCompletionListener(this);
    }

    private Runnable mProgressUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            mPlayPercentageBar.setProgress(mAudioController.getCurrentPosition());
            mHandler.postDelayed(mProgressUpdateRunnable, REFRESH_DURATION);
        }
    };

    @Override
    public void onDetach() {
        mAudioController.removeOnCompletionListener(this);
        mAudioController.release();
        super.onDetach();
    }

    public void playAudio(IAudio audio) {
        mProgressBar.setVisibility(View.VISIBLE);
        mPlayPauseBtn.setVisibility(View.GONE);
        Ion.with(mCoverPic)
                .error(R.drawable.ic_error_24dp)
                .load(audio.getCoverImage());
        mTitleView.setText(audio.getSong());
        mArtistView.setText(audio.getArtists());
        mAudioController.prepareAndPlay(audio, new OnPreparedListener() {
            @Override
            public void onPrepared() {
                mProgressBar.setVisibility(View.GONE);
                mPlayPauseBtn.setVisibility(View.VISIBLE);
                mPlayPauseBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause_24dp, null));
                mPlayPercentageBar.setMax(mAudioController.getTotalDuration());
                mHandler.post(mProgressUpdateRunnable);
            }
        });

        mPlayPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePlayButtonStatus();
            }
        });
    }

    private void updatePlayButtonStatus() {
        @DrawableRes int drawableResource;
        if (mAudioController.isPlaying()) {
            drawableResource = R.drawable.ic_play_24dp;
            mAudioController.pause();
            mHandler.removeCallbacks(mProgressUpdateRunnable);
        } else {
            drawableResource = R.drawable.ic_pause_24dp;
            mAudioController.resume();
            mHandler.post(mProgressUpdateRunnable);
        }
        mPlayPauseBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), drawableResource, null));
    }

    @Override
    public void onCompletion() {
        @DrawableRes int drawableResource = R.drawable.ic_play_24dp;
        mHandler.removeCallbacks(mProgressUpdateRunnable);
        mPlayPauseBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), drawableResource, null));
    }
}
