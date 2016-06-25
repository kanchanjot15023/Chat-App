package com.assignment4.chatapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "chatAppDemo";
    private static final String TABLE_NAME = "Chatdata";
    private static final String TABLE_NAME_FINAL = "ChatdataDemo";
    //private static final String USER_NAME="user_name";
    private static final String CHAT_DATA="chat_data";


    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        // TODO Auto-generated method stub
        String CREATE_TABLE_TODO = "CREATE TABLE "
                + TABLE_NAME_FINAL + "( "+ "FROMID TEXT,TOID TEXT,GROUPID TEXT, FROMNAME TEXT, TONAME TEXT , CHAT TEXT"+ ")";
        db.execSQL(CREATE_TABLE_TODO);
        db.execSQL(
                "create table " + TABLE_NAME +
                        "(" + /*USER_NAME + " text ," +*/ CHAT_DATA + " int)"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertChatData  (/*String name,*/ String chatdata)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
       // contentValues.put(USER_NAME, name);
        contentValues.put(CHAT_DATA, chatdata);
        db.insert(TABLE_NAME, null, contentValues);
        return true;
    }

    public void insertChat(String fromName,String toName,String fromID,String toID,String groupID,String chat)
    {
        Log.i("INSERT",fromID+"  "+toID+"  "+groupID+" "+chat);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        // contentValues.put(USER_NAME, name);
        contentValues.put("FROMID", fromID);
        contentValues.put("TOID",toID);
        contentValues.put("GROUPID",groupID);
        contentValues.put("FROMNAME",fromName);
        contentValues.put("TONAME", toName);
        contentValues.put("CHAT", chat);
        db.insert(TABLE_NAME_FINAL, null, contentValues);


    }
    public ArrayList<Message> getChats(String senderEmail,String receiverEmail)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+TABLE_NAME_FINAL, null );
        res.moveToFirst();
        ArrayList<Message> messages = new ArrayList<>();

        while(!res.isAfterLast()){

            String fromId    = res.getString(res.getColumnIndex("FROMID"));
            String toId      = res.getString(res.getColumnIndex("TOID"));
            String group = res.getString(res.getColumnIndex("GROUPID"));
            if((fromId.equals(senderEmail) && toId.equals(receiverEmail)&& group.equals("NULL")) ||
            (fromId.equals(receiverEmail) && toId.equals(senderEmail)&& group.equals("NULL")))
            {
                Message m = new Message();
                m.setMessage(res.getString(res.getColumnIndex("CHAT")));
                m.setFromEmail(res.getString(res.getColumnIndex("FROMID")));
                m.setToEmail(res.getString(res.getColumnIndex("TOID")));
                m.setFromName(res.getString(res.getColumnIndex("FROMNAME")));
                m.setToName(res.getString(res.getColumnIndex("TONAME")));
                m.setGroupID(res.getString(res.getColumnIndex("GROUPID")));
                messages.add(m);
                Log.i("INFO","From DB Helper Latest from chat getChats"+m.getMessage()+"  "+m.getGroupID());
            }

            res.moveToNext();
        }
        return messages;
    }
    public ArrayList<Message> getAllChats()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+TABLE_NAME_FINAL, null );
        res.moveToFirst();
        ArrayList<Message> messages = new ArrayList<>();

        while(!res.isAfterLast()){

                Message m = new Message();
                m.setMessage(res.getString(res.getColumnIndex("CHAT")));
            m.setFromEmail(res.getString(res.getColumnIndex("FROMID")));
                m.setToEmail(res.getString(res.getColumnIndex("TOID")));
            m.setFromName(res.getString(res.getColumnIndex("FROMNAME")));
                m.setToName(res.getString(res.getColumnIndex("TONAME")));
            m.setGroupID(res.getString(res.getColumnIndex("GROUPID")));
            messages.add(m);
            Log.i("INFO", "From DB Helper Latest from chat getChats All Chats "+m.getMessage()+"  "+m.getGroupID());

            res.moveToNext();
        }
        return messages;
    }

    public ArrayList<Message> getGroupChats(String groupID)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+TABLE_NAME_FINAL, null );
        res.moveToFirst();
        ArrayList<Message> messages = new ArrayList<>();

        while(!res.isAfterLast()){

            String id = res.getString(res.getColumnIndex("GROUPID"));
            if(id.equals(groupID))
            {
                Message m = new Message();
                m.setMessage(res.getString(res.getColumnIndex("CHAT")));
                m.setFromEmail(res.getString(res.getColumnIndex("FROMID")));
                m.setToEmail(res.getString(res.getColumnIndex("TOID")));
                m.setFromName(res.getString(res.getColumnIndex("FROMNAME")));
                m.setToName(res.getString(res.getColumnIndex("TONAME")));
                m.setGroupID(res.getString(res.getColumnIndex("GROUPID")));
                messages.add(m);
                Log.i("INFO","From DB Helper Latest from chat getChats"+m.getMessage());
            }

            res.moveToNext();
        }
        return messages;
    }

    public void deleteGroupChat(String groupId)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_FINAL, "GROUPID ='"+groupId+"'", null);
    }
    public boolean delete()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_FINAL, null, null);
        return true;
    }

    public HashMap<String,String> GetLastMessage(ArrayList<String> allFriends)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+TABLE_NAME_FINAL, null );
        res.moveToFirst();
        ArrayList<Message> messages = new ArrayList<>();

        while(!res.isAfterLast()) {

            String fromID = res.getString(res.getColumnIndex("FROMID"));
            String toID   = res.getString(res.getColumnIndex("TOID"));
            for(int i=0;i<allFriends.size();i++)
            {

            }
            res.moveToNext();
        }
            return null;
    }

}
