package com.secureandroid.secdroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.os.Build;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.secureandroid.secdroid.Adapter.TrafficAdapter;
import com.secureandroid.secdroid.Model.TrafficList;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.*;

import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;


public class TrafficViewActivity extends AppCompatActivity {

    public BottomNavigationView bottomNavigationView;

    private RecyclerView tRecyclerView;
    private RecyclerView.Adapter tAdapter;
    private List<TrafficList> trafficLists = new ArrayList<>();
    private ExamplePacketConsumer obj;
    private Switch switchButton;
    private TextView message;
    public FloatingActionButton downFabButton;

    static public List app = Collections.synchronizedList(new ArrayList());
    static public List icon =Collections.synchronizedList(new ArrayList());
    static public List ipAd = Collections.synchronizedList(new ArrayList());
    static public List portNo = Collections.synchronizedList(new ArrayList());
    static public List protocol = Collections.synchronizedList(new ArrayList());
    static public List timeStamp = Collections.synchronizedList(new ArrayList());

    public ListIterator appList;
    public ListIterator iconList;
    public ListIterator ipList;
    public ListIterator portList;
    public ListIterator protocolList;
    public ListIterator timeList;

    public ListIterator trafficListIterator;

    public boolean chkButt = false;
    public int position;
    public boolean isCalled = false;
    public int size;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    static void gettingData(String appName,Drawable iconLogo, String ip, String prt, String proto,String time){

        app.add(appName);
        icon.add(iconLogo);
        ipAd.add(ip);
        portNo.add(prt);
        protocol.add(proto);
        timeStamp.add(time);
    }

    @SuppressLint("RestrictedApi")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_view);

        switchButton = (Switch) findViewById(R.id.switchButtonId);
        message = (TextView) findViewById(R.id.messageId);
        downFabButton = (FloatingActionButton) findViewById(R.id.downFabId);

        downFabButton.setVisibility(View.INVISIBLE);

        downFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tRecyclerView.smoothScrollToPosition(position);
            }
        });

        message.setVisibility(View.VISIBLE);

        tRecyclerView = (RecyclerView) findViewById(R.id.tRecyclerViewId);
        tRecyclerView.setHasFixedSize(true);
        tRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tRecyclerView.setVerticalScrollBarEnabled(true);

        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                if (isChecked) {
                    chkButt = true;
                    setUpUI();
                    downFabButton.setVisibility(View.VISIBLE);
                }
                if(!isChecked && chkButt) {
                    deleteList();
                    tRecyclerView.setVisibility(View.INVISIBLE);
                    message.setVisibility(View.VISIBLE);
                    downFabButton.setVisibility(View.INVISIBLE);

                }

                // add remaining traffic item to the recycleView when user scroll to end of the item list
                tRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);

                        if (!recyclerView.canScrollVertically(1) && isChecked){
                            size = position;
                            isCalled = true;
                            setUpUI();
                        }

                    }
                });
            }
        });


        setBottomNavigationView();
    }

    private void deleteList() {

        while(appList.hasNext() && iconList.hasNext() && ipList.hasNext() && portList.hasNext() && protocolList.hasNext() && timeList.hasNext())
        {
            appList.remove();
            iconList.remove();
            ipList.remove();
            portList.remove();
            protocolList.remove();
            timeList.remove();
        }

    }

    public void setUpUI(){

        tRecyclerView.setVisibility(View.VISIBLE);

        iterateList();
        while(appList.hasNext() && iconList.hasNext() && ipList.hasNext() && portList.hasNext() && protocolList.hasNext() && timeList.hasNext())
        {

            TrafficList item = new TrafficList(appList.next().toString(), (Drawable)iconList.next(), ipList.next().toString(), portList.next().toString(), protocolList.next().toString(), timeList.next().toString())  ;

            trafficLists.add(item);

            tAdapter = new TrafficAdapter(TrafficViewActivity.this, trafficLists);
            tRecyclerView.setAdapter(tAdapter);
            message.setVisibility(View.INVISIBLE);

            appList.remove();
            iconList.remove();
            ipList.remove();
            portList.remove();
            protocolList.remove();
            timeList.remove();

            if(isCalled){
                tRecyclerView.smoothScrollToPosition(size);
            }
            isCalled = false;
            position = trafficLists.size();

        }
    }

    public void iterateList()
    {
        appList = app.listIterator();
        iconList = icon.listIterator();
        ipList = ipAd.listIterator();
        portList = portNo.listIterator();
        protocolList = protocol.listIterator();
        timeList = timeStamp.listIterator();
    }



    public void setBottomNavigationView()
    {
        bottomNavigationView = findViewById(R.id.tBottomNavigationBarId);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        trafficLists.clear();
        app.clear();
        icon.clear();
        ipAd.clear();
        portNo.clear();
        protocol.clear();
        timeStamp.clear();
    }
}