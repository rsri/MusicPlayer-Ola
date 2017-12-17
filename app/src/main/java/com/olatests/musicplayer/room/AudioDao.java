package com.olatests.musicplayer.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by srikaram on 17-Dec-17.
 */

@Dao
public interface AudioDao {

    @Query("SELECT * FROM audio")
    List<AudioLocal> getAllAudios();

    @Query("SELECT * FROM audio where url = :url and song = :song")
    AudioLocal getAudio(String url, String song);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(AudioLocal... audios);
}
