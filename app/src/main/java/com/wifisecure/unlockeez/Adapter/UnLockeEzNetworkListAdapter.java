package com.wifisecure.unlockeez.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import com.intentfilter.wificonnect.WifiConnectionManager;
import com.wifisecure.unlockeez.Activity.UnLockeEzWebViewActivity;
import com.wifisecure.unlockeez.R;
import java.util.List;

public class UnLockeEzNetworkListAdapter extends RecyclerView.Adapter<UnLockeEzNetworkListAdapter.WifiNetworkConnectItemViewHolder>  {

    private final WifiConnectionManager unLockeEzWifiConnectionManager;
    private final Context unLockeEzContextThis;
    private final List<ScanResult> unLockeEzWifiScanRusts;
    public UnLockeEzNetworkListAdapter(Context unLockeEzContextThis, List<ScanResult> unLockeEzWifiScanRusts) {
        this.unLockeEzContextThis = unLockeEzContextThis;
        this.unLockeEzWifiScanRusts = unLockeEzWifiScanRusts;
        this.unLockeEzWifiConnectionManager = new WifiConnectionManager(unLockeEzContextThis.getApplicationContext());
    }

    @NonNull
    @Override
    public WifiNetworkConnectItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View unLockeEzView = LayoutInflater.from(unLockeEzContextThis).inflate(R.layout.unlockk_network_item_view, parent, false);
        return new WifiNetworkConnectItemViewHolder (unLockeEzView);
    }


    @Override
    public void onBindViewHolder(@NonNull final WifiNetworkConnectItemViewHolder holder, int position) {
        holder.addWifiNetworkToList(unLockeEzWifiScanRusts.get(position));

        holder.UnLockeEzCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View unLockeEzWifiView) {

                WifiManager unLockeEzWifiManager = (WifiManager) unLockeEzContextThis.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

                if(unLockeEzWifiManager.isWifiEnabled()){
                    final ScanResult scanResult = unLockeEzWifiScanRusts.get(holder.getAdapterPosition());
                    Snackbar.make(((Activity) unLockeEzContextThis).findViewById(R.id.status1), "Connecting to: " + scanResult.SSID, Snackbar.LENGTH_LONG).show();

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        WifiNetworkSpecifier.Builder builder = new WifiNetworkSpecifier.Builder();
                        builder.setSsid(scanResult.SSID);
                        WifiNetworkSpecifier wifiNetworkSpecifier = builder.build();
                        NetworkRequest.Builder networkRequestBuilder = new NetworkRequest.Builder();
                        networkRequestBuilder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
                        networkRequestBuilder.removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
                        networkRequestBuilder.setNetworkSpecifier(wifiNetworkSpecifier);
                        NetworkRequest networkRequest = networkRequestBuilder.build();

                        ConnectivityManager unLockeEzWifi = (ConnectivityManager) unLockeEzContextThis.getSystemService(Context.CONNECTIVITY_SERVICE);
                        ConnectivityManager.NetworkCallback networkCallback = new
                                ConnectivityManager.NetworkCallback() {
                                    @Override
                                    public void onAvailable(Network network) {
                                        super.onAvailable(network);
                                        unLockeEzWifi.bindProcessToNetwork(network);
                                        openActivityUnLockeEzWifiWebViewFunction(unLockeEzContextThis);
                                    }
                                };
                        unLockeEzWifi.requestNetwork(networkRequest, networkCallback);
                    }
                    else
                    {
                        unLockeEzWifiConnectionManager.connectToAvailableSSID(scanResult.SSID, new WifiConnectionManager.ConnectionStateChangedListener() {
                            @Override
                            public void onConnectionEstablished() {
                                Toast.makeText(unLockeEzContextThis, "Now connected to " + scanResult.SSID, Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onConnectionError(String reason) {
                                Toast.makeText(unLockeEzContextThis, "Couldn't connect due to: " + reason, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
                else
                {
                    AlertDialog.Builder unLockeEzAlert = new AlertDialog.Builder(unLockeEzContextThis);
                    unLockeEzAlert.setTitle("Wifi Enable");
                    unLockeEzAlert.setMessage("Press ok to enable your wifi");
                    unLockeEzAlert.setPositiveButton("OK",
                            (dialog, id) -> {
                                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                unLockeEzContextThis.startActivity(intent);
                            });
                    unLockeEzAlert.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                    unLockeEzAlert.show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return unLockeEzWifiScanRusts.size();
    }
    public void openActivityUnLockeEzWifiWebViewFunction(Context mainContext)
    {
        Intent unLockeEzIntent=new Intent(mainContext, UnLockeEzWebViewActivity.class);
        mainContext.startActivity(unLockeEzIntent);
    }
    static class WifiNetworkConnectItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView UnLockeEzWifiNetworkNameView;
        private final CardView UnLockeEzCardView;

        WifiNetworkConnectItemViewHolder(View UnLockeEzItemView) {
            super(UnLockeEzItemView);
            UnLockeEzWifiNetworkNameView = UnLockeEzItemView.findViewById(R.id.UnLockeEzWifiNetworkNameViewId);
            UnLockeEzCardView = UnLockeEzItemView.findViewById(R.id.UnLockeEzCardViewId);
        }

        void addWifiNetworkToList(ScanResult UnLockeEzScanResult) {
            UnLockeEzWifiNetworkNameView.setText(UnLockeEzScanResult.SSID);
        }
    }

}
