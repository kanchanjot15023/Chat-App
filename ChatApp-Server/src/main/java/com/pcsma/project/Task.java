package com.pcsma.project;

import java.util.ArrayList;
import java.util.Date;

import org.springframework.data.annotation.Id;

public class Task {

	@Id
	private String id;   //courseId-sequence eg CSE501-1
	private String title;
	private String description;
	private String to;
	private String from;
	private String taskBy;		//student and faculty
	//private ArrayList<String> rating;		
	private ArrayList<String> messageId;	//Taskid-sequence
	private boolean isFinished;
	private String comment;
	private String rating;
	private boolean showToTA;
	private Date taskDate;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTaskBy() {
		return taskBy;
	}
	public void setTaskBy(String taskBy) {
		this.taskBy = taskBy;
	}
	//public ArrayList<String> getRating() {
		//return rating;
	//}
	//public void setRating(ArrayList<String> rating) {
		//this.rating = rating;
	//}
	public ArrayList<String> getMessageId() {
		return messageId;
	}
	public void setMessageId(ArrayList<String> messageId) {
		this.messageId = messageId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public boolean isFinished() {
		return isFinished;
	}
	public void setFinished(boolean isFinished) {
		this.isFinished = isFinished;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getRating() {
		return rating;
	}
	public void setRating(String rating) {
		this.rating = rating;
	}
	public boolean isShowToTA() {
		return showToTA;
	}
	public void setShowToTA(boolean showToTA) {
		this.showToTA = showToTA;
	}
	public Date getDate() {
		return taskDate;
	}
	public void setDate(Date date) {
		this.taskDate = date;
	}
	
}
