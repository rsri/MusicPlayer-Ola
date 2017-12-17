package com.olatests.musicplayer.audio;

import java.io.Serializable;

/**
 * Created by srikaram on 17-Dec-17.
 */

public interface IAudio extends Serializable {
    String getSong();
    String getUrl();
    String getArtists();
    String getCoverImage();
    boolean isFavorite();
    String getDownloadPath();
}
