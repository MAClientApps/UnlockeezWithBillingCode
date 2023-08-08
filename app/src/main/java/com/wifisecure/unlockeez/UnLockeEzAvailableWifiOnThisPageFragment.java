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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.intentfilter.androidpermissions.PermissionManager;
import com.intentfilter.wificonnect.WifiConnectionManager;
import com.wifisecure.unlockeez.Adapter.UnLockeEzNetworkListAdapter;
import com.wifisecure.unlockeez.databinding.FragmentPageThisOnWifiAvailableUnLockeEzBinding;

import java.util.Collections;
import java.util.List;

public class UnLockeEzAvailableWifiOnThisPageFragment extends Fragment {

    private FragmentPageThisOnWifiAvailableUnLockeEzBinding fragmentPageThisOnWifiAvailableUnLockeEzBinding;
    LocationManager unLockeEzLocationManager;
    boolean unLockeEzShowGpsStatue;
    RecyclerView unLockeEzWifiNetworksListView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentPageThisOnWifiAvailableUnLockeEzBinding = FragmentPageThisOnWifiAvailableUnLockeEzBinding.inflate(inflater, container, false);
        View root = fragmentPageThisOnWifiAvailableUnLockeEzBinding.getRoot();
        unLockeEzWifiNetworksListView = root.findViewById(R.id.listof_wifi_networks1);

        PermissionManager UnLockeEzPermissionManager = PermissionManager.getInstance(requireContext());
        UnLockeEzPermissionManager.checkPermissions(Collections.singletonList(ACCESS_COARSE_LOCATION),
                new PermissionManager.PermissionRequestListener() {
                    @Override
                    public void onPermissionGranted() {
                    }

                    @Override
                    public void onPermissionDenied() {
                        Toast.makeText(requireActivity(), "Please provide permission to scan networks", Toast.LENGTH_LONG).show();
                    }
                });
        UnLockeEzPermissionManager.checkPermissions(Collections.singletonList(CHANGE_WIFI_STATE),
                new PermissionManager.PermissionRequestListener() {
                    @Override
                    public void onPermissionGranted() {
                    }

                    @Override
                    public void onPermissionDenied() {
                        Toast.makeText(requireActivity(), "Please provide permission to scan networks", Toast.LENGTH_LONG).show();
                    }
                });
        UnLockeEzPermissionManager.checkPermissions(Collections.singletonList(ACCESS_WIFI_STATE),
                new PermissionManager.PermissionRequestListener() {
                    @Override
                    public void onPermissionGranted() {
                    }

                    @Override
                    public void onPermissionDenied() {
                        Toast.makeText(requireActivity(), "Please provide permission to scan networks", Toast.LENGTH_LONG).show();
                    }
                });
        UnLockeEzPermissionManager.checkPermissions(Collections.singletonList(CHANGE_NETWORK_STATE),
                new PermissionManager.PermissionRequestListener() {
                    @Override
                    public void onPermissionGranted() {
                    }

                    @Override
                    public void onPermissionDenied() {
                        Toast.makeText(requireActivity(), "Please provide permission to scan networks", Toast.LENGTH_LONG).show();
                    }
                });
        UnLockeEzPermissionManager.checkPermissions(Collections.singletonList(ACCESS_NETWORK_STATE),
                new PermissionManager.PermissionRequestListener() {
                    @Override
                    public void onPermissionGranted() {
                    }

                    @Override
                    public void onPermissionDenied() {
                        Toast.makeText(requireActivity(), "Please provide permission to scan networks", Toast.LENGTH_LONG).show();
                    }
                });
        UnLockeEzPermissionManager.checkPermissions(Collections.singletonList(ACCESS_FINE_LOCATION),
                new PermissionManager.PermissionRequestListener() {
                    @Override
                    public void onPermissionGranted() {
                        if (CheckUnLockeEzWifiConnectStatus()) {
                            scanForAvailableUnLockeEzFunction();
                            new Handler().postDelayed(() -> fragmentPageThisOnWifiAvailableUnLockeEzBinding.listofWifiNetworks1.setVisibility(View.VISIBLE), 0);
                        } else {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, 1);
                        }
                    }

                    @Override
                    public void onPermissionDenied() {
                        Toast.makeText(requireActivity(), "Please provide permission to scan networks", Toast.LENGTH_LONG).show();
                    }
                });
        return root;
    }

    public boolean CheckUnLockeEzWifiConnectStatus() {
        unLockeEzLocationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        assert unLockeEzLocationManager != null;
        unLockeEzShowGpsStatue = unLockeEzLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (unLockeEzShowGpsStatue) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onActivityResult(int UnLockeEzRequestCode, int UnLockeEzResultCode, @Nullable Intent UnLockeEzData) {
        super.onActivityResult(UnLockeEzRequestCode, UnLockeEzResultCode, UnLockeEzData);
        if (CheckUnLockeEzWifiConnectStatus()) {
            scanForAvailableUnLockeEzFunction();
            new Handler().postDelayed(() -> fragmentPageThisOnWifiAvailableUnLockeEzBinding.listofWifiNetworks1.setVisibility(View.VISIBLE), 0);
        } else {
            Intent UnLockeEzIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(UnLockeEzIntent, 1);
        }
    }

    private void scanForAvailableUnLockeEzFunction() {
        try {
            WifiConnectionManager AvailableUnLockeEzWifiConnectionManager = new WifiConnectionManager(requireActivity());
            AvailableUnLockeEzWifiConnectionManager.scanForNetworks(this::showNetworkAvailableUnLockeEzFunction);
        } catch (Exception ignored) {
        }
    }

    private void showNetworkAvailableUnLockeEzFunction(List<ScanResult> AvailableUnLockeEzScanResults) {
        try {
            unLockeEzWifiNetworksListView.addItemDecoration(new DividerItemDecoration(requireActivity(), 0));
            unLockeEzWifiNetworksListView.setLayoutManager(new LinearLayoutManager(requireActivity()));
            unLockeEzWifiNetworksListView.setAdapter(new UnLockeEzNetworkListAdapter(requireActivity(), AvailableUnLockeEzScanResults));
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentPageThisOnWifiAvailableUnLockeEzBinding = null;
    }
}
