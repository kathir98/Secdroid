package com.secureandroid.secdroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class AppDetialsActivity extends AppCompatActivity {

    public BottomNavigationView bottomNavigationView;

    private TextView dlappName;
    private TextView dlappPermissions;
    private ImageView applicationIcon;
    private Button settingsAppButton;
    public Button launchAppButton;
    private Bundle extras;
    public List permissionList = new ArrayList();

    public String packgName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detials);

        setBottomNavigationView();

        dlappName = (TextView) findViewById(R.id.dlAppNameId);
        dlappPermissions = (TextView) findViewById(R.id.dlAppPermissionId);
        dlappPermissions.setMovementMethod(new ScrollingMovementMethod());
        applicationIcon = (ImageView) findViewById(R.id.applicationIconId);

        settingsAppButton = (Button) findViewById(R.id.settingsAppButtonId);
        launchAppButton = (Button) findViewById(R.id.launchAppButtonId);

        settingsAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", packgName, null);
                intent.setData(uri);
                if (intent!=null){
                    getApplicationContext().startActivity(intent);
                }
            }
        });

        launchAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packgName);
                if (launchIntent!=null)
                {
                    startActivity(launchIntent);
                }
                else{
                    Toast.makeText(AppDetialsActivity.this, "Unable to launch system services", Toast.LENGTH_LONG).show();
                }
            }
        });

        extras=getIntent().getExtras();

        if(extras!=null)
        {
            dlappName.setText(extras.getString("appname"));
            packgName = extras.getString("pkgName");

            String ePermission = extras.getString("Permission");
            String[] permission = ePermission.split(",");
            for (int i = 0; i<permission.length; i++)
            {
                String appPermission = permission[i];
                appPermission = appPermission.replace("_", " ");
                int lastPosition = appPermission.lastIndexOf(".");

                char arr[] = appPermission.toCharArray();
                String fPermission = "";

                for (int j = lastPosition+1; j < arr.length; j++)
                {
                    fPermission += arr[j] ;

                }
                fPermission = fPermission.substring(0, 1).toUpperCase() + fPermission.substring(1).toLowerCase();

                permissionList.add(fPermission+ "\n");

            }
            String permissions = permissionList.toString();
            permissions = permissions.replace("]", "");
            permissions = permissions.replace("[", " ");
            permissions = permissions.replace(",", "");
            dlappPermissions.setText(permissions);

            byte[] bytes = extras.getByteArray("appIcon");
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            applicationIcon.setImageBitmap(bitmap);
        }
    }

    public void setBottomNavigationView()
    {
        bottomNavigationView = findViewById(R.id.adBottomNavigationBarId);

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