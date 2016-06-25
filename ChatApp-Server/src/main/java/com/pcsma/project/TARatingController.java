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
public class TARatingController {

	
	
	@Autowired
	StudentRepository studentRepository;
	@Autowired
	MongoTemplate mongoTemplate;
	
	
	
	@Autowired
	TaskRepository taskRepository;
	
	@RequestMapping(value="/commentShow",method = RequestMethod.POST)
	public HashMap<String,String> Register(@RequestParam("taskId") String taskId,@RequestParam("emailT") String emailT,@RequestParam("emailF") String emailF,@RequestParam("rating") String rating,@RequestParam("comment") String comment,@RequestParam("recommended") String recommended,@RequestParam("show") String show,HttpSession session,Model model){
		
		
		String course="";
		String[] courses;
		System.out.println("taskId"+taskId);
		
		//System.out.println(s.getEmail()+"  "+s.getName()+"  "+s.getPassword()+"  "+s.getRegisterationId().toString());
		HashMap<String,String> response = new HashMap<String,String>();
		if(taskRepository.findById(taskId) == null)
		{
			response.put("status","error" )    ;
			response.put("msg","Could Not Fetch Data Items");
			return response                    ;  
		}
		else
		{
			Task t=new Task();
			t=taskRepository.findById(taskId);
			t.setComment(comment);
			t.setFrom(emailF);
			t.setTo(emailT);
			t.setRating(rating);
			t.setShowToTA(true);
			taskRepository.save(t);
			
			courses=taskId.split("-");
			course=courses[0];
			
			mongoTemplate.upsert(new Query(Criteria.where("id").is(course)),new Update().pull("task",new Query(Criteria.where("id").is(taskId))),Course.class);
			mongoTemplate.upsert(new Query(Criteria.where("id").is(course)),new Update().push("task",t),Course.class);
			mongoTemplate.upsert(new Query(Criteria.where("email").is(emailT)),new Update().push("recommendSkillSet",recommended),Student.class);
			
			response.put("status","success" )    ;
			return response;                                            
		}
	
		
	}
	
	
	
	
	
	
	
	
	
	@RequestMapping(value="/comment",method = RequestMethod.POST)
	public HashMap<String,String> Register1(@RequestParam("taskId") String taskId,@RequestParam("emailT") String emailT,@RequestParam("emailF") String emailF,@RequestParam("rating") String rating,@RequestParam("comment") String comment,@RequestParam("recommended") String recommended,@RequestParam("show") String show,HttpSession session,Model model){
		
		String course="";
		String[] courses;
		
		//System.out.println(s.getEmail()+"  "+s.getName()+"  "+s.getPassword()+"  "+s.getRegisterationId().toString());
		HashMap<String,String> response = new HashMap<String,String>();
		if(taskRepository.findById(taskId) == null)
		{
			response.put("status","error" )    ;
			response.put("msg","Could Not Fetch Data Items");
			return response                    ;  
		}
		else
		{
			Task t=new Task();
			t=taskRepository.findById(taskId);
			t.setComment(comment);
			t.setFrom(emailF);
			t.setTo(emailT);
			t.setRating(rating);
			t.setShowToTA(false);
			taskRepository.save(t);
			
			
			
			courses=taskId.split("-");
			course=courses[0];
			
			mongoTemplate.upsert(new Query(Criteria.where("id").is(course)),new Update().pull("task",new Query(Criteria.where("id").is(taskId))),Course.class);
			mongoTemplate.upsert(new Query(Criteria.where("id").is(course)),new Update().push("task",t),Course.class);
			
			mongoTemplate.upsert(new Query(Criteria.where("email").is(emailT)),new Update().push("recommendSkillSet",recommended),Student.class);
			
			response.put("status","success" )    ;
			return response;                                            
		}
	
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
