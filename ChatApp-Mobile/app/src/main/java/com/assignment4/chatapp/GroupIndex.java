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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.ArrayList;

public class GroupIndex extends AppCompatActivity {

    private MyListAdapter<Message> adapter;
    private ArrayList<Message> my_list;
    private ListView messagesList;
    private FloatingActionButton floatingActionButton;
    private EditText editTextMessage;
    private DBHelper dbHelper;
    private String senderName;
    private String senderEmail;
    private String groupId;
    private String groupMembers;
    private String QUEUE_NAME;
    private String EXCHANGE_NAME;

    //Strings to register to create intent filter for registering the recivers
    private static final String ACTION_STRING_SERVICE = "ToService";
    private static final String ACTION_STRING_ACTIVITY = "ToActivity";
    private BroadcastReceiver activityReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_index);
        Intent intent = getIntent();

        groupId       = intent.getStringExtra("groupId");
        groupMembers  = intent.getStringExtra("members");
        senderEmail   = intent.getStringExtra("senderEmail");
        senderName    = intent.getStringExtra("senderName");
        String[] info= groupId.split("-");
        this.setTitle(info[0]);
        Log.i("INFO", "Group Index " + groupId + "  " + groupMembers);


        dbHelper=new DBHelper(this);

        floatingActionButton= (FloatingActionButton)findViewById(R.id.sendButton);
        editTextMessage     = (EditText) findViewById(R.id.messageBodyField);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ChatService.isConnectingToInternet())
                {
                    String message = editTextMessage.getText().toString();
                    Message senderMessage = new Message();
                    senderMessage.setFromEmail(senderEmail);
                    senderMessage.setFromName(senderName);
                    senderMessage.setToEmail("");
                    senderMessage.setToName("");
                    senderMessage.setMessage(message);
                    String messagePublish = senderEmail+"-"+senderName+"-"+"ALL"+"-"+"ALL"+"-"+message+"-"+groupId;
                    new send().execute(messagePublish);
                    dbHelper.insertChat(senderName, "", senderEmail, "", groupId, message);
                    my_list.add(senderMessage);
                    adapter.notifyDataSetChanged();
                    messagesList.setSelection(messagesList.getAdapter().getCount()-1);
                    editTextMessage.setText("");
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Please check your internet connectivity", Toast.LENGTH_LONG).show();

                }

            }
        });


        my_list = dbHelper.getGroupChats(groupId);
        messagesList = (ListView) findViewById(R.id.listMessages);
        adapter = new MyListAdapter();
        messagesList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

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
        intentService.putExtra("status", "1");
        intentService.putExtra("email","NULL");
        intentService.putExtra("groupId", groupId);
        sendBroadcast(intentService);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intentService = new Intent();
        intentService.setAction(ACTION_STRING_SERVICE);
        intentService.putExtra("status", "0");
        sendBroadcast(intentService);
        unregisterReceiver(activityReceiver);
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
                String[] groupMem = groupMembers.split(";");
                for(int i=0;i<groupMem.length;i++)
                {
                    if(!groupMem[i].equals(senderEmail))
                    {
                        EXCHANGE_NAME = groupMem[i];
                        QUEUE_NAME    = groupMem[i];
                        channel.exchangeDeclare(EXCHANGE_NAME, "fanout", true);
                        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                        String tempstr = "";
                        for (String aMessage : Message) tempstr += aMessage;

                        channel.basicPublish(EXCHANGE_NAME, QUEUE_NAME, null,
                                tempstr.getBytes());
                    }
                }
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
