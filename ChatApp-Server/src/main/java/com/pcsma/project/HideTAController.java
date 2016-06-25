package com.pcsma.project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HideTAController {

	@Autowired StudentRepository studentRepository;
	@Autowired CourseRepository  courseRepository;
	
	
	// status 1 means show TA button and 0 means hide TA button
	@RequestMapping(value="/hideTA",method = RequestMethod.GET)
	public HashMap<String,String> ShowCoursePage(@RequestParam("email") String email){
	  
		email = email.toUpperCase();
		List<Course>       courses = courseRepository.findAll(); 
		HashMap<String,String> response = new HashMap<String,String>();
		if(courses == null)
		{
			response.put("status","0");
			
		}
		else
		{
			String status="0";
			for(int i=0;i<courses.size();i++)
			{
				ArrayList<String> taList = courses.get(i).getTaId();
				if(taList == null)
				{
					break;
				}
				if(taList.size() == 0)
				{
					break;
				}
				for(int j=0;j<taList.size();j++)
				{
					if(taList.get(j).toUpperCase().equals(email))
					{
						status = "1";
						break;
					}
				}
				if(status.equals("1"))
				{
					break;
				}
			}
			response.put("status", status);
		}
		
		return response;
	}
}
