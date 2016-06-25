package com.pcsma.project;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpSession;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AdminDashboardController {

	@Autowired HttpSession session;
	
	@Autowired FacultyRepository facultyRepository;
	@Autowired CourseRepository courseRepository;
	@Autowired StudentRepository studentRepository;
	@Autowired MessageRepository messageRepository;
	@Autowired TaskRepository    taskRepository;
	@RequestMapping(value="/adminProfile",method = RequestMethod.GET)
	public String viewProfile(HttpSession session,Model model)
	{
		if(session.getAttribute("sessionId") == null)
		{
			model.addAttribute("msg", "Please login first");
			return "errorfile"                             ;
		}
		else
		{
			ArrayList<String> courseId;    
			HashMap<String,String> courses = new HashMap<String,String>();
			courseId   = facultyRepository.findByEmail(session.getAttribute("email").toString()).getCourseId();
			if(courseId == null)
			{
				courseId = new ArrayList<String>();
			}
			else
			{
				for(int i=0;i<courseId.size();i++)
				{
					Course c = courseRepository.findById(courseId.get(i));
					courses.put(c.getId(), c.getName());
					
					
				}
			}
			model.addAttribute("courses",courses);
			model.addAttribute("title","Dr. ");
			return "index";
		}

	}
	@RequestMapping(value="/adminLogout",method = RequestMethod.GET)
	public String  Logout(Model model){
		if(session.getAttribute("sessionId") != null)   
		{

			session.invalidate();
			model.addAttribute("msg","You are successfully logout !!");
			return "errorfile";
		}
		else
		{
			model.addAttribute("msg","Please signin first ");
			return "errorfile";
		}


	}
	
	@RequestMapping(value="/abcdefgh",method = RequestMethod.GET)
	public String  Insert(Model model){
		
		//Courses 
		//Course Details
				/*Course c1 = courseRepository.findById("CSE635");
				Task t1=new Task();
				t1.setId("CSE635-1");
				t1.setTitle("Spring Security");
				t1.setDescription("How to implement Spring Security in spring Boot");
				t1.setTo("JOY1410@IIITD.AC.IN");
				t1.setFrom("NAVEEN15038@IIITD.AC.IN");
				ArrayList<String> messageId=new ArrayList<>();
				messageId.add("CSE635-1-1");
				messageId.add("CSE635-1-2");
				t1.setMessageId(messageId);
				t1.setFinished(true);
				t1.setComment("Have good knowlwdge of Spring Boot.");
				t1.setRating("3");
				t1.setShowToTA(true);
				Date date=new Date();
				t1.setDate(date);
				ArrayList<Task> task=new ArrayList<>();
				task.add(t1);
				c1.setTask(task);
                courseRepository.save(c1);
                
                Message m1=new Message();
        		m1.setId("CSE635-1-1");
        		m1.setMessage("I had created  boot project but still getting error in POM file");
        		m1.setTo("JOY1410@IIITD.AC.IN");
        		m1.setFrom("NAVEEN15038@IIITD.AC.IN");

        		Message m2=new Message();
        		m2.setId("CSE635-1-2");
        		m2.setMessage("Meet me tomorrow in TA Hours.");
        		m2.setTo("NAVEEN15038@IIITD.AC.IN");
        		m2.setFrom("JOY1410@IIITD.AC.IN");

        		messageRepository.save(m1);
        		messageRepository.save(m2); */

				/*
		
		//Student Details			
				Student s1 = new Student();
				s1.setEmail("NAVEEN15038@IIITD.AC.IN");
				s1.setName("Naveen Kumar Patidar");
				s1.setRegId("naveenpatidar");
				s1.setRollno("MT15038");
				s1.setPassword("null");
				s1.setPicUrl("https://lh4.googleusercontent.com/-1yxv105-9M4/AAAAAAAAAAI/AAAAAAAAACM/1zeX2Cx_qJo/photo.jpg?sz=400");
				ArrayList<String> skillSet = new ArrayList<>();
				skillSet.add("C");
				skillSet.add("Java");
				skillSet.add("ANDROID");
				s1.setSkillSet(skillSet);
				ArrayList<String> stcourse = new ArrayList<>();
				stcourse.add("CSE635");
				stcourse.add("CSE507");
				s1.setCourse(stcourse);
				ArrayList<String> myTask = new ArrayList<>();
				myTask.add("CSE635-1");
				s1.setMyTask(myTask);
				studentRepository.save(s1);
				
				Student s2 = new Student();
				s2.setEmail("JOY1410@IIITD.AC.IN");
				s2.setName("Joy Aneja");
				s2.setRegId("joyaneja");
				s2.setRollno("MT1410");
				s2.setPassword("null");
				s2.setPicUrl("https://lh3.googleusercontent.com/--6UTepFLS9c/AAAAAAAAAAI/AAAAAAAAAB0/NEJQq2fR4BU/photo.jpg?sz=400");
                skillSet = new ArrayList<>();
				skillSet.add("C");
				skillSet.add("Android");
				s2.setSkillSet(skillSet);
                ArrayList<String> courseTA = new ArrayList<>();
				courseTA.add("CSE635");
				s2.setCourseTA(courseTA);
				ArrayList<String> TATask = new ArrayList<>();
				TATask.add("CSE635-1");
				s2.setTATask(TATask);
				ArrayList<String> stcourse1 = new ArrayList<>();
				stcourse1.add("CSE507");
				s2.setCourse(stcourse1);
				studentRepository.save(s2);
				*/
				Task t =taskRepository.findById("CSE635-1");
			    taskRepository.delete(t);
				return "";
	}
}
	


