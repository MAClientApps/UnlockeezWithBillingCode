package com.wifisecure.unlockeez.Activity;

import androidx.annotation.NonNull;

public class UnLockeEzSignalInFon implements Comparable{
    public String unLockeEzId;
    public String unLockeEzWifiName;
    public int unLockeEzStrLength;
    public double unLockeEzWifiLatitude;
    public double unLockeEzWifiLongitude;
    public UnLockeEzSignalInFon(String unLockeEzId, String unLockeEzWifiName, int unLockeEzStrLength, double unLockeEzWifiLatitude, double unLockeEzWifiLongitude) {
        this.unLockeEzId = unLockeEzId;
        this.unLockeEzWifiName = unLockeEzWifiName;
        this.unLockeEzStrLength = unLockeEzStrLength;
        this.unLockeEzWifiLatitude = unLockeEzWifiLatitude;
        this.unLockeEzWifiLongitude = unLockeEzWifiLongitude;
    }
    @NonNull
    @Override
    public String toString() {
        return unLockeEzId + " - " + unLockeEzWifiName + " - " + unLockeEzWifiLatitude + ", " + unLockeEzWifiLongitude + " - " + unLockeEzStrLength;
    }
    @Override
    public int compareTo(Object o) {
        UnLockeEzSignalInFon otherSignal = (UnLockeEzSignalInFon) o;
        return otherSignal.unLockeEzStrLength - unLockeEzStrLength;
    }
}
