package com.example.tremorapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.StrictMode;

import android.os.Bundle;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


public class MainActivity extends AppCompatActivity {

    private Button button; Boolean l1,l2,l3,turn;
    static WifiManager wifiManager;
    Context context;
    WifiConfiguration conf;
    public static String networkSSID="Tinuzzz";
    public static String networkPass="yashodha"; byte[] buf = new byte[1024];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        l1=l2=l3=turn=true;
        context=this;
        button=(Button)(findViewById(R.id.button));
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiManager.setWifiEnabled(true);
            Toast.makeText(MainActivity.this,"Wifi Enabled",Toast.LENGTH_SHORT).show();
            }
        });

        // this is for thread policy the AOS doesn't allow to transfer data using wifi module so we take the permission
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);



        Button phistory=(Button)findViewById(R.id.phistory);
        phistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this,
                        patientHistory.class);
                startActivity(myIntent);
            }
        });

        Button srt=(Button)findViewById(R.id.srt);
        srt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this,
                        BluetoothConnection.class);
                startActivity(myIntent);
            }
        });
    }

    // conected with a wifi button.. it connect to esp module when it is pressed
    //remember the nework ssid and pasword needs to be the same as given here
    //other it won't connect
    public void wifi_connect(View v) {


        wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);

        if (turn) {

            turnOnOffWifi(context, turn);
            turn = false;
            Toast.makeText(getApplicationContext(), "turning on...", Toast.LENGTH_SHORT).show();

            //wifi configuration .. all the code below is to explain the wifi configuration of which type the wifi is
            //if it is a WPA-PSK protocol then it would work

            conf = new WifiConfiguration();
            conf.SSID = "\"" + networkSSID + "\"";
            conf.preSharedKey = "\"" + networkPass + "\"yashodha";
            conf.status = WifiConfiguration.Status.ENABLED;
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            int netid = wifiManager.addNetwork(conf);
            wifiManager.disconnect();
            wifiManager.enableNetwork(netid, true);
            wifiManager.reconnect();
        }
    else
     {
        turnOnOffWifi(context, turn);
        turn = true;
        Toast.makeText(getApplicationContext(), "turning off...", Toast.LENGTH_SHORT).show();

    }
    }
    public static void turnOnOffWifi(Context context, boolean isTurnToOn) {
        wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(isTurnToOn);
    }

    //used to send data to esp module
    public class Client implements Runnable{
        private final static String SERVER_ADDRESS = "192.168.4.1";//public ip of my server
        private final static int SERVER_PORT = 8888;


        public void run(){

            InetAddress serverAddr;
            DatagramPacket packet;
            DatagramSocket socket;


            try {
                serverAddr = InetAddress.getByName(SERVER_ADDRESS);
                socket = new DatagramSocket(); //DataGram socket is created
                packet = new DatagramPacket(buf, buf.length, serverAddr, SERVER_PORT);//Data is loaded with information where to send on address and port number
                socket.send(packet);//Data is send in the form of packets
                socket.close();//Needs to close the socket before other operation... its a good programming
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}

