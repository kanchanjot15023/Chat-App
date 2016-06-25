package com.pcsma.project;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class AdminCourseController {

	@Autowired FacultyRepository facultyRepository;
	@Autowired CourseRepository  courseRepository;
	@Autowired MongoTemplate     mongoTemplate;
	
	@RequestMapping(value="/Course",method = RequestMethod.GET)
	public String  ShowCoursePage(HttpSession session,Model model){
		if(session.getAttribute("sessionId") == null)
		{
			model.addAttribute("msg", "Please login first");
			return "errorfile";
		}
		else
		{
            return "course";
		}

	}

	@RequestMapping(value="/Course",method = RequestMethod.POST)
	public String  AddCourse(@ModelAttribute Course c,HttpSession session,Model model)
	{ 

		if(session.getAttribute("sessionId") == null)
		{
			model.addAttribute("msg", "Please login first");
			return "errorfile";
		}
		else
		{
			Course db = courseRepository.findById(c.getId().toUpperCase());
			if(db != null)
			{
				model.addAttribute("msg","Course Id already exists");
				return "course";
			}
			else
			{
				c.setId(c.getId().toUpperCase());
				c.setName(c.getName().toUpperCase());
				courseRepository.save(c);
				
				mongoTemplate.upsert(new Query(Criteria.where("email").is(session.getAttribute("email").toString())),
				new Update().push("courseId",c.getId()),Faculty.class);
				model.addAttribute("msg", "Course Added Successfully");
				return "course";
			}
		}


	}


}
