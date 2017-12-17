package com.olatests.musicplayer.controller;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.olatests.musicplayer.audio.Audio;
import com.olatests.musicplayer.audio.IAudio;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by srikaram on 17-Dec-17.
 */

public class NetworkCoordinator {

    private final Context mContext;
    private List<OnAudioListFetchListener> mAudiosFetchListeners;

    private static final String AUDIOS_URL = "http://starlord.hackerearth.com/studio";

    public NetworkCoordinator(Context mContext) {
        this.mContext = mContext;
        mAudiosFetchListeners = new ArrayList<>();
    }

    public void addOnAudioListFetchListener(OnAudioListFetchListener listener) {
        this.mAudiosFetchListeners.add(listener);
    }

    public void removeOnAudioListFetchListener(OnAudioListFetchListener listener) {
        this.mAudiosFetchListeners.remove(listener);
    }

    public void loadAudioList() {
        Ion.with(mContext)
                .load(AUDIOS_URL)
                .as(new TypeToken<List<Audio>>(){})
                .setCallback(new FutureCallback<List<Audio>>() {
                    @Override
                    public void onCompleted(Exception e, List<Audio> result) {
                        if (e != null) {
                            for (OnAudioListFetchListener listener : mAudiosFetchListeners) {
                                listener.onAudioFetchFailed(e);
                            }
                        } else {
                            for (OnAudioListFetchListener listener : mAudiosFetchListeners) {
                                listener.onAudioListFetched(result);
                            }
                        }
                    }
                });
    }

    public void downloadAudio(final Audio audio, final OnDownloadListener listener) {
        File resultFile = new File(mContext.getFilesDir(), randomAlphaNumeric(10));
        try {
            resultFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onDownloadFailed();
                return;
            }
        }
        Ion.with(mContext)
                .load(audio.getUrl())
                .write(resultFile)
                .setCallback(new FutureCallback<File>() {
                    @Override
                    public void onCompleted(Exception e, File result) {
                        if (e != null) {
                            e.printStackTrace();
                            if (listener != null) {
                                listener.onDownloadFailed();
                            }
                        } else {
                            audio.setDownloadPath(result.getAbsolutePath());
                            if (listener != null) {
                                listener.onDownloadSuccessful();
                            }
                        }
                    }
                });
    }

    private String randomAlphaNumeric(int count) {
        String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    public interface OnAudioListFetchListener {
        void onAudioListFetched(List<Audio> audios);
        void onAudioFetchFailed(Exception e);
    }

    public interface OnDownloadListener {
        void onDownloadSuccessful();
        void onDownloadFailed();
    }
}
