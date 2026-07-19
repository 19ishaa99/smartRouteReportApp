package com.example.smartroute;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.smartroute.fragments.HomeFragment;
import com.example.smartroute.fragments.LocationFragment;
import com.example.smartroute.fragments.MyReportsFragment;
import com.example.smartroute.fragments.ProfileFragment;
import com.example.smartroute.fragments.ReportFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigation = findViewById(R.id.bottomNavigation);

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        bottomNavigation.setOnItemSelectedListener(item -> {

            Fragment selectedFragment;

            int itemId = item.getItemId();

            if (itemId == R.id.navHome) {
                selectedFragment = new HomeFragment();

            } else if (itemId == R.id.navReport) {
                selectedFragment = new ReportFragment();

            } else if (itemId == R.id.navLocation) {
                selectedFragment = new LocationFragment();

            } else if (itemId == R.id.navMyReports) {
                selectedFragment = new MyReportsFragment();

            } else if (itemId == R.id.navProfile) {
                selectedFragment = new ProfileFragment();

            } else {
                return false;
            }

            loadFragment(selectedFragment);
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFragmentContainer, fragment)
                .commit();
    }
}