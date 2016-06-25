package com.assignment4.chatapp;

/**
 * Created by kapish on 07-04-2016.
 */
public class Contact {

        String email;
        String name;
        String status;
        String picURL;
        public Contact(String email, String name, String status, String picURL)
        {
            this.email  = email;
            this.name   = name;
            this.status = status;
            this.picURL = picURL;
        }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPicURL() {
        return picURL;
    }

    public void setPicURL(String picURL) {
        this.picURL = picURL;
    }
}
