package org.osmdroid.util;

import android.util.Log;

import org.osmdroid.views.MapView;

public class Transform {

    private static final double PI = Math.PI;
    private static final double mercatorMax = 20037508.34;

    /**
     * 4326坐标转3857即经纬度转墨卡托
     *
     * @param lon
     * @param lat
     */
    public static void transformTo3857(double lon, double lat) {
        double mercatorx = lon * mercatorMax / 180;
        double mercatory = Math.log(Math.tan(((90 + lat) * PI) / 360)) / (PI / 180);
        mercatory = mercatory * mercatory / 180;
        Log.d("xzw", "onLocationChanged: " + mercatorx + ":" + mercatory);

    }

    /**
     * 墨卡托坐标转3857即墨卡托转经纬度
     *
     * @param mercatorx
     * @param mercatory
     */
    public static void tarnsformTo4326(double mercatorx, double mercatory) {
        double lon = mercatorx / mercatorMax * 180;
        double lat = mercatory / mercatorMax * 180;
        lat = (180 / PI) * (2 * Math.atan(Math.exp((lat * PI) / 180)) - PI / 2);
    }

    public static GeoPoint transformOffsetCoor(double lat, double lon) {
        final double sinus = Math.sin(lat * Math.PI / 180);
//        double lat = location.getLatitude();
        GeoPoint point = new GeoPoint(lat, lon);
        return point;
    }
}
