package org.osmdroid.util;

import org.osmdroid.util.constants.GeoConstants;

/**
 * @author Fabrice Fontaine
 * @since 6.0.2
 */
public class TileSystemWGS84 extends TileSystem {
    public static final double MinLatitude = -90;
    public static final double MaxLatitude = 90;
    public static final double MinLongitude = -180;
    public static final double MaxLongitude = 180;

    public TileSystemWGS84() {
        setmProjectionType(GeoConstants.ProjectionType.PLATTE_CARRE);
    }

    @Override
    public double getX01FromLongitude(final double pLongitude) {
        return (pLongitude - getMinLongitude()) / (getMaxLongitude() - getMinLongitude());
    }

    @Override
    public double getY01FromLatitude(final double pLatitude) {
        return (getMaxLatitude() - pLatitude) / (getMaxLatitude() - getMinLatitude());
    }

    @Override
    public double getLongitudeFromX01(final double pX01) {
        return getMinLongitude() + (getMaxLongitude() - getMinLongitude()) * pX01;
    }

    @Override
    public double getLatitudeFromY01(final double pY01) {
        return getMaxLatitude() - (getMaxLatitude() - getMinLatitude()) * pY01;
    }

    @Override
    public double getMinLatitude() {
        return MinLatitude;
    }

    @Override
    public double getMaxLatitude() {
        return MaxLatitude;
    }

    @Override
    public double getMinLongitude() {
        return MinLongitude;
    }

    @Override
    public double getMaxLongitude() {
        return MaxLongitude;
    }


    /**
     * @since 6.0.0
     */
    @Override
    public GeoPoint getGeoFromMercator(final long pMercatorX, final long pMercatorY, final double pMapSize, final GeoPoint pReuse, boolean horizontalWrapEnabled, boolean verticalWrapEnabled) {
        final GeoPoint out = pReuse == null ? new GeoPoint(0., 0.) : pReuse;
        out.setLatitude(getLatitudeFromY01(getXY01FromMercator(pMercatorY, pMapSize / 2, verticalWrapEnabled), verticalWrapEnabled));
        out.setLongitude(getLongitudeFromX01(getXY01FromMercator(pMercatorX, pMapSize, horizontalWrapEnabled), horizontalWrapEnabled));
        return out;
    }

}
