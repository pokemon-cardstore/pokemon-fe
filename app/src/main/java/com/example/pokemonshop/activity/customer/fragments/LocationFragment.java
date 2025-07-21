package com.example.pokemonshop.activity.customer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pokemonshop.R;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

public class LocationFragment extends Fragment {
    private MapView mapView;
    private Marker userMarker;
    private Marker shopMarker;
    private Polyline routePolyline;
    
    // Vị trí cửa hàng cố định
    private static final double SHOP_LAT = 10.8411329;
    private static final double SHOP_LON = 106.8073081;
    
    // Vị trí giả định của customer (có thể thay đổi)
    private static final double CUSTOMER_LAT = 10.8400;
    private static final double CUSTOMER_LON = 106.8060;

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

        // Vị trí cửa hàng
        GeoPoint shopPoint = new GeoPoint(SHOP_LAT, SHOP_LON);
        mapView.getController().setZoom(17.0);
        mapView.getController().setCenter(shopPoint);

        // Marker cửa hàng
        shopMarker = new Marker(mapView);
        shopMarker.setPosition(shopPoint);
        shopMarker.setTitle("Pokemon Shop");
        mapView.getOverlays().add(shopMarker);

        // Marker vị trí customer (giả định)
        userMarker = new Marker(mapView);
        userMarker.setPosition(new GeoPoint(CUSTOMER_LAT, CUSTOMER_LON));
        userMarker.setTitle("Vị trí của bạn");
        userMarker.setIcon(getResources().getDrawable(android.R.drawable.ic_menu_mylocation));
        mapView.getOverlays().add(userMarker);

        // Button vẽ đường - khai báo local để tránh lỗi
        view.findViewById(R.id.btnShowRoute).setOnClickListener(v -> {
            drawRouteToShop();
        });

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

    private void drawRouteToShop() {
        // Xóa đường cũ nếu có
        if (routePolyline != null) {
            mapView.getOverlays().remove(routePolyline);
        }

        // Tạo đường thẳng từ vị trí customer đến cửa hàng
        List<GeoPoint> routePoints = new ArrayList<>();
        routePoints.add(userMarker.getPosition());
        routePoints.add(shopMarker.getPosition());

        routePolyline = new Polyline();
        routePolyline.setPoints(routePoints);
        routePolyline.setColor(android.graphics.Color.BLUE);
        routePolyline.setWidth(5.0f);
        routePolyline.setTitle("Đường đi đến cửa hàng");

        mapView.getOverlays().add(routePolyline);
        mapView.invalidate();

        Toast.makeText(getContext(), "Đã vẽ đường đến cửa hàng", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mapView != null) {
            mapView.onDetach();
        }
    }
}
