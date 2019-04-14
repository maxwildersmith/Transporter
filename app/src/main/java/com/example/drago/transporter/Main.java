package com.example.drago.transporter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Cap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Main extends FragmentActivity implements OnMapReadyCallback {

    public static final String BASE_URL="https://maps.googleapis.com/maps/api/directions/json?origin=", API="&key=";//TODO: ADD API KEY
    public static final String DISTANCE_URL="https://maps.googleapis.com/maps/api/distancematrix/json?origins=";

    private GoogleMap mMap;
    private List<PickupRequest> destinations;
    private List<Polyline> onMap;
    private RequestQueue queue;
    private Circle radiusCircle;
    private double range = 300;
    private LatLng[] coors = {new LatLng(34.042317, -118.255994),new LatLng(34.041906, -118.252327), new LatLng(34.041666, -118.258459)};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        queue = Volley.newRequestQueue(this);

        onMap = new ArrayList<>();
        destinations = new ArrayList<>();
        destinations.add(new PickupRequest(coors[0],coors[2],queue));
        destinations.add(new PickupRequest(coors[1],coors[0],queue));
    }


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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    9);
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.addMarker(new MarkerOptions().position(coors[0]).title("Destination 1"));
        mMap.addMarker(new MarkerOptions().position(coors[1]).title("Destination 2"));
        mMap.addMarker(new MarkerOptions().position(coors[2]).title("Destination 2"));

        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {

            }
        });
        // Check if we were successful in obtaining the map.
        if (mMap != null) {

            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                //
                @Override
                public void onMyLocationChange(Location arg0) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(arg0.getLatitude(),arg0.getLongitude())));
//// ...
////
////// Instantiate the RequestQueue.
//                        queue = Volley.newRequestQueue(Main.this);
//                        String url ="https://maps.googleapis.com/maps/api/directions/json?origin="+arg0.getLatitude()+","+arg0.getLongitude()+"&destination=The Mayan&mode=walking&key=AIzaSyDQ3MYRpEFdHYU84eRGt8g6Q4eFYmz88T4";
//
//// Request a string response from the provided URL.
//                        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                                new Response.Listener<String>() {
//                                    @Override
//                                    public void onResponse(String response) {
//                                        // Display the first 500 characters of the response string.
//                                        try {
//                                            final JSONObject json = new JSONObject(response).getJSONArray("routes").getJSONObject(0);
//                                            String polyline = json.getJSONObject("overview_polyline").getString("points");
//                                            List<LatLng> pts = PolyUtil.decode(polyline);
//                                            mMap.addPolyline(new PolylineOptions().clickable(true).add(pts.toArray(new LatLng[pts.size()])));
//                                            mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
//                                                @Override
//                                                public void onPolylineClick(Polyline polyline) {
//                                                    try {
//                                                        Toast.makeText(Main.this, json.getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getString("text")+" to get to "+json.getJSONArray("legs").getJSONObject(0).getString("end_address"), Toast.LENGTH_SHORT).show();
//                                                    } catch (JSONException e) {
//                                                        e.printStackTrace();
//                                                    }
//                                                }
//                                            });
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                }, new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                Log.d("asdf", "onErrorResponse: mlem mlem mlem mlem mlem mlem mlem");
//                            }
//                        });
//
//// Add the request to the RequestQueue.
//                        queue.add(stringRequest);

                    radiusCircle.remove();
                    if(radiusCircle!=null)
                        radiusCircle.setCenter(new LatLng(arg0.getLatitude(),arg0.getLongitude()));
                    else
                        radiusCircle=mMap.addCircle(new CircleOptions().center(new LatLng(arg0.getLatitude(),arg0.getLongitude())).radius(range).visible(true).strokeColor(0x4fc3f7aa));


                    inRange(arg0.getLatitude(),arg0.getLongitude());
                }
            });


        }
    }

    public void addRequests(List<PickupRequest> inputs){
        Log.d("asdf", "addRequests: "+inputs.size());
        if(mMap!=null){
            for(Polyline line:onMap)
                inputs.remove(line);

            for(PickupRequest r: inputs) {
                r.makePolyLine(mMap,onMap);
            }
        }
    }

    public void inRange(double lat, double lng){
        String url=DISTANCE_URL+lat+","+lng+"&destinations=";
        for(PickupRequest r:destinations)
            url+=r.getLatLng()+"|";
        url.substring(0,url.length()-1);
        url+="&mode=walking&units=imperial"+API;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray values = new JSONObject(response).getJSONArray("rows").getJSONObject(0).getJSONArray("elements");
                            List<PickupRequest> inRange = new ArrayList<>();
                            for(int i=0;i<destinations.size();i++)
                                if(values.getJSONObject(i).getJSONObject("distance").optInt("value",-1)<=range) {
                                    inRange.add(destinations.get(i));
                                }
                            addRequests(inRange);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("asdf", "onErrorResponse: mlem mlem mlem mlem mlem mlem mlem");
            }
        });

        queue.add(stringRequest);
    }

}
