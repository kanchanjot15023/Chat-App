package com.pcsma.project;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;





@RestController
public class StudentSignInController {

	
	@Autowired
	StudentRepository studentRepository;
	@Autowired
	MongoTemplate mongoTemplate;
	
	
	
	@RequestMapping(value="/studentSignIn",method = RequestMethod.GET)
	public HashMap<String,String> Signin(@ModelAttribute Student s,HttpSession session,Model model){
		
		HashMap<String,String> response = new HashMap<String,String>();
		if(studentRepository.findByEmail(s.getEmail()) == null)
		{
			response.put("status","error" )    ;
			response.put("msg","Please Sign Up before Sign In");
			return response                    ;  
		}
		else
		{
			if(md5(s.getPassword()).equals(studentRepository.findByEmail(s.getEmail()).getPassword()))
			{
				response.put("status", "success");
				response.put("name", studentRepository.findByEmail(s.getEmail()).getName());
				response.put("rollno", studentRepository.findByEmail(s.getEmail()).getRollno());
			}
			else
			{
				response.put("status","error" )                                    ;
				response.put("msg","Incorrect Password");
			}
			return response;
			                                           
		}
	
		
	}
	
	
	
	
	
	
	
	
	
	@RequestMapping(value="/studentGoogle",method = RequestMethod.POST)
	public HashMap<String,String> RegisterGoogle(@ModelAttribute Student s,HttpSession session,Model model){
		
		//System.out.println(s.getEmail()+"  "+s.getName()+"  "+s.getPassword()+"  "+s.getRegisterationId().toString());
		HashMap<String,String> response = new HashMap<String,String>();
		if(studentRepository.findByEmail(s.getEmail()) == null  )
		{
			//System.out.println("hi");
			//s.setPassword(md5(s.getPassword()));
			studentRepository.save(s)          ;
		    response.put("status","success")      ;
		    //System.out.println("Id is "+studentRepository.findByEmail(s.getEmail()).getRegisterationId().toString());
		    return response                    ;	 	    
		    
		}
		else
		{
			Student st = studentRepository.findByEmail(s.getEmail());
			
			response.put("status","error")        ;
			response.put("rollno",st.getRollno());
			response.put("name",st.getName());
			System.out.println(st.getRollno()+"  "+st.getName());
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
