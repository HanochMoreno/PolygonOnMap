package model;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.data.kml.KmlPolygon;

import java.util.ArrayList;
import java.util.List;

import util.MyUtil;

/**
 * Created by Hanoc_000 on 15/09/2017.
 */

public class MyPolygon {

    /**
     * The points that defines the closed polygon.
     */
    public ArrayList<LatLng> points = new ArrayList<>();
    private MyUtil util = new MyUtil();

    public PolygonOptions options = new PolygonOptions()
            .strokeColor(0xFF00AA00)
            .fillColor(0x2200FFFF)
            .strokeWidth(2);

    public LatLng minLng;
    public LatLng minLat;
    public LatLng maxLng;
    public LatLng maxLat;

//-------------------------------------------------------------------------------------------------

    public void updatePolygonPoints(List<LatLng> geometryPoints) {
        points.addAll(geometryPoints);

        options
                .strokeColor(0xFF00AA00)
                .fillColor(0x2200FFFF)
                .addAll(points)
                .strokeWidth(2);
    }

//-------------------------------------------------------------------------------------------------

    /**
     * Determines weather a LatLng point lays inside a given polygon.
     *
     * @param p : The LatLng point.
     * @return true if the point lays inside the polygon, or false if it doesn't.
     */
    public boolean isPointInsidePolygon(LatLng p) {

        if (points == null || points.size() < 3) {
            return false;
        }

        double minX = points.get(0).longitude;
        double maxX = points.get(0).longitude;
        double minY = points.get(0).latitude;
        double maxY = points.get(0).latitude;

        for (int i = 1; i < points.size(); i++) {
            LatLng point = points.get(i);

            minX = Math.min(point.longitude, minX);
            maxX = Math.max(point.longitude, maxX);
            minY = Math.min(point.latitude, minY);
            maxY = Math.max(point.latitude, maxY);
        }

        if (p.longitude < minX || p.longitude > maxX || p.latitude < minY || p.latitude > maxY) {
            // The point is outside the bounding rectangle of the polygon
            return false;
        }

        // http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
        boolean isInside = false;
        for (int i = 0, j = points.size() - 1; i < points.size(); j = i++) {
            if ((points.get(i).latitude > p.latitude) != (points.get(j).latitude > p.latitude) &&
                    p.longitude < (points.get(j).longitude - points.get(i).longitude) * (p.latitude - points.get(i).latitude) / (points.get(j).latitude - points.get(i).latitude) + points.get(i).longitude) {

                isInside = !isInside;
            }
        }

        return isInside;
    }

//-------------------------------------------------------------------------------------------------

    /**
     * Finds the nearest point (LatLng) on a polygon, from a given point.
     *
     * @param point : The LatLng point to compute.
     * @return The LatLng nearest point on the given polygon.
     */
    public LatLng getNearestPointOnPolygonFromPoint(@NonNull LatLng point) {
        double minimumDistance = -1;
        LatLng minimumDistancePoint = point;

        if (points == null || points.size() < 3) {
            return null;
        }

        for (int i = 0; i < points.size(); i++) {
            LatLng segmentStartPoint = points.get(i);

            int segmentEndPointIndex = i + 1;
            if (segmentEndPointIndex >= points.size()) {
                segmentEndPointIndex = 0;
            }

            // Computes the distance on the sphere between the point p and the line segment start to end.
            // http://googlemaps.github.io/android-maps-utils/javadoc/com/google/maps/android/PolyUtil.html
            double distanceFromPointToSegment = PolyUtil.distanceToLine(point, segmentStartPoint, points.get(segmentEndPointIndex));

            if (minimumDistance == -1 || distanceFromPointToSegment < minimumDistance) {
                minimumDistance = distanceFromPointToSegment;
                minimumDistancePoint = util.getNearestPointOnSegment(point, segmentStartPoint, points.get(segmentEndPointIndex));
            }
        }

        return minimumDistancePoint;
    }
}
