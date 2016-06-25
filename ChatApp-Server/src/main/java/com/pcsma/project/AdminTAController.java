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
public class AdminTAController {

	@Autowired FacultyRepository facultyRepository;
	@Autowired CourseRepository  courseRepository;
	@Autowired StudentRepository studentRepository;
	@Autowired MongoTemplate     mongoTemplate;
	
	private static int flag = 0;
	private static String dupEmail = null;
	private static int flagNoTA  = 0;
	private static int flagRemovedTA = 0;
	
	@RequestMapping(value="/AddTA",method = RequestMethod.GET)
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
			if(flag == 1)
			{
				if(dupEmail == null)
				{
					model.addAttribute("msg","TA added successfully");
					flag = 0;
				}
				else
				{
					model.addAttribute("msg","TA with email id "+dupEmail+" already exists");
					dupEmail = null;
					flag = 0;
				}
				
			}
			model.addAttribute("courses",courses);
            return "addta";
		}

	}

	@RequestMapping(value="/AddTA",method = RequestMethod.POST)
	public String AddTA(HttpSession session,@RequestParam("id") String id,@RequestParam("taId") String taId,Model model)
	{
		if(session.getAttribute("sessionId") == null)
		{
			model.addAttribute("msg", "Please login first");
			return "errorfile";
		}
		else
		{
			Course c = courseRepository.findById(id);
			if(c == null)
			{
				model.addAttribute("msg", "Selected course does not exist");
				return "addta";
			}
			else
			{
				ArrayList<String> taIds = c.getTaId();
				if(taIds == null)
				{
					taIds = new ArrayList<String>();
				}
				for(int i = 0;i<taIds.size();i++)
				{
					if(taIds.get(i).equals(taId.toUpperCase()))
					{
						dupEmail = taId;
						flag     = 1;
						String redirectUrl = "/AddTA";
						return "redirect:" + redirectUrl;
					    
					}
				}
				taIds.add(taId.toUpperCase());
				c.setTaId(taIds);
			    courseRepository.save(c);
			    flag = 1;  
			    String redirectUrl = "/AddTA";
				return "redirect:" + redirectUrl;
			    
			}
            
            
		}
	}
	
	@RequestMapping(value="/RemoveTA",method = RequestMethod.GET)
	public String SelectCourse(HttpSession session,Model model)
	{
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
				if(flagNoTA == 1)
				{
					model.addAttribute("msg", "Please add TA first");
					flagNoTA = 0;
				}
				if(flagRemovedTA == 1)
				{
					model.addAttribute("msg", "TAs removed successfully");
					flagRemovedTA = 0;
				}
				return "selectcourseforta";
			}
			
		}
	}
	
	@RequestMapping(value="/ViewAndRemoveTA",method = RequestMethod.GET)
	public String ViewAndRemoveTA(@RequestParam("id") String courseId,HttpSession session,Model model)
	{
		if(session.getAttribute("sessionId") == null)
		{
			model.addAttribute("msg", "Please login first");
			return "errorfile";
		}
		else
		{
			Course c = courseRepository.findById(courseId);
			ArrayList<String> tas = c.getTaId();
			if(tas == null)
			{
				flagNoTA = 1;
				String redirectUrl = "/RemoveTA";
				return "redirect:" + redirectUrl;
			}
			else if(tas.size() == 0)
			{
				flagNoTA = 1;
				String redirectUrl = "/RemoveTA";
				return "redirect:" + redirectUrl;
			}
			HashMap<String,String> taList = new HashMap<String,String>();
			for(int i=0;i<tas.size();i++)
			{
				taList.put(""+i, tas.get(i).toLowerCase());
			}
			model.addAttribute("tas",taList);
			model.addAttribute("id",courseId);
			return "viewandremoveTA";
			
		}
		
	}
	
	@RequestMapping(value="/RemoveTA",method = RequestMethod.POST)
	public String RemoveTA(@RequestParam("ids") String []emailIds,@RequestParam("courseId") String courseId,HttpSession session,Model model)
	{
		if(session.getAttribute("sessionId") == null)
		{
			model.addAttribute("msg", "Please login first");
			return "errorfile";
		}
		else
		{
			//System.out.println("No of TAs selected :" + emailIds.length+"  "+courseId);
			for(int i=0;i<emailIds.length;i++)
			{
				//System.out.println(emailIds[i]);
				Course c = courseRepository.findById(courseId);
				ArrayList<String> tas = c.getTaId();
				if(tas == null)
				{
					//rare case
					tas = new ArrayList<String>();
				}
				for(int j=0;j<tas.size();j++)
				{
					if(tas.get(j).equals(emailIds[i].toUpperCase()))
					{
						tas.remove(j);
					}
					
				}
				c.setTaId(tas);
				courseRepository.save(c);
				Student s = studentRepository.findByEmail(emailIds[i].toUpperCase());
				if(s != null)
				{
					ArrayList<String> taCourses = s.getCourseTA();
					if(taCourses == null)
					{
						//rare case
						taCourses = new ArrayList<String>();
					}
					for(int j =0;j<taCourses.size();j++)
					{
						if(taCourses.get(j).equals(courseId))
						{
							taCourses.remove(j);
						}
					}
					s.setCourseTA(taCourses);
					studentRepository.save(s);
				}
				
			}
			flagRemovedTA = 1;
			String redirectUrl = "/RemoveTA";
			return "redirect:" + redirectUrl;		
		}
	}
	
	@RequestMapping(value="/TADetails",method = RequestMethod.GET)
	public String SelectCourseForTADetails(HttpSession session,Model model)
	{
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
			if(flagNoTA == 1)
			{
				model.addAttribute("msg", "Please add TA first");
				flagNoTA = 0;
			}
			if(flagNoTA == 2)
			{
				model.addAttribute("msg", "TA not registered on mobile app");
				flagNoTA = 0;
			}
			if(flagNoTA == 3)
			{
				model.addAttribute("msg", "No TA exists with this email id in this course");
				flagNoTA = 0;
			}
			if(flagNoTA == 4)
			{
				model.addAttribute("msg", "Skills recommended successfully");
				flagNoTA = 0;
			}
			model.addAttribute("courses",courses);
            return "selectcoursefortaperformance";

		}
	}
	@RequestMapping(value="/TADetails",method = RequestMethod.POST)
	public String TADetails(@RequestParam("id") String id,@RequestParam("taId") String taId,HttpSession session,Model model)
	{
		id = id.toUpperCase();
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
				flagNoTA = 1;
				String redirectUrl = "/TADetails";
				return "redirect:" + redirectUrl;	
				
			}
			else
			{
				int flag = 0;
				System.out.println("ta Id is "+taId);
				for(int i= 0;i<taIds.size();i++)
				{
					System.out.println("ta Id is "+taId+"   "+taIds.get(i));
					if(taIds.get(i).equals(taId))
					{
						flag = 1;
						System.out.println("ta Id is "+taId+"   "+taIds.get(i));
						break;
					}
					
				}
				if(flag==0)
				{
					flagNoTA = 3;
					String redirectUrl = "/TADetails";
					return "redirect:" + redirectUrl;	
					
				}
				Student s = studentRepository.findByEmail(taId.toUpperCase());
				if(s == null)
				{
					flagNoTA = 2;
					String redirectUrl = "/TADetails";
					return "redirect:" + redirectUrl;
				}
				ArrayList<String> courseIds = s.getCourse();
				if(courseIds == null)
				{
					courseIds = new ArrayList<String>();
				}
				//get all courses opted
				HashMap<String,String> courses = new HashMap<String,String>();
				for(int i=0;i<courseIds.size();i++)
				{
					Course crs = courseRepository.findById(courseIds.get(i));
					courses.put(crs.getId(), crs.getName());
				}
				ArrayList<String> taTaskIds = s.getTATask();
				if(taTaskIds == null)
				{
					taTaskIds = new ArrayList<String>();
				}
				
				ArrayList<String> skills            = s.getSkillSet();
				ArrayList<String> recommendedSkills = s.getRecommendSkillSet();
				if(skills == null)
				{
				   model.addAttribute("skills","");
				}
				else
				{
					String skillSets = "";
					int size = skills.size();
					for(int i=0;i<size;i++)
					{

						if(i != size-1)
						{
							skillSets += skills.get(i)+" ,";
						}
						else
						{
							skillSets += skills.get(i);
						}
					}
					model.addAttribute("skills",skillSets);
				}
				if(recommendedSkills == null)
				{
					model.addAttribute("rskills","");
				}
				else
				{
					String rskillSets = "";
					int size = recommendedSkills.size();
					for(int i=0;i<size;i++)
					{
						if(i != size-1)
						{
							rskillSets += recommendedSkills.get(i)+" ,";
						}
						else
						{
							rskillSets += recommendedSkills.get(i);
						}
						
					}
					model.addAttribute("rskills",rskillSets);
				}
				
				//Get average of all rating
				float rating = 0;
				int count  = 0;
				int percentage = 1;
				for(int i=0;i<taTaskIds.size();i++)
				{
					Course course = courseRepository.findById(taTaskIds.get(i).split("-")[0]);
					ArrayList<Task> tasks = course.getTask();
					if(tasks == null)
					{
						tasks = new ArrayList<Task>();
					}
					for(int j=0;j<tasks.size();j++)
					{
						if(tasks.get(j).getId().equals(taTaskIds.get(i)) && (tasks.get(j).getId().split("-")[0].equals(id)))
						{
							if(tasks.get(j).getRating() != null)
							{
								if(tasks.get(j).getRating() != "")
								{
									rating += Integer.parseInt(tasks.get(j).getRating());
									count++;
								}
								
							}
							
						}
					}
							
				}
				if(count == 0)
				{
					rating = 0;
					percentage = 0;
				}
				else
				{
					rating /=  count;
					percentage = (int)(rating*100)/5;
				}
				
				model.addAttribute("name", s.getName());
				model.addAttribute("email",s.getEmail());
				model.addAttribute("courses", courses);
				model.addAttribute("rate", "Rating:"+rating);
				model.addAttribute("rating", "width:"+percentage+"%");
				model.addAttribute("picURL", s.getPicUrl()+"1234");
				model.addAttribute("courseId", id);
				model.addAttribute("email", taId);
				 return "tadetails";
				
			}
           

		}
	}

	@RequestMapping(value="/AddSkills",method = RequestMethod.POST)
	public String AddNewSkills(@RequestParam("email") String email,@RequestParam("skills") String skills,HttpSession session,Model model)
	{
        email  = email.toUpperCase();
        skills = skills.toUpperCase();
        System.out.println(email+"  "+skills);
		if(session.getAttribute("sessionId") == null)
		{
			model.addAttribute("msg", "Please login first");
			return "errorfile";
		}
		else
		{
			Student s = studentRepository.findByEmail(email);
			if(s == null)
			{
				//rarest case
				flagNoTA = 1;
				String redirectUrl = "/TADetails";
				return "redirect:" + redirectUrl;	
			}
			ArrayList<String> rSkills = s.getRecommendSkillSet();
			if(rSkills == null)
			{
				rSkills = new ArrayList<String>();
			}
			String[] newSkills = skills.split("-");
			for(int i=0;i<newSkills.length;i++)
			{
				int isPresent = 0;
				for(int j=0;j<rSkills.size();j++)
				{
					if(rSkills.get(j).toUpperCase().equals(newSkills[i].toUpperCase()))
					{
						isPresent = 1;
						break;
					}
					
				}
				if(isPresent == 0)
				{
					rSkills.add(newSkills[i]);
				}
				
			}
			s.setRecommendSkillSet(rSkills);
			studentRepository.save(s);
			
			flagNoTA = 4;
			String redirectUrl = "/TADetails";
			return "redirect:" + redirectUrl;
		}
	}
}
