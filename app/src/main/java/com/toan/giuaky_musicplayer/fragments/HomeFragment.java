package com.toan.giuaky_musicplayer.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.toan.giuaky_musicplayer.R;

import com.toan.giuaky_musicplayer.adapter.SongAdapter;
import com.toan.giuaky_musicplayer.models.Song;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private SongAdapter songAdapter;
    private ArrayList<Song> songList;
    private ArrayList<Song> searchList;

    DatabaseReference databaseReference;
    DatabaseReference databaseReference1;
    androidx.appcompat.widget.SearchView searchSong;
    RecyclerView recyclerView;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        loadSong();

        databaseReference1= FirebaseDatabase.getInstance().getReference("playing");
        databaseReference1.removeValue();
        recyclerView = view.findViewById(R.id.recycleViewSong);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        songList = new ArrayList<>();
        songAdapter = new SongAdapter(getContext(),songList);
        recyclerView.setAdapter(songAdapter);

        searchSong = view.findViewById(R.id.searchSong);
        searchSong.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList = new ArrayList<>();
                for (Song song : songList){
                    if (song.getSongName().toLowerCase().contains(newText.toLowerCase())){
                        searchList.add(song);
                    }
                }
                songAdapter = new SongAdapter(getContext(),searchList);
                recyclerView.setAdapter(songAdapter);
                songAdapter.notifyDataSetChanged();
                return false;
            }
        });
        return view;
    }

    private void loadSong() {
        databaseReference = FirebaseDatabase.getInstance().getReference("song");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Song song = dataSnapshot.getValue(Song.class);
                    songList.add(song);
                }
                songAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}