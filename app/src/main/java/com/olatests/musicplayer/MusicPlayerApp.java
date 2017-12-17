package com.olatests.musicplayer;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;

import com.olatests.musicplayer.controller.AudioController;
import com.olatests.musicplayer.controller.DBController;
import com.olatests.musicplayer.controller.NetworkCoordinator;
import com.olatests.musicplayer.room.AudioDatabase;

/**
 * Created by srikaram on 17-Dec-17.
 */

public class MusicPlayerApp extends Application {

    private NetworkCoordinator mNetworkCoordinator;
    private AudioController mAudioController;

    private DBController mDbController;
    @Override
    public void onCreate() {
        super.onCreate();
        mNetworkCoordinator = new NetworkCoordinator(this);
        mAudioController = new AudioController(this);

        AudioDatabase audioDatabase = Room.databaseBuilder(this, AudioDatabase.class, AudioDatabase.DB_NAME)
                .allowMainThreadQueries()
                .build();

        mDbController = new DBController(audioDatabase);
    }

    public static MusicPlayerApp getApp(Context context) {
        return (MusicPlayerApp) context.getApplicationContext();
    }

    public DBController getDbController() {
        return mDbController;
    }

    public NetworkCoordinator getNetworkCoordinator() {
        return mNetworkCoordinator;
    }

    public AudioController getAudioController() {
        return mAudioController;
    }
}
