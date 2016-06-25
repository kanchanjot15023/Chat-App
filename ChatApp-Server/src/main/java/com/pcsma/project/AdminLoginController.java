package com.pcsma.project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;




@Controller
public class AdminLoginController {

	@Autowired HttpSession session;
	@Autowired FacultyRepository facultyRepository;
	
	@Autowired CourseRepository courseRepository;
	
	
	
	@RequestMapping(value="/")
	public String  GetLoginPage(Model model){
		if(session.getAttribute("sessionId") == null)
		{
        	return "login";
		}
		else
		{
			String redirectUrl = "/adminProfile";
			return "redirect:" + redirectUrl;
		}


	}
	@RequestMapping(value="/adminRegisteration",method = RequestMethod.POST)
	public String Register(@ModelAttribute Faculty f,Model model){

		//check if admin record already exists in Database throw error otherwise save the details. 

		if(facultyRepository.findByEmail(f.getEmail()) == null)
		{

			f.setPassword(md5(f.getPassword()));
			facultyRepository.save(f)          ;                                                   
			model.addAttribute("msg", "You are successfully registered. Please login with email and Password");
			return "messagefile";    

		}
		else
		{
			model.addAttribute("msg", "User name with same email id already exists");
			return "messagefile"                                                      ;                                             
		}
	}
    @RequestMapping(value="/googleSignIn",method = RequestMethod.POST)
	public String GoogleSignIn(@RequestParam("id") String id,Model model)
	{
		System.out.println("Id is "+id);
	    String response = "";
		try {
			response = sendGET("https://www.googleapis.com/oauth2/v3/tokeninfo?id_token="+id);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ObjectMapper mapper = new ObjectMapper();
		Faculty f = new Faculty();
		try {
		   JsonNode rootNode = mapper.readTree(response);
		   String name     = rootNode.get("name").textValue();
		   String email    = rootNode.get("email").textValue();
		   String picURL   = rootNode.get("picture").textValue();
		   String password = "null";
		   f.setEmail(email);
		   f.setName(name);
		   f.setPicURL(picURL);
		   f.setPassword(password);
		   
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    if(facultyRepository.findByEmail(f.getEmail()) == null)
	    {
	    	facultyRepository.save(f);
	    }
	    session.setAttribute("sessionId", "abcd");
		session.setAttribute("name",f.getName());
		session.setAttribute("email",f.getEmail());
		session.setAttribute("picURL",f.getPicURL());
		session.setAttribute("role", "faculty"); 
	    session.setAttribute("title", "Dr. ");
		System.out.println(session.getMaxInactiveInterval()+"   "+session.getId());
		String redirectUrl = "/adminProfile";
		return "redirect:" + redirectUrl;  
		//return viewProfile(model);
	}
    
@RequestMapping(value="/adminSignIn",method = RequestMethod.POST)
	public String  SignIn(@ModelAttribute Faculty f, Model model){

		String redirectUrl;
		if(facultyRepository.findByEmail(f.getEmail()) != null)
		{

			if(md5(f.getPassword()).equals(facultyRepository.findByEmail(f.getEmail()).getPassword()))
			{
				f = facultyRepository.findByEmail(f.getEmail())                                    ;
				Random random    = new Random()                                                    ;
				int randomInteger = random.nextInt()                                               ;
				String sessionId = AdminLoginController.md5(f.getEmail()+f.getName()+randomInteger);
				session.setAttribute("sessionId", sessionId)                                       ;
				session.setAttribute("name",f.getName())                                           ;
				session.setAttribute("email",f.getEmail())                                         ;
				session.setAttribute("role", "faculty")                                            ;
				session.setAttribute("picURL", "")                                                 ;
				session.setAttribute("title", "Dr. ")                                              ;
				redirectUrl = "/adminProfile"                                                      ;
				return "redirect:" + redirectUrl                                                   ;   
			    // return viewProfile(model);
			}

			else
			{
				model.addAttribute("msg", "Your Password is incorrect");
				return "messagefile"                                   ;
			}
		}

		else
		{
			model.addAttribute("msg", "Please provide correct email id");
			return "messagefile"                                        ;    
		}

	}
    private static String md5(String input) {

		String md5 = null;

		if(null == input) return null;

		try {

			//Create MessageDigest object for MD5
			MessageDigest digest = MessageDigest.getInstance("MD5");

			//Update input string in message digest
			digest.update(input.getBytes(), 0, input.length());

			//Converts message digest value in base 16 (hex) 
			md5 = new BigInteger(1, digest.digest()).toString(16);

		} catch (NoSuchAlgorithmException e) {

			e.printStackTrace();
		}
		return md5;
	}
	private static String sendGET(String url) throws IOException {
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		int responseCode = con.getResponseCode();
		System.out.println("GET Response Code :: " + responseCode);
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			return response.toString();
		} else {
			return "error";
		}

	}
}
