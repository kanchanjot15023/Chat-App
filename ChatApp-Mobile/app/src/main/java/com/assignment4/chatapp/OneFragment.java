package com.assignment4.chatapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;



public class OneFragment extends Fragment{

    private Contact c;
    ImageView imageView;
    EditText editTextName;
    EditText editTextStatus;
    ImageButton imageButtonSave;
    ImageButton imageButtonLogout;

    private static Bitmap imageBitmap = null;
    //Progress Bar
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    private static final String TAG_SUCCESS = "success";
    private int isUpdatedSuccessCode;

    //SharedPreferences
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Boolean isSaved;

    public OneFragment() {
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
        View v         =  inflater.inflate(R.layout.fragment_one, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        //initialization
        imageView        =  (ImageView) v.findViewById(R.id.profile_image);
        editTextName     =  (EditText) v.findViewById(R.id.name);
        editTextStatus   =  (EditText) v.findViewById(R.id.status);
        imageButtonSave  =  (ImageButton)v.findViewById(R.id.save);
        imageButtonLogout=  (ImageButton)v.findViewById(R.id.logout);
        isUpdatedSuccessCode = 0;

        //shared preferences
        pref    = getActivity().getApplicationContext().getSharedPreferences("ChatUserPref", Context.MODE_PRIVATE);
        editor  = pref.edit();
        isSaved = pref.getBoolean("isSaved", false);



        //Setting content
        editTextName.setText(c.getName());
        editTextStatus.setText(c.getStatus());
        try {
            if(c == null)
            {
                c = MainActivity.c;
            }
            Log.i("INFO", "In frag 1 fetching name and picURL " + c.getName() + "   " + c.picURL);
            if(imageBitmap == null)
            {
                imageBitmap = new LoadImage().execute(c.getPicURL()).get();
                imageView.setImageBitmap(imageBitmap);
            }
            else
            {
                imageView.setImageBitmap(imageBitmap);
            }



        } catch (InterruptedException e) {

        } catch (ExecutionException e) {

        }




    imageButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.setName(editTextName.getText().toString());
                c.setStatus(editTextStatus.getText().toString());
                new SaveDetails().execute();
            }
        });

        imageButtonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.remove("isSaved");
                editor.remove("name");
                editor.remove("email");
                editor.remove("status");
                editor.remove("picURL");
                editor.clear();
                editor.commit();
                Toast.makeText(getActivity().getApplicationContext(), "You are successfully Logout.", Toast.LENGTH_LONG).show();
                Intent i = new Intent(getActivity().getApplicationContext(),Login.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                getActivity().finish();
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        if(!isSaved) {
            editor.putBoolean("isSaved", true);
            editor.putString("name", c.getName());
            editor.putString("email", c.getEmail());
            editor.putString("status",c.getStatus());
            editor.putString("picURL",c.getPicURL());
            editor.commit();
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

    class SaveDetails extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Saving Your Details..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            // Building Parameters

            HashMap<String,String> params= new HashMap<String,String>();
            params.put("name", c.getName());
            params.put("email",c.getEmail());
            params.put("status", c.getStatus());
            JSONObject json = jsonParser.makeHttpRequest(ServerURL.url_Update, "POST", params);
            try {
                if(json != null) {
                    int success = json.getInt(TAG_SUCCESS);
                    isUpdatedSuccessCode = success;
                }

            }
            catch (JSONException e) {
                isUpdatedSuccessCode = -1;
                e.printStackTrace();
            }

            return null;
        }
    protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            if(isUpdatedSuccessCode == 1)
            {
                Context context = getActivity().getApplicationContext();
                CharSequence text = "Your Details saved successfully.";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
            else
            {
                Context context = getActivity().getApplicationContext();
                CharSequence text = "Server not working. Please try again later";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }

        }

    }

}