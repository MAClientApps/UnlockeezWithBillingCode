package com.wifisecure.unlockeez;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.Fragment;

import com.wifisecure.unlockeez.data.UnLockeEzMenuItem;
import com.wifisecure.unlockeez.widget.UnLockeEzNavigationDrawer;

import java.util.ArrayList;
import java.util.List;

public class UnLockeEzMainPageActivity extends AppCompatActivity {
    UnLockeEzNavigationDrawer unLockeEzNavigationDrawer;

    RelativeLayout bannerAds;
    Class showFragmentClass;
    public static Fragment showFragment;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_main_un_locke_ez);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        unLockeEzNavigationDrawer = findViewById(R.id.unLockeEzNavigationDrawerId);
        bannerAds = findViewById(R.id.bannerAds);

        Utils.showBannerAds(bannerAds, UnLockeEzMainPageActivity.this);
        Utils.loadMaxInterstitialAd(UnLockeEzMainPageActivity.this);

        List<UnLockeEzMenuItem> menuItems = new ArrayList<>();
        menuItems.add(new UnLockeEzMenuItem("Available Wifi", R.drawable.available_wifi));
        menuItems.add(new UnLockeEzMenuItem("Show Password", R.drawable.showpass_bg));
        menuItems.add(new UnLockeEzMenuItem("Map", R.drawable.map_bg));
        unLockeEzNavigationDrawer.setMenuItemList(menuItems);
        showFragmentClass = UnLockeEzAvailableWifiOnThisPageFragment.class;
        try {
            showFragment = (Fragment) showFragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (showFragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).replace(R.id.frameLayout, showFragment).commit();
        }
        unLockeEzNavigationDrawer.setOnMenuItemClickListener(position -> {
            System.out.println("Position " + position);
            switch (position) {
                case 0: {
                    Utils.showInterstitialAd();
                    showFragmentClass = UnLockeEzAvailableWifiOnThisPageFragment.class;
                    break;
                }
                case 1: {
                    Utils.showInterstitialAd();
                    showFragmentClass = UnLockeEzWifiPassWordOnThisPageFragment.class;
                    break;
                }
                case 2: {
                    Utils.showInterstitialAd();
                    showFragmentClass = UnLockeEzMapWifiOnThisPageFragment.class;
                    break;
                }
            }
            unLockeEzNavigationDrawer.setDrawerListener(new UnLockeEzNavigationDrawer.DrawerListener() {
                @Override
                public void onDrawerOpened() {
                }

                @Override
                public void onDrawerOpening() {
                }

                @Override
                public void onDrawerClosing() {
                    try {
                        showFragment = (Fragment) showFragmentClass.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (showFragment != null) {
                        FragmentManager unLockeEzShowFragmentManager = getSupportFragmentManager();
                        unLockeEzShowFragmentManager.beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).replace(R.id.frameLayout, showFragment).commit();
                    }
                }

                @Override
                public void onDrawerClosed() {
                }

                @Override
                public void onDrawerStateChanged(int newState) {
                }
            });
        });
    }
}
