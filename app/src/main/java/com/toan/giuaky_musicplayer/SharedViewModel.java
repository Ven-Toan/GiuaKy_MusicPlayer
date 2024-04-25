package com.toan.giuaky_musicplayer;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.toan.giuaky_musicplayer.models.Song;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Song> selectedSong = new MutableLiveData<>();

    public void selectSong(Song song) {
        selectedSong.setValue(song);
    }

    public MutableLiveData<Song> getSelectedSong() {
        return selectedSong;
    }
}
