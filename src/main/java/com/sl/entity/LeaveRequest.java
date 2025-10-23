package com.sl.entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="LeaveRequest")
@Data
public class LeaveRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long requestId;
	private LocalDate startDate;
	private LocalDate endDate;
	private String reson;
	
	@Enumerated(EnumType.STRING)
	private LeaveStatus status;
	
	@ManyToOne
	@JoinColumn(name="employee_id")
	//@JsonManagedReference 
	private Employee employee;
	
	@ManyToOne
	@JoinColumn(name="leaveType_id")
	private LeaveType leaveType;
	
	private LocalDate appliedDate;
} 

 


