package com.pcsma.project;

import java.util.ArrayList;

import org.springframework.data.annotation.Id;

public class Student {
	
	@Id
	private String email;
	private String name;
	private String regId;
	private String rollno;
	private String password;
	private String picUrl;
	private ArrayList<String> skillSet=new ArrayList<String>();
	private ArrayList<String> recommendSkillSet = new ArrayList<String>();
	private ArrayList<String> course=new ArrayList<String>();
	private ArrayList<String> courseTA=new ArrayList<String>();
	private ArrayList<String> myTask=new ArrayList<String>();
	private ArrayList<String> tATask=new ArrayList<String>();
	
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
	public String getRollno() {
		return rollno;
	}
	public void setRollno(String rollno) {
		this.rollno = rollno;
	}
	public ArrayList<String> getSkillSet() {
		return skillSet;
	}
	public void setSkillSet(ArrayList<String> skillSet) {
		this.skillSet = skillSet;
	}
	public ArrayList<String> getCourse() {
		return course;
	}
	public void setCourse(ArrayList<String> course) {
		this.course = course;
	}
	public ArrayList<String> getCourseTA() {
		return courseTA;
	}
	public void setCourseTA(ArrayList<String> courseTA) {
		this.courseTA = courseTA;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRegId() {
		return regId;
	}
	public void setRegId(String regId) {
		this.regId = regId;
	}
	public String getPicUrl() {
		return picUrl;
	}
	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
	public ArrayList<String> getMyTask() {
		return myTask;
	}
	public void setMyTask(ArrayList<String> myTask) {
		this.myTask = myTask;
	}
	public ArrayList<String> getTATask() {
		return tATask;
	}
	public void setTATask(ArrayList<String> tATask) {
		tATask = tATask;
	}
	public ArrayList<String> getRecommendSkillSet() {
		return recommendSkillSet;
	}
	public void setRecommendSkillSet(ArrayList<String> recommendSkillSet) {
		this.recommendSkillSet = recommendSkillSet;
	}
	

}
