package com.example.jeffrey.mocklocation2;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends Activity{

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.

    TextView activateMap,copyButton,returnMapButton;
    TextView latHold,lonHold,addressHold,previousLocation,logOut;
    String latitud,longitud;
    TinyDB tiny;
    ArrayList<String> previousLocList;
    Object br=System.getProperty ("line.separator");
    String textFormat;
    Dialog dialog;
    private final String TAG = "tag";
    float savedZoom;
    Intent loginCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_layout);

        tiny = new TinyDB(this);

        loginCheck = new Intent(MainActivity.this,LoginActivity.class);

        String loginString = tiny.getString("MockLocation_login");

        if(loginString.substring(loginString.lastIndexOf(":")+1).equals("42")){
        }else{
            finish();
            startActivity(loginCheck);
        }

        //create google ad
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("154DEA95008A9D569F652F93A4263F87").build();
        AdView adView = (AdView) this.findViewById(R.id.adView);
        adView.loadAd(adRequest);

        //initialize tinyDB for pulling data from sharedpreferences


        //initialize previous location list

        previousLocList = new ArrayList<String>();

        //Initialize buttons/textviews

        latHold = (TextView)findViewById(R.id.latText);
        lonHold = (TextView)findViewById(R.id.lonText);
        addressHold = (TextView)findViewById(R.id.addressText);
        previousLocation=(TextView)findViewById(R.id.previousLocation);

        activateMap = (TextView) findViewById(R.id.button);
        copyButton = (TextView) findViewById(R.id.copyButton);
        returnMapButton=(TextView)findViewById(R.id.returnMapButton);
        logOut = (TextView)findViewById(R.id.logOut);


        if(tiny.getListString("MockLocation_prevLocations").size()==0){
            previousLocList.add("");
            previousLocList.add("");
            previousLocList.add("");
            previousLocList.add("");
            previousLocList.add("");
        }else{
            previousLocList=tiny.getListString("MockLocation_prevLocations");
        }

        try{
            stringSplit(previousLocList.get(0));
        }catch (Exception e){}

        prevUpdate();

        previousLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.previous_location_layout);
                dialog.setTitle("Select location");

                TextView location1 = (TextView) dialog.findViewById(R.id.location1);
                TextView location2 = (TextView) dialog.findViewById(R.id.location2);
                TextView location3 = (TextView) dialog.findViewById(R.id.location3);
                TextView location4 = (TextView) dialog.findViewById(R.id.location4);
                TextView location5 = (TextView) dialog.findViewById(R.id.location5);
                TextView exit = (TextView) dialog.findViewById(R.id.exit);
                exit.setText("Exit");

                try {
                    location1.setText(previousLocList.get(0).substring(previousLocList.get(0).lastIndexOf(":") + 2));
                    location2.setText(previousLocList.get(1).substring(previousLocList.get(1).lastIndexOf(":") + 2));
                    location3.setText(previousLocList.get(2).substring(previousLocList.get(2).lastIndexOf(":") + 2));
                    location4.setText(previousLocList.get(3).substring(previousLocList.get(3).lastIndexOf(":") + 2));
                    location5.setText(previousLocList.get(4).substring(previousLocList.get(4).lastIndexOf(":") + 2));
                }catch (Exception e){}

                location1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stringSplit(previousLocList.get(0));
                        dialog.dismiss();
                    }
                });

                location2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stringSplit(previousLocList.get(1));
                        dialog.dismiss();

                    }
                });

                location3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stringSplit(previousLocList.get(2));
                        dialog.dismiss();

                    }
                });

                location4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stringSplit(previousLocList.get(3));
                        dialog.dismiss();

                    }
                });

                location5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stringSplit(previousLocList.get(4));
                        dialog.dismiss();

                    }
                });


                exit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });


        //mSharedPreferences = getSharedPreferences("superhold", 0);

        //Button launches MapsActivity, which displays the interactive map

        activateMap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);

                latitud=latHold.getText().toString().replaceAll("[^\\d.-]", "");
                longitud=lonHold.getText().toString().replaceAll("[^\\d.-]", "");

                if(latitud!=null & longitud!=null){
                    intent.putExtra("savedLat",latitud);
                    intent.putExtra("savedLon",longitud);
                }

                if(savedZoom!=0.0f){
                    intent.putExtra("savedZoom",savedZoom);
                }

                startActivityForResult(intent, 13);
            }
        });

        copyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", addressHold.getText().toString());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getApplicationContext(), "Copied address to clipboard!", Toast.LENGTH_SHORT).show();
            }
        });

        returnMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);

                latitud=latHold.getText().toString().replaceAll("[^\\d.-]", "");
                longitud=lonHold.getText().toString().replaceAll("[^\\d.-]", "");

                if(latitud!=null & longitud!=null){
                    intent.putExtra("savedLat",latitud);
                    intent.putExtra("savedLon",longitud);
                }

                if(savedZoom!=0.0f){
                    intent.putExtra("savedZoom",savedZoom);
                }

                startActivityForResult(intent, 12);
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i  = new Intent(MainActivity.this,LoginActivity.class);
                i.putExtra("MockLocation_logout","logoutTrue");

                tiny.putString("MockLocation_login","asdfasdf");

                startActivity(i);
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //if user placed a marker and selected get location, deliver latlng and address information

        if (resultCode == RESULT_OK) {

           latitud = data.getExtras().getString("lat");
           longitud = data.getExtras().getString("long");
           savedZoom = data.getExtras().getFloat("savedZoom");

            Log.i(TAG, savedZoom+""+ " hhh");

           latHold.setText("Lat: " + System.getProperty("line.separator") + latitud);
           lonHold.setText("Lon: " + System.getProperty("line.separator") + longitud);

           double lati = Double.parseDouble(data.getExtras().getString("lat"));
           double longi = Double.parseDouble(data.getExtras().getString("long"));

           Geocoder geocoder;
           List<Address> addresses;
           geocoder = new Geocoder(this, Locale.getDefault());

           try {
               addresses = geocoder.getFromLocation(lati, longi, 1);


               String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
               String city = addresses.get(0).getLocality();
               String state = addresses.get(0).getAdminArea();
               String country = addresses.get(0).getCountryName();

               StringBuilder sumAddress = new StringBuilder();

               if (address!=null){
                   sumAddress.append(address+", ");
               }
               if (city!=null){
                   sumAddress.append(city+", "+ System.getProperty ("line.separator"));
               }
               if (state!=null) {
                   sumAddress.append(state + ", " + System.getProperty ("line.separator"));
               }
               if (country!=null){
                   sumAddress.append(country);
               }

               if(sumAddress.length()==0){
                   addressHold.setText("No Address Found.");
               }

               addressHold.setText("Address:" + System.getProperty("line.separator") + sumAddress);

           }
           catch (Exception e){Log.i(TAG, "hhh "+e.getMessage());}

            previousLocList.add(0, latHold.getText().toString() + lonHold.getText().toString() + addressHold.getText().toString());
            previousLocList.remove(previousLocList.size() - 1);

            tiny.putListString("MockLocation_prevLocations", previousLocList);

            prevUpdate();

           super.onActivityResult(requestCode, resultCode, data);

        }
    }

    public void prevUpdate(){
        textFormat="";

        for(int counter =0; counter<previousLocList.size();counter++){
            textFormat = textFormat + previousLocList.get(counter)+ System.getProperty ("line.separator");
        }

    }

    public void stringSplit(String hold){
        String[] seperated = hold.split(":");

        latHold.setText("Lat: "+ seperated[1]);
        lonHold.setText("Lon: "+ seperated[2]);
        addressHold.setText("Address: "+seperated[3]);
    }



}
