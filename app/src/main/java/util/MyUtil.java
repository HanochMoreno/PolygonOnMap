package util;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Hanoc_000 on 15/09/2017.
 */

public class MyUtil {

    /**
     * Calculates the distance between two LatLng points A and B.
     *
     * @param point1 : point A.
     * @param point2 : point B.
     *
     * @return The distance between two LatLng points, in meters.
     */
    public static double getDistanceBetweenPoints(final LatLng point1, final LatLng point2) {

        Location loc1 = new Location("");
        loc1.setLatitude(point1.latitude);
        loc1.setLongitude(point1.longitude);

        Location loc2 = new Location("");
        loc2.setLatitude(point2.latitude);
        loc2.setLongitude(point2.longitude);

        return loc1.distanceTo(loc2);
    }

//-------------------------------------------------------------------------------------------------

    /**
     * Finds the nearest point (LatLng) on a segment, from a given point.
     *
     * @param point : The LatLng point to compute.
     * @param segmentStart : The LatLng points that starts the segment.
     * @param segmentEnd : The LatLng points that ends the segment.
     * @return The LatLng nearest point on the calculated segment.
     *
     * * Based on `distanceToLine` method from PolyUtil.java:
     * https://github.com/googlemaps/android-maps-utils/blob/master/library/src/com/google/maps/android/PolyUtil.java
     */
    public LatLng getNearestPointOnSegment(final LatLng point, final LatLng segmentStart, final LatLng segmentEnd) {
        if (segmentStart.equals(segmentEnd)) {
            return segmentStart;
        }

        final double pLat = Math.toRadians(point.latitude);
        final double pLng = Math.toRadians(point.longitude);
        final double ssLat = Math.toRadians(segmentStart.latitude);
        final double ssLng = Math.toRadians(segmentStart.longitude);
        final double seLat = Math.toRadians(segmentEnd.latitude);
        final double seLng = Math.toRadians(segmentEnd.longitude);

        double seSsLat = seLat - ssLat;
        double seSsLng = seLng - ssLng;

        final double u = ((pLat - ssLat) * seSsLat + (pLng - ssLng) * seSsLng)
                / (seSsLat * seSsLat + seSsLng * seSsLng);

        if (u <= 0) {
            return segmentStart;
        }

        if (u >= 1) {
            return segmentEnd;
        }

        return new LatLng(segmentStart.latitude + (u * (segmentEnd.latitude - segmentStart.latitude)),
                segmentStart.longitude + (u * (segmentEnd.longitude - segmentStart.longitude)));
    }

//-------------------------------------------------------------------------------------------------

    /**
     * Round a double to 2 decimal places
     */
    public static double roundDouble(double value) {

        long factor = (long) Math.pow(10, 2);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
