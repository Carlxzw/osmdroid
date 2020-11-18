package org.osmdroid.util;

import org.osmdroid.util.constants.GeoConstants;

/**
 * @since 6.0.2
 * @author Fabrice Fontaine
 */
public class TileSystemWGS84 extends TileSystem{
//    private static final double MinLatitude = -85.05112877980658;
//    private static final double MaxLatitude = 85.05112877980658;
    public static final double MinLatitude = -90;
    public static final double MaxLatitude = 90;
    public static final double MinLongitude = -180;
    public static final double MaxLongitude = 180;

    public TileSystemWGS84(){
        setmProjectionType(GeoConstants.ProjectionType.PLATTE_CARRE);
    }

    @Override
    public double getX01FromLongitude(final double pLongitude) {
        return (pLongitude - getMinLongitude()) / (getMaxLongitude() - getMinLongitude());
    }

    @Override
    public double getY01FromLatitude(final double pLatitude) {
//        final double sinus = Math.sin(pLatitude * Math.PI / 180);
//        return (0.5 - Math.log((1 + sinus) / (1 - sinus)) / (4 * Math.PI))  ;
       return  (getMaxLatitude() - pLatitude) / (getMaxLatitude() - getMinLatitude());
    }

    @Override
    public double getLongitudeFromX01(final double pX01) {
        return getMinLongitude() + (getMaxLongitude() - getMinLongitude()) * pX01;
    }

    @Override
    public double getLatitudeFromY01(final double pY01) {
        return getMaxLatitude() - (getMaxLatitude() - getMinLatitude()) * pY01;
//        return 90 - (360 * Math.atan(Math.exp((pY01 - 0.5) * 2 * Math.PI)) / Math.PI);
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
        out.setLongitude(getLongitudeFromX01(getXY01FromMercator(pMercatorX, pMapSize , horizontalWrapEnabled), horizontalWrapEnabled));
        return out;
    }

}
