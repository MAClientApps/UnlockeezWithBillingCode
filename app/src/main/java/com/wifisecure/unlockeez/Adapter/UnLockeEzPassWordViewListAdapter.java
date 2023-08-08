package com.wifisecure.unlockeez.Adapter;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.intentfilter.wificonnect.WifiConnectionManager;
import com.wifisecure.unlockeez.Activity.UnLockeEzWebViewActivity;
import com.wifisecure.unlockeez.R;

import java.util.List;


public class UnLockeEzPassWordViewListAdapter extends RecyclerView.Adapter<UnLockeEzPassWordViewListAdapter.WifiNetworkItemViewHolder>  {

    private final WifiConnectionManager unLockeEzWifiPassConnectionManager;
    private final Context unLockeEzContext;

    private final List<ScanResult> unLockeEzWifiScanResults;
    public UnLockeEzPassWordViewListAdapter(WifiConnectionManager unLockeEzWifiPassConnectionManager, Context unLockeEzContext, List<ScanResult> unLockeEzWifiScanResults) {
        this.unLockeEzWifiPassConnectionManager = unLockeEzWifiPassConnectionManager;
        this.unLockeEzContext = unLockeEzContext;
        this.unLockeEzWifiScanResults = unLockeEzWifiScanResults;
    }

    @NonNull
    @Override
    public WifiNetworkItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        
        View unLockeEzInflateView = LayoutInflater.from(unLockeEzContext).inflate(R.layout.show_pass_item_view, parent, false);
        return new WifiNetworkItemViewHolder(unLockeEzInflateView);

    }


    @Override
    public void onBindViewHolder(@NonNull final WifiNetworkItemViewHolder unLockeEzHolder, int unLockeEzPosition) {
        unLockeEzHolder.addWifiNetworkToList(unLockeEzWifiScanResults.get(unLockeEzPosition));
        final boolean[] showingFirst = {true};
        unLockeEzHolder.unLockeEzWifiShowThePass.setOnClickListener(v -> {
            if(showingFirst[0]){
                unLockeEzHolder.unLockeEzShowWifiPass.setVisibility(View.VISIBLE);
                showingFirst[0] = false;
            }else{
                unLockeEzHolder.unLockeEzShowWifiPass.setVisibility(View.GONE);
                showingFirst[0] = true;
            }
        });
        
        unLockeEzHolder.unLockeEzWifiPassWordCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showingFirst[0]){
                    unLockeEzHolder.unLockeEzShowWifiPass.setVisibility(View.VISIBLE);
                    showingFirst[0] = false;
                }else{
                    unLockeEzHolder.unLockeEzShowWifiPass.setVisibility(View.GONE);
                    showingFirst[0] = true;
                }
                WifiManager unLockeEzWifiManager = (WifiManager) unLockeEzContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

                if(unLockeEzWifiManager.isWifiEnabled()){
                    final ScanResult unLockeEzWifiScanResult = unLockeEzWifiScanResults.get(unLockeEzHolder.getAdapterPosition());

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        WifiNetworkSpecifier.Builder builder = new WifiNetworkSpecifier.Builder();
                        builder.setSsid(unLockeEzWifiScanResult.SSID);
                        WifiNetworkSpecifier wifiNetworkSpecifier = builder.build();
                        NetworkRequest.Builder unLockeEzWifiNetworkRequestBuilder = new NetworkRequest.Builder();
                        unLockeEzWifiNetworkRequestBuilder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
                        unLockeEzWifiNetworkRequestBuilder.removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
                        unLockeEzWifiNetworkRequestBuilder.setNetworkSpecifier(wifiNetworkSpecifier);
                        NetworkRequest networkRequest = unLockeEzWifiNetworkRequestBuilder.build();

                        ConnectivityManager unLockeEzWifiP = (ConnectivityManager) unLockeEzContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                        ConnectivityManager.NetworkCallback networkCallback = new
                                ConnectivityManager.NetworkCallback() {
                                    @Override
                                    public void onAvailable(Network network) {
                                        super.onAvailable(network);
                                        unLockeEzWifiP.bindProcessToNetwork(network);
                                        openActivityWebViewFunction(unLockeEzContext);
                                    }
                                };
                        unLockeEzWifiP.requestNetwork(networkRequest, networkCallback);
                    }
                    else
                    {
                        unLockeEzWifiPassConnectionManager.connectToAvailableSSID(unLockeEzWifiScanResult.SSID, new WifiConnectionManager.ConnectionStateChangedListener() {
                            @Override
                            public void onConnectionEstablished() {
                                Toast.makeText(unLockeEzContext, "Now connected to " + unLockeEzWifiScanResult.SSID, Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onConnectionError(String reason) {
                                Toast.makeText(unLockeEzContext, "Couldn't connect due to: " + reason, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
                else
                {
                    AlertDialog.Builder unLockeEzWifiAlert = new AlertDialog.Builder(unLockeEzContext);
                    unLockeEzWifiAlert.setTitle("Wifi Enable");
                    unLockeEzWifiAlert.setMessage("Press ok to enable your wifi");
                    unLockeEzWifiAlert.setPositiveButton("OK",
                            (dialog, id) -> {
                                Intent unLockeEzIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                                unLockeEzIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                unLockeEzContext.startActivity(unLockeEzIntent);
                            });
                    unLockeEzWifiAlert.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                    unLockeEzWifiAlert.show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return unLockeEzWifiScanResults.size();
    }
    public void openActivityWebViewFunction(Context mainContext)
    {
        Intent unLockeEzIntent=new Intent(mainContext, UnLockeEzWebViewActivity.class);
        mainContext.startActivity(unLockeEzIntent);
    }
    static class WifiNetworkItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView unLockeEzWifiNameView, unLockeEzShowWifiPass;
        private final CardView unLockeEzWifiPassWordCardView;
        private final ImageView unLockeEzWifiShowThePass;

        WifiNetworkItemViewHolder(View itemView) {
            super(itemView);
            unLockeEzShowWifiPass = itemView.findViewById(R.id.unLockeEzShowWifiPassId);
            unLockeEzWifiNameView = itemView.findViewById(R.id.unLockeEzWifiNameViewId);
            unLockeEzWifiPassWordCardView = itemView.findViewById(R.id.unLockeEzWifiPassWordCardViewId);
            unLockeEzWifiShowThePass = itemView.findViewById(R.id.unLockeEzWifiShowThePassId);
        }
        void addWifiNetworkToList(ScanResult scanResult) {
            unLockeEzWifiNameView.setText(scanResult.SSID);
            String s = UnLockeEzRandomPassWordStringFunction.getAlphaNumericString(8);
            unLockeEzShowWifiPass.setText(s);
        }
    }
    private static class UnLockeEzRandomPassWordStringFunction {
        public static String getAlphaNumericString(int n) {
            // chose a Character random from this String
            String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                    + "0123456789"
                    + "abcdefghijklmnopqrstuvxyz";
            // create StringBuffer size of AlphaNumericString
            StringBuilder sb = new StringBuilder(n);
            for (int i = 0; i < n; i++) {
                // generate a random number between
                // 0 to AlphaNumericString variable length
                int index
                        = (int) (AlphaNumericString.length()
                        * Math.random());
                // add Character one by one in end of sb
                sb.append(AlphaNumericString
                        .charAt(index));
            }
            return sb.toString();
        }
    }
}
