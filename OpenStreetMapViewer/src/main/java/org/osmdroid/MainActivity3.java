package org.osmdroid;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.shape.ShapeConverter;
import org.osmdroid.tileprovider.modules.ArchiveFileFactory;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.util.Transform;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.PolyOverlayWithIW;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.gridlines.LatLonGridlineOverlay2;
import org.osmdroid.views.overlay.mylocation.DirectedLocationOverlay;
import org.osmdroid.wms.WMSTileSource;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity3 extends AppCompatActivity implements LocationListener, MapEventsReceiver {
    private boolean hasFix = false;
    private DirectedLocationOverlay overlay;
    MapView mMapView;
    private boolean isAdd = true;
    private Polyline polyline;

    @Override
    public void onResume() {
        super.onResume();
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        try {
            //on API15 AVDs,network provider fails. no idea why
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        } catch (Exception ex) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDetach();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mMapView = findViewById(R.id.mapview);
        mMapView.setDrawingCacheEnabled(true);
        mMapView.setMultiTouchControls(true);// 触控手势放大缩小
        mMapView.setMaxZoomLevel(20.0);
        mMapView.setMinZoomLevel(1.0);
        this.mMapView.setHorizontalMapRepetitionEnabled(false);
        this.mMapView.setVerticalMapRepetitionEnabled(false);
        polyline = new Polyline(mMapView);
        LatLonGridlineOverlay2 grids = new LatLonGridlineOverlay2();
        mMapView.getOverlayManager().add(grids);
        OnlineTileSourceBase TDTWSource = new OnlineTileSourceBase("Tian Di Tu Wmts", 1, 18, 256, "",
                new String[]{"http://t0.tianditu.gov.cn/img_w/wmts"}) {
            @Override
            public String getTileURLString(long pMapTileIndex) {
                String url = getBaseUrl() + "?SERVICE=WMTS" + "&REQUEST=GetTile" + "&VERSION=1.0.0" +
                        "&LAYER=img" + "&STYLE=default" + "&TILEMATRIXSET=w" + "&FORMAT=tiles" +
                        "&TILEMATRIX=" + MapTileIndex.getZoom(pMapTileIndex) +
                        "&TILEROW=" + MapTileIndex.getY(pMapTileIndex) +
                        "&TILECOL=" + MapTileIndex.getX(pMapTileIndex) +
                        "&tk=6d3110e391067a6345d705919e164790";
//                Log.d("xzw", "getTileURLString: " + url);
                return url;
            }
        };
        OnlineTileSourceBase TDTCSource = new OnlineTileSourceBase("Tian Di Tu Wmts", 1, 18, 256, "",
                new String[]{"http://t0.tianditu.gov.cn/img_c/wmts"}) {
            @Override
            public String getTileURLString(long pMapTileIndex) {
                String url = getBaseUrl() + "?SERVICE=WMTS" + "&REQUEST=GetTile" + "&VERSION=1.0.0" +
                        "&LAYER=img" + "&STYLE=default" + "&TILEMATRIXSET=c" + "&FORMAT=tiles" +
                        "&TILEMATRIX=" + MapTileIndex.getZoom(pMapTileIndex) +
                        "&TILEROW=" + MapTileIndex.getY(pMapTileIndex) +
                        "&TILECOL=" + MapTileIndex.getX(pMapTileIndex) +
                        "&tk=6d3110e391067a6345d705919e164790";
//                Log.d("xzw", "getTileURLString: " + url);
                return url;
            }
        };
        OnlineTileSourceBase WMTSSource = new OnlineTileSourceBase("Wmts", 1, 18, 256, "",
                new String[]{"https://fxpc.mem.gov.cn/wmts"}) {

            @Override
            public String getTileURLString(long pMapTileIndex) {
                int tileRow = (int) Math.floor(MapTileIndex.getY(pMapTileIndex));
                int tileCol = MapTileIndex.getX(pMapTileIndex);
                String url = getBaseUrl() + "?SERVICE=WMTS" + "&REQUEST=GetTile" + "&VERSION=1.0.0" +
                        "&LAYER=img" + "&STYLE=default" + "&TILEMATRIXSET=c" + "&FORMAT=tiles" +
                        "&TILEMATRIX=" + (MapTileIndex.getZoom(pMapTileIndex)) +
                        "&TILEROW=" + tileRow +
                        "&TILECOL=" + tileCol +
                        "&tk=4989e906aa138e5bb1b49a3eb83a6128";
//                Log.d("xzw", "getTileURLString:" + url);
                return url;
            }
        };
        WMSTileSource wmsTileSource = new WMSTileSource("WMS", new String[]{"https://fxpc.mem.gov.cn/wmts"}, "wms", "1.0", "EPSG:4326", "default", 256);

        String PREFS_TILE_SOURCE = "tilesource";
        String PREFS_NAME = "org.andnav.osm.prefs";
        SharedPreferences mPrefs = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        final String tileSourceName = mPrefs.getString(PREFS_TILE_SOURCE,
                TileSourceFactory.DEFAULT_TILE_SOURCE.name());
        try {
            final ITileSource tileSource = TileSourceFactory.getTileSource(tileSourceName);

            mMapView.setTileSource(tileSource);
        } catch (final IllegalArgumentException e) {
            mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        }

        mMapView.setTileSource(TDTWSource);


        overlay = new DirectedLocationOverlay(this);
        overlay.setShowAccuracy(true);
        Toast.makeText(this, "Requires location services turned on", Toast.LENGTH_LONG).show();
        mMapView.getOverlays().add(overlay);
        mMapView.getOverlays().add(new MapEventsOverlay(this));
        showPicker();
    }

    @Override
    public void onLocationChanged(Location location) {
        //after the first fix, schedule the task to change the icon
        if (!hasFix) {
            Toast.makeText(this, "Location fixed, scheduling icon change", Toast.LENGTH_LONG).show();
            TimerTask changeIcon = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.sfgpuci);
                                overlay.setDirectionArrow(drawable.getBitmap());
                            } catch (Throwable t) {
                                //insultates against crashing when the user rapidly switches fragments/activities
                            }
                        }
                    });

                }
            };
            Timer timer = new Timer();
            timer.schedule(changeIcon, 5000);
        }
        hasFix = true;

        if (!mMapView.getOverlays().contains(polyline)) {
            overlay.setBearing(location.getBearing());
            overlay.setAccuracy((int) location.getAccuracy());
            GeoPoint point = Transform.transformOffsetCoor(location.getLatitude(), location.getLongitude());
            overlay.setLocation(point);
            mMapView.invalidate();
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        Log.d("xzw", "singleTapConfirmedHelper: " + p.toString());
        return false;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        return false;
    }

    private void showPicker() {
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);

        Set<String> registeredExtensions = ArchiveFileFactory.getRegisteredExtensions();
        registeredExtensions.add("shp");


        String[] ret = new String[registeredExtensions.size()];
        ret = registeredExtensions.toArray(ret);
        properties.extensions = ret;

        FilePickerDialog dialog = new FilePickerDialog(MainActivity3.this, properties);
        dialog.setTitle("Select a File");
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                //files is the array of the paths of files selected by the Application User.
                try {
                    List<Overlay> folder = ShapeConverter.convert(mMapView, new File(files[0]));
                    for (final Overlay item : folder) {
                        if (item instanceof PolyOverlayWithIW) {
                            final PolyOverlayWithIW poly = (PolyOverlayWithIW) item;
//                            poly.setDowngradePixelSizes(3000, 20);
//                            poly.setDowngradeDisplay(true);
                            final Paint paint = poly.getOutlinePaint();
                            paint.setStyle(Paint.Style.STROKE);
                            paint.setStrokeJoin(Paint.Join.ROUND);
                            paint.setStrokeCap(Paint.Cap.ROUND);
                            paint.setStrokeWidth(5);
                            paint.setColor(Color.RED);
                            for (GeoPoint actualPoint : poly.getActualPoints()) {
                                Marker marker = new Marker(mMapView);
                                marker.setPosition(actualPoint);
                                mMapView.getOverlayManager().add(marker);
                            }
                        }
                    }
                    mMapView.getOverlayManager().addAll(folder);
                    mMapView.getController().animateTo(folder.get(0).getBounds().getCenterWithDateLine(), 18.0, null);
                    mMapView.invalidate();
                } catch (Exception e) {
                    Toast.makeText(MainActivity3.this, "Error importing file: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("xzw", "error importing file from " + files[0], e);
                }

            }

        });
        dialog.show();

    }


}