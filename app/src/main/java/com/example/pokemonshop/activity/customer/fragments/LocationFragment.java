package com.example.pokemonshop.activity.customer.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.pokemonshop.R;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class LocationFragment extends Fragment {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private MapView mapView;
    private android.location.LocationManager locationManager;
    private Marker userMarker;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location, container, false);

        Context ctx = getActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(ctx.getPackageName());

        mapView = view.findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        // Vị trí cố định: 10.8411329, 106.8073081
        double lat = 10.8411329;
        double lon = 106.8073081;
        GeoPoint fixedPoint = new GeoPoint(lat, lon);
        mapView.getController().setZoom(17.0);
        mapView.getController().setCenter(fixedPoint);

        Marker marker = new Marker(mapView);
        marker.setPosition(fixedPoint);
        marker.setTitle("Pokemon Shop");
        mapView.getOverlays().add(marker);

        Spinner mapTypeSpinner = view.findViewById(R.id.mapTypeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.map_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mapTypeSpinner.setAdapter(adapter);

        mapTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mapView != null) {
                    switch (position) {
                        case 0:
                            mapView.setTileSource(TileSourceFactory.MAPNIK);
                            break;
                        case 1:
                            mapView.setTileSource(TileSourceFactory.USGS_SAT);
                            break;
                        case 2:
                            mapView.setTileSource(TileSourceFactory.MAPNIK);
                            break;
                        case 3:
                            mapView.setTileSource(TileSourceFactory.USGS_TOPO);
                            break;
                        default:
                            mapView.setTileSource(TileSourceFactory.MAPNIK);
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        return view;
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), "Chưa có quyền truy cập vị trí", Toast.LENGTH_SHORT).show();
            return;
        }
        locationManager.requestLocationUpdates(android.location.LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
        locationManager.requestLocationUpdates(android.location.LocationManager.NETWORK_PROVIDER, 1000, 1, locationListener);
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            showLocationOnMap(location);
        }
        @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
        @Override public void onProviderEnabled(String provider) {}
        @Override public void onProviderDisabled(String provider) {}
    };

    private void showLocationOnMap(Location location) {
        GeoPoint userPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        mapView.getController().setZoom(17.0);
        mapView.getController().setCenter(userPoint);
        if (userMarker == null) {
            userMarker = new Marker(mapView);
            userMarker.setTitle("Vị trí của bạn");
            mapView.getOverlays().add(userMarker);
        }
        userMarker.setPosition(userPoint);
        mapView.invalidate();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(getContext(), "Bạn cần cấp quyền vị trí để xem bản đồ!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }
}
