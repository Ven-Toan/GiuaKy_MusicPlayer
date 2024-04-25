package com.toan.giuaky_musicplayer.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.toan.giuaky_musicplayer.R;
import com.toan.giuaky_musicplayer.SharedViewModel;
import com.toan.giuaky_musicplayer.adapter.SongAdapter;
import com.toan.giuaky_musicplayer.models.SliderItem;
import com.toan.giuaky_musicplayer.models.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;


public class PlayFragment extends Fragment {
    private DatabaseReference databaseReference;
    ImageView imageSong;
    Button btnPlaylist;
    Button btnHeart;
    ArrayList<Song> songList = new ArrayList<>();
    SongAdapter songAdapter;
    TextView txtSongName, txtSongArtist;
    MediaPlayer mediaPlayer;
    CircleImageView ivPlay, ivPause;
    ImageView ivNext, ivPrev;
    Integer currentSongIndex = 0;
    SeekBar seekBar;
    TextView tvPass, tvDue;
    Handler handler;
    String out, out2;
    Integer totalTime;
    ImageView  ivRepeat, ivShuffer;
    ArrayList<String> imageUrls = new ArrayList<>();
    ArrayList<String> songNames = new ArrayList<>();
    ArrayList<String> songArtists = new ArrayList<>();
    ArrayList<String> songUrls = new ArrayList<>();
    Runnable update;
    boolean isShuffle = false;
    private SharedViewModel sharedViewModel;
    List<SliderItem> sliderItems = new ArrayList<>();

