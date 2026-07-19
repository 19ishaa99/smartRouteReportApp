package com.example.smartroute.fragments;

import android.Manifest;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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

    private Double selectedLatitude;
    private Double selectedLongitude;

    private final ActivityResultLauncher<String[]> permissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestMultiplePermissions(),
                    permissions -> {

                        boolean fineGranted =
                                Boolean.TRUE.equals(
                                        permissions.get(
                                                Manifest.permission.ACCESS_FINE_LOCATION
                                        )
                                );

                        boolean coarseGranted =
                                Boolean.TRUE.equals(
                                        permissions.get(
                                                Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                );

                        if (fineGranted || coarseGranted) {

                            enableMyLocation();
                            findCurrentLocation();

                        } else {

                            if (txtLocationStatus != null) {
                                txtLocationStatus.setText(
                                        "Location permission denied. " +
                                                "You can still select a location by tapping the map."
                                );
                            }

                            Toast.makeText(
                                    requireContext(),
                                    "Please allow location permission",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
            );

    public LocationFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        View view = inflater.inflate(
                R.layout.fragment_location,
                container,
                false
        );

        txtLocationStatus =
                view.findViewById(R.id.txtLocationStatus);

        txtLatitude =
                view.findViewById(R.id.txtLatitude);

        txtLongitude =
                view.findViewById(R.id.txtLongitude);

        etManualLocation =
                view.findViewById(R.id.etManualLocation);

        btnGetLocation =
                view.findViewById(R.id.btnGetLocation);

        btnSaveLocation =
                view.findViewById(R.id.btnSaveLocation);

        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(
                        requireActivity()
                );

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager()
                        .findFragmentById(R.id.mapContainer);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        btnGetLocation.setOnClickListener(v ->
                checkLocationPermission()
        );

        btnSaveLocation.setOnClickListener(v ->
                saveLocation()
        );

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {

        googleMap = map;

        LatLng zanzibar =
                new LatLng(-6.1659, 39.2026);

        googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                        zanzibar,
                        12f
                )
        );

        selectedMarker = googleMap.addMarker(
                new MarkerOptions()
                        .position(zanzibar)
                        .title("Selected issue location")
        );

        updateSelectedLocation(zanzibar);

        googleMap.setOnMapClickListener(latLng -> {

            if (selectedMarker == null) {

                selectedMarker = googleMap.addMarker(
                        new MarkerOptions()
                                .position(latLng)
                                .title("Selected issue location")
                );

            } else {

                selectedMarker.setPosition(latLng);
                selectedMarker.setTitle(
                        "Selected issue location"
                );
            }

            googleMap.animateCamera(
                    CameraUpdateFactory.newLatLng(latLng)
            );

            updateSelectedLocation(latLng);
        });

        googleMap.getUiSettings()
                .setZoomControlsEnabled(true);

        googleMap.getUiSettings()
                .setCompassEnabled(true);

        if (hasLocationPermission()) {
            enableMyLocation();
        }
    }

    private void updateSelectedLocation(LatLng latLng) {

        selectedLatitude = latLng.latitude;
        selectedLongitude = latLng.longitude;

        String latitude = String.format(
                Locale.getDefault(),
                "%.6f",
                selectedLatitude
        );

        String longitude = String.format(
                Locale.getDefault(),
                "%.6f",
                selectedLongitude
        );

        txtLatitude.setText(latitude);
        txtLongitude.setText(longitude);

        txtLocationStatus.setText(
                "Location selected from the map."
        );
    }

    private void checkLocationPermission() {

        if (hasLocationPermission()) {

            enableMyLocation();
            findCurrentLocation();

        } else {

            permissionLauncher.launch(
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    }
            );
        }
    }

    private boolean hasLocationPermission() {

        boolean fineGranted =
                ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED;

        boolean coarseGranted =
                ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
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

            Toast.makeText(
                    requireContext(),
                    "Location permission is required",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void findCurrentLocation() {

        if (!hasLocationPermission()) {
            return;
        }

        txtLocationStatus.setText(
                "Detecting your current location..."
        );

        try {

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {

                        if (!isAdded()) {
                            return;
                        }

                        if (location != null) {

                            moveMapToLocation(location);

                        } else {

                            txtLocationStatus.setText(
                                    "Current location is unavailable. " +
                                            "Turn on location and try again."
                            );

                            Toast.makeText(
                                    requireContext(),
                                    "No recent location was found",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    })
                    .addOnFailureListener(error -> {

                        if (!isAdded()) {
                            return;
                        }

                        txtLocationStatus.setText(
                                "Failed to retrieve current location."
                        );

                        Toast.makeText(
                                requireContext(),
                                "Location request failed",
                                Toast.LENGTH_SHORT
                        ).show();
                    });

        } catch (SecurityException exception) {

            Toast.makeText(
                    requireContext(),
                    "Location permission is required",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void moveMapToLocation(Location location) {

        if (googleMap == null) {
            return;
        }

        LatLng currentLocation =
                new LatLng(
                        location.getLatitude(),
                        location.getLongitude()
                );

        if (selectedMarker == null) {

            selectedMarker = googleMap.addMarker(
                    new MarkerOptions()
                            .position(currentLocation)
                            .title("Current location")
            );

        } else {

            selectedMarker.setPosition(currentLocation);
            selectedMarker.setTitle("Current location");
        }

        googleMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                        currentLocation,
                        17f
                )
        );

        updateSelectedLocation(currentLocation);

        txtLocationStatus.setText(
                "Current location detected successfully."
        );
    }

    private void saveLocation() {

        String description =
                etManualLocation.getText()
                        .toString()
                        .trim();

        if (selectedLatitude == null ||
                selectedLongitude == null) {

            Toast.makeText(
                    requireContext(),
                    "Select a location on the map",
                    Toast.LENGTH_SHORT
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

        String savedLocation =
                description +
                        "\nLatitude: " + selectedLatitude +
                        "\nLongitude: " + selectedLongitude;

        Toast.makeText(
                requireContext(),
                "Location saved:\n" + savedLocation,
                Toast.LENGTH_LONG
        ).show();
    }
}