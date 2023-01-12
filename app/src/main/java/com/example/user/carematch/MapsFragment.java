//package com.example.user.carematch;
//import android.Manifest;
//import android.content.pm.PackageManager;
//import android.location.Address;
//import android.location.Geocoder;
//import android.location.Location;
//import android.os.Build;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentActivity;
//import android.os.Bundle;
//import android.support.v4.content.ContextCompat;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.Toast;
//
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.location.LocationListener;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//
//import java.io.IOException;
//import java.util.List;
//
//public class MapsFragment extends Fragment implements OnMapReadyCallback,
//        GoogleApiClient.ConnectionCallbacks,
//        GoogleApiClient.OnConnectionFailedListener,
//        LocationListener{
//
//
//    private GoogleMap mMap;
//    private GoogleApiClient client;
//    private LocationRequest locationRequest;
//    private Location lastlocation;
//    private Marker currentLocationmMarker;
//    public static final int REQUEST_LOCATION_CODE = 99;
//    int PROXIMITY_RADIUS = 10000;
//    double latitude,longitude;
//    public Button B_search;
//
//    public ImageButton B_hopistals;
//
//
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_maps1);
//
//
//    public static MapsFragment newInstance() {
//        MapsFragment fragment = new MapsFragment();
//        return fragment;
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.activity_maps1, container, false);
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//        {
//            checkLocationPermission();
//
//        }
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//
//
//
//
//        //新增加
//        B_hopistals=(ImageButton)view.findViewById(R.id.B_hopistals);
//        B_hopistals.setOnClickListener(new View.OnClickListener()
//
//        {
//            @Override
//            public void onClick(View v) {
//
//
//                Object dataTransfer[] = new Object[2];
//                GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
//
//                mMap.clear();
//                String hospital = "hospital";
//                String url = getUrl(latitude, longitude, hospital);
//                dataTransfer[0] = mMap;
//                dataTransfer[1] = url;
//
//                getNearbyPlacesData.execute(dataTransfer);
//                Toast.makeText(MapsFragment.this.getActivity(), "Showing Nearby Hospitals", Toast.LENGTH_SHORT).show();
//
//            }
//        });
//
//        B_search=(Button) view.findViewById(R.id.B_search);
//        B_search.setOnClickListener(new View.OnClickListener()
//
//        {
//            @Override
//            public void onClick(View v) {
//
//
//
//                Object dataTransfer[] = new Object[2];
//                GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
//
//                mMap.clear();
//                EditText tf_location =  v.findViewById(R.id.TF_location);
//                String location = tf_location.getText().toString();
//                List<Address> addressList;
//
//
//                if(!location.equals(""))
//                {
//                    Geocoder geocoder = new Geocoder(getActivity());
//
//                    try {
//                        addressList = geocoder.getFromLocationName(location, 5);
//
//                        if(addressList != null)
//                        {
//                            for(int i = 0;i<addressList.size();i++)
//                            {
//                                LatLng latLng = new LatLng(addressList.get(i).getLatitude() , addressList.get(i).getLongitude());
//                                MarkerOptions markerOptions = new MarkerOptions();
//                                markerOptions.position(latLng);
//                                markerOptions.title(location);
//                                mMap.addMarker(markerOptions);
//                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//                                mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
//                            }
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//            }
//        });
//
//        return view;
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch(requestCode)
//        {
//            case REQUEST_LOCATION_CODE:
//                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
//                {
//                    if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) !=  PackageManager.PERMISSION_GRANTED)
//                    {
//                        if(client == null)
//                        {
//                            bulidGoogleApiClient();
//                        }
//                        mMap.setMyLocationEnabled(true);
//                    }
//                }
//                else
//                {
//                    Toast.makeText(getActivity(),"Permission Denied" , Toast.LENGTH_LONG).show();
//                }
//        }
//    }
//
//    /**
//     * Manipulates the map once available.
//     * This callback is triggered when the map is ready to be used.
//     * This is where we can add markers or lines, add listeners or move the camera. In this case,
//     * we just add a marker near Sydney, Australia.
//     * If Google Play services is not installed on the device, the user will be prompted to install
//     * it inside the SupportMapFragment. This method will only be triggered once the user has
//     * installed Google Play services and returned to the app.
//     */
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            bulidGoogleApiClient();
//            mMap.setMyLocationEnabled(true);
//        }
//    }
//
//
//    protected synchronized void bulidGoogleApiClient() {
//        client = new GoogleApiClient.Builder(getActivity()).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
//        client.connect();
//
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//
//        latitude = location.getLatitude();
//        longitude = location.getLongitude();
//        lastlocation = location;
//        if(currentLocationmMarker != null)
//        {
//            currentLocationmMarker.remove();
//
//        }
//        Log.d("lat = ",""+latitude);
//        LatLng latLng = new LatLng(location.getLatitude() , location.getLongitude());
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(latLng);
//        markerOptions.title("Current Location");
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//        currentLocationmMarker = mMap.addMarker(markerOptions);
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));
//
//        if(client != null)
//        {
//            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
//        }
//    }
//
//
//
////    public void onClick(View view)
////    {
////        Object dataTransfer[] = new Object[2];
////        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
////
////
////
////
////        switch(view.getId())
////        {
////            case R.id.B_search:
////                mMap.clear();
////                EditText tf_location =  view.findViewById(R.id.TF_location);
////                String location = tf_location.getText().toString();
////                List<Address> addressList;
////
////
////                if(!location.equals(""))
////                {
////                    Geocoder geocoder = new Geocoder(getActivity());
////
////                    try {
////                        addressList = geocoder.getFromLocationName(location, 5);
////
////                        if(addressList != null)
////                        {
////                            for(int i = 0;i<addressList.size();i++)
////                            {
////                                LatLng latLng = new LatLng(addressList.get(i).getLatitude() , addressList.get(i).getLongitude());
////                                MarkerOptions markerOptions = new MarkerOptions();
////                                markerOptions.position(latLng);
////                                markerOptions.title(location);
////                                mMap.addMarker(markerOptions);
////                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
////                                mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
////                            }
////                        }
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
////                }
////                break;
////
////
//////            case R.id.B_hopistals:
//////                mMap.clear();
//////                String hospital = "hospital";
//////                String url = getUrl(latitude, longitude, hospital);
//////                dataTransfer[0] = mMap;
//////                dataTransfer[1] = url;
//////
//////                getNearbyPlacesData.execute(dataTransfer);
//////                Toast.makeText(getActivity(), "Showing Nearby Hospitals", Toast.LENGTH_SHORT).show();
//////
//////
//////                break;
////
////
////
////
//////            case R.id.B_schools:
//////                mMap.clear();
//////                String school = "school";
//////                url = getUrl(latitude, longitude, school);
//////                dataTransfer[0] = mMap;
//////                dataTransfer[1] = url;
//////
//////                getNearbyPlacesData.execute(dataTransfer);
//////                Toast.makeText(MapsFragment.this, "Showing Nearby Schools", Toast.LENGTH_SHORT).show();
//////                break;
//////            case R.id.B_restaurants:
//////                mMap.clear();
//////                String resturant = "restuarant";
//////                url = getUrl(latitude, longitude, resturant);
//////                dataTransfer[0] = mMap;
//////                dataTransfer[1] = url;
//////
//////                getNearbyPlacesData.execute(dataTransfer);
//////                Toast.makeText(MapsFragment.this, "Showing Nearby Restaurants", Toast.LENGTH_SHORT).show();
//////                break;
//////            case R.id.B_to:
////        }
////    }
//
//
//
//
//    private String getUrl(double latitude , double longitude , String nearbyPlace)
//    {
//
//        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
//        googlePlaceUrl.append("location="+latitude+","+longitude);
//        googlePlaceUrl.append("&radius="+PROXIMITY_RADIUS);
//        googlePlaceUrl.append("&type="+nearbyPlace);
//        googlePlaceUrl.append("&sensor=true");
//        googlePlaceUrl.append("&key="+"AIzaSyBLEPBRfw7sMb73Mr88L91Jqh3tuE4mKsE");
//
//        Log.d("MapsFragment", "url = "+googlePlaceUrl.toString());
//
//        return googlePlaceUrl.toString();
//    }
//
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//
//        locationRequest = new LocationRequest();
//        locationRequest.setInterval(100);
//        locationRequest.setFastestInterval(1000);
//        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
//
//
//        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
//        {
//            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
//        }
//    }
//
//
//    public boolean checkLocationPermission()
//    {
//        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED )
//        {
//
//            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION))
//            {
//                ActivityCompat.requestPermissions(getActivity(),new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
//            }
//            else
//            {
//                ActivityCompat.requestPermissions(getActivity(),new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
//            }
//            return false;
//
//        }
//        else
//            return true;
//    }
//
//
//    @Override
//    public void onConnectionSuspended(int i) {
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//    }
//}
//
//

