package com.pcsma.project;

import org.springframework.data.mongodb.repository.MongoRepository;





public interface CourseRepository extends MongoRepository<Course, String> {

	
	public Course findById(String id);
	public Course findByStudentId(String studentId);
}
