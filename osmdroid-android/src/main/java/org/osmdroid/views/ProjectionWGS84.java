package org.osmdroid.views;

import android.graphics.Rect;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.TileSystem;
@Deprecated
public class ProjectionWGS84 extends Projection {
    public ProjectionWGS84(MapView mapView) {
        this(
                mapView.getZoomLevelDouble(), mapView.getIntrinsicScreenRect(null),
                mapView.getExpectedCenter(),
                mapView.getMapScrollX(), mapView.getMapScrollY(),
                mapView.getMapOrientation(),
                mapView.isHorizontalMapRepetitionEnabled(), mapView.isVerticalMapRepetitionEnabled(),
                MapView.getTileSystem(),
                mapView.getMapCenterOffsetX(),
                mapView.getMapCenterOffsetY());
    }

    public ProjectionWGS84(double pZoomLevel, Rect pScreenRect, GeoPoint pCenter, long pScrollX, long pScrollY, float pOrientation, boolean pHorizontalWrapEnabled, boolean pVerticalWrapEnabled, TileSystem pTileSystem, int pMapCenterOffsetX, int pMapCenterOffsetY) {
        super(pZoomLevel, pScreenRect, pCenter, pScrollX, pScrollY, pOrientation, pHorizontalWrapEnabled, pVerticalWrapEnabled, pTileSystem, pMapCenterOffsetX, pMapCenterOffsetY);
        mMercatorMapSizeX = TileSystem.MapSizeX(mZoomLevelProjection);
        mMercatorMapSizeY = (TileSystem.MapSizeY(mZoomLevelProjection));
    }

    public ProjectionWGS84(double pZoomLevel, int pWidth, int pHeight, GeoPoint pCenter, float pOrientation, boolean pHorizontalWrapEnabled, boolean pVerticalWrapEnabled, int pMapCenterOffsetX, int pMapCenterOffsetY) {
        this(pZoomLevel, new Rect(0, 0, pWidth, pHeight),
                pCenter,
                0, 0,
                pOrientation,
                pHorizontalWrapEnabled, pVerticalWrapEnabled,
                MapView.getTileSystem(),
                pMapCenterOffsetX, pMapCenterOffsetY);
    }
}
