package com.pcsma.project;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.servlet.http.HttpSession;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.separator.DefaultRecordSeparatorPolicy;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class AdminStudentController {


	@Autowired FacultyRepository facultyRepository;
	@Autowired CourseRepository  courseRepository;
	@Autowired StudentRepository studentRepository;
	@Autowired MongoTemplate     mongoTemplate;
	 
	private static int flag = 0;
	private static int flagNoStudent = 0;
	private static int flagRemovedStudent = 0;
	private static ArrayList<String> dupEmails = null;
	
	@RequestMapping(value="/AddStudents",method = RequestMethod.GET)
	public String  ShowAddStudentPage(HttpSession session,Model model){
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
				model.addAttribute("msg","File is not in CSV format");
				flag = 0;
			}
			else if(flag == 2)
			{
				if(dupEmails.size() > 0)
				{
					String msg = "";
					model.addAttribute("msg","Following Students already added in this course.");
					model.addAttribute("emails",dupEmails);
					dupEmails = null;
				}
				else
				{
					model.addAttribute("msg", "Students added successfully");
				}
				flag = 0;
			}
			else if(flag == 3)
			{
				model.addAttribute("msg", "Failed to upload the CSV. Contact Developers.");
				flag = 0;
			}
			else if(flag == 4)
			{
				model.addAttribute("msg","File is empty");
				flag = 0;
			}
			else if(flag == 5)
			{
				model.addAttribute("msg","Unable to read file");
				flag = 0;
			}
			model.addAttribute("courses",courses);
            return "addstudent";
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/upload")
	public String handleFileUpload(@RequestParam("file") MultipartFile file,
		@RequestParam("id") String courseId,HttpSession session,Model model) {
		String name=file.getOriginalFilename();
		if(!name.contains(".csv"))
		{
			flag = 1;
			String redirectUrl = "/AddStudents";
			return "redirect:" + redirectUrl;
		    
		}
		if (!file.isEmpty()) {
			try {
				BufferedOutputStream stream = new BufferedOutputStream(
						new FileOutputStream(new File(courseId+".csv")));
				FileCopyUtils.copy(file.getInputStream(), stream);
				stream.close();
				if(session.getAttribute("sessionId") == null)
				{
					model.addAttribute("msg", "Please login first");
					return "errorfile";
				}
				else
				{
					ArrayList<String> ids = readDetails(courseId+".csv");
					Course c = courseRepository.findById(courseId);
					ArrayList<String> studentIds = c.getStudentId();
					dupEmails = new ArrayList<String>();
					if(studentIds == null)
					{
						studentIds = new ArrayList<String>();
						if(ids.size() != 0)
						{
							c.setStudentId(ids);
							courseRepository.save(c);
							flag = 2;
							String redirectUrl = "/AddStudents";
							return "redirect:" + redirectUrl;
						}
						else
						{
							flag = 5;
							String redirectUrl = "/AddStudents";
							return "redirect:" + redirectUrl;
						}
						
						
					}
					else
					{
						for(int i=0;i<ids.size();i++)
						{
							if(!StudentExists(c, ids.get(i)))
							{
								studentIds.add(ids.get(i));
								
							}
							else
							{
								dupEmails.add(ids.get(i));
							}
						}
						
					}
					c.setStudentId(studentIds);
					courseRepository.save(c);
					flag = 2;
					String redirectUrl = "/AddStudents";
					return "redirect:" + redirectUrl;
				}
				
				
				
			}
			catch (Exception e) {
				flag = 3;
				String redirectUrl = "/AddStudents";
				return "redirect:" + redirectUrl;
			}
		}
		else {
			flag = 4;
			String redirectUrl = "/AddStudents";
			return "redirect:" + redirectUrl;
		}

		
	}
	
	private static boolean StudentExists(Course c,String email)
	{
		ArrayList<String> studentIds = c.getStudentId();
		if(studentIds == null)
		{
			return false;
		}
		else
		{
			for(int i=0;i<studentIds.size();i++)
			{
				if(studentIds.get(i).equals(email.toUpperCase()))
				{
					return true;
				}
			}
		}
		return false;
	}
	private static ArrayList<String> readDetails(String csvFile) throws Exception {

		
		
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		ArrayList<String> det = new ArrayList<String>();
		try {

			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {

			        // use comma as separator
				System.out.println(line);
				String[] ids = line.split(cvsSplitBy);
				System.out.println("Id :"+ids[0]);
				
                det.add(ids[0].toUpperCase());  
				

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			
			}
	}
		return det;
		
	}
	
	@RequestMapping(value="/RemoveStudent",method = RequestMethod.GET)
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
				if(flagNoStudent == 1)
				{
					model.addAttribute("msg", "Please add students first");
					flagNoStudent = 0;
				}
				if(flagRemovedStudent == 1)
				{
					model.addAttribute("msg", "Students removed successfully");
					flagRemovedStudent = 0;
				}
				return "selectcourseforstudent";
			}
			
		}
	}
	
	@RequestMapping(value="/ViewAndRemoveStudent",method = RequestMethod.GET)
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
			ArrayList<String> students = c.getStudentId();
			if(students == null)
			{
				flagNoStudent = 1;
				String redirectUrl = "/RemoveStudent";
				return "redirect:" + redirectUrl;
			}
			else if(students.size() == 0)
			{
				flagNoStudent = 1;
				String redirectUrl = "/RemoveStudent";
				return "redirect:" + redirectUrl;
			}
			HashMap<String,String> studentList = new HashMap<String,String>();
			for(int i=0;i<students.size();i++)
			{
				studentList.put(""+i, students.get(i).toLowerCase());
			}
			model.addAttribute("students",studentList);
			model.addAttribute("id",courseId);
			return "viewandremovestudent";
			
		}
		
	}
	

	@RequestMapping(value="/RemoveStudent",method = RequestMethod.POST)
	public String RemoveStudent(@RequestParam("ids") String []emailIds,@RequestParam("courseId") String courseId,HttpSession session,Model model)
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
				ArrayList<String> students = c.getStudentId();
				if(students == null)
				{
					//rare case
					students = new ArrayList<String>();
				}
				for(int j=0;j<students.size();j++)
				{
					if(students.get(j).equals(emailIds[i].toUpperCase()))
					{
						students.remove(j);
					}
					
				}
				c.setStudentId(students);
				courseRepository.save(c);
				Student s = studentRepository.findByEmail(emailIds[i].toUpperCase());
				if(s != null)
				{
					ArrayList<String> studentCourses = s.getCourse();
					if(studentCourses == null)
					{
						//rare case
						studentCourses = new ArrayList<String>();
					}
					for(int j =0;j<studentCourses.size();j++)
					{
						if(studentCourses.get(j).equals(courseId))
						{
							studentCourses.remove(j);
						}
					}
					s.setCourseTA(studentCourses);
					studentRepository.save(s);
				}
				
			}
			flagRemovedStudent = 1;
			String redirectUrl = "/RemoveStudent";
			return "redirect:" + redirectUrl;		

		
		}
	}
}
