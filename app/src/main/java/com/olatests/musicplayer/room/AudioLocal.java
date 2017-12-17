package com.olatests.musicplayer.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.olatests.musicplayer.audio.IAudio;

/**
 * Created by srikaram on 17-Dec-17.
 */
@Entity(tableName = "audio")
public class AudioLocal implements IAudio {

    @PrimaryKey
    @ColumnInfo(name = "url")
    @NonNull
    private String url;

    @ColumnInfo(name = "song")
    private String song;

    @ColumnInfo(name = "artists")
    private String artists;

    @ColumnInfo(name = "coverImage")
    private String coverImage;

    @ColumnInfo(name = "downloaded_path")
    private String downloadPath;

    @ColumnInfo(name = "favorite")
    private boolean favorite;

    public AudioLocal() {
    }

    public AudioLocal(IAudio audio) {
        this.url = audio.getUrl();
        this.artists = audio.getArtists();
        this.coverImage = audio.getCoverImage();
        this.song = audio.getSong();
        this.favorite = audio.isFavorite();
        this.downloadPath = audio.getDownloadPath();
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getArtists() {
        return artists;
    }

    public void setArtists(String artists) {
        this.artists = artists;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
