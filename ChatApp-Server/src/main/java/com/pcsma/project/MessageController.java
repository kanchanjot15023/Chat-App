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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

	@Autowired
	StudentRepository studentRepository;
	
	
	@Autowired
	CourseRepository courseRepository;
	
	@Autowired
	TaskRepository taskRepository;
	
	
	@Autowired
	MessageRepository messageRepository;
	
	
	@Autowired
	MongoTemplate mongoTemplate;	
	
	@RequestMapping(value="/fetchMessage",method = RequestMethod.GET)
	public HashMap<String,String> Signin(@RequestParam("task") String taskId,HttpSession session,Model model){
		
		int length;
		
		ArrayList<String> messageId=new ArrayList<String>();
		
		int k=0;
		
		HashMap<String,String> response = new HashMap<String,String>();
		if(taskRepository.findById(taskId) == null)
		{
			response.put("status","error" )    ;
			response.put("msg","Could Not Fetch Data Items");
			return response                    ;  
		}
		else
		{
			response.put("status","success" )    ;
			if(taskRepository.findById(taskId).getMessageId() == null)
			{
				
				
				length=0;
			}
				else	
				{
					
					for(int i=0;i<taskRepository.findById(taskId).getMessageId().size();i++)
					{
						messageId.add(taskRepository.findById(taskId).getMessageId().get(i));
					}
					
					length=taskRepository.findById(taskId).getMessageId().size();
					
				
				}
			
			
			response.put("message"+k,taskRepository.findById(taskId).getDescription());
			response.put("send"+k,taskRepository.findById(taskId).getFrom());
			k++;
			
			for(int i=0;i<length;i++)
			{
				response.put("message"+k,messageRepository.findById(messageId.get(i)).getMessage()); 
				response.put("send"+k,messageRepository.findById(messageId.get(i)).getFrom());
				
				k++;
			}
			
			length=length+1;
			response.put("length", Integer.toString(length));
			return response;
			                                           
		}
	
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@RequestMapping(value="/sendMessage",method = RequestMethod.POST)
	public HashMap<String,String> Signin1(@RequestParam("task") String taskId,@RequestParam("emailF") String emailF,@RequestParam("emailT") String emailT,@RequestParam("message") String message,HttpSession session,Model model){
		
		String course="";
		String[] courses;
		
		int length=0;
		
		HashMap<String,String> response = new HashMap<String,String>();
		if(taskRepository.findById(taskId) == null)
		{
			response.put("status","error" )    ;
			response.put("msg","Could Not Fetch Data Items");
			return response                    ;  
		}
		else
		{
			
			if(taskRepository.findById(taskId).getMessageId()==null)
				length=0;
			
			else
				length=taskRepository.findById(taskId).getMessageId().size();
			
			
			response.put("status","success" )    ;
			
			length=length+1;
			
	Message m=new Message();
	
	String id=taskId+"-"+length;
	m.setId(id);
	m.setFrom(emailF);
	m.setTo(emailT);
	m.setMessage(message);

	messageRepository.save(m);
	
	courses=taskId.split("-");
	course=courses[0];
	
	mongoTemplate.upsert(new Query(Criteria.where("id").is(taskId)),new Update().push("messageId",id),Task.class);
	
	
	Task t=new Task();
	t=taskRepository.findById(taskId);
	
	mongoTemplate.upsert(new Query(Criteria.where("id").is(course)),new Update().pull("task",new Query(Criteria.where("id").is(taskId))),Course.class);
	mongoTemplate.upsert(new Query(Criteria.where("id").is(course)),new Update().push("task",t),Course.class);
			
		
			 
			return response;
		}
	
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
