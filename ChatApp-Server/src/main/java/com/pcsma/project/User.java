package com.pcsma.project;

import java.util.ArrayList;

import org.springframework.data.annotation.Id;

public class User {

	@Id
	private String email;
	private String name ;
	private String status;
	private String picURL;
	private String registerationId;
	private ArrayList<String> friendList;
	private ArrayList<String> groupList;
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
	public String getRegisterationId() {
		return registerationId;
	}
	public void setRegisterationId(String registerationId) {
		this.registerationId = registerationId;
	}
	public ArrayList<String> getFriendList() {
		return friendList;
	}
	public void setFriendList(ArrayList<String> friendList) {
		this.friendList = friendList;
	}
	public ArrayList<String> getGroupList() {
		return groupList;
	}
	public void setGroupList(ArrayList<String> groupList) {
		this.groupList = groupList;
	}
	
}
