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
public class NotificationController {

	
	
	
	

	@Autowired
	CourseRepository courseRepository;
	
	@Autowired
	StudentRepository studentRepository;
	
	@Autowired
	TaskRepository taskRepository;
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	
	@RequestMapping(value="/viewTATask",method = RequestMethod.GET)
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
						if(!taskRepository.findById(studentRepository.findByEmail(email).getTATask().get(i)).isFinished())
						{
							finalLength=finalLength+1;
							response.put("from"+k,taskRepository.findById(studentRepository.findByEmail(email).getTATask().get(i)).getFrom());
							response.put("taskId"+k,taskRepository.findById(studentRepository.findByEmail(email).getTATask().get(i)).getId());
							response.put("title"+k,taskRepository.findById(studentRepository.findByEmail(email).getTATask().get(i)).getTitle());
						k++;
						}
						
					}
					
				
					response.put("length", Integer.toString(finalLength));
				
				}
			
		
			 
			return response;
		}
	
		
	}
	
	
	
	
	
	

	
	
	@RequestMapping(value="/taskFinished",method = RequestMethod.POST)
	public HashMap<String,String> Signin1(@RequestParam("codeTask") String code,HttpSession session,Model model){
		
		String course="";
		String[] courses;
		
		HashMap<String,String> response = new HashMap<String,String>();
		if(taskRepository.findById(code) == null)
		{
			response.put("status","error" )    ;
			response.put("msg","Could Not Fetch Data Items");
			return response                    ;  
		}
		else
		{
			response.put("status","success" )    ;
	Task t=new Task();
	t=taskRepository.findById(code);
	t.setFinished(true);
	taskRepository.save(t);
	
	courses=code.split("-");
	course=courses[0];
	
	
	mongoTemplate.upsert(new Query(Criteria.where("id").is(course)),new Update().pull("task",new Query(Criteria.where("id").is(code))),Course.class);
	mongoTemplate.upsert(new Query(Criteria.where("id").is(course)),new Update().push("task",t),Course.class);
			
		
			 
			return response;
		}
	
		
	}
	
	
	
	
	
}
