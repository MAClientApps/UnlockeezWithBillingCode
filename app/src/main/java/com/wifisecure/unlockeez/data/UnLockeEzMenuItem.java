package com.wifisecure.unlockeez.data;

public class UnLockeEzMenuItem {
    String unLockeEzWifiTitle;
    int unLockeEzWifiImageId;
    public UnLockeEzMenuItem(String unLockeEzWifiTitle, int unLockeEzWifiImageIdimageId) {
        this.unLockeEzWifiTitle = unLockeEzWifiTitle;
        this.unLockeEzWifiImageId = unLockeEzWifiImageIdimageId;
    }
    public String getTitle() {
        return unLockeEzWifiTitle;
    }
    public int getImageId() {
        return unLockeEzWifiImageId;
    }
}


