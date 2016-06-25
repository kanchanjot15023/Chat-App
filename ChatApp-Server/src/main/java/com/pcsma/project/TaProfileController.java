package com.pcsma.project;

import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TaProfileController {

	@Autowired
	StudentRepository studentRepository;
	@Autowired
	MongoTemplate mongoTemplate;	
	
	@RequestMapping(value="/taProfile",method = RequestMethod.GET)
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
			response.put("status","success" )    ;
			response.put("email",email )    ;
			if(studentRepository.findByEmail(email).getCourseTA() == null)
			{
				
				response.put("length", "0");
				length=0;
			}
				else	
				{
					response.put("length", Integer.toString(studentRepository.findByEmail(email).getCourseTA().size()));
				length=studentRepository.findByEmail(email).getCourseTA().size();
				}
			for(int i=0;i<length;i++)
			{
				response.put("course"+i,studentRepository.findByEmail(email).getCourseTA().get(i)); 
			}
			 
			return response;
			                                           
		}
	
		
	}
	
	
}
