package com.secureandroid.secdroid;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;


public class IpLookupActivity extends AppCompatActivity {

    public BottomNavigationView bottomNavigationView;

    private TextView ipAdd;
    private TextView country;
    private TextView region;
    private TextView city;
    private TextView district;
    private TextView location;
    private TextView reverseDomain;
    private MapView mapView;
    private MapController mapController;
    private int STORAGE_PERMISSION_CODE =1;


    private Bundle extras;
    public String ipAddress;
    public String url;
    public String status;
    public double lat;
    public double lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip_lookup);

        setBottomNavigationView();

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        ipAdd = (TextView) findViewById(R.id.ipAddressID);
        country = (TextView) findViewById(R.id.countryId);
        region =(TextView) findViewById(R.id.regionId);
        city =(TextView) findViewById(R.id.cityId);
        district =(TextView) findViewById(R.id.districtId);
        location =(TextView) findViewById(R.id.locationId);
        reverseDomain =(TextView) findViewById(R.id.domainId);

        extras=getIntent().getExtras();
        if (extras != null)
        {
            ipAddress = extras.getString("IP");
            ipAdd.setText(ipAddress);
        }

        url ="http://ip-api.com/json/"+ipAddress+"?fields=status,country,regionName,city,district,lat,lon,reverse";

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Response", response.toString());
                try {
                    status = response.getString("status");
                    if (status.equals("success"))
                    {
                        country.setText(response.getString("country"));
                        region.setText(response.getString("regionName"));
                        city.setText(response.getString("city"));
                        district.setText(response.getString("district"));
                        location.setText(response.getString("lat")+" , "+response.getString("lon"));
                        reverseDomain.setText(response.getString("reverse"));

                        lat = response.getDouble("lat");
                        lon = response.getDouble("lon");
                        if (response.getString("country").equals(""))
                        {
                            country.setText("Unknown");
                        }
                        if (response.getString("regionName").equals(""))
                        {
                            region.setText("Unknown");
                        }
                        if (response.getString("city").equals(""))
                        {
                            city.setText("Unknown");
                        }
                        if (response.getString("district").equals(""))
                        {
                            district.setText("Unknown");
                        }
                        if (response.get("lat").equals("") && response.getString("lon").equals(""))
                        {
                            location.setText("Unknown");
                        }
                        else
                        {
                            showMap();
                        }
                        if (response.getString("reverse").equals(""))
                        {
                            reverseDomain.setText("Unknown");
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error", error.toString());
            }
        });
        requestQueue.add(jsonObjectRequest);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void showMap() {

        //Get storage permission to store tile cache for osm
        if (!(ContextCompat.checkSelfPermission(IpLookupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)){
            requestStoragePermission();
        }

        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mapView.setMultiTouchControls(true);
        mapView.setBuiltInZoomControls(true);
        mapController = (MapController) mapView.getController();
        mapController.setZoom(6);
        GeoPoint gpt = new GeoPoint(lat, lon);
        Marker locationPointer = new Marker(mapView);
        locationPointer.setPosition(gpt);
        locationPointer.setIcon(getDrawable(R.drawable.ic_baseline_location_on_24));
        locationPointer.setTitle("Server location");
        mapView.getOverlays().add(locationPointer);
        mapController.setCenter(gpt);

    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(IpLookupActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED )
            {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void setBottomNavigationView()
    {
        bottomNavigationView = findViewById(R.id.iBottomNavigationBarId);

        bottomNavigationView.setSelectedItemId(R.id.trafficButtonId);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.homeButtonId:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.appsButtonId:
                        startActivity( new Intent(getApplicationContext(),ListAppsActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.scanButtonId:
                        startActivity(new Intent(getApplicationContext(),ScanFilesActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.trafficButtonId:
                        return true;
                }
                return false;
            }
        });

    }
}
