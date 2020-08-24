package com.example.healthapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.DashPathEffect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.example.healthapplication.MainActivity;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.Toast;


import java.lang.Math;
import java.util.Arrays;
import java.util.Calendar;
import com.example.healthapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static android.os.Looper.getMainLooper;

public class MapsFragment extends Fragment  implements  OnMapReadyCallback{

    double tempLat = 36.374101;
    double tempLong = 127.365497;


    private static final String TAG = "Map";
    public static ArrayList<String> places; //  위도 & 경도 저장하는 배열
    public static GoogleMap mMap;
    private static final int LOCATION_REQUEST = 500;
    private FusedLocationProviderClient mFusedLocationClient;
    private Boolean mLocationPermissionsGranted = false;
    private static final long FASTEST_INTERVAL=1000 * 20 * 1;
    private static final long INTERVAL= 1000*20*1;
    ArrayList<LatLng> listPoints;
    public static ArrayList<LatLng> click;
    int clicknum=0;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    MarkerOptions markerOptions= new MarkerOptions();
    public static double distance=0.0;
    private int flag=0;
    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+9:00"));
    public static int[] start_time = new int[3];  // h m s + Month/Day
    public static int[] end_time = new int[3];

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG,"onCreate View");
        View view=inflater.inflate(R.layout.fragment_map, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG, "onViewCreated");
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ImageView stop_map=(ImageView)view.findViewById(R.id.stop_map);
        stop_map.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Calendar cal1 = Calendar.getInstance(TimeZone.getTimeZone("GMT+9:00"));
                                        end_time[0]=cal1.get(Calendar.HOUR);
                                        end_time[1]=cal1.get(Calendar.MINUTE);
                                        end_time[2]=cal1.get(Calendar.SECOND);
                                        /*
                                        end_time[3]=cal1.get(Calendar.MONTH)+1;
                                        end_time[4]=cal1.get(Calendar.DAY_OF_MONTH);
                                        */
                                        System.out.println(end_time[0]+"h "+end_time[1]+"m "+end_time[2]+"s ");
                                        int count=0;
                                        if (mFusedLocationClient != null) {
                                            System.out.print(listPoints.size());
                                            if(listPoints.size()>=2) {
                                                while (listPoints.size() - 1 > count) {
                                                    distance += caldistance(listPoints.get(count), listPoints.get(count + 1));
                                                    System.out.println("Adding distance");
                                                    count++;
                                                }
                                            }
                                            else{
                                                System.out.println("Move more ");
                                            }
                                            System.out.println(distance+" km walked");
                                            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                                        }
                                    }
                                });

                callPermissions();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        click=new ArrayList<>();
        List<LatLng> routeArray = new ArrayList<>(); // 마커를 연결하기 위해 위도&경도 정보를 저장할 배열 생성

        Log.e(TAG,"onMapReady");
        // Add a marker in Sydney and move the camera
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }
        mMap.setMyLocationEnabled(true);


        /*
        마커 찍기
         */

        int colorOption = 0;
        BitmapDescriptor color = null;
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                                           @Override
                                           public void onMapLongClick(LatLng latLng) {
                                               if(clicknum==0){
                                                   mMap.addMarker(new MarkerOptions()
                                                           .position(latLng)
                                                           .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                                                   clicknum++;
                                               }
                                               else if(clicknum==1){
                                                   mMap.addMarker(new MarkerOptions()
                                                           .position(latLng)
                                                           .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                                                   clicknum++;
                                               }
                                               else{
                                                   mMap.addMarker(new MarkerOptions()
                                                           .position(latLng)
                                                           .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                                    clicknum=0;
                                               }
                                               click.add(latLng);
                                           }
                                       });


        for(int i=0; i<places.size(); i+=2){
            // 마커 색 설정
            if(colorOption % 3 == 0 ){
                color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN);
            }else if(colorOption % 3 == 1){
                color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
            }else{
                color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
            }
            colorOption++;
            // 마커 옵션 설정
            /*
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions
                    .position(new LatLng(Double.parseDouble(places.get(i)), Double.parseDouble(places.get(i+1))))
                    .icon(color);

             */
            routeArray.add(new LatLng(Double.parseDouble(places.get(i)), Double.parseDouble(places.get(i+1))));

            //mMap.addMarker(markerOptions); // 마커 생성

                listPoints = new ArrayList<>();

        /*
        선 그리기
         */

        // 한 줄로 간단히 적을 수도 있음
        // ex) Polyline line = mMap.addPolyline(new PolylineOptions().add(new LatLng(Double.parseDouble(places.get(i)), Double.parseDouble(places.get(i+1)))).width(2).color(Color.WHITE).geodesic(true));
    }

        routeArray.add(new LatLng(Double.parseDouble(places.get(0)), Double.parseDouble(places.get(1))));

        // Draw a solid green polyline
        mMap.addPolyline(new PolylineOptions()
                .addAll(routeArray)
                .width(15)
                .color(getContext().getColor(R.color.colorPrimary)));

        // Draw a dashed (60px spaced) blue polyline
        List<PatternItem> dashedPattern = Arrays.asList(new Dash(60), new Gap(60));
        mMap.addPolyline(new PolylineOptions()
                .addAll(routeArray)
                .width(15)
                .pattern(dashedPattern)
                .color(Color.YELLOW));

    }

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    public void callPermissions(){
        Log.e(TAG,"callPermissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        Permissions.check(requireContext()/*context*/, permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                Log.e(TAG,"callPermissions");
                requestLocationUpdates();
            }
            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions){
                super.onDenied(context, deniedPermissions);
                callPermissions();
            }
        });
    }

    public void requestLocationUpdates(){
        Log.e(TAG,"requestLocationUpdates");
        if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient = new FusedLocationProviderClient(requireActivity());
            mLocationRequest = new LocationRequest();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
            mLocationRequest.setInterval(INTERVAL);
            start_time[0]=cal.get(Calendar.HOUR);
            start_time[1]=cal.get(Calendar.MINUTE);
            start_time[2]=cal.get(Calendar.SECOND);
            /*
            start_time[3]=cal.get(Calendar.MONTH)+1;
            start_time[4]=cal.get(Calendar.DAY_OF_MONTH);
            */



            System.out.println(start_time[0]+"h "+start_time[1]+"m "+start_time[2]+"s ");
            mLocationCallback=  new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    LatLng a= new LatLng(tempLat, tempLong);
                    //LatLng a= new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                    System.out.println("@@@@@@@@@"+a);
                    listPoints.add(a);
                    if(listPoints.size() !=0){
                        markerOptions.position(listPoints.get(listPoints.size()-1));
                        if(flag==0) {
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                            flag=1;
                        }
                        else if(flag==1){
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                            flag=2;
                        }
                        else{
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                            flag=0;
                        }
                        mMap.addMarker(markerOptions);
                    }
                    Log.e(TAG, "lat: "+locationResult.getLastLocation().getLatitude()+" long: "+ locationResult.getLastLocation().getLongitude());
                    moveCamera(a,16f);
                    tempLat -= 0.0001;
                    tempLong -= 0.0001;

                }
            };
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,mLocationCallback, getMainLooper());  //execute the code on main UI
        }

        else{
            callPermissions();
        }

    }
    public double caldistance(LatLng a, LatLng b){
       double phi1=a.latitude *Math.PI/180.0;
       double phi2=b.latitude*Math.PI/180.0;
       double del_phi=(b.latitude-a.latitude)*Math.PI/180.0;
       double del_lamb=(b.longitude-a.longitude)*Math.PI/180.0;

       double const1= Math.sin(del_phi/2)*Math.sin(del_phi/2)+ Math.cos(phi1)*Math.cos(phi2)*Math.sin(del_lamb/2)*Math.sin(del_lamb/2);

       double const2= 2*Math.atan2(Math.sqrt(const1), Math.sqrt(1-const1));

       final double R= 6371e3;

       return (R*const2)/1000.0;
    }


 }