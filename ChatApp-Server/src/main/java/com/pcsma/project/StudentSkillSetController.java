package com.pcsma.project;

import java.util.ArrayList;
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

public class StudentSkillSetController {

	
	@Autowired
	StudentRepository studentRepository;
	@Autowired
	MongoTemplate mongoTemplate;	
	
	@RequestMapping(value="/studentSkill",method = RequestMethod.GET)
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
			if(studentRepository.findByEmail(s.getEmail()).getSkillSet() == null)
			{
				response.put("length", "0");
				length=0;
			}
				else	
				{
					response.put("length", Integer.toString(studentRepository.findByEmail(s.getEmail()).getSkillSet().size()));
				length=studentRepository.findByEmail(s.getEmail()).getSkillSet().size();
				}
			for(int i=0;i<length;i++)
			response.put("skill"+i,studentRepository.findByEmail(s.getEmail()).getSkillSet().get(i) )    ;               //change here
			return response;
			                                           
		}
	
		
	}
	
	
	
	
	
	
	
	
	@RequestMapping(value="/studentSkillAdd",method=RequestMethod.POST)
	public HashMap<String,String> Remove(@RequestParam("email") String email,@RequestParam("length") int length,
			@RequestParam("skill") String skill
			,HttpSession session,Model model){
		System.out.println(email+"  "+length+"  "+skill+"  ");
		HashMap<String,String> response = new HashMap<String,String>();
		
		Student s = studentRepository.findByEmail(email);
		
		
		if(s != null)
		{
			ArrayList<String> skillArrayList = new ArrayList<String>();
			if(length==1)
			{
				String skillset=skill;
				skillArrayList.add(skillset);
				mongoTemplate.upsert(new Query(Criteria.where("email").is(s.getEmail())),new Update().push("skillSet",skillArrayList.get(0)),Student.class);
			}
			
			else
			{
				String[] skillArray = skill.split("--");
				for(int i = 0;i<skillArray.length;i++)
				{
					skillArrayList.add(skillArray[i]);
				}
				mongoTemplate.upsert(new Query(Criteria.where("email").is(s.getEmail())),new Update().push("skillSet",skillArrayList),Student.class);
			}
			
				response.put("status","success");
	            return response;
			
			
			
		    
		}
		else
		{
			response.put("status","error");
			response.put("msg","No Student exists in our database");
			return response;                                             
		}
	
		
	}
	
	
	
	
	
	
	
}
