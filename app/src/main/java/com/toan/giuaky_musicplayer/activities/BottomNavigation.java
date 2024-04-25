package com.toan.giuaky_musicplayer.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.toan.giuaky_musicplayer.R;
import com.toan.giuaky_musicplayer.adapter.AdapterViewPager;
import com.toan.giuaky_musicplayer.fragments.HomeFragment;
import com.toan.giuaky_musicplayer.fragments.PlayFragment;

import java.util.ArrayList;

public class BottomNavigation extends AppCompatActivity {

    private ViewPager2 viewPager2;
    private final ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);
        setControl();
        fragmentArrayList.add(new HomeFragment());
        fragmentArrayList.add(new PlayFragment());
        AdapterViewPager adapterViewPager = new AdapterViewPager(this, fragmentArrayList);
        viewPager2.setAdapter(adapterViewPager);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId()==R.id.itHome){
                viewPager2.setCurrentItem(0, true);
            }
            if (item.getItemId()==R.id.itPlay){
                viewPager2.setCurrentItem(1, true);
            }
            return true;
        });
    }

    private void setControl() {
        viewPager2 = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottomNav);
    }
}