package com.example.hanoc_000.hanochmap;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.data.Geometry;
import com.google.maps.android.data.kml.KmlContainer;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.data.kml.KmlPlacemark;
import com.google.maps.android.data.kml.KmlPolygon;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import model.MyPolygon;
import util.MyUtil;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marker;
    private KmlLayer layer;
    private MyPolygon polygon;

//-------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }

//-------------------------------------------------------------------------------------------------

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            // Creating the layer and adding it to the map so I could extract the polygon points
            layer = new KmlLayer(googleMap, R.raw.allowed_area, getApplicationContext());
            layer.addLayerToMap();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (layer != null) {
            generatePolygonFromLayer(layer);

            // After extracting the polygon points, I don't need the layer anymore and it can be removed
            layer.removeLayerFromMap();

            // Move the camera and zoom in on the polygon
            if (polygon.points.size() > 2) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(polygon.points.get(0), 13));
                mMap.addPolygon(polygon.options);
            }
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                if (marker != null) {
                    marker.remove();
                }

                marker = mMap.addMarker(new MarkerOptions().position(point));

                if (polygon != null) {

                    if (polygon.isPointInsidePolygon(point)) {
                        marker.setTitle("You are inside the polygon!");
                    } else {
                        LatLng nearestPointOnPolygon = polygon.getNearestPointOnPolygonFromPoint(point);

                        double shortestDistanceFromPointToPolygon =
                                MyUtil.getDistanceBetweenPoints(marker.getPosition(), nearestPointOnPolygon);

                        marker.setTitle("Shortest distance to polygon is : "
                                + MyUtil.roundDouble(shortestDistanceFromPointToPolygon) + " meters");
                    }

                    marker.showInfoWindow();
                }
            }
        });
    }

//-------------------------------------------------------------------------------------------------

    public void generatePolygonFromLayer(@NonNull KmlLayer layer) {
        if (polygon == null) {
            polygon = new MyPolygon();

            for (KmlContainer c : layer.getContainers()) {
                for (KmlPlacemark p : c.getPlacemarks()) {
                    Geometry geometry = p.getGeometry();
                    if (geometry.getGeometryType().equals("Polygon")) {
                        polygon.updatePolygonPoints(((KmlPolygon) geometry).getOuterBoundaryCoordinates());
                    }
                }
            }
        }
    }

}
