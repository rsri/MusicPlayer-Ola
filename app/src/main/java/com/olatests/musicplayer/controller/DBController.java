package com.olatests.musicplayer.controller;

import com.olatests.musicplayer.audio.Audio;
import com.olatests.musicplayer.room.AudioDatabase;
import com.olatests.musicplayer.room.AudioLocal;

import java.util.List;

/**
 * Created by srikaram on 17-Dec-17.
 */

public class DBController {


    private AudioDatabase mAudioDatabase;

    public DBController(AudioDatabase audioDatabase) {
        mAudioDatabase = audioDatabase;
    }

    public void insertAll(AudioLocal... audios) {
        mAudioDatabase.getAudioDao().insertAll(audios);
    }

    public void updateAudioWithLocal(List<Audio> audios) {
        for (Audio audio : audios) {
            AudioLocal localAudio = mAudioDatabase.getAudioDao().getAudio(audio.getUrl(), audio.getSong());
            if (localAudio != null) {
                audio.setFavorite(localAudio.isFavorite());
                audio.setDownloadPath(localAudio.getDownloadPath());
            }
        }
    }

    public List<AudioLocal> getAllSavedAudios() {
        return mAudioDatabase.getAudioDao().getAllAudios();
    }

}
