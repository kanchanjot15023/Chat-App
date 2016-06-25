package com.pcsma.project;

import java.util.Date;
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
public class TaskController {

	
	@Autowired
	CourseRepository courseRepository;
	
	@Autowired
	TaskRepository taskRepository;
	
	@Autowired
	MongoTemplate mongoTemplate;	
	
	@RequestMapping(value="/askTA",method = RequestMethod.POST)
	public HashMap<String,String> Signin(@RequestParam("course") String course,@RequestParam("emailF") String emailF,@RequestParam("emailT") String emailT,@RequestParam("title") String title,@RequestParam("description") String description,HttpSession session,Model model){
		
		int length;
		
		HashMap<String,String> response = new HashMap<String,String>();
		if(courseRepository.findById(course) == null)
		{
			response.put("status","error" )    ;
			response.put("msg","Could Not Fetch Data Items");
			return response                    ;  
		}
		else
		{
			if(courseRepository.findById(course).getTask()==null)
				length=1;
			else
			{
				length=courseRepository.findById(course).getTask().size();
			length=length+1;
			}
			Task task=new Task();
			task.setId(course+"-"+length);
			task.setTo(emailT);
			task.setFrom(emailF);
			task.setTitle(title);
			task.setDescription(description);
			Date dt = new Date();
			task.setDate(dt);
			taskRepository.save(task);
			mongoTemplate.upsert(new Query(Criteria.where("id").is(course)),new Update().push("task",task),Course.class);
			mongoTemplate.upsert(new Query(Criteria.where("email").is(emailT)),new Update().push("tATask",task.getId()),Student.class);
			mongoTemplate.upsert(new Query(Criteria.where("email").is(emailF)),new Update().push("myTask",task.getId()),Student.class);
			
			response.put("status","success" )    ;
		return response;
			
			
			//ends here
		}
	
		
	}
	
	
}

	
	
	

