package com.example.grupo_1_mapa_tarea;

import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.GnssStatus;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.library.BuildConfig;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class MainActivity extends AppCompatActivity implements MapListener {
  private MapView mapView;
  private IMapController controller;
  private MyLocationNewOverlay myLocationNewOverlay;
  private static final int PERMISSION_REQUEST_CODE = 1;

  private GnssStatus.Callback gnssStatusCallback = new GnssStatus.Callback() {
    @Override
    public void onStarted() {
      super.onStarted();
    }

    @Override
    public void onStopped() {
      super.onStopped();
    }

    @Override
    public void onFirstFix(int ttffMillis) {
      super.onFirstFix(ttffMillis);
    }

    @Override
    public void onSatelliteStatusChanged(@NonNull GnssStatus status) {
      super.onSatelliteStatusChanged(status);
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_main);
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
      Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
      return insets;
    });
    Configuration.getInstance().load(getApplicationContext(), getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE));

    Configuration.getInstance().setUserAgentValue(BuildConfig.BUILD_TYPE);

    mapView = findViewById(R.id.mapView);
    mapView.setTileSource(TileSourceFactory.MAPNIK);
    mapView.setMultiTouchControls(true);
    mapView.getLocalVisibleRect(new Rect());

    myLocationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);
    controller = mapView.getController();

    myLocationNewOverlay.enableMyLocation();
    myLocationNewOverlay.enableFollowLocation();
    myLocationNewOverlay.setDrawAccuracyEnabled(true);
    myLocationNewOverlay.runOnFirstFix(() -> runOnUiThread(() -> {
      controller.setCenter(myLocationNewOverlay.getMyLocation());
      controller.animateTo(myLocationNewOverlay.getMyLocation());
    }));

    controller.setZoom(6.0);

    Log.e("TAG", "onCreate:in " + controller.zoomIn());
    Log.e("TAG", "onCreate: out  " + controller.zoomOut());

    mapView.getOverlays().add(myLocationNewOverlay);
    mapView.addMapListener(this);

    if (checkPermissions()) {
      registerGnssStatusCallback();
    } else {
      requestPermissions();
    }
  }

  private boolean checkPermissions() {
    int fineLocationPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
    int coarseLocationPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
    int backgroundLocationPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION);

    return fineLocationPermission == PackageManager.PERMISSION_GRANTED && coarseLocationPermission == PackageManager.PERMISSION_GRANTED && backgroundLocationPermission == PackageManager.PERMISSION_GRANTED;
  }

  private void requestPermissions() {
    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION}, PERMISSION_REQUEST_CODE);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == PERMISSION_REQUEST_CODE) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        registerGnssStatusCallback();
      } else {
        Log.d("Error permisos", "Permisos denegados");
      }
    }
  }

  private void registerGnssStatusCallback() {
    LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    if (locationManager != null) {
      if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        locationManager.registerGnssStatusCallback(gnssStatusCallback);
      }
    }
  }

  @Override
  public boolean onScroll(ScrollEvent event) {
    Log.d(":Latitud", "la " + event.getSource().getMapCenter().getLatitude());
    Log.d("Longitud", "lo " + event.getSource().getMapCenter().getLongitude());
    return true;
  }

  @Override
  public boolean onZoom(ZoomEvent event) {
    Log.d("TAG", "onZoom zoom level: " + event.getZoomLevel() + "   source:  " + event.getSource());
    return false;
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    if (locationManager != null) {
      locationManager.unregisterGnssStatusCallback(gnssStatusCallback);
    }
  }
}