package com.example.user.carematch;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsFragment extends Fragment implements OnMapReadyCallback,

        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{


    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastlocation;
    private Marker currentLocationmMarker;
    public static final int REQUEST_LOCATION_CODE = 99;
    int PROXIMITY_RADIUS = 10000;
    double latitude,longitude;
    public Button B_search;
    private EditText tf_location;
    public ImageButton B_hopistals;
    private FirebaseFirestore db;
    public FirebaseAuth firebaseAuth;


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_maps1);


    public static MapsFragment newInstance() {
        MapsFragment fragment = new MapsFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        final View view = inflater.inflate(R.layout.fragment_maps, container, false);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkLocationPermission();

        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




        //新增加
        B_hopistals=(ImageButton)view.findViewById(R.id.B_hopistals);
        B_hopistals.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {


                Object dataTransfer[] = new Object[2];
                GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();

                mMap.clear();
                String hospital = "hospital";
                String url = getUrl(latitude, longitude, hospital);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MapsFragment.this.getActivity(), "顯示附近醫院", Toast.LENGTH_SHORT).show();
                LatLng Myhome = new LatLng(25.040017, 121.430935);
                mMap.addMarker(new MarkerOptions().position(Myhome).title("天主教輔大醫院").icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulance)));
                LatLng Myhome1 = new LatLng(25.051356, 121.509473);
                mMap.addMarker(new MarkerOptions().position(Myhome1).title("臺北市立聯合醫院中興院區").icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulance)));
                LatLng Myhome2 = new LatLng(25.105582, 121.532209);
                mMap.addMarker(new MarkerOptions().position(Myhome2).title("臺北市立聯合醫院陽明院區").icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulance)));
                LatLng Myhome3 = new LatLng(24.954143, 121.504065);
                mMap.addMarker(new MarkerOptions().position(Myhome3).title("天主教耕莘醫療財團法人耕莘醫院").icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulance)));
                LatLng Myhome4 = new LatLng(25.000200, 121.558166);
                mMap.addMarker(new MarkerOptions().position(Myhome4).title("臺北市立萬芳醫院").icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulance)));
                LatLng Myhome5= new LatLng(24.800759, 120.990822);
                mMap.addMarker(new MarkerOptions().position(Myhome5).title("財團法人馬偕紀念醫院新竹分院").icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulance)));

            }
        });

        B_search=(Button) view.findViewById(R.id.B_search);
        B_search.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {




                Object dataTransfer[] = new Object[2];
                GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();

                mMap.clear();
                tf_location =  (EditText) view.findViewById(R.id.TF_location);
                String location = tf_location.getText().toString();
                List<Address> addressList;


                if(!location.equals(""))
                {
                    Geocoder geocoder = new Geocoder(getActivity());

                    try {
                        addressList = geocoder.getFromLocationName(location, 5);

                        if(addressList != null)
                        {
                            for(int i = 0;i<addressList.size();i++)
                            {
                                LatLng latLng = new LatLng(addressList.get(i).getLatitude() , addressList.get(i).getLongitude());
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(latLng);
                                markerOptions.title(location);
                                LatLng Myhome = new LatLng(25.040017, 121.430935);
                                mMap.addMarker(new MarkerOptions().position(Myhome).title("天主教輔大醫院").icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulance)));
                                LatLng Myhome1 = new LatLng(25.051356, 121.509473);
                                mMap.addMarker(new MarkerOptions().position(Myhome1).title("臺北市立聯合醫院中興院區").icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulance)));
                                LatLng Myhome2 = new LatLng(25.105582, 121.532209);
                                mMap.addMarker(new MarkerOptions().position(Myhome2).title("臺北市立聯合醫院陽明院區").icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulance)));
                                LatLng Myhome3 = new LatLng(24.954143, 121.504065);
                                mMap.addMarker(new MarkerOptions().position(Myhome3).title("天主教耕莘醫療財團法人耕莘醫院").icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulance)));
                                LatLng Myhome4 = new LatLng(25.000200, 121.558166);
                                mMap.addMarker(new MarkerOptions().position(Myhome4).title("臺北市立萬芳醫院").icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulance)));
                                LatLng Myhome5= new LatLng(24.800759, 120.990822);
                                mMap.addMarker(new MarkerOptions().position(Myhome5).title("財團法人馬偕紀念醫院新竹分院").icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulance)));
                                mMap.addMarker(markerOptions);
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode)
        {
            case REQUEST_LOCATION_CODE:
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(MapsFragment.this.getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) !=  PackageManager.PERMISSION_GRANTED)
                    {
                        if(client == null)
                        {
                            bulidGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else
                {
                    Toast.makeText(getActivity(),"Permission Denied" , Toast.LENGTH_LONG).show();
                }
        }
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
        LatLng Myhome = new LatLng(25.040017, 121.430935);
        mMap.addMarker(new MarkerOptions().position(Myhome).title("天主教輔大醫院").icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulance)));
        LatLng Myhome1 = new LatLng(25.051356, 121.509473);
        mMap.addMarker(new MarkerOptions().position(Myhome1).title("臺北市立聯合醫院中興院區").icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulance)));
        LatLng Myhome2 = new LatLng(25.105582, 121.532209);
        mMap.addMarker(new MarkerOptions().position(Myhome2).title("臺北市立聯合醫院陽明院區").icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulance)));
        LatLng Myhome3 = new LatLng(24.954143, 121.504065);
        mMap.addMarker(new MarkerOptions().position(Myhome3).title("天主教耕莘醫療財團法人耕莘醫院").icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulance)));
        LatLng Myhome4 = new LatLng(25.000200, 121.558166);
        mMap.addMarker(new MarkerOptions().position(Myhome4).title("臺北市立萬芳醫院").icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulance)));
        LatLng Myhome5= new LatLng(24.800759, 120.990822);
        mMap.addMarker(new MarkerOptions().position(Myhome5).title("財團法人馬偕紀念醫院新竹分院").icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulance)));


        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            bulidGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }


    protected synchronized void bulidGoogleApiClient() {
        client = new GoogleApiClient.Builder(getActivity()).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect();

    }


    @Override
    public void onLocationChanged(Location location) {
        final String currentUserID = firebaseAuth.getCurrentUser().getUid();

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        lastlocation = location;
        if(currentLocationmMarker != null)
        {
            currentLocationmMarker.remove();

        }
        Log.d("long= ",""+longitude);
        Log.d("lat = ",""+latitude);

        DocumentReference locate  =db.collection("users").document(currentUserID);
        locate.update("Latitude",String.valueOf(latitude));
        locate.update("Longitude",String.valueOf(longitude));



        LatLng latLng = new LatLng(location.getLatitude() , location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentLocationmMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));

        if(client != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
        }
    }



