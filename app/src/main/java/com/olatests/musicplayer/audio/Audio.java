package com.olatests.musicplayer.audio;

/**
 * Created by srikaram on 17-Dec-17.
 */

public class Audio implements IAudio {

    private String song;

    private String url;

    private String artists;

    private String cover_image;

    private transient boolean favorite;

    private transient String downloadPath;

    public Audio() {
    }

    public Audio(IAudio audio) {
        this.url = audio.getUrl();
        this.artists = audio.getArtists();
        this.cover_image = audio.getCoverImage();
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
        return cover_image;
    }

    public void setCoverImage(String coverImage) {
        this.cover_image = coverImage;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }
}
