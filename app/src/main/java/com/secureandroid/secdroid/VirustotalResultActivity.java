package com.secureandroid.secdroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.secureandroid.secdroid.Adapter.VtAdapter;
import com.secureandroid.secdroid.Model.avResults;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class VirustotalResultActivity extends AppCompatActivity {

    public BottomNavigationView bottomNavigationView;
    private TextView fileNameHolder;
    private TextView positivesHolder;
    private RequestQueue queue;
    private RecyclerView vtRecyclerView;
    private RecyclerView.Adapter vtAdapter;
    public List<avResults> listofav= new ArrayList<>();

    String antiVirusName;
    String virusName;
    Drawable icon;
    Boolean state;
    int positives;

    String resourceId;

    String fileName;
    String apikey;
    String url;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virustotal_result);

        setBottomNavigationView();

        vtRecyclerView = (RecyclerView) findViewById(R.id.vRecyclerViewId);
        vtRecyclerView.setHasFixedSize(true);
        vtRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        fileNameHolder = (TextView) findViewById(R.id.fileNameHolderId);
        positivesHolder = (TextView) findViewById(R.id.positivesHolderId);

        Bundle vExtras = getIntent().getExtras();

        if (vExtras != null)
        {
            resourceId =vExtras.getString("resourceKey");
            fileName = vExtras.getString("nameOfFile");

        }

        getScanResuts(resourceId,fileName);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void getScanResuts(String resourceId, final String fileName) {

        SharedPreferences sharedPreferences = getSharedPreferences("keyFile", MODE_PRIVATE);

        if (sharedPreferences.contains("apiKey"))
        {
            apikey = sharedPreferences.getString("apiKey", "Wrong apikey");
        }
        url = "https://www.virustotal.com/vtapi/v2/file/report?apikey="+apikey+"&resource="+resourceId;

        queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    int responsecode = response.getInt("response_code");

                    if (responsecode==1)
                    {
                        positives = response.getInt("positives");
                        sendPositivesCount(positives);

                        fileNameHolder.setText(fileName);

                        JSONObject antivirusresultsall = response.getJSONObject("scans");

                        JSONArray antivirusnames =antivirusresultsall.names();

                        for (int i=0; i<antivirusnames.length(); i++)
                        {
                            antiVirusName = antivirusnames.getString(i);

                            JSONObject resultset = antivirusresultsall.getJSONObject(antiVirusName);

                            state = resultset.getBoolean("detected");

                            virusName = resultset.getString("result");

                            if (!state){
                                icon = getDrawable(R.drawable.ic_baseline_check_circle_24);
                            }
                            else {
                                icon = getDrawable(R.drawable.ic_baseline_error_24
                                );
                            }

                            avResults item = new avResults(
                                    antiVirusName,
                                    virusName,
                                    icon,
                                    state
                            );

                            listofav.add(item);

                        }
                        onSuccess(listofav);

                    }
                    else if (responsecode==0)
                    {
                        String titleText = "Upload Failed";
                        String messageText = "Issue may be happened in uploading file, Please try again";
                        createDialog(titleText,messageText);
                    }
                    else if (responsecode==-2)
                    {
                        String titleText = "Attention";
                        String messageText = "File queued try after some time";
                        createDialog(titleText,messageText);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(VirustotalResultActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("er message", error.toString());
                if (error.toString().equals("com.android.volley.AuthFailureError"))
                {
                    String title = "Incrorrect api key";
                    String message ="Please enter the correct api key by reinstalling the app";
                    createDialog(title,message);
                }

            }
        });
        queue.add(jsonObjectRequest);

    }

    private void createDialog(String title,String description)
    {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(description)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(VirustotalResultActivity.this,ScanFilesActivity.class);
                        startActivity(intent);
                    }
                })
                .setCancelable(false)
                .create()
                .show();;
    }

    private void sendPositivesCount(int positives) {
        String a = String.valueOf(positives);
        positivesHolder.setText(a);
    }

    private void onSuccess(List<avResults> listofav) {
        List<avResults> detectedList = new ArrayList<>();
        List<avResults> unDetectedList = new ArrayList<>();
        for (avResults data : listofav)
        {
            if (data.getState())
            {
                detectedList.add(data);
            }
            if(!data.getState()) {
                unDetectedList.add(data);
            }
        }

        List<avResults> finalList = new ArrayList(detectedList);
        finalList.addAll(unDetectedList);

        vtAdapter = new VtAdapter(this, finalList);
        vtRecyclerView.setAdapter(vtAdapter);
    }


    public void setBottomNavigationView()
    {
        bottomNavigationView = findViewById(R.id.vBottomNavigationBarId);

        bottomNavigationView.setSelectedItemId(R.id.scanButtonId);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.homeButtonId:
                        startActivity( new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.appsButtonId:
                        startActivity(new Intent(getApplicationContext(),ListAppsActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.scanButtonId:
                        return true;
                    case R.id.trafficButtonId:
                        startActivity(new Intent(getApplicationContext(),TrafficViewActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

    }
}