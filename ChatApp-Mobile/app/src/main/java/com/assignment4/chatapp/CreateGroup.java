package com.assignment4.chatapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class CreateGroup extends Activity {

    private ArrayList<String> contact_list   = new ArrayList<>();
    private ArrayList<Integer> select_list   = new ArrayList<>();
    private ArrayList<Contact> contact       = new ArrayList<>();

    MyCustomAdapter dataAdapter = null;

    private String adminEmail;
    private String groupName ;
    private String selectedList;
    private FloatingActionButton floatingActionButtonAdd;
    private EditText             editTextName;


    private static final String TAG_SUCCESS = "success";
    private int isFetchedSuccessCode;
    private ProgressDialog pDialog;

    //SharedPreferences
    SharedPreferences pref;
    Boolean isSaved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        //Initialization
        editTextName            = (EditText) findViewById(R.id.groupName);
        floatingActionButtonAdd = (FloatingActionButton) findViewById(R.id.sendButton);

        //receiving Group Admin Email Id:
        Intent intent = getIntent();
        adminEmail = intent.getStringExtra("adminEmail");
        Log.i("INFO", "In group creation " + adminEmail);

        isFetchedSuccessCode = -1;

        new GetContacts().execute();

        //OnSetListener
        floatingActionButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    groupName = editTextName.getText().toString();
                    selectedList = "";
                    if(groupName == null)
                    {
                        Toast.makeText(getApplicationContext(),"Please provide group name",Toast.LENGTH_LONG).show();
                    }
                    else{
                        if(groupName.isEmpty())
                        {
                            Toast.makeText(getApplicationContext(),"Please provide group name",Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            for(int i=0;i<select_list.size();i++)
                            {
                                if(select_list.get(i) == 1)
                                {
                                    selectedList += contact.get(i).getEmail()+";";
                                }
                            }
                            if(selectedList.equals(""))
                            {
                                Toast.makeText(getApplicationContext(),"Please select group member",Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                //save in db
                                new AddGroup().execute();
                            }
                        }

                    }

            }
        });

            }

    @Override
    protected void onResume() {
        super.onResume();
        displayListView();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void displayListView() {
        dataAdapter = new MyCustomAdapter(this,R.layout.check_box, contact_list);
        ListView listView = (ListView) findViewById(R.id.listContacts);
        listView.setAdapter(dataAdapter);
    }

    private class MyCustomAdapter extends ArrayAdapter<String> {


        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<String> contact_list) {
            super(context, textViewResourceId, contact_list);


        }

        private class ViewHolder {
            TextView code;
            CheckBox name;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.check_box, null);

                holder = new ViewHolder();
                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);

                holder.name.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        if (cb.isChecked()) {
                            select_list.set(position, 1);

                        } else {
                            select_list.set(position, 0);

                        }
                    }
                });
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if(contact_list.size() > 0)
            {
                Log.i("INFO","In Group creation activity "+contact_list.get(position));
                holder.name.setText(contact_list.get(position));
            }
            return convertView;
        }
    }

    class GetContacts extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {

            // Building Parameters

            HashMap<String,String> params= new HashMap<String,String>();
            params.put("email",adminEmail);
            JSONParser jsonParser = new JSONParser();
            JSONObject json = jsonParser.makeHttpRequest(ServerURL.url_GetContacts, "GET", params);
            try {
                if(json != null) {
                    int success = json.getInt(TAG_SUCCESS);
                    isFetchedSuccessCode = success;
                    Log.i("INFO","Group Creation get all contacts "+isFetchedSuccessCode+"  "+json.toString());
                    if(isFetchedSuccessCode == 1)
                    {
                        int total = json.getInt("total");
                        for(int i=1;i<=total;i++)
                        {
                            String email  = json.getString("email"+i);
                            String name   = json.getString("name"+i);
                            String status = json.getString("status"+i);
                            String picURL = json.getString("picURL"+i);
                            Contact c     = new Contact(email,name,status,picURL);
                            contact_list.add(name);
                            select_list.add(0);
                            contact.add(c);
                        }


                    }

                }

            }
            catch (JSONException e) {
                isFetchedSuccessCode = -1;
                e.printStackTrace();
            }

            return null;
        }
        protected void onPostExecute(String file_url) {
            // pDialog.dismiss();
            if(isFetchedSuccessCode == 1)
            {
                dataAdapter.notifyDataSetChanged();
            }
            else if(isFetchedSuccessCode == 0)
            {
                //nothing. No friends fetched
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Server not working !! Please try again later",Toast.LENGTH_SHORT).show();
            }

        }

    }

    class AddGroup extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CreateGroup.this);
            pDialog.setMessage("Creating new group..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
            isFetchedSuccessCode = -1;
        }

        protected String doInBackground(String... args) {

            // Building Parameters

            HashMap<String,String> params= new HashMap<String,String>();
            params.put("groupId",groupName+"-"+adminEmail);
            params.put("members",selectedList);
            JSONParser jsonParser = new JSONParser();
            JSONObject json = jsonParser.makeHttpRequest(ServerURL.url_AddGroup, "POST", params);
            try {
                if(json != null) {
                    int success = json.getInt(TAG_SUCCESS);
                    isFetchedSuccessCode = success;
                    Log.i("INFO","Group Creation add group "+isFetchedSuccessCode+"  "+json.toString());

                }

            }
            catch (JSONException e) {
                isFetchedSuccessCode = -1;
                e.printStackTrace();
            }

            return null;
        }
        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            if(isFetchedSuccessCode == 1)
            {
                //redirect to main activity
                Toast.makeText(getApplicationContext(),"Group created successfully",Toast.LENGTH_LONG).show();
                pref = getApplicationContext().getSharedPreferences("ChatUserPref", MODE_PRIVATE);
                Intent intent=new Intent(CreateGroup.this,MainActivity.class);
                intent.putExtra("name",pref.getString("name","NULL"));
                intent.putExtra("picURL",pref.getString("picURL","NULL"));
                intent.putExtra("email", pref.getString("email", "NULL"));
                intent.putExtra("status",pref.getString("status","NULL"));
                startActivity(intent);
            }
            else if(isFetchedSuccessCode == 0)
            {
                //group already exists
                Toast.makeText(getApplicationContext(),"Group already exists",Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Server not working !! Please try again later",Toast.LENGTH_SHORT).show();
            }

        }

    }

}


