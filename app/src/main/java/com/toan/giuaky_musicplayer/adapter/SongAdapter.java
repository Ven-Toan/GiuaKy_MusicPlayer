package com.toan.giuaky_musicplayer.adapter;

import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.toan.giuaky_musicplayer.R;
import com.toan.giuaky_musicplayer.SharedViewModel;
import com.toan.giuaky_musicplayer.models.Song;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Song> listSong;
    public SongAdapter(Context context, ArrayList<Song> listSong) {
        this.context = context;
        this.listSong = listSong;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(v -> {
            int position = viewHolder.getAdapterPosition();
            SharedViewModel sharedViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(SharedViewModel.class);
            sharedViewModel.selectSong(listSong.get(position));
            DatabaseReference playRef = FirebaseDatabase.getInstance().getReference("playing").child(String.valueOf(listSong.get(position).getIdSong()));
            playRef.setValue(listSong.get(position));
            Toast.makeText(context, "Đã thêm vào danh sách phát", Toast.LENGTH_SHORT).show();
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song currentSong = listSong.get(position);
        DatabaseReference favoriteRef = FirebaseDatabase.getInstance().getReference("favorite").child(String.valueOf(currentSong.getIdSong()));
        favoriteRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    holder.ivFavorite.setImageResource(R.drawable.ic_heart_on);
                } else {
                    holder.ivFavorite.setImageResource(R.drawable.ic_heart_off);
                }
            }
        });
        Glide.with(context).load(currentSong.getImageUrl()).into(holder.ivSongImage);
        holder.tvSongName.setText(currentSong.getSongName());
        holder.tvArtistName.setText(currentSong.getSongArtist());
        holder.ivFavorite.setOnClickListener(v -> {
            favoriteRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        favoriteRef.removeValue();
                        holder.ivFavorite.setImageResource(R.drawable.ic_heart_off); // Replace with your non-favorite image resource
                        Toast.makeText(context, "Đã xóa khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
                    } else {
                        favoriteRef.setValue(currentSong);
                        holder.ivFavorite.setImageResource(R.drawable.ic_heart_on); // Replace with your favorite image resource
                        Toast.makeText(context, "Đã thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return listSong.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivSongImage;
        public TextView tvSongName;
        public TextView tvArtistName;
        public ImageView ivFavorite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSongImage = itemView.findViewById(R.id.ivSongImage);
            tvSongName = itemView.findViewById(R.id.tvSongName);
            tvArtistName = itemView.findViewById(R.id.tvArtistName);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
        }
    }

}
