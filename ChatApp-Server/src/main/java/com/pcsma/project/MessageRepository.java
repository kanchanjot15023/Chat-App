package com.pcsma.project;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepository extends MongoRepository<Message, String>{

	
	
	public Message findById(String id);
	
	
	
}
