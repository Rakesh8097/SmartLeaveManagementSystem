package com.sl.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="Employee")
@Data
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long empId; 
	private String name; 
	private String email; 
	private String password;
	private String department; 
	private String role;
	
	@OneToMany(mappedBy = "employee",cascade = CascadeType.ALL)
	//@JsonManagedReference 
	private List<LeaveRequest> leaveRequests;
	@OneToOne(mappedBy = "employee",cascade = CascadeType.ALL)
	private LeaveBalance leaveBalance;
} 










