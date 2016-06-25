package com.pcsma.project;

import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class SkillViewController {

	
	@Autowired
	CourseRepository courseRepository;
	
	@Autowired
	StudentRepository studentRepository;
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	
	@RequestMapping(value="/viewSkill",method = RequestMethod.GET)
	public HashMap<String,String> Signin(@RequestParam("course") String course,HttpSession session,Model model){
		
		int length;
		int k=0;
		int len=0;
		int lengthofrecommended=0;
		
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
				if(studentRepository.findByEmail(courseRepository.findById(course).getTaId().get(i)) != null )	
				{
				if(studentRepository.findByEmail((courseRepository.findById(course).getTaId().get(i))).getSkillSet() == null)    
			len=0;
				else
				{
					len=studentRepository.findByEmail((courseRepository.findById(course).getTaId().get(i))).getSkillSet().size();
				}
				
				
				if(studentRepository.findByEmail((courseRepository.findById(course).getTaId().get(i))).getRecommendSkillSet() == null)    
					lengthofrecommended=0;
						else
						{
							lengthofrecommended=studentRepository.findByEmail((courseRepository.findById(course).getTaId().get(i))).getRecommendSkillSet().size();
						}
				
				
				for(int j=0;j<lengthofrecommended;j++)
				{
					response.put("skill"+k,studentRepository.findByEmail((courseRepository.findById(course).getTaId().get(i))).getRecommendSkillSet().get(j)); 
				k=k+1;
				}
				
				
				for(int j=0;j<len;j++)
				{
					response.put("skill"+k,studentRepository.findByEmail((courseRepository.findById(course).getTaId().get(i))).getSkillSet().get(j)); 
				k=k+1;
				}
				}
			}
		} 
			
			response.put("length", Integer.toString(k));
			return response;
		}
	
		
	}
	
	
	
}
