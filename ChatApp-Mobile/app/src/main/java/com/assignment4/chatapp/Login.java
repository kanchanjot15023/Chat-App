package com.assignment4.chatapp;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;


public class Login extends Activity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private SignInButton GoogleSignIn;
    private int check;
    private GoogleApiClient mGoogleApiClient  ;
    private boolean mIntentInProgress         ;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
    private static final int RC_SIGN_IN = 0      ;

    private String name;
    private String email;
    private String status;
    private String picURL;
    private String registerationID;

    private static final String EXTRA_MESSAGE = "message";
    private static final String PROPERTY_REG_ID = "registration_id";
    private String SENDER_ID = "351751669482";
    private static final String UNSUCCESSFULMSG = "Unable to Login. Unable to fetch Information.";

    GoogleCloudMessaging gcm;

    //SharedPreferences
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Boolean isSaved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        GoogleSignIn = (SignInButton) findViewById(R.id.google_sign_in_button);
        GoogleSignIn.setOnClickListener(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();

    }

    // OnStart
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    //OnResume
    @Override
    protected void onResume() {
        super.onResume();
        pref = getApplicationContext().getSharedPreferences("ChatUserPref", MODE_PRIVATE);
        isSaved = pref.getBoolean("isSaved", false);
        if(isSaved)
        {
            Intent intent=new Intent(Login.this,MainActivity.class);
            intent.putExtra("name",pref.getString("name","NULL"));
            intent.putExtra("picURL",pref.getString("picURL","NULL"));
            intent.putExtra("email", pref.getString("email","NULL"));
            intent.putExtra("status",pref.getString("status","NULL"));
            //start Service
            startActivity(intent);
            ChatService.EXCHANGE_NAME = pref.getString("email","NULL");
            ChatService.QUEUE_NAME    = pref.getString("email","NULL");
            Log.i("INFO", "Logging " + ChatService.QUEUE_NAME);
            Intent i= new Intent(getApplicationContext(), ChatService.class);
            i.putExtra("id", pref.getString("email", "NULL"));
            getApplicationContext().startService(i);

        }
    }

    //OnStop
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onClick(View v) {
        signInWithGplus();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }

    }

    @Override
    protected void onActivityResult(final int requestCode, final int responseCode, final Intent intent) {

        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
                Log.i("INFO","I am here for u to login");
            }

        }

    }

    @Override
    public void onConnected(Bundle arg0) {
        mSignInClicked = false;
        // Get patient information
        new Register().execute();

        getProfileInformation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();

    }
    /**
     * Sign-in into google
     * */
    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();

        }
    }

    /**
     * Method to resolve any signin errors
     * */
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;

                mGoogleApiClient.connect();
            }
        }
    }



    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {

                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                Log.i("INFO","fetching profile");

                this.name   = currentPerson.getDisplayName();
                this.email  = Plus.AccountApi.getAccountName(mGoogleApiClient);

                // getting image of size 150
                String temp = currentPerson.getImage().getUrl();
                this.picURL = temp.substring(0,(temp.length()-2))+"400";
                this.status = "Available";

                new Signin().execute();

//                intent.putExtra("img", (CharSequence) currentPerson.getImage());

                signOutFromGplus();


            } else {
                Toast.makeText(getApplicationContext(), UNSUCCESSFULMSG, Toast.LENGTH_LONG).show();
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //SignOut from Google Plus
    private void signOutFromGplus() {

        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();

        }
    }


    class Register extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            String msg = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                }
                String regid = gcm.register(SENDER_ID);
                registerationID = regid;
                msg = "Device registered, registration ID=" + regid;



            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) {

            Log.i("INFO", msg);

        }


    }

    class Signin extends AsyncTask<String, String, String> {



        @Override
        protected String doInBackground(String... strings) {
            HashMap<String,String> params = new HashMap<String,String>();
            params.put("name",name);
            params.put("email",email);
            params.put("status",status);
            params.put("picURL",picURL);
            params.put("registerationId",registerationID);
            // getting JSON Object
            // Note that create product url accepts POST method
            JSONParser jsonParserCheck = new JSONParser();
            JSONObject json = jsonParserCheck.makeHttpRequest(ServerURL.url_Register, "POST",params);
            try {
                if(json != null) {
                    int success = json.getInt("success");
                    check=success;
                    if(success == 1)
                    {
                        check = 1;
                        name  = json.getString("name");
                        email = json.getString("email");
                        picURL= json.getString("picURL");
                        status= json.getString("status");
                    }
                    else
                    {
                        check = 0;
                        //Toast.makeText(getActivity().getApplicationContext(),"Error..",Toast.LENGTH_SHORT).show();
                    }

                }
                else
                {
                    check=-1;
                }
            }
            catch (JSONException e) {
                check=-1;
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done

            if(check == -1)
                Toast.makeText(getApplicationContext(), "Something went wrong !! Contact Developer.", Toast.LENGTH_SHORT).show();
            else if(check == 0)
            {
                Toast.makeText(getApplicationContext(),"Not able to Login!! Check Internet Connectivity",Toast.LENGTH_SHORT).show();
            }

            else if(check == 1)
            {
                Toast.makeText(getApplicationContext(),"You are able to Login to ChatApp", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(Login.this,MainActivity.class);
                intent.putExtra("name",name);
                intent.putExtra("picURL", picURL);
                intent.putExtra("email", email);
                intent.putExtra("status", status);
                startActivity(intent);

                ChatService.EXCHANGE_NAME = email;
                ChatService.QUEUE_NAME    = email;
                Intent i= new Intent(getApplicationContext(), ChatService.class);
                i.putExtra("id", email);
                getApplicationContext().startService(i);

            }
        }

    }



}
