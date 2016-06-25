package com.pcsma.project;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
public class StudentLoginController {

	@Autowired
	StudentRepository studentRepository;
	@Autowired
	MongoTemplate mongoTemplate;
	@RequestMapping(value="/student",method = RequestMethod.POST)
	public HashMap<String,String> Register(@RequestParam("email") String email,@RequestParam("name") String name,@RequestParam("password") String password,@RequestParam("rollno") String rollno,HttpSession session,Model model){
		
		//System.out.println(s.getEmail()+"  "+s.getName()+"  "+s.getPassword()+"  "+s.getRegisterationId().toString());
		HashMap<String,String> response = new HashMap<String,String>();
		if(studentRepository.findByEmail(email) == null  )
		{
			//System.out.println("hi");
			Student s=new Student();
			s.setPassword(md5(password));
			s.setEmail(email);
			s.setName(name);
			s.setRollno(rollno);
			studentRepository.save(s)          ;
		    response.put("msg","success")      ;
		    //System.out.println("Id is "+studentRepository.findByEmail(s.getEmail()).getRegisterationId().toString());
		    return response                    ;	 	    
		    
		}
		else
		{
			response.put("msg","error")        ;
			return response                    ;                                             
		}
	
		
	}
	

	
	
	
	
	@RequestMapping(value="/studentRoll",method = RequestMethod.POST)
	public HashMap<String,String> RegisterRoll(@ModelAttribute Student s,HttpSession session,Model model){
		
	
		HashMap<String,String> response = new HashMap<String,String>();
		if(studentRepository.findByEmail(s.getEmail()) != null  )
		{
			
			Student s1 = studentRepository.findByEmail(s.getEmail());
		    s1.setRollno(s.getRollno()); 
		    studentRepository.save(s1);
		    System.out.println("I am here"+s1.getRollno()+"  "+s.getRollno());
			response.put("msg","success")      ;
		    return response                    ;	 	    
		    
		}
		else
		{
			response.put("msg","error")        ;
			return response                    ;                                             
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

	
	
	
	
	
	
}
