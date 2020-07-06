package com.secureandroid.secdroid;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.secureandroid.secdroid.Adapter.MyAdapter;
import com.secureandroid.secdroid.Model.appdetials;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.view.MenuItem;

public class ListAppsActivity extends AppCompatActivity {

    public BottomNavigationView bottomNavigationView;


    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<appdetials> listofapps;
    String appPermissionNo;
    String appPermission;
    String applicationName;
    Drawable applicationLogo;
    String pkgName;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_apps);

        setBottomNavigationView();

        recyclerView = (RecyclerView) findViewById(R.id.aRecyclerViewId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listofapps = new ArrayList<>();
        getAppinfo();

        adapter = new MyAdapter(this, listofapps);
        recyclerView.setAdapter(adapter);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void getAppinfo() {
        PackageManager packageManager = getPackageManager();
        List<ApplicationInfo> packagesName = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        for (int j=0;j<packagesName.size();j++) {
            applicationName = String.valueOf(packagesName.get(j).loadLabel(getPackageManager()));
            applicationLogo = packagesName.get(j).loadIcon(getPackageManager());
            pkgName = packagesName.get(j).packageName;

            // TO GET THE PERMISSION LIST
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(packagesName.get(j).packageName, PackageManager.GET_PERMISSIONS);
                String[] requestedPermission = packageInfo.requestedPermissions;
                if (requestedPermission!=null)
                {
                    int permissionNo = requestedPermission.length;
                    appPermissionNo = permissionNo+" Permissions";
                    appPermission = Arrays.toString(requestedPermission);

                }
            }
            catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            appdetials item = new appdetials(
                    applicationName,
                    appPermissionNo,
                    applicationLogo,
                    appPermission,
                    pkgName

            );
            listofapps.add(item);
        }
    }

    public void setBottomNavigationView()
    {
        bottomNavigationView = findViewById(R.id.aBottomNavigationBarId);

        bottomNavigationView.setSelectedItemId(R.id.appsButtonId);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.homeButtonId:
                        startActivity( new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.appsButtonId:
                        return true;
                    case R.id.scanButtonId:
                        startActivity(new Intent(getApplicationContext(),ScanFilesActivity.class));
                        overridePendingTransition(0, 0);
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