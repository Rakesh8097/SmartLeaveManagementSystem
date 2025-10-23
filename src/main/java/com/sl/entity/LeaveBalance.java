package com.sl.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="LeaveBalance")
@Data
public class LeaveBalance {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long balanceId; 
	private Integer totalLeave;
	private Integer usedLeave;
	private Integer remainingLeave;
	
	@OneToOne
	@JoinColumn(name="employee_id")
	private Employee employee;
}
