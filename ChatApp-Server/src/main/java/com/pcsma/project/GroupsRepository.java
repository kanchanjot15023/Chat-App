package com.pcsma.project;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface GroupsRepository extends MongoRepository<Groups, String>{
	
	public Groups findByGroupId(String groupId);

}