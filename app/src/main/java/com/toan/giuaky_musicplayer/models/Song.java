package com.toan.giuaky_musicplayer.models;

import android.os.Parcel;

import java.io.Serializable;

public class Song implements Serializable {
    int idSong;
    String imageUrl;
    String songArtist;
    String songName;
    String songUrl;

    public Song() {
    }

    public Song(int idSong, String imageUrl, String songArtist, String songName, String songUrl) {
        this.idSong = idSong;
        this.imageUrl = imageUrl;
        this.songArtist = songArtist;
        this.songName = songName;
        this.songUrl = songUrl;
    }

    protected Song(Parcel in) {
        idSong = in.readInt();
        imageUrl = in.readString();
        songArtist = in.readString();
        songName = in.readString();
        songUrl = in.readString();
    }



    public int getIdSong() {
        return idSong;
    }

    public void setIdSong(int idSong) {
        this.idSong = idSong;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }


}
