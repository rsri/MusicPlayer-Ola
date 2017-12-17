package com.olatests.musicplayer.room;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by srikaram on 17-Dec-17.
 */

@Database(entities = {AudioLocal.class}, version = 1, exportSchema = false)
public abstract class AudioDatabase extends RoomDatabase {

    public static final String DB_NAME = "audio_database";

    public abstract AudioDao getAudioDao();
}
