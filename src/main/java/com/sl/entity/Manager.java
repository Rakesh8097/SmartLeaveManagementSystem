package com.sl.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="manager")
@Data
public class Manager {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private Long managerId;
	private String name;
	private String email;
	private String password;
	private String department;
}
 