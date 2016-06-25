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
public class RollNoController {

	
	@Autowired
	StudentRepository studentRepository;
	@Autowired
	MongoTemplate mongoTemplate;	
	
	@RequestMapping(value="/fetchRollNo",method = RequestMethod.GET)
	public HashMap<String,String> Signin(@RequestParam("email") String email,HttpSession session,Model model){
		
		int length;
		
		HashMap<String,String> response = new HashMap<String,String>();
		if(studentRepository.findByEmail(email) == null)
		{
			response.put("status","error" )    ;
			response.put("msg","Could Not Fetch Data Items");
			return response                    ;  
		}
		else
		{
			
			
			if(studentRepository.findByEmail(email).getRollno() == null)
			{
				response.put("status","error" )    ;
				response.put("msg","Could Not Fetch Data Items");
				
			}
				else	
				{
					response.put("status","success" )    ;
					response.put("rollno",studentRepository.findByEmail(email).getRollno());
				}
		
			 
			return response;
			                                           
		}
	
		
	}
	
	
}