    public PlayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play, container, false);
        setControl(view);
        update = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(mCurrentPosition);
                }
                handler.postDelayed(this, 1000);
            }
        };
        setPlayList();
        setPlaySong();
        sharedViewModel.getSelectedSong().observe(getViewLifecycleOwner(), new Observer<Song>() {
            @Override
            public void onChanged(Song song) {
                songList.add(song);
                songAdapter.notifyDataSetChanged();
                playSong(songList.size() - 1);
            }
        });
        return view;
    }

    private Task<Void> loadSongFavorite() {
        TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("favorite");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                songList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Song song = dataSnapshot.getValue(Song.class);
                    songList.add(song);
                }
                songAdapter.notifyDataSetChanged();
                if (!taskCompletionSource.getTask().isComplete()) {
                    taskCompletionSource.setResult(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Check if the task is already completed
                if (!taskCompletionSource.getTask().isComplete()) {
                    taskCompletionSource.setException(error.toException());
                }
            }
        });

        return taskCompletionSource.getTask();
    }

    private void setPlaySong() {
        mediaPlayer = new MediaPlayer();
        handler = new Handler();
        playPauseButton();

        ivNext.setOnClickListener(v -> {
            if (isShuffle) {
                Random random = new Random();
                currentSongIndex = random.nextInt(songList.size()); // Play a random song
            } else {
                if (currentSongIndex < songList.size() - 1) {
                    currentSongIndex++;
                } else {
                    currentSongIndex = 0; // Go back to the first song if it's the last song
                }
            }
            playSong(currentSongIndex);
        });

        ivPrev.setOnClickListener(v -> {
            if (isShuffle) {
                Random random = new Random();
                currentSongIndex = random.nextInt(songList.size()); // Play a random song
            } else {
                if (currentSongIndex >0) {
                    currentSongIndex--;
                } else {
                    currentSongIndex = songList.size()-1; // Go back to the first song if it's the last song
                }
            }
            playSong(currentSongIndex);
        });

        ivRepeat.setOnClickListener(v -> {
            if (mediaPlayer.isLooping()) {
                mediaPlayer.setLooping(false);
                ivRepeat.setImageResource(R.drawable.ic_repeat_off);
            } else {
                mediaPlayer.setLooping(true);
                ivRepeat.setImageResource(R.drawable.ic_repeat_on);
            }
        });

        ivShuffer.setOnClickListener(v -> {
            //isShuffle = !isShuffle; // Toggle the shuffle mode
            if (isShuffle) {
                ivShuffer.setImageResource(R.drawable.ic_shuffer_off);
                isShuffle = false;
            } else {
                ivShuffer.setImageResource(R.drawable.ic_shuffle_on);
                isShuffle = true;
            }
        });

        seekBar.setMax(100);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                out = String.format("%02d:%02d", progress / 60000, (progress % 60000) / 1000);
                tvPass.setText(out);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(update);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(update);
                int progress = seekBar.getProgress();
                mediaPlayer.seekTo(progress);
                update.run();
            }
        });

        mediaPlayer.setOnCompletionListener(mp -> {
            playNextSong();
        });

        update.run();
    }
    private void playNextSong() {
        if (isShuffle) {
            Random random = new Random();
            currentSongIndex = random.nextInt(songList.size()); // Play a random song
        } else {
            if (currentSongIndex < songList.size() - 1) {
                currentSongIndex++;
            } else {
                currentSongIndex = 0; // Go back to the first song if it's the last song
            }
        }
        playSong(currentSongIndex);
    }
    private void playPauseButton() {
        ivPlay.setOnClickListener(v -> {
            mediaPlayer.start();
            ivPlay.setVisibility(View.GONE);
            ivPause.setVisibility(View.VISIBLE);
        });

        ivPause.setOnClickListener(v -> {
            mediaPlayer.pause();
            ivPause.setVisibility(View.GONE);
            ivPlay.setVisibility(View.VISIBLE);

        });
    }

    private void playSong(Integer currentSongIndex) {
        if (songList.isEmpty()) {
            return;
        }
        try {
            if (mediaPlayer.isPlaying() || mediaPlayer != null) {
                mediaPlayer.reset();
            }
            mediaPlayer.setDataSource(songList.get(currentSongIndex).getSongUrl());
            mediaPlayer.prepare();
            mediaPlayer.start();
            ivPlay.setVisibility(View.GONE);
            ivPause.setVisibility(View.VISIBLE);
            txtSongName.setText(songList.get(currentSongIndex).getSongName());
            txtSongArtist.setText(songList.get(currentSongIndex).getSongArtist());
            totalTime = mediaPlayer.getDuration();
            seekBar.setMax(totalTime);
            out2 = String.format("%02d:%02d", totalTime / 60000, (totalTime % 60000) / 1000);
            tvDue.setText(out2);
            Glide.with(getContext())
                    .load(songList.get(currentSongIndex).getImageUrl())
                    .into(imageSong);
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    private Task<Void> loadSongFromFirebase() {
        TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("playing");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                songList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Song song = dataSnapshot.getValue(Song.class);
                    songList.add(song);
                }
                songAdapter.notifyDataSetChanged();
                if (!taskCompletionSource.getTask().isComplete()) {
                    taskCompletionSource.setResult(null);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (!taskCompletionSource.getTask().isComplete()) {
                    taskCompletionSource.setException(error.toException());
                }
            }
        });


        return taskCompletionSource.getTask();
    }

    private void setPlayList() {
        btnPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSongFromFirebase().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Danh sách phát");
                        String[] songNames = new String[songList.size()];
                        for (int i = 0; i < songList.size(); i++) {
                            songNames[i] = songList.get(i).getSongName();
                        }
                        builder.setItems(songNames, (dialog, which) -> {
                            currentSongIndex = which;
                            playSong(currentSongIndex);
                        });
                        builder.setNegativeButton("Xóa", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String songKey = String.valueOf(songList.get(currentSongIndex).getIdSong());
                                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("playing");
                                databaseReference1.child(songKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            songList.remove(currentSongIndex);
                                            builder.setItems(songNames, (dialog2, which2) -> {
                                                currentSongIndex = which2;
                                                playSong(currentSongIndex);
                                            });
                                            Toast.makeText(getContext(), "Đã xóa khỏi danh sách phát", Toast.LENGTH_SHORT).show();
                                        } else {
                                        }
                                    }
                                });

                            }
                        });
                        builder.create().show();
                    }
                });
            }
        });

        btnHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSongFavorite().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Danh sách yêu thích");
                        String[] songNames = new String[songList.size()];
                        for (int i = 0; i < songList.size(); i++) {
                            songNames[i] = songList.get(i).getSongName();
                        }
                        builder.setItems(songNames, (dialog, which) -> {
                            currentSongIndex = which;
                            playSong(currentSongIndex);
                        });
                        builder.setNegativeButton("Xóa", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String songKey = String.valueOf(songList.get(currentSongIndex).getIdSong());
                                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("favorite");
                                databaseReference1.child(songKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            songList.remove(currentSongIndex);
                                            builder.setItems(songNames, (dialog2, which2) -> {
                                                currentSongIndex = which2;
                                                playSong(currentSongIndex);
                                            });
                                            Toast.makeText(getContext(), "Đã xóa khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
                                        } else {
                                        }
                                    }
                                });

                            }
                        });
                        builder.create().show();
                    }
                });
            }
        });
    }


    private void setControl(View view) {
        btnPlaylist = view.findViewById(R.id.btnPlaylist);
        songAdapter = new SongAdapter(getContext(), songList);
        imageSong = view.findViewById(R.id.imageSong);
        txtSongName = view.findViewById(R.id.txtSongName);
        txtSongArtist = view.findViewById(R.id.txtSongArtist);
        ivPlay = view.findViewById(R.id.ivPlay);
        ivPause = view.findViewById(R.id.ivPause);
        ivPrev = view.findViewById(R.id.ivPrev);
        ivNext = view.findViewById(R.id.ivNext);
        seekBar = view.findViewById(R.id.seekBar);
        tvPass = view.findViewById(R.id.tvPass);
        tvDue = view.findViewById(R.id.tvDue);
        btnHeart = view.findViewById(R.id.btnHeart);
        ivRepeat = view.findViewById(R.id.ivRepeat);
        ivShuffer = view.findViewById(R.id.ivShuffer);
    }
}