//    public void onClick(View view)
//    {
//        Object dataTransfer[] = new Object[2];
//        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
//
//
//
//
//        switch(view.getId())
//        {
//            case R.id.B_search:
//                mMap.clear();
//                EditText tf_location =  view.findViewById(R.id.TF_location);
//                String location = tf_location.getText().toString();
//                List<Address> addressList;
//
//
//                if(!location.equals(""))
//                {
//                    Geocoder geocoder = new Geocoder(getActivity());
//
//                    try {
//                        addressList = geocoder.getFromLocationName(location, 5);
//
//                        if(addressList != null)
//                        {
//                            for(int i = 0;i<addressList.size();i++)
//                            {
//                                LatLng latLng = new LatLng(addressList.get(i).getLatitude() , addressList.get(i).getLongitude());
//                                MarkerOptions markerOptions = new MarkerOptions();
//                                markerOptions.position(latLng);
//                                markerOptions.title(location);
//                                mMap.addMarker(markerOptions);
//                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//                                mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
//                            }
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                break;
//
//
////            case R.id.B_hopistals:
////                mMap.clear();
////                String hospital = "hospital";
////                String url = getUrl(latitude, longitude, hospital);
////                dataTransfer[0] = mMap;
////                dataTransfer[1] = url;
////
////                getNearbyPlacesData.execute(dataTransfer);
////                Toast.makeText(getActivity(), "Showing Nearby Hospitals", Toast.LENGTH_SHORT).show();
////
////
////                break;
//
//
//
//
////            case R.id.B_schools:
////                mMap.clear();
////                String school = "school";
////                url = getUrl(latitude, longitude, school);
////                dataTransfer[0] = mMap;
////                dataTransfer[1] = url;
////
////                getNearbyPlacesData.execute(dataTransfer);
////                Toast.makeText(MapsActivity.this, "Showing Nearby Schools", Toast.LENGTH_SHORT).show();
////                break;
////            case R.id.B_restaurants:
////                mMap.clear();
////                String resturant = "restuarant";
////                url = getUrl(latitude, longitude, resturant);
////                dataTransfer[0] = mMap;
////                dataTransfer[1] = url;
////
////                getNearbyPlacesData.execute(dataTransfer);
////                Toast.makeText(MapsActivity.this, "Showing Nearby Restaurants", Toast.LENGTH_SHORT).show();
////                break;
////            case R.id.B_to:
//        }
//    }




    private String getUrl(double latitude , double longitude , String nearbyPlace)
    {

        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type="+nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+"AIzaSyBLEPBRfw7sMb73Mr88L91Jqh3tuE4mKsE");

        Log.d("MapsActivity", "url = "+googlePlaceUrl.toString());

        return googlePlaceUrl.toString();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }
    }


    public boolean checkLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED )
        {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(getActivity(),new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            else
            {
                ActivityCompat.requestPermissions(getActivity(),new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            return false;

        }
        else
            return true;
    }


    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
}