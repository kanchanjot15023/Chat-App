package com.assignment4.chatapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;

public class ChatService extends Service {

    public static String QUEUE_NAME;
    public static String EXCHANGE_NAME;
    private DBHelper dbHelper;
    private MessageConsumer mConsumer;

    private BroadcastReceiver serviceReceiver;
    private static final String ACTION_STRING_SERVICE = "ToService";
    private static final String ACTION_STRING_ACTIVITY = "ToActivity";
    private int flagActivityActive;
    private String activeUserEmail;
    private String activeGroup;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    private void Notify(Context context,String notificationTitle, String notificationMessage){
        Intent notificationIntent = new Intent(context, Login.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(Login.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher1))
                .setContentTitle(notificationTitle)
                .setSmallIcon(R.mipmap.ic_launcher1)
                .setContentText(notificationMessage);
        builder.setAutoCancel(true);
        builder.setContentIntent(notificationPendingIntent);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(generateRandom(), builder.build());

    }
    public int generateRandom(){
        Random random = new Random();
        return random.nextInt(9999 - 1000) + 1000;
    }



    public static  boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) ChatApplication.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }




    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        flagActivityActive = 0;
        dbHelper=new DBHelper(this);
        QUEUE_NAME = intent.getStringExtra("id");
        EXCHANGE_NAME = intent.getStringExtra("id");
        Log.i("INFO","I am here. Service started "+QUEUE_NAME+"  "+EXCHANGE_NAME);
        mConsumer = new MessageConsumer(ServerURL.url_Host, EXCHANGE_NAME,QUEUE_NAME, "fanout");

        serviceReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                String flagStatus = intent.getStringExtra("status");
                flagActivityActive = Integer.parseInt(flagStatus);
                if(flagActivityActive == 1)
                {
                    activeUserEmail = intent.getStringExtra("email");
                    activeGroup     = intent.getStringExtra("groupId");
                    Log.i("INFO","I am from service. Activity is active now "+activeUserEmail+"  "+activeGroup);
                }
                else
                {
                    Log.i("INFO","I am from service. Activity is stopped now");
                }

            }
        };
        if (serviceReceiver != null) {
            IntentFilter intentFilter = new IntentFilter(ACTION_STRING_SERVICE);
            registerReceiver(serviceReceiver, intentFilter);
        }



        new consumerconnect().execute();

        mConsumer.setOnReceiveMessageHandler(new MessageConsumer.OnReceiveMessageHandler() {

            public void onReceiveMessage(byte[] message) {
                String text = "";
                text = text.concat(MessageConsumer.message);
                Log.i("INFO",text);
                Message m = new Message();
                String[] allFields = text.split("-", 6);
                Log.i("INFO", "in Service entire message received " + text);
                if (allFields.length == 6) {
                    m.setFromEmail(allFields[0]);
                    m.setFromName(allFields[1]);
                    m.setToEmail(allFields[2]);
                    m.setToName(allFields[3]);
                    m.setMessage(allFields[4]);
                    m.setGroupID(allFields[5]);
                    Log.i("INFO", "while setting message Checking Service message " + m.getMessage());
                    dbHelper.insertChat(m.getFromName(), m.getToName(), m.getFromEmail(), m.getToEmail(), m.getGroupID(), m.getMessage());
                } else {
                    //rarely happens
                    m.setMessage("");
                    m.setToEmail("");
                    m.setFromName("");
                    m.setToName("");
                    m.setFromEmail("");
                    m.setGroupID("");
                }
                Log.i("INFO", "From Service " + text);
                if (flagActivityActive == 1) {

                    if (m.getFromEmail().equals(activeUserEmail) || m.getGroupID().equals(activeGroup)) {
                        Intent new_intent = new Intent();
                        new_intent.setAction(ACTION_STRING_ACTIVITY);
                        new_intent.putExtra("message", m.getMessage());
                        new_intent.putExtra("fromEmail", m.getFromEmail());
                        new_intent.putExtra("toEmail", m.getToEmail());
                        new_intent.putExtra("fromName", m.getFromName());
                        new_intent.putExtra("toName", m.getToName());
                        sendBroadcast(new_intent);
                    } else {
                        if (m.getGroupID().equals("NULL")) {
                            Notify(getApplicationContext(), "Message from " + m.getFromName(), m.getMessage());
                        } else {
                            Notify(getApplicationContext(), "Message in " + m.getGroupID().split("-")[0]+" Group", m.getFromName()+":"+m.getMessage());
                        }

                    }
                } else {
                    if (m.getGroupID().equals("NULL")) {
                        Notify(getApplicationContext(), "Message from " + m.getFromName(), m.getMessage());
                    } else {
                        Notify(getApplicationContext(), "Message In " + m.getGroupID().split("-")[0]+" Group",m.getFromName()+":"+m.getMessage());
                    }
                }
                m = null;
            }
        });

        return START_REDELIVER_INTENT;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }



    private class consumerconnect extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... Message) {

            try {
                // Connect to broker
                mConsumer.connectToRabbitMQ();


            }
            catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }


            return null;
        }

    }
}
