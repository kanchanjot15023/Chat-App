package com.pcsma.project;

import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ViewTAController {

	@Autowired
	CourseRepository courseRepository;
	
	@Autowired
	StudentRepository studentRepository;
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	
	@RequestMapping(value="/viewTA",method = RequestMethod.GET)
	public HashMap<String,String> Signin(@RequestParam("course") String course,@RequestParam("code") String code,HttpSession session,Model model){
		
		int length;
		int k=0;
		int len;
		
		HashMap<String,String> response = new HashMap<String,String>();
		if(courseRepository.findById(course) == null)
		{
			response.put("status","error" )    ;
			response.put("msg","Could Not Fetch Data Items");
			return response                    ;  
		}
		else
		{
			response.put("status","success" )    ;
	
			if(courseRepository.findById(course).getTaId() == null)
			{
				
				response.put("length", "0");
				length=0;
			}
				else	
				{
					
				length=courseRepository.findById(course).getTaId().size();
				
			for(int i=0;i<length;i++)
			{
				
				if(studentRepository.findByEmail(courseRepository.findById(course).getTaId().get(i)).getRecommendSkillSet() !=null)
				{
					if(studentRepository.findByEmail(courseRepository.findById(course).getTaId().get(i)).getRecommendSkillSet().contains(code))
					{
						response.put("ta"+k,courseRepository.findById(course).getTaId().get(i)); 

						
						if(studentRepository.findByEmail(courseRepository.findById(course).getTaId().get(i)).getRecommendSkillSet() == null)
				response.put("len"+k, "0");
			
						else
						{
							len=studentRepository.findByEmail(courseRepository.findById(course).getTaId().get(i)).getRecommendSkillSet().size();
							response.put("len"+k, Integer.toString(len));
							
							for(int j=0;j<len;j++)
							{
								response.put("recommended"+k+"-"+j,studentRepository.findByEmail(courseRepository.findById(course).getTaId().get(i)).getRecommendSkillSet().get(j) );
							}
							
						}
			
			
			
						k=k+1;
					}
					
					else{
						if(studentRepository.findByEmail(courseRepository.findById(course).getTaId().get(i)).getSkillSet().contains(code))
						{
							response.put("ta"+k,courseRepository.findById(course).getTaId().get(i)); 
							
							
							if(studentRepository.findByEmail(courseRepository.findById(course).getTaId().get(i)).getRecommendSkillSet() == null)
								response.put("len"+k, "0");
							
										else
										{
											len=studentRepository.findByEmail(courseRepository.findById(course).getTaId().get(i)).getRecommendSkillSet().size();
											response.put("len"+k, Integer.toString(len));
											
											for(int j=0;j<len;j++)
											{
												response.put("recommended"+k+"-"+j,studentRepository.findByEmail(courseRepository.findById(course).getTaId().get(i)).getRecommendSkillSet().get(j) );
											}
											
										}
							
							
							
							k=k+1;
						}
					}
				}
				else 
				{
					if(studentRepository.findByEmail(courseRepository.findById(course).getTaId().get(i)).getSkillSet().contains(code))
					{
						response.put("ta"+k,courseRepository.findById(course).getTaId().get(i)); 
						
						
						
						
						if(studentRepository.findByEmail(courseRepository.findById(course).getTaId().get(i)).getRecommendSkillSet() == null)
							response.put("len"+k, "0");
						
									else
									{
										len=studentRepository.findByEmail(courseRepository.findById(course).getTaId().get(i)).getRecommendSkillSet().size();
										response.put("len"+k, Integer.toString(len));
										
										for(int j=0;j<len;j++)
										{
											response.put("recommended"+k+"-"+j,studentRepository.findByEmail(courseRepository.findById(course).getTaId().get(i)).getRecommendSkillSet().get(j) );
										}
										
									}
						
						
						
						k=k+1;
					}
				}
			}
			response.put("length", Integer.toString(k));
			
		}
	
			return response;
	}

	
}
	
	
	
	
	
	
	
	@RequestMapping(value="/viewAllTA",method = RequestMethod.GET)
	public HashMap<String,String> Signin1(@RequestParam("course") String course,@RequestParam("code") String code,HttpSession session,Model model){
		
		int length;
		int k=0;
		int len;
		
		HashMap<String,String> response = new HashMap<String,String>();
		if(courseRepository.findById(course) == null)
		{
			response.put("status","error" )    ;
			response.put("msg","Could Not Fetch Data Items");
			return response                    ;  
		}
		else
		{
			response.put("status","success" )    ;
	
			if(courseRepository.findById(course).getTaId() == null)
			{
				
				response.put("length", "0");
				length=0;
			}
				else	
				{
					
				length=courseRepository.findById(course).getTaId().size();
				
			for(int i=0;i<length;i++)
			{
				
				response.put("ta"+i,courseRepository.findById(course).getTaId().get(i));
				
				if(studentRepository.findByEmail(courseRepository.findById(course).getTaId().get(i)) != null)
				{
				if(studentRepository.findByEmail(courseRepository.findById(course).getTaId().get(i)).getRecommendSkillSet() == null)
					response.put("len"+i, "0");
				
							else
							{
								len=studentRepository.findByEmail(courseRepository.findById(course).getTaId().get(i)).getRecommendSkillSet().size();
								response.put("len"+i, Integer.toString(len));
								
								for(int j=0;j<len;j++)
								{
									response.put("recommended"+i+"-"+j,studentRepository.findByEmail(courseRepository.findById(course).getTaId().get(i)).getRecommendSkillSet().get(j) );
								}
								
							}
			
				}
			}
			response.put("length", Integer.toString(length));
			
		}
	
			return response;
	}
		
	
	
	
	
	}
	
	@RequestMapping(value="/nameTA",method = RequestMethod.GET)
	public HashMap<String,String> Signin1(@RequestParam("email") String email){
    
		email = email.toUpperCase();
		Student s = studentRepository.findByEmail(email);
		HashMap<String,String> response = new HashMap<String,String>();
		if(s == null)
		{
			response.put("status", "error");
			
		}
		else
		{
			String name = s.getName();
			String roll = s.getRollno();
			if(name != null && roll != null)
			{
				response.put("status", "success");
				response.put("name", name);
				response.put("roll",roll);
			}
			else
			{
				response.put("status", "error");
			}
		}
		return response;	
 	}
}