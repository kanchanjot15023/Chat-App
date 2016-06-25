package com.pcsma.project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatGroupController {

	@Autowired GroupsRepository chatgroupRepository;
	@Autowired UserRepository      userRepository;
	
	@RequestMapping(value="/addGroup",method = RequestMethod.POST)
	public HashMap<String,String> AddGroup(@RequestParam("groupId") String groupId,@RequestParam("members") String members)
	{
		HashMap<String,String> res = new HashMap<String,String>();
	    Groups db = chatgroupRepository.findByGroupId(groupId);
		if(db != null)
		{
			res.put("success", "0");
		}
		else
		{
			Groups g = new Groups();
			g.setGroupId(groupId);
			String[] groupMembers = members.split(";");
			ArrayList<String> mem = new ArrayList<String>();
			for(int i = 0;i<groupMembers.length;i++)
			{
				
				mem.add(groupMembers[i]);
				User u = userRepository.findByEmail(groupMembers[i]);
				ArrayList<String> groupList = u.getGroupList();
				if(groupList == null)
				{
					groupList = new ArrayList<String>();
					groupList.add(groupId);
				}
				else
				{
					groupList.add(groupId);
				}
				u.setGroupList(groupList);
				userRepository.save(u);
			}
			mem.add(groupId.split("-")[1]);
			User u = userRepository.findByEmail(groupId.split("-")[1]);
			ArrayList<String> groupList = u.getGroupList();
			if(groupList == null)
			{
				groupList = new ArrayList<String>();
				groupList.add(groupId);
			}
			else
			{
				groupList.add(groupId);
			}
			u.setGroupList(groupList);
			userRepository.save(u);
			g.setMembers(mem);
			chatgroupRepository.save(g);
			res.put("success", "1");

		}
		return res;
	}
	@RequestMapping(value="/FetchAllGroups",method = RequestMethod.GET)
	public HashMap<String,String> FetchGroup(@RequestParam("email") String email)
	{
		HashMap<String,String> resp= new HashMap<String,String>();
		User u = userRepository.findByEmail(email);
	    ArrayList<String> groupIds = u.getGroupList();
	    if(groupIds == null)
	    {
	    	resp.put("success", "0");
	    }
	    else
	    {
	    	resp.put("success", "1");
	    	for(int i=1;i<=groupIds.size();i++)
	    	{
	    	    Groups g = chatgroupRepository.findByGroupId(groupIds.get(i-1));
	    		resp.put("groupId"+i, g.getGroupId());
	    		String members = "";
	    		ArrayList<String> mem = g.getMembers();
	    		for(int j=0;j<mem.size();j++)
	    		{
	    			members += mem.get(j)+";";
	    		}
	    		resp.put("members"+i,members);
	    	}
	    	resp.put("total", ""+groupIds.size());
	    }
		return resp;
	}
	
	@RequestMapping(value="/RemoveUserFromGroup",method = RequestMethod.POST)
	public HashMap<String,String> RemoveUser(@RequestParam("groupId") String groupId,@RequestParam("userEmail") String userEmail)
	{
		HashMap<String,String> resp = new HashMap<String,String>();
		userEmail = userEmail.toLowerCase();
		User u = userRepository.findByEmail(userEmail);
		if(u == null)
		{
			resp.put("success", "0");
			return resp;
		}
		ArrayList<String> groupList = u.getGroupList();
		for(int i=0;i<groupList.size();i++)
		{
			if(groupList.get(i).equals(groupId))
			{
				groupList.remove(i);
				break;
			}
		}
		u.setGroupList(groupList);
		userRepository.save(u);
		Groups group = chatgroupRepository.findByGroupId(groupId);
		ArrayList<String> members = group.getMembers();
		for(int i=0;i<members.size();i++)
		{
			if(members.get(i).equals(userEmail))
			{
				members.remove(i);
				break;
			}
		}
		group.setMembers(members);
		chatgroupRepository.save(group);
		resp.put("success", "1");
		return resp;
	}
	@RequestMapping(value="/AddUserToGroup",method = RequestMethod.POST)
	public HashMap<String,String> AddUser(@RequestParam("groupId") String groupId,@RequestParam("userEmail") String userEmail)
	{
	    HashMap<String,String> resp = new HashMap<String,String>();
		userEmail = userEmail.toLowerCase();
	    User u = userRepository.findByEmail(userEmail);
	    if(u == null)
		{
			resp.put("success", "-1");
			return resp;
		}
	    ArrayList<String> groupList = u.getGroupList();
	    int flagRepeatGroup = 0;
	    if(groupList == null)
	    {
	       groupList = new ArrayList<String>();
	       groupList.add(groupId);
	    }
	    else
	    {
	    	for(int i=0;i<groupList.size();i++)
	    	{
	    		if(groupList.get(i).equals(groupId))
	    		{
	    			resp.put("success", "0"); 	
	    			return resp;
	    		}
	    	}
	    	groupList.add(groupId);
	    }
	    u.setGroupList(groupList);
	    userRepository.save(u);
	    Groups group = chatgroupRepository.findByGroupId(groupId);
	    ArrayList<String> members = group.getMembers();
	    if(members == null)
	    {
	    	members = new ArrayList<String>();
	    	members.add(userEmail);
	    }
	    else
	    {
	    	for(int i=0;i<members.size();i++)
	    	{
	    		if(members.get(i).equals(userEmail))
	    		{
	    			resp.put("success", "0"); 	
	    			return resp;
	    		}
	    	}
	    	members.add(userEmail);
	    }
	    
	    group.setMembers(members);
	    chatgroupRepository.save(group);
	    resp.put("success", "1"); 

	    
	    return resp;
	}
	
	@RequestMapping(value="/RemoveGroup",method = RequestMethod.POST)
	public HashMap<String,String> RemoveGroup(@RequestParam("groupId") String groupId)
	{
		HashMap<String,String> resp = new HashMap<String,String>();
		System.out.println("I am here to remove group "+groupId.trim());
		Groups group = chatgroupRepository.findByGroupId(groupId);
		if(group == null)
		{
			System.out.println("Null is returned");
		}
		ArrayList<String> members = group.getMembers();
		for(int i=0;i<members.size();i++)
		{
			User u = userRepository.findByEmail(members.get(i));
			ArrayList<String> groupList = u.getGroupList();
			for(int j=0;j<groupList.size();j++)
			{
				if(groupList.get(j).equals(groupId)){
					groupList.remove(j);
				}
			}
			u.setGroupList(groupList);
			userRepository.save(u);
		}
		chatgroupRepository.delete(group);
		resp.put("success", "1");
		return resp;
	}
}
