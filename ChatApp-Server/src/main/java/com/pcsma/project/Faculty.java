package com.pcsma.project;

import java.util.ArrayList;

import org.springframework.data.annotation.Id;

public class Faculty {

	@Id
	private String email              ;
	private String name               ;
    private String password           ;
    private ArrayList<String> courseId;
	private String picURL             ;
	public Faculty()
    {
         this.courseId = new ArrayList<String>();
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public ArrayList<String> getCourseId() {
		return courseId;
	}
	public void setCourseId(ArrayList<String> courseId) {
		this.courseId = courseId;
	}
	public String getPicURL() {
		return picURL;
	}
	public void setPicURL(String picURL) {
		this.picURL = picURL;
	}
	
}
