package com.sl.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sl.entity.Employee;
import com.sl.entity.LeaveBalance;
import com.sl.entity.LeaveRequest;
import com.sl.entity.LeaveStatus;
import com.sl.entity.LeaveType;
import com.sl.model.UserRequestDto;
import com.sl.repository.EmployeeRepository;
import com.sl.repository.LeaveBalanceRepository;
import com.sl.repository.LeaveRequestRepository;
import com.sl.repository.LeaveTypeRepository;

@Service 
public class EmployeeServiceImpl implements IEmployeeService{

	@Autowired 
	EmployeeRepository empRepo; 
	
	@Autowired
    private LeaveRequestRepository leaveRepo;
	
	@Autowired 
	private LeaveTypeRepository leaveTypeRepo; 
	
	@Autowired 
	private LeaveBalanceRepository balanceRepo;
	
	@Override
	public Employee saveEmployee(UserRequestDto userRequestDto) {
	    Employee emp = new Employee();

	    try {
	        emp.setName(userRequestDto.getName());
	        emp.setEmail(userRequestDto.getEmail());
	        emp.setPassword(userRequestDto.getPassword());
	        emp.setDepartment(userRequestDto.getDepartment());
	        emp.setRole(userRequestDto.getRole());

	        // Save employee first
	        Employee savedEmp = empRepo.save(emp);

	        // Automatically create LeaveBalance
	        LeaveBalance balance = new LeaveBalance();
	        balance.setEmployee(savedEmp);
	        balance.setTotalLeave(20);       // Default total leaves
	        balance.setUsedLeave(0);
	        balance.setRemainingLeave(20);
 
	        balanceRepo.save(balance);

	        return savedEmp;

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return emp;
	}
 

	@Override
	public LeaveRequest applyLeave(Long empId, LeaveRequest request) {
	    // Find employee
	    Employee emp = empRepo.findById(empId)
	            .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + empId));

	    // Find leave type
	    LeaveType type = leaveTypeRepo.findById(request.getLeaveType().getTypeId())
	            .orElseThrow(() -> new RuntimeException("Invalid Leave Type"));

	    // Get leave balance
	    LeaveBalance balance = balanceRepo.findByEmployeeEmpId(empId);
	    if (balance == null) {
	        throw new RuntimeException("LeaveBalance not found for Employee ID: " + empId);
	    }

	    // Calculate requested days
	    int requestedDays = (int) ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;

	    // Check if enough leave is remaining
	    if (requestedDays > balance.getRemainingLeave()) {
	        throw new RuntimeException("Not enough remaining leaves.");
	    }

	    // Set request details
	    request.setEmployee(emp);
	    request.setLeaveType(type);
	    request.setStatus(LeaveStatus.PENDING); // still pending for manager approval
	    request.setAppliedDate(LocalDate.now());

	    LeaveRequest savedLeave = leaveRepo.save(request);

	    // Update leave balance (immediate deduction)
//	    balance.setUsedLeave(balance.getUsedLeave() + requestedDays);
//	    balance.setRemainingLeave(balance.getRemainingLeave() - requestedDays);
//	    balanceRepo.save(balance); 
 
	    return savedLeave;
	}
 

	@Override
	public List<LeaveRequest> viewLeaveHistory(Long empId) {
		return leaveRepo.findByEmployeeEmpId(empId); 
	}

	   @Override
	    public LeaveBalance viewLeaveBalance(Long empId) {
		    System.out.println("view balance");
	        return balanceRepo.findByEmployeeEmpId(empId);	        
	    } 
} 


















