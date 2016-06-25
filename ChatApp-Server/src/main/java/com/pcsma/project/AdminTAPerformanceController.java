package com.pcsma.project;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminTAPerformanceController {

	@Autowired FacultyRepository facultyRepository;
	@Autowired CourseRepository  courseRepository;
	@Autowired StudentRepository studentRepository;
	@Autowired TaskRepository    taskRepository;
	@Autowired MessageRepository messageRepository;
	@Autowired MongoTemplate     mongoTemplate;
	
	private static int flagMessage  = 0;
	
	@RequestMapping(value="/TAStudentDetails",method = RequestMethod.GET)
	public String  ShowAddTAPage(HttpSession session,Model model){
		if(session.getAttribute("sessionId") == null)
		{
			model.addAttribute("msg", "Please login first");
			return "errorfile";
		}
		else
		{
			Faculty f = facultyRepository.findByEmail(session.getAttribute("email").toString());
			ArrayList<String> courseIds = f.getCourseId();
			HashMap<String,String> courses = new HashMap<String,String>();
			if(courseIds == null)
			{
				courseIds = new ArrayList<String>();
			}
			for(int i=0;i<courseIds.size();i++)
			{
				Course c = courseRepository.findById(courseIds.get(i));
				courses.put(c.getId(),c.getName());
			}
			if(flagMessage == 1)
			{
				model.addAttribute("msg", "Please add TA first");
				flagMessage = 0;
			}
			else if(flagMessage == 2)
			{
				model.addAttribute("msg", "TA not registered on mobile app");
				flagMessage = 0;
			}
			else if(flagMessage == 3)
			{
				model.addAttribute("msg", "No TA exists with this email id in this course");
				flagMessage = 0;
			}
			else if(flagMessage == 4)
			{
				model.addAttribute("msg", "Not assigned any task till now");
				flagMessage = 0;
			}
			//System.out.println("I am here abc1234");
			model.addAttribute("courses",courses);
            return "selecttaforinteraction";

		}
	}
	
	@RequestMapping(value="/ViewAllChats",method = RequestMethod.GET)
	public String  ShowAllChats(@RequestParam("id") String id,@RequestParam("taId") String taId,HttpSession session,Model model){
		id   = id.toUpperCase();
		taId = taId.toUpperCase();
		if(session.getAttribute("sessionId") == null)
		{
			model.addAttribute("msg", "Please login first");
			return "errorfile";
		}
		else
		{
			Course c = courseRepository.findById(id);
			ArrayList<String> taIds = c.getTaId();
			if(taIds == null)
			{
				flagMessage = 1;
				String redirectUrl = "/TAStudentDetails";
				return "redirect:" + redirectUrl;	
				
			}
			else
			{
				int flag = 0;
				System.out.println("ta Id is "+taId);
				for(int i= 0;i<taIds.size();i++)
				{
					if(taIds.get(i).toUpperCase().equals(taId))
					{
						flag = 1;
						break;
					}
					
				}
				if(flag==0)
				{
					flagMessage = 3;
					String redirectUrl = "/TAStudentDetails";
					return "redirect:" + redirectUrl;	
					
				}
				Student s = studentRepository.findByEmail(taId.toUpperCase());
				if(s == null)
				{
					flagMessage = 2;
					String redirectUrl = "/TAStudentDetails";
					return "redirect:" + redirectUrl;
				}
				ArrayList<String> tasks = s.getTATask();
				if(tasks == null)
				{
					flagMessage = 4;
					String redirectUrl = "/TAStudentDetails";
					return "redirect:" + redirectUrl;	
				}
			    if(tasks.size() == 0)
			    {
			    	flagMessage = 4;
					String redirectUrl = "/TAStudentDetails";
					return "redirect:" + redirectUrl;
			    }
			    int isPresent = 0;
			    HashMap<String,String> chats = new HashMap<String,String>();
			   for(int i=0;i<tasks.size();i++)
			   {
				   if(tasks.get(i).split("-")[0].toUpperCase().equals(id))
				   {
					   isPresent = 1;
					   Task t             = taskRepository.findById(tasks.get(i).toUpperCase());
					   String date        = t.getDate().toString();
					   String title       = t.getTitle();
					   String taskId      = t.getId();
					   String from        = t.getFrom();
					   Student st         = studentRepository.findByEmail(from);
					   String fromName    = st.getName();
					   String toName      = s.getName();
					   
					   chats.put(taskId, "Discussion Regarding "+title+" between "+fromName+" and "+toName+" held on "+date);
					   
					   
				   }
			    }
			    if(isPresent == 0)
			    {
			    	flagMessage = 4;
					String redirectUrl = "/TAStudentDetails";
					return "redirect:" + redirectUrl;
			    }
			    model.addAttribute("chats", chats);
			    return "viewallchats";
				
		   }
			
		}
	}
	
	@RequestMapping(value="/TAStudentDetails",method = RequestMethod.POST)
	public String  FetchChat(@RequestParam("id") String id,HttpSession session,Model model){
		id   = id.toUpperCase();
		if(session.getAttribute("sessionId") == null)
		{
			model.addAttribute("msg", "Please login first");
			return "errorfile";
		}
		else
		{
			Task t =  taskRepository.findById(id);
			String from  = t.getFrom().toUpperCase();
			String to    = t.getTo().toUpperCase();
			Student s1 = studentRepository.findByEmail(t.getFrom());
			Student s2 = studentRepository.findByEmail(t.getTo());
			
			String title = t.getTitle();
			String des = t.getDescription();
			boolean isFinished = t.isFinished();
			int rate = 0;
			String comments = "Rating and comments not given till now";
			int percentage = 1;
			if(isFinished && t.getRating()!= null)
			{
				rate = Integer.parseInt(t.getRating());
				comments = t.getComment();
			}
			ArrayList<String> messages = t.getMessageId();
			if(messages == null)
			{
				messages = new ArrayList<String>();
				
			}
			percentage = (rate*100)/5;
			ArrayList<Message> msgObjects = new ArrayList<Message>();
			for(int i =0 ;i<messages.size();i++)
			{
				Message m =messageRepository.findById(messages.get(i).toUpperCase());
				
				msgObjects.add(m);
			}
			model.addAttribute("studentName", s1.getName());
			model.addAttribute("taName", s2.getName());
			model.addAttribute("chatRate","Rating : "+rate);
			model.addAttribute("chatRating","width:"+percentage+"%");
			model.addAttribute("comment", comments);
			model.addAttribute("title", title);
			model.addAttribute("description",des);
			model.addAttribute("msgs",msgObjects);
			return "tastudentchat";
	        
		}
	}
	
	@RequestMapping(value="/TAPerformance",method = RequestMethod.GET)
	public String  TAPerformance(HttpSession session,Model model){
		if(session.getAttribute("sessionId") == null)
		{
			model.addAttribute("msg", "Please login first");
			return "errorfile";
		}
		else
		{
			ArrayList<String> courseIds    = facultyRepository.findByEmail(session.getAttribute("email").toString()).getCourseId();
			HashMap<String,String> courses = new HashMap<String,String>();
			if(courseIds == null)
			{
				model.addAttribute("courses",courses);
				return "selectcourseforta";
			}
			else
			{
				for(int i=0;i<courseIds.size();i++)
				{
					Course c = courseRepository.findById(courseIds.get(i));
					courses.put(c.getId(),c.getName());
				}
				model.addAttribute("courses",courses);
				if(flagMessage == 1)
				{
					model.addAttribute("msg", "No TA assigned in this course.");
					flagMessage = 0;
				}
				if(flagMessage == 2)
				{
					model.addAttribute("msg", "No Task assigned in this course.");
					flagMessage = 0;
				}
				
				return "selectcoursefortotaltask";
			}
		
		}
	}
	@RequestMapping(value="/AnalyseTotalTask",method = RequestMethod.POST)
	public String  AnalyseTotalTasks(@RequestParam("id") String id,HttpSession session,Model model){
		id   = id.toUpperCase();
		if(session.getAttribute("sessionId") == null)
		{
			model.addAttribute("msg", "Please login first");
			return "errorfile";
		}
		else
		{
			Course c = courseRepository.findById(id);
			ArrayList<String> taList = c.getTaId();
			if(taList == null)
			{
				//redirect to main page with msg "No TA assigned in this course"
				flagMessage = 1;
				String redirectUrl = "/TAPerformance";
				return "redirect:" + redirectUrl;
			}
			if(taList.size() == 0)
			{
				//redirect to main page with msg "No TA assigned in this course"
				flagMessage = 1;
				String redirectUrl = "/TAPerformance";
				return "redirect:" + redirectUrl;
			}
			ArrayList<Task> taskLists = c.getTask();
			if(taskLists == null)
			{
				//redirect to main page with msg "No Task assigned in this course"
				flagMessage = 2;
				String redirectUrl = "/TAPerformance";
				return "redirect:" + redirectUrl;
			}
			if(taskLists.size() == 0)
			{
				//redirect to main page with msg "No Task assigned in this course"
				flagMessage = 2;
				String redirectUrl = "/TAPerformance";
				return "redirect:" + redirectUrl;
			}
			int totalTask = taskLists.size();
			ArrayList<String> names = new ArrayList<String>();
			ArrayList<Integer> totalTasks = new ArrayList<Integer>();
			for(int i=0;i<taList.size();i++)
			{
				int total = 0;
				for(int j=0;j<taskLists.size();j++)
				{
					if(taskLists.get(j).getTo().toUpperCase().equals(taList.get(i).toUpperCase()))
					{
						total++;
					}
				}
				names.add(taList.get(i));
				totalTasks.add(total);
			}
			for(int i=0;i<names.size();i++)
			{
				System.out.println(names.get(i)+"   "+totalTasks.get(i));
			}
			
			model.addAttribute("names",names);
			model.addAttribute("taskCount", totalTasks);
			model.addAttribute("text","Total Task Analysis- "+id);
			return "taskdone";
		}
	}
	@RequestMapping(value="/TARatings",method = RequestMethod.POST)
	public String  GetAllRatings(@RequestParam("id") String id,HttpSession session,Model model){
		id   = id.toUpperCase();
		if(session.getAttribute("sessionId") == null)
		{
			model.addAttribute("msg", "Please login first");
			return "errorfile";
		}
		else
		{
			Course c = courseRepository.findById(id);
			ArrayList<String> taList = c.getTaId();
			if(taList == null)
			{
				//redirect to main page with msg "No TA assigned in this course"
				flagMessage = 1;
				String redirectUrl = "/TAPerformance";
				return "redirect:" + redirectUrl;
			}
			if(taList.size() == 0)
			{
				//redirect to main page with msg "No TA assigned in this course"
				flagMessage = 1;
				String redirectUrl = "/TAPerformance";
				return "redirect:" + redirectUrl;
			}
			ArrayList<Task> taskLists = c.getTask();
			if(taskLists == null)
			{
				//redirect to main page with msg "No Task assigned in this course"
				flagMessage = 2;
				String redirectUrl = "/TAPerformance";
				return "redirect:" + redirectUrl;
			}
			if(taskLists.size() == 0)
			{
				//redirect to main page with msg "No Task assigned in this course"
				flagMessage = 2;
				String redirectUrl = "/TAPerformance";
				return "redirect:" + redirectUrl;
			}
			int totalTask = taskLists.size();
			ArrayList<String> names  = new ArrayList<String>();
			ArrayList<Float> avgRate = new ArrayList<Float>();
			for(int i=0;i<taList.size();i++)
			{
				int count = 0;
				float totalRate = 0;
				for(int j=0;j<taskLists.size();j++)
				{
					if(taskLists.get(j).getTo().toUpperCase().equals(taList.get(i).toUpperCase()) && (taskLists.get(j).isFinished()))
					{
						if(taskLists.get(j).getRating() != null)
						{
							count++;
							totalRate += Integer.parseInt(taskLists.get(j).getRating());
						}
						
						
					}
					
					
					
				}
				if(count != 0)
				{
					totalRate = totalRate/count;
				}
				names.add(taList.get(i));
				avgRate.add(totalRate);
			}
			for(int i=0;i<names.size();i++)
			{
				System.out.println(names.get(i)+"   "+avgRate.get(i));
			}
			
			model.addAttribute("names",names);
			model.addAttribute("avgRate", avgRate);
			model.addAttribute("text","TAs Rating- "+id);
			return "ratinganalysis";
		}
	}
}
