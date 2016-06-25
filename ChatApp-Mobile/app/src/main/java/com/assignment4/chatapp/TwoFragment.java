package com.assignment4.chatapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class TwoFragment extends Fragment{

    private Contact c;
    private FloatingActionButton floatingActionButtonAdd;
    ListView l;

    MyListAdapter<ChatGroup> adapter;
    ArrayList<ChatGroup> my_list;
    private int isFetchedSuccessCode;
    private static final String TAG_SUCCESS = "success";

    private static String DIALOGBOXTITLE     = "Group Info";

    private ProgressDialog pDialog;

    private DBHelper dbHelper;

    public TwoFragment() {
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

        View v= inflater.inflate(R.layout.fragment_two, container, false);
        floatingActionButtonAdd = (FloatingActionButton)v.findViewById(R.id.add);
        l =(ListView) v.findViewById(R.id.groupList);

        dbHelper=new DBHelper(getActivity());

        my_list=new ArrayList<ChatGroup>();
        adapter=new MyListAdapter<>();
        l.setAdapter(adapter);
        isFetchedSuccessCode = -1;
        new GetGroups().execute();


        //OnSetListItemListener
        l.setClickable(true);
        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(getActivity(),GroupIndex.class);
                ChatGroup g = my_list.get(position);
                intent.putExtra("groupId", g.getGroupId());
                intent.putExtra("members", g.getMembers());
                intent.putExtra("senderEmail",c.getEmail());
                intent.putExtra("senderName",c.getName());
                startActivity(intent);
            }
        });

        l.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
              //  Toast.makeText(getActivity().getApplicationContext(), "Long press done on "+my_list.get(position).getGroupId(), Toast.LENGTH_SHORT).show();
                //Alert Dialog Box to take email id
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

                alert.setTitle(DIALOGBOXTITLE);

                final LinearLayout layout = new LinearLayout(getActivity());
                layout.setOrientation(LinearLayout.VERTICAL);

                ChatGroup group = my_list.get(position);

                TextView textViewGroupNameText = new TextView(getActivity());
                textViewGroupNameText.setText("\n"+"     "+ "Group Name : ");
                textViewGroupNameText.setTextSize(15);

                final TextView textViewGroupName = new TextView(getActivity());
                textViewGroupName.setText("     " + group.getGroupId().split("-")[0] + "\n");
                textViewGroupName.setTextSize(15);
                layout.addView(textViewGroupNameText);
                layout.addView(textViewGroupName);

                final TextView textViewGroupOwnerIdText = new TextView(getActivity());
                textViewGroupOwnerIdText.setText("     " + "Owner Id : ");
                textViewGroupOwnerIdText.setTextSize(15);
                final TextView textViewGroupOwnerId = new TextView(getActivity());
                textViewGroupOwnerId.setText("     " + group.getGroupId().split("-")[1] + "\n");
                textViewGroupOwnerId.setTextSize(15);
                layout.addView(textViewGroupOwnerIdText);
                layout.addView(textViewGroupOwnerId);

                final TextView textViewMembersText = new TextView(getActivity());
                textViewMembersText.setText("     " + "Member Ids : ");
                textViewMembersText.setTextSize(15);
                final TextView textViewGroupMembers = new TextView(getActivity());
                textViewGroupMembers.setText("     " + group.getMembers().replace(";", "\n" + "     ") + "\n");
                textViewGroupMembers.setTextSize(15);

                layout.addView(textViewMembersText);
                layout.addView(textViewGroupMembers);

                alert.setView(layout);
                if(c.getEmail().equals(group.getGroupId().split("-")[1]))
                {
                    alert.setPositiveButton("Update Group", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            final AlertDialog.Builder alert1 = new AlertDialog.Builder(getActivity());

                            alert1.setTitle("Update Group Members");

                            final LinearLayout layout1 = new LinearLayout(getActivity());
                            layout1.setOrientation(LinearLayout.VERTICAL);
                            final EditText editTextEmail = new EditText(getActivity());
                            layout1.addView(editTextEmail);
                            alert1.setView(layout1);
                            alert1.setPositiveButton("Add Member", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    String email = editTextEmail.getText().toString();
                                    if (email == null) {
                                        Toast.makeText(getActivity().getApplicationContext(), "Please provide email id", Toast.LENGTH_SHORT).show();
                                    } else if (email.isEmpty()) {
                                        Toast.makeText(getActivity().getApplicationContext(), "Please provide email id", Toast.LENGTH_SHORT).show();

                                    } else if (email.equals(c.getEmail())) {
                                        Toast.makeText(getActivity().getApplicationContext(), "You cannot add yourself again", Toast.LENGTH_SHORT).show();

                                    } else {
                                        isFetchedSuccessCode = -2;
                                        try {
                                            isFetchedSuccessCode = new AddMember().execute(my_list.get(position).getGroupId() + ";" + email).get();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        } catch (ExecutionException e) {
                                            e.printStackTrace();
                                        }
                                        if (isFetchedSuccessCode == 1) {
                                            Toast.makeText(getActivity().getApplicationContext(), "Member " + email + "  added successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                            alert1.setNegativeButton("Remove Member", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    String email = editTextEmail.getText().toString();
                                    if (email == null) {
                                        Toast.makeText(getActivity().getApplicationContext(), "Please provide email id", Toast.LENGTH_SHORT).show();
                                    } else if (email.isEmpty()) {
                                        Toast.makeText(getActivity().getApplicationContext(), "Please provide email id", Toast.LENGTH_SHORT).show();

                                    } else if (email.equals(c.getEmail())) {
                                        Toast.makeText(getActivity().getApplicationContext(), "You cannot remove yourself", Toast.LENGTH_SHORT).show();

                                    } else {
                                        isFetchedSuccessCode = -1;
                                        try {
                                            isFetchedSuccessCode = new ExitGroup().execute(my_list.get(position).getGroupId() + ";" + email).get();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        } catch (ExecutionException e) {
                                            e.printStackTrace();
                                        }
                                        if (isFetchedSuccessCode == 1) {

                                            Toast.makeText(getActivity().getApplicationContext(), "Member " + email + "  removed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }


                            });

                            AlertDialog alertDialog1 = alert1.create();
                            alertDialog1.show();
                        }

                    });

                }
                else
                {
                    alert.setPositiveButton("Exit Group", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //remove user from group
                            isFetchedSuccessCode = -1;
                            try {
                                isFetchedSuccessCode = new ExitGroup().execute(my_list.get(position).getGroupId() + ";" + c.getEmail()).get();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                            if (isFetchedSuccessCode == 1) {
                                Toast.makeText(getActivity().getApplicationContext(), "Group exited successfully", Toast.LENGTH_LONG).show();
                                dbHelper.deleteGroupChat(my_list.get(position).getGroupId());
                                my_list.remove(position);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
                if(c.getEmail().equals(group.getGroupId().split("-")[1])) {

                    alert.setNegativeButton("Delete Group", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            isFetchedSuccessCode = 0;
                            try {
                                isFetchedSuccessCode = new RemoveGroup().execute(my_list.get(position).getGroupId()).get();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                            if(isFetchedSuccessCode == 1)
                            {
                                dbHelper.deleteGroupChat(my_list.get(position).getGroupId());
                                my_list.remove(position);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
                else
                {
                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();

                        }
                    });
                }
                AlertDialog alertDialog = alert.create();
                alertDialog.show();


        return true;
            }

        });
                //OnSetListener
        floatingActionButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateGroup.class);
                Log.i("INFO", "Second Frag checking email " + c.getEmail());
                intent.putExtra("adminEmail", c.getEmail());
                startActivity(intent);

            }
        });
                return v;

    }

    private class MyListAdapter<A> extends ArrayAdapter<ChatGroup> {

        public MyListAdapter() {
            super(getActivity(), R.layout.grid_single, my_list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View itemView = convertView;
            if (itemView == null) {
                itemView = getActivity().getLayoutInflater().inflate(R.layout.grid_single, parent, false);
            }

            ChatGroup chatGroup = my_list.get(position);


            ImageView pic = (ImageView) itemView.findViewById(R.id.profile_image);
            pic.setImageResource(R.drawable.group);
            String [] GroupInfo = chatGroup.getGroupId().split("-");
            TextView name = (TextView) itemView.findViewById(R.id.name);
            name.setText(GroupInfo[0]);

            TextView status = (TextView) itemView.findViewById(R.id.status);
            status.setText("Group Admin :"+GroupInfo[1]);

            return itemView;
        }
    }

    class GetGroups extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {

            // Building Parameters

            HashMap<String,String> params= new HashMap<String,String>();
            params.put("email",c.getEmail());
            JSONParser jsonParser = new JSONParser();
            JSONObject json = jsonParser.makeHttpRequest(ServerURL.url_FetchAllGroups, "GET", params);
            try {
                if(json != null) {
                    Log.i("INFO","Frag 2 get all groups "+json.toString());
                    int success = json.getInt(TAG_SUCCESS);
                    isFetchedSuccessCode = success;
                    if(isFetchedSuccessCode == 1)
                    {
                        int total = json.getInt("total");
                        for(int i=1;i<=total;i++)
                        {
                            String groupId  = json.getString("groupId"+i);
                            String members  = json.getString("members"+i);
                            ChatGroup group = new ChatGroup();
                            group.setGroupId(groupId);
                            group.setMembers(members);
                            my_list.add(group);

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
                Toast.makeText(getActivity().getApplicationContext(), "Server not working !! Please try again later", Toast.LENGTH_SHORT).show();
            }

        }

    }

    class ExitGroup extends AsyncTask<String, String, Integer> {

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
            params.put("userEmail",args[0].split(";")[1]);
            params.put("groupId",args[0].split(";")[0]);
            JSONParser jsonParser = new JSONParser();
            JSONObject json = jsonParser.makeHttpRequest(ServerURL.url_RemoveUserFromGroup, "POST", params);
            try {
                if(json != null) {
                    Log.i("INFO","Frag 2 exit group "+json.toString());
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

            }
            else if(isFetchedSuccessCode == 0)
            {
                Toast.makeText(getActivity().getApplicationContext(),"Please provide valid email id.",Toast.LENGTH_SHORT).show();

            }
            else
            {
                Toast.makeText(getActivity().getApplicationContext(),"Server is not working. Not able to remove Contact.",Toast.LENGTH_SHORT).show();
            }

        }

    }


    class AddMember extends AsyncTask<String, String, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Adding Member to Group..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
            isFetchedSuccessCode = 0;
        }

        protected Integer doInBackground(String... args) {

            // Building Parameters

            HashMap<String,String> params= new HashMap<String,String>();
            params.put("userEmail",args[0].split(";")[1]);
            params.put("groupId",args[0].split(";")[0]);
            JSONParser jsonParser = new JSONParser();
            JSONObject json = jsonParser.makeHttpRequest(ServerURL.url_AddUserToGroup, "POST", params);
            try {
                if(json != null) {
                    Log.i("INFO","Frag 2 add member to group "+json.toString());
                    int success = json.getInt(TAG_SUCCESS);
                    isFetchedSuccessCode = success;


                }

            }
            catch (JSONException e) {
                isFetchedSuccessCode = -1;

            }

            return isFetchedSuccessCode;
        }
        protected void onPostExecute(Integer file_url) {
            pDialog.dismiss();
            if(isFetchedSuccessCode == 1)
            {
                //removed successfully

            }
            else if(isFetchedSuccessCode == 0)
            {
                Toast.makeText(getActivity().getApplicationContext(),"You are already member of the group !!",Toast.LENGTH_SHORT).show();

            }
            else if(isFetchedSuccessCode == -1)
            {
                Toast.makeText(getActivity().getApplicationContext(),"Please provide valid email id",Toast.LENGTH_SHORT).show();

            }
            else
            {
                Toast.makeText(getActivity().getApplicationContext(),"Server is not working. Not able to remove Contact.",Toast.LENGTH_SHORT).show();
            }

        }

    }

    class RemoveGroup extends AsyncTask<String, String, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Removing Group..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
            isFetchedSuccessCode = 0;
        }

        protected Integer doInBackground(String... args) {

            // Building Parameters

            HashMap<String,String> params= new HashMap<String,String>();
            params.put("groupId",args[0]);
            JSONParser jsonParser = new JSONParser();
            JSONObject json = jsonParser.makeHttpRequest(ServerURL.url_RemoveGroup, "POST", params);
            try {
                if(json != null) {
                    Log.i("INFO","Frag 2 Remove entire group "+json.toString());
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
                Toast.makeText(getActivity().getApplicationContext(),"Group removed successfully.",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getActivity().getApplicationContext(),"Server is not working. Not able to remove Contact.",Toast.LENGTH_SHORT).show();
            }

        }

    }

}