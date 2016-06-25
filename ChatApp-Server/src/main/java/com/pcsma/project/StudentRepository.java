package com.pcsma.project;

import org.springframework.data.mongodb.repository.MongoRepository;



public interface StudentRepository extends MongoRepository<Student, String>{

	public Student findByName(String name);
	public Student findByEmail(String email);
	
}
