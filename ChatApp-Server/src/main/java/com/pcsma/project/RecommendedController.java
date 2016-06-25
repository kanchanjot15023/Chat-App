package com.pcsma.project;

import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RecommendedController {

	@Autowired
	StudentRepository studentRepository;
	@Autowired
	MongoTemplate mongoTemplate;	
	
	@RequestMapping(value="/recommendedSkill",method = RequestMethod.GET)
	public HashMap<String,String> Signin(@ModelAttribute Student s,HttpSession session,Model model){
		
		int length;
		
		HashMap<String,String> response = new HashMap<String,String>();
		if(studentRepository.findByEmail(s.getEmail()) == null)
		{
			response.put("status","error" )    ;
			response.put("msg","Could Not Fetch Data Items");
			return response                    ;  
		}
		else
		{
			response.put("status","success" )    ;
			response.put("email",s.getEmail() )    ;
			if(studentRepository.findByEmail(s.getEmail()).getRecommendSkillSet() == null)
			{
				response.put("length", "0");
				length=0;
			}
				else	
				{
					response.put("length", Integer.toString(studentRepository.findByEmail(s.getEmail()).getRecommendSkillSet().size()));
				length=studentRepository.findByEmail(s.getEmail()).getRecommendSkillSet().size();
				}
			for(int i=0;i<length;i++)
			response.put("recommended"+i,studentRepository.findByEmail(s.getEmail()).getRecommendSkillSet().get(i) )    ;               //change here
			return response;
			                                           
		}
	
		
	}
	
}
