package com.sl.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sl.entity.LeaveRequest;
import com.sl.entity.LeaveStatus;

@Repository 
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

	List<LeaveRequest> findByEmployeeEmpId(Long id); 
	
	// for view pending leave request(Manager)
	List<LeaveRequest> findByEmployee_EmpIdInAndStatus(List<Long> empIds, LeaveStatus pending);

	// View Leave History of Team
	// For multiple employees
    List<LeaveRequest> findByEmployeeEmpIdIn(List<Long> empIds);
} 
  