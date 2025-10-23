package com.sl.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="LeaveType")
@Data
public class LeaveType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long typeId;
	private String typeName;
	private Integer maxDays;
} 
