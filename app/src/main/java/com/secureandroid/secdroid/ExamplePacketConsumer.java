package com.secureandroid.secdroid;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.util.Date;

import edu.uci.calit2.antmonitor.lib.logging.ConnectionValue;
import edu.uci.calit2.antmonitor.lib.logging.PacketConsumer;
import edu.uci.calit2.antmonitor.lib.logging.PacketLogQueue;
import edu.uci.calit2.antmonitor.lib.logging.PacketProcessor;
import edu.uci.calit2.antmonitor.lib.util.PacketDumpInfo;

import static edu.uci.calit2.antmonitor.lib.util.IpDatagram.readDestinationIP;
import static edu.uci.calit2.antmonitor.lib.util.IpDatagram.readDestinationPort;
import static edu.uci.calit2.antmonitor.lib.util.IpDatagram.readProtocol;

public class ExamplePacketConsumer extends PacketConsumer{

    public Context mContext;
    public String applicationName;
    public Drawable applicationIcon;
    public String destinationIp;
    public String destinationPort;
    public String protocol;
    public String getTimeStamps;
    public String protocolNumber;
    public String time;
    public String packageName;



    public ExamplePacketConsumer(Context context, PacketProcessor.TrafficType trafficType, String userID) {
        super(context, trafficType, userID);
        mContext = context;

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void consumePacket(PacketDumpInfo packetDumpInfo) {
        super.consumePacket(packetDumpInfo);

        PacketLogQueue packetLogQueue = new PacketLogQueue();
        packetLogQueue.put(packetDumpInfo);

        ConnectionValue connectionValue =mapPacketToApp(packetDumpInfo);
        String appName = connectionValue.getAppName();


        packageName = connectionValue.getAppName();
        destinationIp = readDestinationIP(packetDumpInfo.getDump());
        destinationPort = String.valueOf(readDestinationPort(packetDumpInfo.getDump()));
        protocolNumber = String.valueOf(readProtocol(packetDumpInfo.getDump()));
        if (protocolNumber.equals("6"))
        {
            protocol = "TCP";
        }
        else if (protocolNumber.equals("17"))
        {
            protocol = "UDP";
        }
        else{
            protocol="UNKNOWN";
        }


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date(packetDumpInfo.getTimestamp());

        time = simpleDateFormat.format(date);


        if (!destinationPort.equals("53")) {
            PackageManager packageManager = mContext.getPackageManager();
            ApplicationInfo applicationInfo;
            try {
                applicationInfo = packageManager.getApplicationInfo(packageName, 0);
                if (applicationInfo != null) {
                    applicationName = (String) packageManager.getApplicationLabel(applicationInfo);
                    applicationIcon = packageManager.getApplicationIcon(packageName);
                }
            } catch (PackageManager.NameNotFoundException e) {
                applicationName = "Unknown";
                applicationIcon = mContext.getDrawable(R.drawable.ic_launcher_background);
            }

            TrafficViewActivity.gettingData(applicationName, applicationIcon, destinationIp, destinationPort, protocol, time);
        }

    }

}
