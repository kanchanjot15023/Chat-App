package com.pcsma.project;

import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class StudentAddCourseController {
	
	
	
	
	@Autowired
	StudentRepository studentRepository;
	
	@Autowired
	CourseRepository courseRepository;
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	@RequestMapping(value="/studentCourse",method = RequestMethod.POST)
	public HashMap<String,String> Register(@ModelAttribute Student s,@RequestParam("course") String courseId,
	HttpSession session,Model model){
		int length=0;
		System.out.println(s.getEmail()+"  "+s.getCourse().get(0));
		HashMap<String,String> response = new HashMap<String,String>();
		if(studentRepository.findByEmail(s.getEmail()) != null  )
		{
			
			if(courseRepository.findById(s.getCourse().get(0)) == null)
			{
				response.put("status","error")      ;
				response.put("msg","Course Not Present")        ;
				return response ;
			}
			
			else
			{
				if(courseRepository.findById(s.getCourse().get(0)).getStudentId() == null)
				{
					
					length=0;
				}
					else	
					{
						
					length=courseRepository.findById(s.getCourse().get(0)).getStudentId().size();
					}
				System.out.println(length);
				
				
				for(int i=0;i<length;i++)
				{
					String compareStr=(courseRepository.findById(s.getCourse().get(0)).getStudentId().get(i));
					System.out.println(compareStr+" "+s.getEmail());
					if(compareStr.equals(s.getEmail()))      //change here
					{
						if(!studentRepository.findByEmail(s.getEmail()).getCourse().contains(courseId))
						mongoTemplate.upsert(new Query(Criteria.where("email").is(s.getEmail())),new Update().push("course",s.getCourse().get(0)),Student.class);
						response.put("status","success")      ;
					   // System.out.println("Id is "+studentRepository.fidByEmail(s.getEmail()).getRegisterationId().toString());
					    return response                    ;	 
						
					}
				}
				
				
				
				
					response.put("status","error")      ;
					response.put("msg","You are not Registered for this course")        ;
					return response ;
				
				
			
				
				
			}
		   
			
		}
		else
		{
			response.put("status","error")      ;
			response.put("msg","Could Not Add Course")        ;
			return response                    ;                                             
		}
	
		
	}
	
	
	
	
	
	
	
	
	
	@RequestMapping(value="/studentCourseRemove",method = RequestMethod.POST)
	public HashMap<String,String> Remove(@ModelAttribute Student s,HttpSession session,Model model){
		int length=0;
		//System.out.println(s.getEmail()+"  "+s.getName()+"  "+s.getPassword()+"  "+s.getRegisterationId().toString());
		HashMap<String,String> response = new HashMap<String,String>();
		if(studentRepository.findByEmail(s.getEmail()) != null  )
		{
			
			if(studentRepository.findByEmail(s.getEmail()).getCourse() == null)
			{
				
				length=0;
			}
				else	
				{
					
				length=studentRepository.findByEmail(s.getEmail()).getCourse().size();
				}
			
			
			
			
			  //change here
			for(int i=0;i<length;i++)
			{
				String compareStr=(studentRepository.findByEmail(s.getEmail()).getCourse().get(i));
				System.out.println(compareStr+"  "+s.getCourse().get(0));
				if(compareStr.equals(s.getCourse().get(0)))      //change here
				{
				
					mongoTemplate.upsert(new Query(Criteria.where("email").is(s.getEmail())),new Update().pull("course",s.getCourse().get(0)),Student.class);
					response.put("status","success")      ;
				   // System.out.println("Id is "+studentRepository.fidByEmail(s.getEmail()).getRegisterationId().toString());
				    return response                    ;	 
					
				}
			}	
			
			
			System.out.println("hello");
			
			response.put("status","error")      ;
			response.put("msg","Wrong Entry")        ;
			return response                    ; 
			
			
			
				    
		    
		}
		else
		{
			response.put("status","error")      ;
			response.put("msg","Could Not Remove Course")        ;
			return response                    ;                                             
		}
	
		
	}
	
	
	
	
	

}
