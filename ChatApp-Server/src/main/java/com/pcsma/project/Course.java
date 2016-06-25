package com.pcsma.project;

import java.util.ArrayList;

import org.springframework.data.annotation.Id;

public class Course {
	
	@Id
	private String id;
	private String name;
	private ArrayList<String> studentId;
	private ArrayList<String> taId;
	private ArrayList<Task> task;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<String> getStudentId() {
		return studentId;
	}
	public void setStudentId(ArrayList<String> studentId) {
		this.studentId = studentId;
	}
	public ArrayList<String> getTaId() {
		return taId;
	}
	public void setTaId(ArrayList<String> taId) {
		this.taId = taId;
	}
	public ArrayList<Task> getTask() {
		return task;
	}
	public void setTask(ArrayList<Task> task) {
		this.task = task;
	}

}
