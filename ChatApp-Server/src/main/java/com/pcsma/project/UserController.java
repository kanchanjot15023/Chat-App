package com.pcsma.project;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

	@Autowired UserRepository userRepository;
	
	@RequestMapping(value="/signin",method = RequestMethod.POST)
	public HashMap<String,String> SignIn(@ModelAttribute User u)
	{
		HashMap<String,String> resp = new HashMap<String,String>();
		User db = userRepository.findByEmail(u.getEmail());
		if(db== null)
		{
			userRepository.save(u);
			resp.put("name",u.getName());
			resp.put("email",u.getEmail());
			resp.put("picURL",u.getPicURL());
			resp.put("status",u.getStatus());
		}
		else
		{
			resp.put("name",db.getName());
			resp.put("email",db.getEmail());
			resp.put("picURL",db.getPicURL());
			resp.put("status",db.getStatus());
		}
		
		resp.put("success", "1");
		
		return resp;
		
	}
	@RequestMapping(value="/update",method = RequestMethod.POST)
	public HashMap<String,String> UpdateDetails(@ModelAttribute User u)
	{
		HashMap<String,String> resp = new HashMap<String,String>();
		User db = userRepository.findByEmail(u.getEmail());
		if(db== null)
		{
			resp.put("success", "0");
		}
		else
		{
			db.setName(u.getName());
			db.setStatus(u.getStatus());
			userRepository.save(db);
			resp.put("success", "1");
		}
		return resp;
	}
	
	@RequestMapping(value="/getContacts",method = RequestMethod.GET)
	public HashMap<String,String> GetDetails(@RequestParam("email") String email)
	{
		HashMap<String,String> resp = new HashMap<String,String>();
		System.out.println("fetching contacts of Email id "+email);
		User db = userRepository.findByEmail(email);
		
		if(db == null)
		{
			//this case rarely happens
			resp.put("success", "0");
			return resp;
		}
		else
		{
			ArrayList<String> friendList = db.getFriendList();
			if(friendList == null)
			{
				resp.put("success", "0");
				return resp;
			}
			else
			{
				
				int total = friendList.size();
				resp.put("success", "1");
				resp.put("total", total+"");
				for(int i=1;i<=total;i++)
				{
					User u = userRepository.findByEmail(friendList.get(i-1));
				    System.out.println("Email id is "+u.getEmail());
			        resp.put("name"+i,u.getName());
			        resp.put("email"+i,u.getEmail());
			        resp.put("status"+i,u.getStatus());
			        resp.put("picURL"+i,u.getPicURL());
			        
				}
				return resp;
				
			}
			
		}
		
		
	}
	
	@RequestMapping(value="/addContact",method = RequestMethod.POST)
	public HashMap<String,String> AddContact(@RequestParam("toemail") String toemail,@RequestParam("addemail") String addemail)
	{
		HashMap<String,String> resp = new HashMap<String,String>();
		toemail  = toemail.toLowerCase();
		addemail = addemail.toLowerCase();
		
		System.out.println("Adding contact of email "+toemail+"  "+addemail);
		User addu = userRepository.findByEmail(addemail);
		if(addu == null)
		{
			resp.put("success", "0");
			return resp;
		}
		else
		{
			User tou = userRepository.findByEmail(toemail);
			ArrayList<String> friendList = tou.getFriendList();
			if(friendList == null)
			{
				
				friendList = new ArrayList<String>();
				friendList.add(addemail);
				tou.setFriendList(friendList);
				userRepository.save(tou);
				resp.put("success", "1");
				resp.put("name", addu.getName());
				resp.put("email", addu.getEmail());
				resp.put("status",addu.getStatus());
				resp.put("picURL", addu.getPicURL());
				return resp;
			}
			else
			{ 
				int flag = 0;
				for(int i=0;i<friendList.size();i++)
				{
					if(friendList.get(i).equals(addemail))
					{
						flag  = 1;
						break;
					}
				}
				if(flag == 1)
				{
					resp.put("success", "-1");
					return resp;
				}
				else
				{
					friendList.add(addemail);
					tou.setFriendList(friendList);
					userRepository.save(tou);
					resp.put("success", "1");
					resp.put("name", addu.getName());
					resp.put("email", addu.getEmail());
					resp.put("status",addu.getStatus());
					resp.put("picURL", addu.getPicURL());
					return resp;
				}
                   			
			}
			
		}
	
	}
	@RequestMapping(value="/insert",method = RequestMethod.GET)
	public String Demo()
	{
		User u = userRepository.findByEmail("kapish15026@iiitd.ac.in");
		ArrayList<String> contacts = new ArrayList<String>();
		contacts.add("naveen15038@iiitd.ac.in");
		u.setFriendList(contacts);
		userRepository.save(u);
		return "Hello !! Insertion made";
	}
	@RequestMapping(value="/removeContact",method = RequestMethod.POST)
	public HashMap<String,String> RemoveContact(@RequestParam("userEmail") String userEmail,@RequestParam("friendEmail") String friendEmail)
	{
		
		HashMap<String,String> resp = new HashMap<String,String>();
		User u = userRepository.findByEmail(userEmail);
		ArrayList<String> contactList = u.getFriendList();
		for(int i=0;i<contactList.size();i++)
		{
			if(contactList.get(i).equals(friendEmail))
			{
				contactList.remove(i);
			}
		}
         u.setFriendList(contactList);
         userRepository.save(u);
         resp.put("success", "1");
	   	return resp;
	}
	
}

