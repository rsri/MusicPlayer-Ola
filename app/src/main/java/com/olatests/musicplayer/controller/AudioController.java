package com.olatests.musicplayer.controller;

import android.content.Context;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.devbrackets.android.exomedia.AudioPlayer;
import com.devbrackets.android.exomedia.core.audio.ExoAudioPlayer;
import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.olatests.musicplayer.audio.IAudio;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by srikaram on 17-Dec-17.
 */

public class AudioController {

    private static final String USER_AGENT_FORMAT = "ExoMedia %s (%d) / Android %s / %s";
    private String userAgent = String.format(Locale.getDefault(), USER_AGENT_FORMAT, com.devbrackets.android.exomedia.BuildConfig.VERSION_NAME, com.devbrackets.android.exomedia.BuildConfig.VERSION_CODE, Build.VERSION.RELEASE, Build.MODEL);

    private final Context mContext;
    private final AudioPlayer mAudioPlayer;

    private List<OnCompletionListener> mOnCompletionListeners;

    private long mPausedPosition;

    public AudioController(Context context) {
        this.mContext = context;
        mAudioPlayer = new AudioPlayer(new ExoAudioPlayer(mContext));
        mAudioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mOnCompletionListeners = new ArrayList<>();
    }

    public void prepareAndPlay(@NonNull IAudio audio, final OnPreparedListener preparedListener) {
        Uri uri;
        if (audio.getDownloadPath() != null) {
            uri = Uri.fromFile(new File(audio.getDownloadPath()));
            mAudioPlayer.setDataSource(uri);
        } else if (isNetworkAvailable()){
            uri = Uri.parse(audio.getUrl());
            mAudioPlayer.setDataSource(uri, buildMediaSource(uri));
        } else {
            Toast.makeText(mContext, "No internet connection to play.", Toast.LENGTH_SHORT).show();
            if (preparedListener != null) {
                preparedListener.onPrepared();
            }
            return;
        }
        mAudioPlayer.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared() {
                if (preparedListener != null) {
                    preparedListener.onPrepared();
                }
                mAudioPlayer.seekTo(0);
                mAudioPlayer.start();
            }
        });
        mAudioPlayer.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion() {
                for (OnCompletionListener completionListener : mOnCompletionListeners) {
                    completionListener.onCompletion();
                }
                mAudioPlayer.reset();
            }
        });
        mAudioPlayer.prepareAsync();
    }

    public void addOnCompletionListener(OnCompletionListener completionListener) {
        this.mOnCompletionListeners.add(completionListener);
    }

    public void removeOnCompletionListener(OnCompletionListener completionListener) {
        this.mOnCompletionListeners.remove(completionListener);
    }

    public void release() {
        mAudioPlayer.stopPlayback();
        mAudioPlayer.release();
    }

    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory(
                userAgent, null,
                DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                true);
        if (uri != null) {
            return new ExtractorMediaSource(uri, dataSourceFactory, new DefaultExtractorsFactory(),
                    new Handler(), null);
        }
        throw new IllegalStateException("Unable to build media source");
    }

    public void pause() {
        if (mAudioPlayer.isPlaying()) {
            mAudioPlayer.pause();
            mPausedPosition = mAudioPlayer.getCurrentPosition();
        }
    }

    public void resume() {
        if (!mAudioPlayer.isPlaying()) {
            mAudioPlayer.seekTo(mPausedPosition);
            mAudioPlayer.start();
        }
    }

    public boolean isPlaying() {
        return mAudioPlayer.isPlaying();
    }

    public int getTotalDuration() {
        return (int) TimeUnit.MILLISECONDS.toSeconds(mAudioPlayer.getDuration());
    }

    public int getCurrentPosition() {
        return (int) TimeUnit.MILLISECONDS.toSeconds(mAudioPlayer.getCurrentPosition());
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
