package com.example.smartroute.fragments;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.smartroute.R;
import com.example.smartroute.data.entity.SavedLocation;
import com.example.smartroute.data.repository.SavedLocationRepository;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

public class LocationFragment extends Fragment
        implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private Marker selectedMarker;

    private TextView txtLocationStatus;
    private TextView txtLatitude;
    private TextView txtLongitude;

    private EditText etManualLocation;

    private Button btnGetLocation;
    private Button btnSaveLocation;

    private FusedLocationProviderClient fusedLocationClient;
    private SavedLocationRepository locationRepository;

    /*
     * These remain null until the user taps the map
     * or selects their current location.
     */
    private Double selectedLatitude;
    private Double selectedLongitude;

    private final ActivityResultLauncher<String[]>
            permissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestMultiplePermissions(),
                    permissions -> {

                        boolean fineGranted =
                                Boolean.TRUE.equals(
                                        permissions.get(
                                                Manifest.permission
                                                        .ACCESS_FINE_LOCATION
                                        )
                                );

                        boolean coarseGranted =
                                Boolean.TRUE.equals(
                                        permissions.get(
                                                Manifest.permission
                                                        .ACCESS_COARSE_LOCATION
                                        )
                                );

                        if (fineGranted || coarseGranted) {

                            enableMyLocation();
                            findCurrentLocation();

                        } else {

                            if (txtLocationStatus != null) {

                                txtLocationStatus.setText(
                                        "Location permission was denied. " +
                                                "You can still select a location " +
                                                "by tapping the map."
                                );
                            }

                            if (isAdded()) {

                                Toast.makeText(
                                        requireContext(),
                                        "Location permission was not granted",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                    }
            );

    public LocationFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        return inflater.inflate(
                R.layout.fragment_location,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {

        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);

        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(
                        requireActivity()
                );

        locationRepository =
                new SavedLocationRepository(
                        requireContext().getApplicationContext()
                );

        SupportMapFragment mapFragment =
                (SupportMapFragment)
                        getChildFragmentManager()
                                .findFragmentById(
                                        R.id.mapContainer
                                );

        if (mapFragment != null) {

            mapFragment.getMapAsync(this);

        } else {

            Toast.makeText(
                    requireContext(),
                    "Unable to load the map",
                    Toast.LENGTH_LONG
            ).show();
        }

        btnGetLocation.setOnClickListener(
                v -> checkLocationPermission()
        );

        btnSaveLocation.setOnClickListener(
                v -> saveLocation()
        );
    }

    private void initializeViews(
            @NonNull View view
    ) {

        txtLocationStatus =
                view.findViewById(
                        R.id.txtLocationStatus
                );

        txtLatitude =
                view.findViewById(
                        R.id.txtLatitude
                );

        txtLongitude =
                view.findViewById(
                        R.id.txtLongitude
                );

        etManualLocation =
                view.findViewById(
                        R.id.etManualLocation
                );

        btnGetLocation =
                view.findViewById(
                        R.id.btnGetLocation
                );

        btnSaveLocation =
                view.findViewById(
                        R.id.btnSaveLocation
                );
    }

    @Override
    public void onMapReady(
            @NonNull GoogleMap map
    ) {

        googleMap = map;

        LatLng zanzibar =
                new LatLng(
                        -6.1659,
                        39.2026
                );

        /*
         * Only move the camera to Zanzibar.
         * Do not automatically select Zanzibar as the issue location.
         */
        googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                        zanzibar,
                        12f
                )
        );

        googleMap.setOnMapClickListener(
                latLng -> selectMapLocation(
                        latLng,
                        "Selected issue location"
                )
        );

        googleMap.getUiSettings()
                .setZoomControlsEnabled(true);

        googleMap.getUiSettings()
                .setCompassEnabled(true);

        googleMap.getUiSettings()
                .setMapToolbarEnabled(false);

        if (hasLocationPermission()) {
            enableMyLocation();
        }
    }

    /**
     * Places or moves the marker and records its coordinates.
     */
    private void selectMapLocation(
            @NonNull LatLng latLng,
            @NonNull String markerTitle
    ) {

        if (googleMap == null) {
            return;
        }

        if (selectedMarker == null) {

            selectedMarker =
                    googleMap.addMarker(
                            new MarkerOptions()
                                    .position(latLng)
                                    .title(markerTitle)
                    );

        } else {

            selectedMarker.setPosition(latLng);
            selectedMarker.setTitle(markerTitle);
        }

        googleMap.animateCamera(
                CameraUpdateFactory.newLatLng(
                        latLng
                )
        );

        updateSelectedLocation(latLng);
    }

    private void updateSelectedLocation(
            @NonNull LatLng latLng
    ) {

        selectedLatitude =
                latLng.latitude;

        selectedLongitude =
                latLng.longitude;

        String latitudeText =
                String.format(
                        Locale.getDefault(),
                        "%.6f",
                        selectedLatitude
                );

        String longitudeText =
                String.format(
                        Locale.getDefault(),
                        "%.6f",
                        selectedLongitude
                );

        if (txtLatitude != null) {
            txtLatitude.setText(latitudeText);
        }

        if (txtLongitude != null) {
            txtLongitude.setText(longitudeText);
        }

        if (txtLocationStatus != null) {

            txtLocationStatus.setText(
                    "Location selected. Add a short description, then save it."
            );
        }
    }

    private void checkLocationPermission() {

        if (hasLocationPermission()) {

            enableMyLocation();
            findCurrentLocation();

        } else {

            permissionLauncher.launch(
                    new String[]{
                            Manifest.permission
                                    .ACCESS_FINE_LOCATION,

                            Manifest.permission
                                    .ACCESS_COARSE_LOCATION
                    }
            );
        }
    }

    private boolean hasLocationPermission() {

        if (!isAdded()) {
            return false;
        }

        boolean fineGranted =
                ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission
                                .ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED;

        boolean coarseGranted =
                ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission
                                .ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED;

        return fineGranted || coarseGranted;
    }

    private void enableMyLocation() {

        if (googleMap == null ||
                !hasLocationPermission()) {

            return;
        }

        try {

            googleMap.setMyLocationEnabled(true);

            googleMap.getUiSettings()
                    .setMyLocationButtonEnabled(true);

        } catch (SecurityException exception) {

            if (isAdded()) {

                Toast.makeText(
                        requireContext(),
                        "Location permission is required",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    private void findCurrentLocation() {

        if (!hasLocationPermission()) {
            return;
        }

        if (txtLocationStatus != null) {

            txtLocationStatus.setText(
                    "Detecting your current location..."
            );
        }

        btnGetLocation.setEnabled(false);
        btnGetLocation.setText("Locating...");

        try {

            fusedLocationClient
                    .getCurrentLocation(
                            Priority.PRIORITY_HIGH_ACCURACY,
                            null
                    )
                    .addOnSuccessListener(
                            location -> {

                                if (!isAdded()) {
                                    return;
                                }

                                resetCurrentLocationButton();

                                if (location != null) {

                                    moveMapToLocation(location);

                                } else {

                                    txtLocationStatus.setText(
                                            "Current location is unavailable. " +
                                                    "Turn on location services and try again."
                                    );

                                    Toast.makeText(
                                            requireContext(),
                                            "Current location was not found",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }
                    )
                    .addOnFailureListener(
                            error -> {

                                if (!isAdded()) {
                                    return;
                                }

                                resetCurrentLocationButton();

                                txtLocationStatus.setText(
                                        "Failed to retrieve current location."
                                );

                                String message =
                                        error.getMessage();

                                if (message == null ||
                                        message.trim().isEmpty()) {

                                    message =
                                            "Location request failed";
                                }

                                Toast.makeText(
                                        requireContext(),
                                        message,
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                    );

        } catch (SecurityException exception) {

            resetCurrentLocationButton();

            if (isAdded()) {

                Toast.makeText(
                        requireContext(),
                        "Location permission is required",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    private void resetCurrentLocationButton() {

        if (btnGetLocation == null) {
            return;
        }

        btnGetLocation.setEnabled(true);

        btnGetLocation.setText(
                "Use Current Location"
        );
    }

    private void moveMapToLocation(
            @NonNull Location location
    ) {

        if (googleMap == null) {
            return;
        }

        LatLng currentLocation =
                new LatLng(
                        location.getLatitude(),
                        location.getLongitude()
                );

        selectMapLocation(
                currentLocation,
                "Current location"
        );

        googleMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                        currentLocation,
                        17f
                )
        );

        txtLocationStatus.setText(
                "Current location detected. Add a short description, then save it."
        );
    }

    private void saveLocation() {

        etManualLocation.setError(null);

        String description =
                etManualLocation
                        .getText()
                        .toString()
                        .trim();

        if (selectedLatitude == null ||
                selectedLongitude == null) {

            Toast.makeText(
                    requireContext(),
                    "Tap the map or use your current location first",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        if (TextUtils.isEmpty(description)) {

            etManualLocation.setError(
                    "Add a short location description"
            );

            etManualLocation.requestFocus();

            return;
        }

        int userId =
                getLoggedInUserId();

        if (userId == -1) {

            Toast.makeText(
                    requireContext(),
                    "Login session not found. Please sign in again.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        SavedLocation savedLocation =
                new SavedLocation(
                        userId,
                        description,
                        selectedLatitude,
                        selectedLongitude,
                        System.currentTimeMillis()
                );

        insertLocation(savedLocation);
    }

    private void insertLocation(
            @NonNull SavedLocation savedLocation
    ) {

        setLoading(true);

        locationRepository.insertLocation(
                savedLocation,
                new SavedLocationRepository.InsertCallback() {

                    @Override
                    public void onSuccess(
                            long locationId
                    ) {

                        if (!isAdded()) {
                            return;
                        }

                        requireActivity()
                                .runOnUiThread(
                                        () -> {

                                            if (!isAdded()) {
                                                return;
                                            }

                                            setLoading(false);

                                            saveSelectedLocationToSession(
                                                    locationId,
                                                    savedLocation
                                            );

                                            /*
                                             * Send the selected map location
                                             * back to ReportFragment.
                                             */
                                            returnLocationToReport(
                                                    savedLocation
                                            );

                                            Toast.makeText(
                                                    requireContext(),
                                                    "Location selected successfully",
                                                    Toast.LENGTH_SHORT
                                            ).show();

                                            /*
                                             * Return to ReportFragment.
                                             */
                                            getParentFragmentManager()
                                                    .popBackStack();
                                        }
                                );
                    }

                    @Override
                    public void onError(
                            String message
                    ) {

                        if (!isAdded()) {
                            return;
                        }

                        requireActivity()
                                .runOnUiThread(
                                        () -> {

                                            if (!isAdded()) {
                                                return;
                                            }

                                            setLoading(false);

                                            String errorMessage =
                                                    message;

                                            if (errorMessage == null ||
                                                    errorMessage
                                                            .trim()
                                                            .isEmpty()) {

                                                errorMessage =
                                                        "Unable to save the location";
                                            }

                                            Toast.makeText(
                                                    requireContext(),
                                                    errorMessage,
                                                    Toast.LENGTH_LONG
                                            ).show();
                                        }
                                );
                    }
                }
        );
    }

    /**
     * Sends the selected location to ReportFragment.
     */
    private void returnLocationToReport(
            @NonNull SavedLocation location
    ) {

        Bundle result =
                new Bundle();

        result.putString(
                ReportFragment.KEY_SELECTED_ADDRESS,
                location.getDescription()
        );

        result.putDouble(
                ReportFragment.KEY_SELECTED_LATITUDE,
                location.getLatitude()
        );

        result.putDouble(
                ReportFragment.KEY_SELECTED_LONGITUDE,
                location.getLongitude()
        );

        getParentFragmentManager()
                .setFragmentResult(
                        ReportFragment.LOCATION_REQUEST_KEY,
                        result
                );
    }

    private int getLoggedInUserId() {

        SharedPreferences preferences =
                requireContext()
                        .getSharedPreferences(
                                "smart_route_session",
                                Context.MODE_PRIVATE
                        );

        return preferences.getInt(
                "user_id",
                -1
        );
    }

    private void saveSelectedLocationToSession(
            long locationId,
            @NonNull SavedLocation location
    ) {

        requireContext()
                .getSharedPreferences(
                        "smart_route_session",
                        Context.MODE_PRIVATE
                )
                .edit()
                .putLong(
                        "selected_location_id",
                        locationId
                )
                .putString(
                        "selected_location_description",
                        location.getDescription()
                )
                .putString(
                        "selected_latitude",
                        String.valueOf(
                                location.getLatitude()
                        )
                )
                .putString(
                        "selected_longitude",
                        String.valueOf(
                                location.getLongitude()
                        )
                )
                .apply();
    }

    private void setLoading(
            boolean loading
    ) {

        if (btnSaveLocation != null) {

            btnSaveLocation.setEnabled(
                    !loading
            );

            if (loading) {

                btnSaveLocation.setText(
                        "Saving location..."
                );

            } else {

                btnSaveLocation.setText(
                        "Save Location"
                );
            }
        }

        if (btnGetLocation != null) {

            btnGetLocation.setEnabled(
                    !loading
            );
        }

        if (etManualLocation != null) {

            etManualLocation.setEnabled(
                    !loading
            );
        }
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();

        googleMap = null;
        selectedMarker = null;

        txtLocationStatus = null;
        txtLatitude = null;
        txtLongitude = null;

        etManualLocation = null;

        btnGetLocation = null;
        btnSaveLocation = null;
    }
}