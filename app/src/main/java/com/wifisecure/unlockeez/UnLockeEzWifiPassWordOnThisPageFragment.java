package com.wifisecure.unlockeez;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.CHANGE_NETWORK_STATE;
import static android.Manifest.permission.CHANGE_WIFI_STATE;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.intentfilter.androidpermissions.PermissionManager;
import com.intentfilter.wificonnect.WifiConnectionManager;
import com.wifisecure.unlockeez.Adapter.UnLockeEzPassWordViewListAdapter;
import com.wifisecure.unlockeez.databinding.FragmentPageThisOnWifiPasswordUnLockeEzBinding;
import java.util.Collections;
import java.util.List;

public class UnLockeEzWifiPassWordOnThisPageFragment extends Fragment {
    private FragmentPageThisOnWifiPasswordUnLockeEzBinding fragmentPageThisOnWifiPasswordUnLockeEzBinding;
    LocationManager unLockeEzWifiPasswordLocationManager;
    boolean unLockeEzWifiPasswordShowGpsStatus;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentPageThisOnWifiPasswordUnLockeEzBinding = FragmentPageThisOnWifiPasswordUnLockeEzBinding.inflate (inflater, container, false);
        View root = fragmentPageThisOnWifiPasswordUnLockeEzBinding.getRoot ();
        PermissionManager unLockeEzWifiPasswordPermissionManager = PermissionManager.getInstance (requireContext ());
        unLockeEzWifiPasswordPermissionManager.checkPermissions (Collections.singletonList (ACCESS_COARSE_LOCATION),
                new PermissionManager.PermissionRequestListener () {
                    @Override
                    public void onPermissionGranted() {
                    }
                    @Override
                    public void onPermissionDenied() {
                        Toast.makeText (requireActivity (), "Please provide permission to scan networks", Toast.LENGTH_LONG).show ();
                    }
                });
        unLockeEzWifiPasswordPermissionManager.checkPermissions (Collections.singletonList (CHANGE_WIFI_STATE),
                new PermissionManager.PermissionRequestListener () {
                    @Override
                    public void onPermissionGranted() {
                    }
                    @Override
                    public void onPermissionDenied() {
                        Toast.makeText (requireActivity (), "Please provide permission to scan networks", Toast.LENGTH_LONG).show ();
                    }
                });
        unLockeEzWifiPasswordPermissionManager.checkPermissions (Collections.singletonList (ACCESS_WIFI_STATE),
                new PermissionManager.PermissionRequestListener () {
                    @Override
                    public void onPermissionGranted() {
                    }
                    @Override
                    public void onPermissionDenied() {
                        Toast.makeText (requireActivity (), "Please provide permission to scan networks", Toast.LENGTH_LONG).show ();
                    }
                });
        unLockeEzWifiPasswordPermissionManager.checkPermissions (Collections.singletonList (CHANGE_NETWORK_STATE),
                new PermissionManager.PermissionRequestListener () {
                    @Override
                    public void onPermissionGranted() {
                    }
                    @Override
                    public void onPermissionDenied() {
                        Toast.makeText (requireActivity (), "Please provide permission to scan networks", Toast.LENGTH_LONG).show ();
                    }
                });
        unLockeEzWifiPasswordPermissionManager.checkPermissions (Collections.singletonList (ACCESS_NETWORK_STATE),
                new PermissionManager.PermissionRequestListener () {
                    @Override
                    public void onPermissionGranted() {
                    }
                    @Override
                    public void onPermissionDenied() {
                        Toast.makeText (requireActivity (), "Please provide permission to scan networks", Toast.LENGTH_LONG).show ();
                    }
                });
        unLockeEzWifiPasswordPermissionManager.checkPermissions (Collections.singletonList (ACCESS_FINE_LOCATION),
                new PermissionManager.PermissionRequestListener () {
                    @Override
                    public void onPermissionGranted() {
                        if (chkGpsWifiStatus ()) {
                            new Handler().postDelayed(() -> fragmentPageThisOnWifiPasswordUnLockeEzBinding.listOfWifiNetworks.setVisibility (View.VISIBLE), 0);
                            scanForAvailableNetwork ();
                        } else {
                            Intent unLockeEzWifiPasswordIntent = new Intent (Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult (unLockeEzWifiPasswordIntent, 1);
                        }
                    }
                    @Override
                    public void onPermissionDenied() {
                        Toast.makeText (requireActivity (), "Please provide permission to scan networks", Toast.LENGTH_LONG).show ();
                    }
                });
        return root;
    }
    public boolean chkGpsWifiStatus() {
        unLockeEzWifiPasswordLocationManager = (LocationManager) requireContext ().getSystemService (Context.LOCATION_SERVICE);
        assert unLockeEzWifiPasswordLocationManager != null;
        unLockeEzWifiPasswordShowGpsStatus = unLockeEzWifiPasswordLocationManager.isProviderEnabled (LocationManager.GPS_PROVIDER);
        if (unLockeEzWifiPasswordShowGpsStatus) {
            return true;
        } else {
            return false;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        if (chkGpsWifiStatus ()) {
            scanForAvailableNetwork ();
            new Handler ().postDelayed(() -> fragmentPageThisOnWifiPasswordUnLockeEzBinding.listOfWifiNetworks.setVisibility (View.VISIBLE), 0);
        } else {
            Intent intent = new Intent (Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult (intent, 1);
        }
    }
    private void scanForAvailableNetwork() {
        WifiConnectionManager wifiConnectionManager = new WifiConnectionManager (requireContext ());
        wifiConnectionManager.scanForNetworks (scanResults -> {
            try {
                showNetworkAvailableFunction(scanResults);
            }catch (Exception ignored){
            }
        });
    }
    public void showNetworkAvailableFunction(List<ScanResult> unLockeEzWifiPasswordScanResults) {
        if(unLockeEzWifiPasswordScanResults == null){
            Toast.makeText (requireActivity().getApplicationContext (), "error", Toast.LENGTH_SHORT).show ();
        }else{
            fragmentPageThisOnWifiPasswordUnLockeEzBinding.listOfWifiNetworks.setHasFixedSize(true);
            fragmentPageThisOnWifiPasswordUnLockeEzBinding.listOfWifiNetworks.setLayoutManager(new LinearLayoutManager(getContext()));
            WifiConnectionManager wifiPassConnectionManager = null;
            fragmentPageThisOnWifiPasswordUnLockeEzBinding.listOfWifiNetworks.setAdapter(new UnLockeEzPassWordViewListAdapter(null, getContext (),unLockeEzWifiPasswordScanResults));
        }
    }
}
