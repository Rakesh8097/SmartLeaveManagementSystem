package com.sl.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sl.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long>{

	Employee findByEmail(String email);

	List<Employee> findByDepartment(String department); 
} 
