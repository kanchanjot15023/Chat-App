package com.assignment4.chatapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;


public class ThreeFragment extends Fragment{

    ListView l;
    ArrayList<Contact> my_list;

    ImageView pic;
    FloatingActionButton floatingActionButtonAdd;
    Bitmap bitmap;
    MyListAdapter<Contact> adapter;
    private Contact c;

    //Progress Bar
    private ProgressDialog pDialog;
    private static final String TAG_SUCCESS = "success";
    private int isFetchedSuccessCode;

    //alert box
    private String email;
    private static String DIALOGBOXTITLE     = "Add New Contact";
    private static String ADD                = "Add";
    private static String CANCEL             = "Cancel";


    public ThreeFragment() {
        // Required empty public constructor

    }
    public void check(Contact c)
    {
        this.c = c;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_three, container, false);
        l= (ListView) v.findViewById(R.id.contactlist);
        floatingActionButtonAdd = (FloatingActionButton)v.findViewById(R.id.add);

        my_list=new ArrayList<Contact>();
        adapter=new MyListAdapter<>();
        l.setAdapter(adapter);

        isFetchedSuccessCode = -1;
        new GetContacts().execute();

        //OnSetListItemListener
        l.setClickable(true);
        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Toast.makeText(getActivity().getApplicationContext(), my_list.get(position).getEmail(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), Index.class);
                Log.i("INFO", "In Frag 3 I will send " + my_list.get(position).getName() + "  " + my_list.get(position).getEmail());
                intent.putExtra("receiverName", my_list.get(position).getName());
                intent.putExtra("receiverEmail", my_list.get(position).getEmail());
                intent.putExtra("senderEmail", c.getEmail());
                intent.putExtra("senderName", c.getName());
                startActivity(intent);
            }
        });

        l.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id) {

                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                Contact contact = my_list.get(position);
                alert.setTitle(contact.getName()+"\n"+contact.getEmail());

                alert.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {


                        isFetchedSuccessCode = 0;
                        try {
                            isFetchedSuccessCode = new RemoveContact().execute(my_list.get(position).getEmail()).get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        if(isFetchedSuccessCode == 1)
                        {
                            my_list.remove(position);
                            adapter.notifyDataSetChanged();
                        }

                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                        dialog.cancel();
                        // new SendFileToServer().execute();
                    }
                });
                AlertDialog alertDialog = alert.create();
                alertDialog.show();
                return true;
            }
        });
                //OnSetListener
        floatingActionButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Alert Dialog Box to take email id
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

                alert.setTitle(DIALOGBOXTITLE);

                final LinearLayout layout = new LinearLayout(getActivity());
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText editTextEmail = new EditText(getActivity());
                editTextEmail.setHint("Email Id");


                layout.addView(editTextEmail);
                alert.setView(layout);
                alert.setPositiveButton(ADD, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        email = editTextEmail.getEditableText().toString();


                        if (email == null) {
                            Toast.makeText(getActivity().getApplicationContext(), "Please provide email id", Toast.LENGTH_LONG).show();
                        } else if (email.length() == 0) {
                            Toast.makeText(getActivity().getApplicationContext(), "Please provide email id", Toast.LENGTH_LONG).show();
                        } else if (c.getEmail().equals(email.toLowerCase())) {
                            Toast.makeText(getActivity().getApplicationContext(), "You cannot add yourself to contacts", Toast.LENGTH_LONG).show();
                        } else {
                            //action Async Task need to be called
                            new AddContact().execute();
                        }

                    }
                });

                alert.setNegativeButton(CANCEL, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                        dialog.cancel();
                        // new SendFileToServer().execute();
                    }
                });
                AlertDialog alertDialog = alert.create();
                alertDialog.show();


            }
        });

        return v;
    }

    @Override
    public void onStart() {

        super.onStart();
    }


    private class MyListAdapter<A> extends ArrayAdapter<Contact> {

        public MyListAdapter() {
            super(getActivity(), R.layout.grid_single, my_list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View itemView = convertView;
            if (itemView == null) {
                itemView = getActivity().getLayoutInflater().inflate(R.layout.grid_single, parent, false);
            }

            final Contact contact = my_list.get(position);


             pic = (ImageView) itemView.findViewById(R.id.profile_image);


                try {
                    pic.setImageBitmap(new LoadImage().execute(contact.getPicURL()).get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }


            TextView name = (TextView) itemView.findViewById(R.id.name);
            name.setText(contact.getName());

            TextView status = (TextView) itemView.findViewById(R.id.status);
            status.setText(contact.getStatus());

            return itemView;
        }
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }
        protected Bitmap doInBackground(String... args) {
            Bitmap img = null;
            try {

                URL url = new URL(args[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                img = BitmapFactory.decodeStream(input);

            } catch (MalformedURLException e) {

            } catch (IOException e) {

            }
            if(img == null)
            {
                Bitmap profileIcon = BitmapFactory.decodeResource(getResources(), R.drawable.profile);
                return profileIcon;
            }
            else
            {
                return img;
            }


        }

        protected void onPostExecute(Bitmap image) {

            if(image != null){
                Log.i("INFODUMP","I am setting image");


            }else{

                Log.i("INFODUMP", "Failed in loading image");
            }
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
            params.put("email",c.getEmail());
            JSONParser jsonParser = new JSONParser();
            JSONObject json = jsonParser.makeHttpRequest(ServerURL.url_GetContacts, "GET", params);
            try {
                if(json != null) {
                    Log.i("INFO","Frag 3 get all contacts "+json.toString());
                    int success = json.getInt(TAG_SUCCESS);
                    isFetchedSuccessCode = success;
                    if(isFetchedSuccessCode == 1)
                    {
                        int total = json.getInt("total");
                        for(int i=1;i<=total;i++)
                        {
                            String name   = json.getString("name"+i);
                            String email  = json.getString("email"+i);
                            String status = json.getString("status"+i);
                            String picURL = json.getString("picURL"+i);
                            Contact c     = new Contact(email,name,status,picURL);
                            my_list.add(c);

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
                adapter.notifyDataSetChanged();
            }
            else if(isFetchedSuccessCode == 0)
            {
                //nothing. No friends fetched
            }
            else
            {
                Toast.makeText(getActivity().getApplicationContext(),"Server not working !! Please try again later",Toast.LENGTH_SHORT).show();
            }

        }

    }

    class AddContact extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Adding your Contact..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
            isFetchedSuccessCode = -2;
        }

        protected String doInBackground(String... args) {

            // Building Parameters

            HashMap<String,String> params= new HashMap<String,String>();
            params.put("toemail",c.getEmail());
            params.put("addemail",email.toLowerCase());
            JSONParser jsonParser = new JSONParser();
            JSONObject json = jsonParser.makeHttpRequest(ServerURL.url_AddContact, "POST", params);

            try {
                if(json != null) {
                    Log.i("INFO","Frag 3 add contact "+json.toString());
                    int success = json.getInt(TAG_SUCCESS);
                    isFetchedSuccessCode = success;
                    if(isFetchedSuccessCode == 1)
                    {
                        String name   = json.getString("name");
                        String email  = json.getString("email");
                        String status = json.getString("status");
                        String picURL = json.getString("picURL");
                        Contact c     = new Contact(email,name,status,picURL);
                        my_list.add(c);
                    }


                }

            }
            catch (JSONException e) {
                isFetchedSuccessCode = -2;
                e.printStackTrace();
            }

            return null;
        }
        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            if(isFetchedSuccessCode == 1)
            {
                adapter.notifyDataSetChanged();
            }
            else if(isFetchedSuccessCode == 0)
            {
                Toast.makeText(getActivity().getApplicationContext(),"No Contact found with this email id",Toast.LENGTH_SHORT).show();
            }
            else if(isFetchedSuccessCode == -1)
            {
                Toast.makeText(getActivity().getApplicationContext(),"Contact already present in your list",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getActivity().getApplicationContext(),"Server is not working. Please try again later.",Toast.LENGTH_SHORT).show();
            }

        }

    }
    class RemoveContact extends AsyncTask<String, String, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Removing Contact..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
            isFetchedSuccessCode = 0;
        }

        protected Integer doInBackground(String... args) {

            // Building Parameters

            HashMap<String,String> params= new HashMap<String,String>();
            params.put("userEmail",c.getEmail());
            params.put("friendEmail",args[0]);
            JSONParser jsonParser = new JSONParser();
            JSONObject json = jsonParser.makeHttpRequest(ServerURL.url_RemoveContact, "POST", params);
            try {
                if(json != null) {
                    Log.i("INFO","Frag 3 remove contact "+json.toString());
                    int success = json.getInt(TAG_SUCCESS);
                    isFetchedSuccessCode = success;


                }

            }
            catch (JSONException e) {
                isFetchedSuccessCode = 0;

            }

            return isFetchedSuccessCode;
        }
        protected void onPostExecute(Integer file_url) {
            pDialog.dismiss();
            if(isFetchedSuccessCode == 1)
            {
                //removed successfully
                Toast.makeText(getActivity().getApplicationContext(),"Removed successfully.",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getActivity().getApplicationContext(),"Server is not working. Not able to remove Contact.",Toast.LENGTH_SHORT).show();
            }

        }

    }

}
