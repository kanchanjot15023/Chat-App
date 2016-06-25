package com.pcsma.project;

import java.util.ArrayList;

import org.springframework.data.annotation.Id;

public class Groups {

	@Id
	private String groupId;
	private ArrayList<String> members;
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public ArrayList<String> getMembers() {
		return members;
	}
	public void setMembers(ArrayList<String> members) {
		this.members = members;
	}
	
}
