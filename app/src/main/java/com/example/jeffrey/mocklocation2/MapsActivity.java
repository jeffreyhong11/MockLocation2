package com.example.jeffrey.mocklocation2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.location.Address;
import android.location.Geocoder;

import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback  {

    private GoogleMap mMap;
    android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
    LatLng latLng;
    MarkerOptions markerOptions;
    EditText etLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.setMyLocationEnabled(true);

        String longitud = getIntent().getStringExtra("savedLon");
        String latitud = getIntent().getStringExtra("savedLat");
        float zoom = getIntent().getFloatExtra("savedZoom",0.0f);

        if(longitud.length()!=0 && latitud.length()!=0){
            Double savLon = Double.parseDouble(longitud);
            Double savLat = Double.parseDouble(latitud);

            LatLng savLatLng = new LatLng(savLat,savLon);

            markerOptions = new MarkerOptions();
            markerOptions.position(savLatLng);

            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(savLatLng,zoom));

        }

        // Getting reference to btn_find of the layout
        Button btn_find = (Button) findViewById(R.id.btn_find);
        OnClickListener findClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Getting reference to EditText to get the user input location
                etLocation = (EditText) findViewById(R.id.et_location);

                // Getting user input location
                String location = etLocation.getText().toString();

                if(location!=null && !location.equals("")){
                    new GeocoderTask().execute(location);
                }
            }
        };

        btn_find.setOnClickListener(findClickListener);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng position) {

                mMap.addMarker(new MarkerOptions().position(position).title("Marker"));
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(final Marker marker) {

                class AlertDFragment extends android.support.v4.app.DialogFragment {

                    //create dialog box that opens when user clicks on marker

                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState) {
                        return new AlertDialog.Builder(getActivity())
                                // Set Dialog Icon
                                .setTitle("Options")
                                        // Set Dialog Message
                                        //.setMessage("Alert DialogFragment Tutorial")

                                        // Set location allows user to find latlng/address of location
                                .setPositiveButton("Set Location", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        String lat = String.valueOf(marker.getPosition().latitude);
                                        String lon = String.valueOf(marker.getPosition().longitude);

                                        Intent data = new Intent();
                                        data.putExtra("lat", lat);
                                        data.putExtra("long", lon);
                                        data.putExtra("savedZoom", googleMap.getCameraPosition().zoom);

                                        Log.i("tag", "hhh "+ googleMap.getCameraPosition().zoom);

                                        setResult(RESULT_OK, data);

                                        finish();
                                    }
                                })

                                .setNeutralButton("View", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent i = new Intent(MapsActivity.this,CommentActivity.class);
                                        startActivity(i);
                                    }
                                })

                                        // Allows user to remove the marker
                                .setNegativeButton("Delete Marker", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        marker.remove();
                                    }
                                }).create();

                    }
                }

                AlertDFragment alertdFragment = new AlertDFragment();
                alertdFragment.show(fm, "Alert Dialog Fragment");

                return true;
            }

        });
    }

    // An AsyncTask class for accessing the GeoCoding Web Service
    private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(String... locationName) {
            // Creating an instance of Geocoder class
            Geocoder geocoder = new Geocoder(getBaseContext());
            List<Address> addresses = null;

            try {
                // Getting a maximum of 3 Address that matches the input text
                addresses = geocoder.getFromLocationName(locationName[0], 3);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {

            if(addresses==null || addresses.size()==0){
                Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
            }

            // Clears all the existing markers on the map
            mMap.clear();

            // Adding Markers on Google Map for each matching address
            for(int i=0;i<addresses.size();i++){

                Address address = (Address) addresses.get(i);

                // Creating an instance of GeoPoint, to display in Google Map
                latLng = new LatLng(address.getLatitude(), address.getLongitude());

                String addressText = String.format("%s, %s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getCountryName());

                markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(addressText);

                mMap.addMarker(markerOptions);

                // Locate the first location
                if(i==0)
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        }
    }



}
