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
public class ReviewCommentsController {

	
	


	@Autowired
	CourseRepository courseRepository;
	
	@Autowired
	StudentRepository studentRepository;
	
	@Autowired
	TaskRepository taskRepository;
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	
	@RequestMapping(value="/reviewComments",method = RequestMethod.GET)
	public HashMap<String,String> Signin(@RequestParam("email") String email,HttpSession session,Model model){
		
		int length;
		int finalLength = 0;
		int k=0;
		
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
	
			if(studentRepository.findByEmail(email).getTATask() == null)
			{
				
				response.put("length", "0");
				length=0;
			}
				else	
				{
					length=studentRepository.findByEmail(email).getTATask().size();
					
					for(int i=0;i<length;i++)
					{
						if(taskRepository.findById(studentRepository.findByEmail(email).getTATask().get(i)).isShowToTA())
						{
							finalLength=finalLength+1;
							response.put("from"+k,taskRepository.findById(studentRepository.findByEmail(email).getTATask().get(i)).getFrom());
							//response.put("taskId"+k,taskRepository.findById(studentRepository.findByEmail(email).getMyTask().get(i)).getId());
							response.put("title"+k,taskRepository.findById(studentRepository.findByEmail(email).getTATask().get(i)).getTitle());
							response.put("rating"+k,taskRepository.findById(studentRepository.findByEmail(email).getTATask().get(i)).getRating());
							response.put("comment"+k,taskRepository.findById(studentRepository.findByEmail(email).getTATask().get(i)).getComment());
							
							k++;
						}
						
					}
					
				
					response.put("length", Integer.toString(finalLength));
				
				}
			
		
			 
			return response;
		}
	
		
	}
	
	
	
	
	
	
	
	
}
