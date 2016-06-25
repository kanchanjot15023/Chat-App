package com.pcsma.project;

import org.springframework.data.mongodb.repository.MongoRepository;



public interface FacultyRepository extends MongoRepository<Faculty, String>{

	public Faculty findByName(String name);
	public Faculty findByEmail(String email);
	
}
