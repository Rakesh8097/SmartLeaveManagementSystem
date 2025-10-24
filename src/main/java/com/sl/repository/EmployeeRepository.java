package com.sl.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sl.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long>{

	Employee findByEmail(String email);
} 
