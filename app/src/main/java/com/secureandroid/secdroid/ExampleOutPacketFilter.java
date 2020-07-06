/*
This class is never called and not initialised, TO implement the filter based on packets or  port number or protocol,
call this in main activity and passing instance as parameter to vpncontroller instances.

Add a block button and get the ip or protocol or portno and pass it to this class. Use d like sqlite to save the blocked ip
or portno or protocol in the db and filter it at every time of vpn connection.
 */


package com.secureandroid.secdroid;

import android.content.Context;
import android.util.Log;

import java.nio.ByteBuffer;

import edu.uci.calit2.antmonitor.lib.logging.ConnectionValue;
import edu.uci.calit2.antmonitor.lib.logging.PacketAnnotation;
import edu.uci.calit2.antmonitor.lib.logging.PacketProcessor;
import edu.uci.calit2.antmonitor.lib.util.IpDatagram;
import edu.uci.calit2.antmonitor.lib.util.*;
import edu.uci.calit2.antmonitor.lib.util.TCPPacket;
import edu.uci.calit2.antmonitor.lib.util.UDPPacket;
import edu.uci.calit2.antmonitor.lib.vpn.OutPacketFilter;
import edu.uci.calit2.antmonitor.lib.vpn.PacketFilter;



public class ExampleOutPacketFilter extends OutPacketFilter {
    private final PacketAnnotation BLOCK = new PacketAnnotation(false);
    public ExampleOutPacketFilter(Context context) {
        super(context);
    }

    @Override
    public PacketAnnotation acceptIPDatagram(ByteBuffer packet) {
        IpDatagram ipDatagram =new IpDatagram(packet);
        //Log.d("length", String.valueOf(ipDatagram.getDatagramLength()));

        /*ConnectionValue connectionValue = mapDatagramToApp(packet);
        return blockNw(connectionValue); */

        return blockIp(ipDatagram);
    }

    // BLOCKS SELECTED IP ADDRESS (SUSPCETABLE IP)
    public PacketAnnotation blockIp(IpDatagram ipDatagram) {
        //String appName = connectionValue.getAppName();
        //InetAddress ip = ipDatagram.getDestinationIP();
        String ip = ipDatagram.getDestinationIP().toString();

        //String protocol = String.valueOf(ipDatagram.getProtocol());


        if (ip.equals("/111.118.214.164"))
        {
            //Log.d("IP ADDRESS", ip+ " is blocked");
            return BLOCK;
        }
        else {
            return DEFAULT_ANNOTATION;
        }
    }

    // BLOCKS THE NETWORK ACCESS TO WHOLE APP
    public PacketAnnotation blockNw(ConnectionValue connectionValue){

        String appName = connectionValue.getAppName();
        if (appName.equals("com.android.chrome"))
        {
            Log.d("APP", appName+"  BLOCKED");
            return BLOCK;
        }
        else {
            return DEFAULT_ANNOTATION;
        }

    }

}

