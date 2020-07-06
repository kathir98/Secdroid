package com.secureandroid.secdroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.annotation.Nullable;

import android.net.VpnService;
import android.util.Log;
import android.widget.Button;


import edu.uci.calit2.antmonitor.lib.AntMonitorActivity;
import edu.uci.calit2.antmonitor.lib.logging.PacketProcessor;
import edu.uci.calit2.antmonitor.lib.vpn.OutPacketFilter;
import edu.uci.calit2.antmonitor.lib.vpn.VpnController;
import edu.uci.calit2.antmonitor.lib.vpn.VpnState;

public class MainActivity extends AppCompatActivity implements AntMonitorActivity {

    public BottomNavigationView bottomNavigationView;
    public TextView statusHolder;
    public SwitchCompat vpnControlButton;
    public static final String USERID = "Android user";
    public VpnController vpnController;
    private Button connectButton;
    private Button disConnectButton;
    public Button nextButton;
    public ExamplePacketConsumer outPacketConsumer;

    private SharedPreferences sharedPreferences;
    public static final String ex ="Switch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        setBottomNavigationView();
        vpnControlButton = findViewById(R.id.vpnControlButtonId);
        statusHolder = (TextView) findViewById(R.id.statusHolderId);

        sharedPreferences = getSharedPreferences(" ",MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        vpnControlButton.setChecked(sharedPreferences.getBoolean(ex, false));


        vpnControlButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)
                {
                    Toast.makeText(MainActivity.this, "VPN ON", Toast.LENGTH_LONG).show();
                    Intent intent = VpnService.prepare(getApplicationContext());

                    if (intent!= null)
                    {
                        // Ask user for VPN rights. If they are granted,
                        // onActivityResult will be called with RESULT_OK
                        startActivityForResult(intent, 0);
                    }
                    else
                    {
                        // VPN rights were granted before, attempt a connection
                        onActivityResult(0, RESULT_OK, null);
                    }

                    editor.putBoolean(ex, true);
                }
                else{
                    Toast.makeText(MainActivity.this, "VPN OFF", Toast.LENGTH_LONG).show();
                    vpnController.disconnect();

                    editor.putBoolean(ex, false);

                }

                editor.commit();
            }
        });

        vpnController = vpnController.getInstance(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        // bind to our service here. You must bind before you connect to VPN
        vpnController.bind();
        // Note that we are not yet bound. We will be bound when we receive our first update about
        // the VPN state in onVpnStateChanged(), at which point we can enable the connect button
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service as we no longer need to receive VPN status updates and we
        // will no longer be controlling the VPN (no connect/disconnect)
        vpnController.unbind();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check if the user granted us rights to VPN
        if (resultCode == RESULT_OK)
        {

            // Prepare Packet Filter
            //OutPacketFilter outPacketFilter = new ExampleOutPacketFilter(this);

            // If so, we can attempt a connection

            // Prepare packet consumers
            ExamplePacketConsumer inPacketConsumer = new ExamplePacketConsumer(this, PacketProcessor.TrafficType.INCOMING_PACKETS,USERID);
            outPacketConsumer = new ExamplePacketConsumer(this, PacketProcessor.TrafficType.OUTGOING_PACKETS,USERID);
            //ExampleOutPacketFilter exampleOutPacketFilter = new ExampleOutPacketFilter(this, PacketProcessor.TrafficType.OUTGOING_PACKETS);
            vpnController.connect(null, null, null, outPacketConsumer);

        }
    }

    public void setBottomNavigationView()
    {
        bottomNavigationView = findViewById(R.id.hBottomNavigationBarId);

        bottomNavigationView.setSelectedItemId(R.id.homeButtonId);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.homeButtonId:
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
                        startActivity(new Intent(getApplicationContext(),TrafficViewActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

    }

    @Override
    public void onVpnStateChanged(VpnState vpnState) {

        Log.d("STATE", String.valueOf(vpnState));

        if (vpnState==VpnState.CONNECTED)
        {
            statusHolder.setText("CONNECTED");
        }
        else if(vpnState==VpnState.CONNECTING)
        {
            statusHolder.setText("CONNECTING");
        }
        else {
            statusHolder.setText("DISCONNECTED");
            vpnControlButton.setChecked(false);
        }
    }
}