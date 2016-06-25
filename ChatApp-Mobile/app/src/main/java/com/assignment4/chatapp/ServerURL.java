package com.assignment4.chatapp;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by kapish on 10-04-2016.
 */
public class ServerURL {

 static String url_Register           = "http://192.168.56.59:8080/signin";
 static String url_Update             = "http://192.168.56.59:8080/update";
 static String url_GetContacts        = "http://192.168.56.59:8080/getContacts";
 static String url_AddContact         = "http://192.168.56.59:8080/addContact";
 static String url_AddGroup           = "http://192.168.56.59:8080/addGroup";
 static String url_FetchAllGroups     = "http://192.168.56.59:8080/FetchAllGroups";
 static String url_RemoveContact      = "http://192.168.56.59:8080/removeContact";
 static String url_RemoveUserFromGroup= "http://192.168.56.59:8080/RemoveUserFromGroup";
 static String url_RemoveGroup        = "http://192.168.56.59:8080/RemoveGroup";
 static String url_AddUserToGroup     = "http://192.168.56.59:8080/AddUserToGroup";
 static String url_Host               = "192.168.56.59";

}
