package com.wifisecure.unlockeez.Activity;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;

import java.util.ArrayList;

public class UnLockeEzSignAln implements Comparable {
    private String UnlockWifiid; // Mac address or BSSID
    private Integer UnlockWifilevel; // RSSI. -70...-30
    private Integer UnlockWifistrength; // Calculated strength based on RSSI. 0..100
    private Integer UnlockWififreq; // Frequency (only wifi)
    private String UnlockWifiname; // SSID for wifi
    private String UnlockWifivenue; // Registered venue of access point (only wifi)
    private String UnlockWifitype; // Wifi
    private ArrayList<Integer> UnlockWifihistory;


    public UnLockeEzSignAln(ScanResult wifi) {
        this.UnlockWifiid = wifi.BSSID;
        this.UnlockWifilevel = wifi.level;
        this.UnlockWifistrength = WifiManager.calculateSignalLevel(wifi.level, 100);
        this.UnlockWififreq = wifi.frequency;
        this.UnlockWifiname = wifi.SSID==null?"NA WiFi":wifi.SSID;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.UnlockWifivenue = wifi.venueName.toString();
        }
        this.UnlockWifitype = "wifi";
        UnlockWifihistory = new ArrayList<>();
        UnlockWifihistory.add(this.UnlockWifilevel);
    }

    @Override
    public int compareTo(Object o) {
        UnLockeEzSignAln otherSignaln = (UnLockeEzSignAln) o;
        return otherSignaln.UnlockWifilevel - UnlockWifilevel;
    }

    @Override
    public String toString() {
        return UnlockWifitype + " - " + UnlockWifiname + " - " + UnlockWifistrength;
    }

    public void update(int level) {
        this.UnlockWifilevel = level;
        UnlockWifistrength = WifiManager.calculateSignalLevel(level, 100);
        UnlockWifihistory.add(level);
        while (UnlockWifihistory.size() > 21) {
            UnlockWifihistory.remove(0);
        }
    }

    @Override
    public boolean equals(Object obj) {
        UnLockeEzSignAln otherSignaln = (UnLockeEzSignAln) obj;
        return UnlockWifiid.equals(otherSignaln.UnlockWifiid);
    }

    public String getUnlockWifiid() {
        return UnlockWifiid;
    }

    public ArrayList<Integer> getUnlockWifihistory() {
        return UnlockWifihistory;
    }

    public int getUnlockWifilevel() {
        return UnlockWifilevel;
    }

    public int getUnlockWifistrength() {
        return UnlockWifistrength;
    }

    public String getUnlockWifiname() {
        return UnlockWifiname;
    }

    public String getUnlockWifivenue() {
        return UnlockWifivenue;
    }

    public int getUnlockWififreq() {
        return UnlockWififreq;
    }

    public String getUnlockWifitype() {
        return UnlockWifitype;
    }
}
