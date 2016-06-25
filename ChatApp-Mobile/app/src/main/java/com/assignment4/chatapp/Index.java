package com.assignment4.chatapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Index extends AppCompatActivity {


    private MyListAdapter<Message> adapter;
    private ArrayList<Message> my_list;
    private ListView messagesList;
    private FloatingActionButton floatingActionButton;
    private EditText editTextMessage;
    private DBHelper dbHelper;
    private String senderName;
    private String senderEmail;
    private String receiverName;
    private String receiverEmail;
    private String QUEUE_NAME;
    private String EXCHANGE_NAME;


    //Strings to register to create intent filter for registering the recivers
    private static final String ACTION_STRING_SERVICE = "ToService";
    private static final String ACTION_STRING_ACTIVITY = "ToActivity";
    private BroadcastReceiver activityReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);


        Intent intent=getIntent();
        receiverName = intent.getStringExtra("receiverName");
        receiverEmail= intent.getStringExtra("receiverEmail");
        senderName   = intent.getStringExtra("senderName");
        senderEmail  = intent.getStringExtra("senderEmail");

        dbHelper=new DBHelper(this);

        QUEUE_NAME    = receiverEmail;
        EXCHANGE_NAME = receiverEmail;

        Log.i("INFO","Name in index activity " + receiverName+" "+senderName);
        Log.i("INFO", "Email in index activity "+receiverEmail+"  "+senderEmail);

        this.setTitle(receiverName);
        floatingActionButton= (FloatingActionButton)findViewById(R.id.sendButton);
        editTextMessage     = (EditText) findViewById(R.id.messageBodyField);

        dbHelper.getAllChats();
        my_list = dbHelper.getChats(senderEmail,receiverEmail);
        messagesList = (ListView) findViewById(R.id.listMessages);
        adapter = new MyListAdapter();
        messagesList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ChatService.isConnectingToInternet())
                {
                    String message = editTextMessage.getText().toString();
                    Message senderMessage = new Message();
                    senderMessage.setFromEmail(senderEmail);
                    senderMessage.setFromName(senderName);
                    senderMessage.setToEmail(receiverEmail);
                    senderMessage.setToName(receiverName);
                    senderMessage.setMessage(message);
                    String messagePublish = senderEmail+"-"+senderName+"-"+receiverEmail+"-"+receiverName+"-"+message+"-NULL";
                    new send().execute(messagePublish);
                    dbHelper.insertChat(senderName, receiverName, senderEmail, receiverEmail, "NULL", message);
                    my_list.add(senderMessage);
                    adapter.notifyDataSetChanged();
                    messagesList.setSelection(messagesList.getAdapter().getCount()-1);
                    editTextMessage.setText("");

                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Please check your internet connectivity",Toast.LENGTH_LONG).show();
                }
            }
        });

        activityReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                Message receiverMessage = new Message();
                receiverMessage.setMessage(intent.getStringExtra("message"));
                receiverMessage.setFromEmail(intent.getStringExtra("fromEmail"));
                receiverMessage.setToEmail(intent.getStringExtra("toEmail"));
                receiverMessage.setFromName(intent.getStringExtra("fromName"));
                receiverMessage.setToName(intent.getStringExtra("toName"));
                my_list.add(receiverMessage);
                adapter.notifyDataSetChanged();
                messagesList.setSelection(messagesList.getAdapter().getCount()-1);
            }
        };
        if (activityReceiver != null) {
            IntentFilter intentFilter = new IntentFilter(ACTION_STRING_ACTIVITY);
            registerReceiver(activityReceiver, intentFilter);
        }
        Intent intentService = new Intent();
        intentService.setAction(ACTION_STRING_SERVICE);
        intentService.putExtra("status","1");
        intentService.putExtra("email",receiverEmail);
        intentService.putExtra("groupId","NULLCHAT");
        sendBroadcast(intentService);

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intentService = new Intent();
        intentService.setAction(ACTION_STRING_SERVICE);
        intentService.putExtra("status", "0");
        sendBroadcast(intentService);
        unregisterReceiver(activityReceiver);
    }

    private class MyListAdapter<A> extends ArrayAdapter<Message> {

        public MyListAdapter() {
            super(getApplicationContext(), R.layout.message_left, my_list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View itemView;

                Message m = my_list.get(position);
                if(m.getFromEmail().equals(senderEmail))
                {
                    itemView = getLayoutInflater().inflate(R.layout.message_left, parent, false);
                }
                else
                {
                    itemView = getLayoutInflater().inflate(R.layout.message_right, parent, false);
                }
                TextView messageText   = (TextView) itemView.findViewById(R.id.txtMessage);
               TextView messageSender = (TextView) itemView.findViewById(R.id.txtSender);
                messageSender.setText(m.getFromName());
                messageText.setText(m.getMessage());

            return itemView;
        }

    }

    private class send extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... Message) {
            try {

                ConnectionFactory factory = new ConnectionFactory();
                factory.setHost(ServerURL.url_Host);
                factory.setUsername("kapish");
                factory.setPassword("kapish");
                factory.setPort(5672);
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel();
                channel.exchangeDeclare(EXCHANGE_NAME, "fanout", true);
                channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                String tempstr = "";
                for (String aMessage : Message) tempstr += aMessage;

                channel.basicPublish(EXCHANGE_NAME, QUEUE_NAME, null,
                        tempstr.getBytes());

                channel.close();

                connection.close();

            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
            // TODO Auto-generated method stub
            return null;
        }

    }

}
