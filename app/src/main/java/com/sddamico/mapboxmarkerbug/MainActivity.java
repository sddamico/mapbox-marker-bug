package com.sddamico.mapboxmarkerbug;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

public class MainActivity extends AppCompatActivity {

    public static final double THRESHOLD = 5.0;

    private MapView mapView;
    private MapboxMap mapboxMap;
    private double lastZoom;
    private boolean isShowingHighThresholdMarker;
    private boolean isShowingLowThresholdMarker;

    private MarkerOptions lowThresholdMarker;
    private MarkerOptions  highThresholdMarker;
    private Marker activeMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MapboxAccountManager.start(this, "pk.");

        final Icon icon = IconFactory.getInstance(this).fromResource(R.drawable.default_marker);

        lowThresholdMarker = new MarkerOptions()
                .icon(icon)
                .position(new LatLng(-0.1, 0));

        highThresholdMarker = new MarkerOptions()
                .icon(icon)
                .position(new LatLng(0.1, 0));

        setContentView(R.layout.activity_main);

        mapView = (MapView) findViewById(R.id.map_view);

        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                MainActivity.this.mapboxMap = mapboxMap;

                updateZoom(mapboxMap.getCameraPosition().zoom);

                mapboxMap.moveCamera(CameraUpdateFactory.zoomTo(4.9f));

                mapboxMap.setOnCameraChangeListener(new MapboxMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition position) {
                        final double zoom = position.zoom;

                        updateZoom(zoom);
                    }
                });
            }
        });
    }

    private void updateZoom(double zoom) {
        if (lastZoom == zoom) {
            return;
        }

        lastZoom = zoom;

        if (zoom > THRESHOLD) {
            showHighThresholdMarker();
        } else {
            showLowThresholdMarker();
        }
    }

    private void showLowThresholdMarker() {
        if (isShowingLowThresholdMarker) {
            return;
        }

        Log.d("MarkerTest", "showLowThresholdMarker()");

        isShowingLowThresholdMarker = true;
        isShowingHighThresholdMarker = false;

        if (activeMarker != null) {
            mapboxMap.removeMarker(activeMarker);
        }

        activeMarker = mapboxMap.addMarker(lowThresholdMarker);
    }

    private void showHighThresholdMarker() {
        if (isShowingHighThresholdMarker) {
            return;
        }

        Log.d("MarkerTest", "showHighThresholdMarker()");

        isShowingLowThresholdMarker = false;
        isShowingHighThresholdMarker = true;

        if (activeMarker != null) {
            mapboxMap.removeMarker(activeMarker);
        }

        activeMarker = mapboxMap.addMarker(highThresholdMarker);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
