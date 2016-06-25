package com.assignment4.chatapp;

import android.app.Application;
import android.content.Context;

/**
 * Created by kapish on 26-04-2016.
 */

public class ChatApplication extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        ChatApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return ChatApplication.context;
    }
}
