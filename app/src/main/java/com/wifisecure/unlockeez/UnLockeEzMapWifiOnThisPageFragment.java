package com.wifisecure.unlockeez;

import static android.content.Context.WIFI_SERVICE;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;
import com.wifisecure.unlockeez.Activity.UnLockeEzGPSTracker;
import com.wifisecure.unlockeez.Activity.UnLockeEzPopWindow;
import com.wifisecure.unlockeez.Activity.UnLockeEzSignalInFon;
import com.wifisecure.unlockeez.Activity.UnLockeEzSignAln;
import com.wifisecure.unlockeez.Adapter.UnLockeEzSQLiteHelper;
import com.wifisecure.unlockeez.databinding.FragmentPageThisOnWifiMapUnLockeEzBinding;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class UnLockeEzMapWifiOnThisPageFragment extends Fragment implements OnMapReadyCallback {
    public FragmentPageThisOnWifiMapUnLockeEzBinding fragmentPageThisOnWifiMapUnLockeEzBinding;
    private GoogleMap unLockeEzMapGoogleMap;
    FusedLocationProviderClient unLockeEzMapLocationProvider;
    Handler unLockeEzMapHandler = new Handler (Looper.getMainLooper ());
    double unLockeEzMapLatitude, unLockeEzMapLongitude;
    UnLockeEzGPSTracker unLockeEzMapGPSTracker;
    LatLng unLockeEzMapCurPos;
    UnLockeEzSQLiteHelper unLockeEzMapSQLLiteHelper;
    private static final int unLockeEzMapPERMISSION_FINE_LOCATION = 100;
    private static final String unLockeEzMapTAG = "MapsActivity";
    private static final float unLockeEzMapDEFAULT_ZOOM = 18f;
    private static final int unLockeEzMapPERMISSION_ALL = 1;
    private static final String[] unLockeEzMapPERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private EditText unLockeEzMapSearchTextWifi;
    private ImageView unLockeEzMapGpsWifi;
    private Spinner unLockeEzMapSpinnerWifi;
    private ImageView unLockeEzMapRotateWifi;
    private HeatmapTileProvider unLockeEzMapHeatmapTileProvider;
    private TileOverlay unLockeEzMapTileOverlay;

    private WifiManager unLockeEzMapWifiManager;
    private Map<String, UnLockeEzSignAln> unLockeEzMapWifiSignals;
    private Map<String, UnLockeEzSignAln> unLockeEzMapNewWifiSignals;
    private SignalWifiUnlockerReceiver unLockeEzMapSignalWifiUnlockerReceiver;

    public List<PriorityQueue<UnLockeEzSignalInFon>> unLockeEzMapTopFiveList = new ArrayList<> ();
    private Runnable unLockeEzMapRunnable;

    Button unLockeEzMapMark;

    ImageView unLockeEzMapSaveBtn;

    public UnLockeEzMapWifiOnThisPageFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        fragmentPageThisOnWifiMapUnLockeEzBinding = FragmentPageThisOnWifiMapUnLockeEzBinding.inflate (inflater, container, false);
        View unLockeEzMapRoot = fragmentPageThisOnWifiMapUnLockeEzBinding.getRoot ();
        unLockeEzMapSQLLiteHelper = new UnLockeEzSQLiteHelper(requireActivity ());
        unLockeEzMapSearchTextWifi = unLockeEzMapRoot.findViewById (R.id.unLockeEzMapSearchTextWifiId);
        unLockeEzMapGpsWifi = unLockeEzMapRoot.findViewById (R.id.unLockeEzMapGpsWifiId);
        unLockeEzMapRotateWifi = unLockeEzMapRoot.findViewById (R.id.unLockeEzMapRotateWifiId);
        unLockeEzMapMark = unLockeEzMapRoot.findViewById (R.id.unLockeEzMapMarkId);
        unLockeEzMapSaveBtn = unLockeEzMapRoot.findViewById (R.id.unLockeEzMapSaveBtnId);
        unLockeEzMapSpinnerWifi = unLockeEzMapRoot.findViewById (R.id.unLockeEzMapSpinnerWifiId);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        if (ActivityCompat.checkSelfPermission (requireActivity (), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getDeviceLocation ();
        } else {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                requestPermissions (new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, unLockeEzMapPERMISSION_FINE_LOCATION);
            }
        }
        return unLockeEzMapRoot;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView ();
        fragmentPageThisOnWifiMapUnLockeEzBinding = null;
    }

    @Override
    public void onPause() {
        super.onPause ();
        try {
            requireContext ().unregisterReceiver (unLockeEzMapSignalWifiUnlockerReceiver);
        }catch (Exception ignored){
        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        unLockeEzMapGoogleMap = googleMap;
        init ();
        setSignalWifiUnlockerReceiver ();
        startSignalSearchWifiUnlocker ();
        spinner ();
        startBtn ();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat ("HH");
        String unLockeEzMapCurrentDateAndTime = sdf.format (new Date ());
        // Night Mode from 20 pm - 7 am
        if (Integer.parseInt (unLockeEzMapCurrentDateAndTime) > 20 || Integer.parseInt (unLockeEzMapCurrentDateAndTime) < 7) {
            try {
                // Customize the styling of the base map using a JSON object defined in a raw resource file
                boolean unLockeEzMapSuccess = googleMap.setMapStyle (
                        MapStyleOptions.loadRawResourceStyle (
                                requireActivity (), R.raw.map_style));
                if (!unLockeEzMapSuccess) {
                    Log.e (unLockeEzMapTAG, "Style parsing failed.");
                }
            } catch (Resources.NotFoundException e) {
                Log.e (unLockeEzMapTAG, "Can't find style. Error: ", e);
            }
        }

        if (ContextCompat.checkSelfPermission (requireActivity (), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getDeviceLocation ();
            unLockeEzMapGoogleMap.setMyLocationEnabled (true);
            unLockeEzMapGoogleMap.getUiSettings ().setMyLocationButtonEnabled (false);
        }
        unLockeEzMapWifiSignals = new HashMap<> ();
        unLockeEzMapNewWifiSignals = new HashMap<> ();
        try {
            populateHeatmap ();
        } catch (JSONException e) {
            Toast.makeText (requireActivity (), "Problem getting data from db.", Toast.LENGTH_LONG).show ();
        }
    }
    private void init() {
        unLockeEzMapSearchTextWifi.setOnEditorActionListener ((unLockeEzMapTextView, unLockeEzMapActionId, unLockeEzMapKeyEvent) -> {
            if (unLockeEzMapActionId == EditorInfo.IME_ACTION_SEARCH || unLockeEzMapActionId == EditorInfo.IME_ACTION_DONE ||
                    unLockeEzMapKeyEvent.getAction () == KeyEvent.ACTION_DOWN || unLockeEzMapKeyEvent.getAction () == KeyEvent.KEYCODE_ENTER) {
                // Start searching
                geoLocate ();
            }
            return false;
        });
        unLockeEzMapGpsWifi.setOnClickListener (v -> {
            Log.d (unLockeEzMapTAG, "onClick: clicked gps icon");
            getDeviceLocation ();
        });

        unLockeEzMapRotateWifi.setOnClickListener (v -> {
            CameraPosition cameraPosition = new CameraPosition.Builder ()
                    .target (unLockeEzMapCurPos)
                    .zoom (18)
                    .bearing (45)
                    .tilt (90)                   // Tilt can only be 0 - 90
                    .build ();
            unLockeEzMapGoogleMap.animateCamera (CameraUpdateFactory.newCameraPosition (cameraPosition));
        });
    }
    private void geoLocate() {
        String searchString = unLockeEzMapSearchTextWifi.getText ().toString ();
        Geocoder unLockeEzMapGeocoder = new Geocoder (requireContext ());
        List<Address> list = new ArrayList<> ();
        try {
            list = unLockeEzMapGeocoder.getFromLocationName (searchString, 1);
        } catch (IOException e) {
            Log.e (unLockeEzMapTAG, "geoLocate: IOException: " + e.getMessage ());
        }

        if (list.size () > 0) {
            Address unLockeEzMapAddress = list.get (0);
            Log.d (unLockeEzMapTAG, "geoLocate: found a location:" + unLockeEzMapAddress.toString ());
            moveCamera (new LatLng (unLockeEzMapAddress.getLatitude (), unLockeEzMapAddress.getLongitude ()), unLockeEzMapDEFAULT_ZOOM);
        } else {
            Log.d (unLockeEzMapTAG, "geoLocate: list size == 0");
        }
    }
    // Enable the Start Button
    private void startBtn() {
        // Create the gradient
        final int[] unLockeEzMapColors = setColors (102, 255, 0, 255, 0, 0);
        final float[] unLockeEzMapStartPoints = setStartPoints (0.2f, 1f);
        final Gradient gradient = setGradient (unLockeEzMapColors, unLockeEzMapStartPoints);

        unLockeEzMapMark.setOnClickListener (new View.OnClickListener () {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View unLockeEzMapView) {
                unLockeEzMapSaveBtn.setVisibility (View.INVISIBLE);
                if (unLockeEzMapMark.getText ().equals ("Start")) {
                    unLockeEzMapMark.setText ("Stop");
                    if (unLockeEzMapTileOverlay != null) {
                        unLockeEzMapTileOverlay.remove ();
                    }
                    // Updating all signals every other second
                    unLockeEzMapRunnable = new Runnable () {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void run() {
                            unLockeEzMapGPSTracker = new UnLockeEzGPSTracker(requireContext ());
                            unLockeEzMapLatitude = unLockeEzMapGPSTracker.getLatitude ();
                            unLockeEzMapLongitude = unLockeEzMapGPSTracker.getLongitude ();
                            unLockeEzMapCurPos = new LatLng (unLockeEzMapLatitude, unLockeEzMapLongitude);
                            Log.i ("CurPos", unLockeEzMapCurPos.toString ());
                            unLockeEzMapWifiSignals = updateWifi ();
                            getBestKLoc (unLockeEzMapWifiSignals);
                            getBestKWifi ();
                            if (!unLockeEzMapTopFiveList.isEmpty ()) {
                                drawHeatmap (getHeatmap (unLockeEzMapTopFiveList.get (0)), gradient, 15, 0.7);
                            }
                            // Delete heatmap every 2 seconds
                            unLockeEzMapHandler.postDelayed (() -> {
                                if (unLockeEzMapTileOverlay != null) {
                                    unLockeEzMapTileOverlay.remove ();
                                }
                            }, 2000);
                            unLockeEzMapHandler.postDelayed (this, 2000);
                        }
                    };
                    unLockeEzMapHandler.postDelayed (unLockeEzMapRunnable, 1000);
                } else {
                    try {
                        if (!unLockeEzMapTopFiveList.isEmpty ()) {
                            makeJSON (unLockeEzMapTopFiveList.get (0));
                        }
                    } catch (JSONException e) {
                        Log.i ("makeJson", "Sorry failed to print.");
                    }

                    // Pop-window
                    for (int i = 0; i < unLockeEzMapTopFiveList.size (); i++) {
                        Log.i ("top_" + (i + 1) + ": ", unLockeEzMapTopFiveList.get (i).toString ());
                    }
                    Intent intent = new Intent (requireActivity (), UnLockeEzPopWindow.class);
                    intent.putExtra ("name1", unLockeEzMapTopFiveList.get (0).peek ().unLockeEzWifiName);
                    intent.putExtra ("strength1", unLockeEzMapTopFiveList.get (0).peek ().unLockeEzStrLength);

                    intent.putExtra ("name2", unLockeEzMapTopFiveList.get (1).peek ().unLockeEzWifiName);
                    intent.putExtra ("strength2", unLockeEzMapTopFiveList.get (1).peek ().unLockeEzStrLength);

                    intent.putExtra ("name3", unLockeEzMapTopFiveList.get (2).peek ().unLockeEzWifiName);
                    intent.putExtra ("strength3", unLockeEzMapTopFiveList.get (2).peek ().unLockeEzStrLength);

                    startActivityForResult (intent, 1000);
                    unLockeEzMapMark.setText ("View");
                    unLockeEzMapHandler.removeCallbacks (unLockeEzMapRunnable);
                }
            }
        });
    }

    private void saveBtn(String s) {
        final String data = s;
        unLockeEzMapSaveBtn.setVisibility (View.VISIBLE);
        unLockeEzMapSaveBtn.setOnClickListener (v -> new AlertDialog.Builder (requireContext ())
                .setMessage ("Are you sure to save this heat map?")
                .setPositiveButton ("Yes", (dialog, which) -> {
                    // Save data to DB
                    unLockeEzMapSQLLiteHelper.addData (data);
                })
                .setNegativeButton ("No", (dialog, which) -> dialog.dismiss ())
                .create ().show ());
    }
    // Enable the Dropdown List
    private void spinner() {
        List<String> types = new ArrayList<> ();
        types.add ("Normal");
        types.add ("Hybrid");
        types.add ("Satellite");
        types.add ("Terrain");
        // Style and populate the spinner
        ArrayAdapter<String> unLockeEzMapDataAdapter;
        unLockeEzMapDataAdapter = new ArrayAdapter<> (requireActivity (), android.R.layout.simple_spinner_item, types);
        // Dropdown layout style
        unLockeEzMapDataAdapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item);

        // Attaching data adapter to spinner
        unLockeEzMapSpinnerWifi.setAdapter (unLockeEzMapDataAdapter);
        unLockeEzMapSpinnerWifi.setOnItemSelectedListener (new AdapterView.OnItemSelectedListener () {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition (position).equals ("Normal")) {
                    unLockeEzMapGoogleMap.setMapType (GoogleMap.MAP_TYPE_NORMAL);
                } else if (parent.getItemAtPosition (position).equals ("Hybrid")) {
                    unLockeEzMapGoogleMap.setMapType (GoogleMap.MAP_TYPE_HYBRID);
                } else if (parent.getItemAtPosition (position).equals ("Satellite")) {
                    unLockeEzMapGoogleMap.setMapType (GoogleMap.MAP_TYPE_SATELLITE);
                } else if (parent.getItemAtPosition (position).equals ("Terrain")) {
                    unLockeEzMapGoogleMap.setMapType (GoogleMap.MAP_TYPE_TERRAIN);
                }
                // On selecting a spinner item
                String item = parent.getItemAtPosition (position).toString ();
                // Show selected spinner item
                Toast.makeText (parent.getContext (), "Selected: " + item, Toast.LENGTH_SHORT).show ();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void getDeviceLocation() {
        unLockeEzMapLocationProvider = LocationServices.getFusedLocationProviderClient (requireActivity ());
        try {
            if (ContextCompat.checkSelfPermission (requireContext (), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Task location = unLockeEzMapLocationProvider.getLastLocation ();
                location.addOnCompleteListener (task -> {
                    if (task.isSuccessful ()) {
                        Log.d (unLockeEzMapTAG, "onComplete: found location!");
                        Location unLockeEzMapCurrentLocation = (Location) task.getResult ();
                        if (unLockeEzMapCurrentLocation != null) {
                            unLockeEzMapCurPos = new LatLng (unLockeEzMapCurrentLocation.getLatitude (), unLockeEzMapCurrentLocation.getLongitude ());
                            moveCamera (unLockeEzMapCurPos, unLockeEzMapDEFAULT_ZOOM);
                        }
                    } else {
                        Log.d (unLockeEzMapTAG, "onComplete: current location is null");
                        Toast.makeText (requireContext (), "unable to get current location", Toast.LENGTH_SHORT).show ();
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e (unLockeEzMapTAG, "getDeviceLocation: SecurityException:" + e.getMessage ());
        }
    }
    // Move camera
    private void moveCamera(LatLng latLng, float zoom) {
        Log.d (unLockeEzMapTAG, "moveCamera: moving the camera to : lat:" + latLng.latitude + ", lng:" + latLng.longitude);
        CameraPosition cameraPosition = new CameraPosition.Builder ()
                .target (latLng)
                .zoom (zoom)
                .build ();
        unLockeEzMapGoogleMap.animateCamera (CameraUpdateFactory.newCameraPosition (cameraPosition));
    }
    private void populateHeatmap() throws JSONException {
        Log.d ("MyDB", "populateHeatmap: Displaying data in Heatmap.");
        // Get data from db
        Cursor data = unLockeEzMapSQLLiteHelper.getData ();
        ArrayList<WeightedLatLng> unLockeEzMapList = new ArrayList<> ();
        while (data.moveToNext ()) {
            // Get Json String data
            Log.i ("getData", data.getString (1));
            String json = data.getString (1);
            JSONArray array = new JSONArray (json);
            for (int i = 0; i < array.length (); i++) {
                JSONObject unLockeEzMapJSONObject = array.getJSONObject (i);
                double lat = unLockeEzMapJSONObject.getDouble ("lat");
                double lng = unLockeEzMapJSONObject.getDouble ("lng");
                int strength = unLockeEzMapJSONObject.getInt ("strength");
                unLockeEzMapList.add (new WeightedLatLng (new LatLng (lat, lng), strength));
            }
        }
        if (unLockeEzMapList.isEmpty ()) return;
        // Draw heatmap
        unLockeEzMapHeatmapTileProvider = new HeatmapTileProvider.Builder ()
                .weightedData (unLockeEzMapList)
                .build ();
        unLockeEzMapTileOverlay = unLockeEzMapGoogleMap.addTileOverlay (new TileOverlayOptions ().tileProvider (unLockeEzMapHeatmapTileProvider));
    }
    // Generate heatmap data from pq
    private List<WeightedLatLng> getHeatmap(PriorityQueue<UnLockeEzSignalInFon> pq) {
        List<WeightedLatLng> list = new ArrayList<> ();
        for (UnLockeEzSignalInFon info : pq) {
            double lat = info.unLockeEzWifiLatitude;
            double lng = info.unLockeEzWifiLongitude;
            list.add (new WeightedLatLng (new LatLng (lat, lng), info.unLockeEzStrLength));
            Log.i ("Location ", "( " + lat + " , " + lng + " )");
        }
        Log.i ("size:", "" + pq.size ());
        return list;
    }
    // Draw heatmap
    private void drawHeatmap(List<WeightedLatLng> data, Gradient gradient, int radius, double opacity) {
        // Create the tile provider
        unLockeEzMapHeatmapTileProvider = new HeatmapTileProvider.Builder ()
                .weightedData (data)
                .gradient (gradient)
                .radius (radius)
                .opacity (opacity)
                .build ();
        // Add the tile overlay to the map
        unLockeEzMapTileOverlay = unLockeEzMapGoogleMap.addTileOverlay (new TileOverlayOptions ().tileProvider (unLockeEzMapHeatmapTileProvider));
    }
    private int[] setColors(int r1, int g1, int b1, int r2, int g2, int b2) {
        return new int[]{
                Color.rgb (r1, g1, b1), //Outer Color
                Color.rgb (r2, g2, b2)  //Inner Color
        };
    }
    // Set Heatmap StartPoints
    private float[] setStartPoints(float a, float b) {
        return new float[]{a, b};
    }
    // Set Heatmap Gradients
    private Gradient setGradient(int[] colors, float[] startPoints) {
        return new Gradient (colors, startPoints);
    }
    // Reconstruction when refreshing the top 5 wifi pqs
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void get5WifiPQ() {
        PriorityQueue<UnLockeEzSignalInFon> unLockeEzMapTopFiveWifiList = new PriorityQueue<> ((o1, o2) -> o2.unLockeEzStrLength - o1.unLockeEzStrLength);
        Set<String> unLockeEzMapSet = new HashSet<> ();
        HashMap<String, UnLockeEzSignAln> unLockeEzMapUpdatedWifiList = new HashMap<> ();
        for (UnLockeEzSignAln UnLockeEzMapSignAln : unLockeEzMapWifiSignals.values ()) {
            if (unLockeEzMapNewWifiSignals.containsKey (UnLockeEzMapSignAln.getUnlockWifiid ())) {
                unLockeEzMapUpdatedWifiList.put (UnLockeEzMapSignAln.getUnlockWifiname (), UnLockeEzMapSignAln);
            }
        }
        for (UnLockeEzSignAln UnLockeEzMapSignAln : unLockeEzMapNewWifiSignals.values ()) {
            if (unLockeEzMapUpdatedWifiList.containsKey (UnLockeEzMapSignAln.getUnlockWifiname ())) {
                UnLockeEzSignAln unLockeEzMapCur = unLockeEzMapUpdatedWifiList.get (UnLockeEzMapSignAln.getUnlockWifiname ());
                unLockeEzMapCur.update (UnLockeEzMapSignAln.getUnlockWifilevel ());
            } else {
                unLockeEzMapUpdatedWifiList.put (UnLockeEzMapSignAln.getUnlockWifiname (), UnLockeEzMapSignAln);
            }
            if (!unLockeEzMapSet.contains (UnLockeEzMapSignAln.getUnlockWifiname ())) {
                unLockeEzMapTopFiveWifiList.add (new UnLockeEzSignalInFon(UnLockeEzMapSignAln.getUnlockWifiid (), UnLockeEzMapSignAln.getUnlockWifiname (), UnLockeEzMapSignAln.getUnlockWifistrength (),
                        unLockeEzMapLatitude, unLockeEzMapLongitude));
                unLockeEzMapSet.add (UnLockeEzMapSignAln.getUnlockWifiname ());
            }
            while (unLockeEzMapTopFiveWifiList.size () > 5) {
                unLockeEzMapTopFiveWifiList.remove ();
            }
        }
        initPQ (unLockeEzMapTopFiveWifiList);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initPQ(PriorityQueue<UnLockeEzSignalInFon> pqInfo) {
        while (!pqInfo.isEmpty ()) {
            PriorityQueue<UnLockeEzSignalInFon> unLockeEzMapPriorityQueue = new PriorityQueue<> ((o1, o2) -> o2.unLockeEzStrLength - o1.unLockeEzStrLength);
            unLockeEzMapPriorityQueue.add (pqInfo.poll ());
            unLockeEzMapTopFiveList.add (unLockeEzMapPriorityQueue);
        }
    }
    // Update top 5 Wifi
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getBestKWifi() {
        PriorityQueue<UnLockeEzSignAln> UnLockeEzPriorityQueueSignAln = new PriorityQueue<> ((o1, o2) -> o2.getUnlockWifistrength () - o1.getUnlockWifistrength ());
        HashSet<String> set = new HashSet<> ();
        for (UnLockeEzSignAln unLockeEzMapSignAln : unLockeEzMapNewWifiSignals.values ()) {
            Log.i (unLockeEzMapSignAln.getUnlockWifiname (), "new wifi: ");
            for (PriorityQueue<UnLockeEzSignalInFon> UnLockeEzPriorityQueueSi : unLockeEzMapTopFiveList)
                if (UnLockeEzPriorityQueueSi.peek ().unLockeEzWifiName.equals (unLockeEzMapSignAln.getUnlockWifiname ())) {
                    if (!set.contains (unLockeEzMapSignAln.getUnlockWifiname ())) {
                        set.add (unLockeEzMapSignAln.getUnlockWifiname ());
                        UnLockeEzPriorityQueueSignAln.add (unLockeEzMapSignAln);
                    }
                }
        }
        unLockeEzMapTopFiveList.sort (new PQComparator ());
    }
    // Comparator to the sorted list
    class PQComparator implements Comparator<PriorityQueue<UnLockeEzSignalInFon>> {
        public int compare(PriorityQueue<UnLockeEzSignalInFon> p1, PriorityQueue<UnLockeEzSignalInFon> p2) {
            return findMedian (p2) - findMedian (p1);
        }
    }
    private Integer findMedian(PriorityQueue<UnLockeEzSignalInFon> pq) {
        List<UnLockeEzSignalInFon> UnLockeEzList = new ArrayList<> ();
        int half = pq.size () / 2;
        int res = 0;
        while (half > 0) {
            UnLockeEzSignalInFon curr = pq.poll ();
            if (curr != null) {
                UnLockeEzList.add (curr);
                res = curr.unLockeEzStrLength;
            }
            half--;
        }
        for (UnLockeEzSignalInFon s : UnLockeEzList) {
            pq.add (s);
        }
        return res;
    }
    // Get the strongest 100 signal sources
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getBestKLoc(Map<String, UnLockeEzSignAln> map) {
        String UnLockeEzWifiId;
        String UnLockeEzWifiName;
        if (unLockeEzMapTopFiveList.size () < 1) {
            get5WifiPQ ();
        } else {
            for (Map.Entry<String, UnLockeEzSignAln> entry : map.entrySet ()) {
                String name = entry.getValue ().getUnlockWifiname ();
                for (int i = 0; i < unLockeEzMapTopFiveList.size (); i++) {
                    PriorityQueue<UnLockeEzSignalInFon> UnLockeEzInfoPQ = new PriorityQueue<> ((o1, o2) -> o2.unLockeEzStrLength - o1.unLockeEzStrLength);
                    for (UnLockeEzSignalInFon s : unLockeEzMapTopFiveList.get (i)) {
                        UnLockeEzInfoPQ.add (s);
                    }
                    if (UnLockeEzInfoPQ.peek ().unLockeEzWifiName.equals (name)) {
                        int strength = entry.getValue ().getUnlockWifistrength ();
                        UnLockeEzWifiId = entry.getValue ().getUnlockWifiid ();
                        UnLockeEzWifiName = entry.getValue ().getUnlockWifiname ();
                        UnLockeEzInfoPQ.add (new UnLockeEzSignalInFon(UnLockeEzWifiId, UnLockeEzWifiName, strength, unLockeEzMapLatitude, unLockeEzMapLongitude));
                        unLockeEzMapTopFiveList.set (i, UnLockeEzInfoPQ);
                    }
                }
            }
        }
    }
    private String makeJSON(PriorityQueue<UnLockeEzSignalInFon> pq) throws JSONException {
        JSONArray array = new JSONArray ();
        for (UnLockeEzSignalInFon element : pq) {
            JSONObject UnLockeEzJsonElement = new JSONObject ();
            UnLockeEzJsonElement.put ("name", element.unLockeEzWifiName);
            UnLockeEzJsonElement.put ("lat", element.unLockeEzWifiLatitude);
            UnLockeEzJsonElement.put ("lng", element.unLockeEzWifiLongitude);
            UnLockeEzJsonElement.put ("strength", element.unLockeEzStrLength);
            array.put (UnLockeEzJsonElement);
        }
        return array.toString ();
    }
    public void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale (requireActivity (), Manifest.permission.ACCESS_COARSE_LOCATION)) {
            new AlertDialog.Builder (requireContext ())
                    .setTitle ("Permission required")
                    .setMessage ("Location permission is needed to get signals in area")
                    .setPositiveButton ("Accept", (dialog, which) -> ActivityCompat.requestPermissions (requireActivity (), unLockeEzMapPERMISSIONS, unLockeEzMapPERMISSION_ALL))
                    .setNegativeButton ("Deny", (dialog, which) -> {
                        dialog.dismiss ();
                        Toast.makeText (requireActivity (), "Enable the location permission in settings if you want to use this app", Toast.LENGTH_SHORT).show ();
                    })
                    .create ().show ();
        } else {
            ActivityCompat.requestPermissions (requireActivity (), unLockeEzMapPERMISSIONS, unLockeEzMapPERMISSION_ALL);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult (requestCode, permissions, grantResults);
        if (requestCode == unLockeEzMapPERMISSION_ALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText (requireActivity (), "Permission granted", Toast.LENGTH_SHORT).show ();
                onMapReady (unLockeEzMapGoogleMap);
            } else {
                Toast.makeText (requireActivity (), "Permission denied", Toast.LENGTH_SHORT).show ();
                requestLocationPermission ();
            }
        }
    }
    /**
     * Start: Initialize wifi search
     **/
    private HashMap<String, UnLockeEzSignAln> updateWifi() {
        HashMap<String, UnLockeEzSignAln> updatedWifi = new HashMap<> ();
        for (UnLockeEzSignAln unLockeEzMapSignAln : unLockeEzMapWifiSignals.values ()) {
            if (unLockeEzMapNewWifiSignals.containsKey (unLockeEzMapSignAln.getUnlockWifiid ())) {
                updatedWifi.put (unLockeEzMapSignAln.getUnlockWifiid (), unLockeEzMapSignAln);
            }
        }
        for (UnLockeEzSignAln unLockeEzMapSignAln : unLockeEzMapNewWifiSignals.values ()) {
            if (updatedWifi.containsKey (unLockeEzMapSignAln.getUnlockWifiid ())) {
                UnLockeEzSignAln cur = updatedWifi.get (unLockeEzMapSignAln.getUnlockWifiid ());
                cur.update (unLockeEzMapSignAln.getUnlockWifilevel ());
            } else {
                updatedWifi.put (unLockeEzMapSignAln.getUnlockWifiid (), unLockeEzMapSignAln);
            }
        }
        return updatedWifi;
    }
    private void wifiReceived() {
        HashMap<String, UnLockeEzSignAln> tempWifiSignals = new HashMap<> ();
        @SuppressLint("MissingPermission") List<ScanResult> wifiScanResults = unLockeEzMapWifiManager.getScanResults ();
        for (ScanResult wifi : wifiScanResults) {
            UnLockeEzSignAln unLockeEzMapWifiSignAln = new UnLockeEzSignAln(wifi);
            tempWifiSignals.put (wifi.BSSID, unLockeEzMapWifiSignAln);
        }
        unLockeEzMapNewWifiSignals = tempWifiSignals;
        unLockeEzMapWifiManager.startScan ();
    }
    public class SignalWifiUnlockerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction ();
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals (action)) {
                wifiReceived ();
            }
        }
    }
    /* Set up broadcast receiver to identify signals */
    private void setSignalWifiUnlockerReceiver() {
        unLockeEzMapSignalWifiUnlockerReceiver = new SignalWifiUnlockerReceiver ();
        IntentFilter signalIntent = new IntentFilter ();
        signalIntent.addAction (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        requireContext ().registerReceiver (unLockeEzMapSignalWifiUnlockerReceiver, signalIntent);
    }
    /* Start searching for signals in area signals */
    @SuppressLint("WifiManagerLeak")
    private void startSignalSearchWifiUnlocker() {
        // for wifi
        unLockeEzMapWifiManager = (WifiManager) requireContext ().getSystemService (WIFI_SERVICE);
        if (unLockeEzMapWifiManager.isWifiEnabled ()) {
            unLockeEzMapWifiManager.startScan ();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra ("result");
                if (result.equals ("1")) {
                    if (unLockeEzMapTileOverlay != null) unLockeEzMapTileOverlay.remove ();
                    int[] colors = setColors (102, 225, 0, 255, 0, 0);
                    float[] startPoints = setStartPoints (0.2f, 1f);
                    Gradient gradient = setGradient (colors, startPoints);
                    drawHeatmap (getHeatmap (unLockeEzMapTopFiveList.get (0)), gradient, 15, 0.7);
                    // Convert pq to JSON String
                    try {
                        String json = makeJSON (unLockeEzMapTopFiveList.get (0));
                        saveBtn (json);
                    } catch (JSONException e) {
                        Log.i ("Error", "Json convert failed.");
                    }
                }
                if (result.equals ("2")) {
                    if (unLockeEzMapTileOverlay != null) unLockeEzMapTileOverlay.remove ();
                    int[] colors = setColors (0, 0, 255, 255, 255, 0);
                    float[] startPoints = setStartPoints (0.2f, 1f);
                    Gradient gradient = setGradient (colors, startPoints);
                    drawHeatmap (getHeatmap (unLockeEzMapTopFiveList.get (1)), gradient, 15, 0.7);
                    // Convert pq to JSON String
                    try {
                        String json = makeJSON (unLockeEzMapTopFiveList.get (1));
                        saveBtn (json);
                    } catch (JSONException e) {
                        Log.i ("Error", "Json convert failed.");
                    }
                }
                if (result.equals ("3")) {
                    if (unLockeEzMapTileOverlay != null) unLockeEzMapTileOverlay.remove ();
                    int[] colors = setColors (128, 128, 128, 255, 0, 0);
                    float[] startPoints = setStartPoints (0.2f, 1f);
                    Gradient gradient = setGradient (colors, startPoints);
                    drawHeatmap (getHeatmap (unLockeEzMapTopFiveList.get (2)), gradient, 15, 0.7);
                    // Convert pq to JSON String
                    try {
                        String UnLockeEzJsonString = makeJSON (unLockeEzMapTopFiveList.get (2));
                        saveBtn (UnLockeEzJsonString);
                    } catch (JSONException ignored) {
                    }
                }
            }
        }
    }
}