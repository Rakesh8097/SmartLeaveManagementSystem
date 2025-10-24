package com.sl.service;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sl.entity.Employee;
import com.sl.entity.LeaveBalance;
import com.sl.entity.LeaveRequest;
import com.sl.model.UserRequestDto;

@Repository 
public interface IEmployeeService {

	public Employee saveEmployee(UserRequestDto userRequestDto);

	public LeaveRequest applyLeave(Long empId, LeaveRequest request);

	public List<LeaveRequest> viewLeaveHistory(Long empId);

	public LeaveBalance viewLeaveBalance(Long empId);

	public LeaveRequest updateLeave(Long empId, Long leaveId, LeaveRequest request);

	public String cancelLeave(Long empId, Long leaveId);

	public Employee checkUserDetails(UserRequestDto dto);

	public Employee viewProfile(Long empId);

	public Employee updateProfile(Long empId, Employee updated); 

} 
