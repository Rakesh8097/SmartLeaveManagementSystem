package com.sl.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
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
	
    @Autowired
    private EmailService emailService;
	
	// 1. Register
	@Override
	public Employee saveEmployee(UserRequestDto userRequestDto) {
	    Employee emp = new Employee();

	    try {
	        emp.setName(userRequestDto.getName());
	        emp.setEmail(userRequestDto.getEmail());
	        emp.setPassword(Base64.getEncoder().encodeToString(userRequestDto.getPassword().getBytes()));
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
 
	// 2. Login
	@Override
	public Employee checkUserDetails(UserRequestDto dto) {
		Employee byEmail = empRepo.findByEmail(dto.getEmail());
		
		if(byEmail!=null){
			String decode = new String(Base64.getDecoder().decode(byEmail.getPassword()));
			
			if(decode.equals(dto.getPassword())){ 
				return byEmail;
			}
		}
		return byEmail; 
	}
	
    // 3. Apply leave
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
	    
	    try {
	        String subject = "Leave Application Submitted";
	        String body = "Hello " + emp.getName() + ",\n\n" +
	                "Your leave request has been successfully submitted.\n\n" +
	                "Leave Type: " + type.getTypeName() + "\n" +
	                "From: " + request.getStartDate() + " to " + request.getEndDate() + "\n" +
	                "Total Days: " + requestedDays + "\n" +
	                "Status: " + request.getStatus() + "\n\n" +
	                "You will receive another email once your manager reviews your request.\n\n" +
	                "Best Regards,\nSmart Leave Management System";

	        emailService.sendEmail(emp.getEmail(), subject, body);
	    } catch (Exception e) {
	        System.err.println("Failed to send leave application email: " + e.getMessage());
	    }
 
	    return savedLeave; 
	}
 
    // 4. View leave history
	@Override
	public List<LeaveRequest> viewLeaveHistory(Long empId) {
		return leaveRepo.findByEmployeeEmpId(empId); 
	}

	// 5. View leave balance
    @Override
	public LeaveBalance viewLeaveBalance(Long empId) { 
		System.out.println("view balance");
	    return balanceRepo.findByEmployeeEmpId(empId);	        
	}
    
    // 6. update leave
    @Override
    public LeaveRequest updateLeave(Long empId, Long leaveId, LeaveRequest updatedRequest) {
        LeaveRequest existing = leaveRepo.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave not found"));

        // Only pending requests can be edited
        if (existing.getStatus() != LeaveStatus.PENDING) {
            throw new RuntimeException("Cannot edit leave that is already " + existing.getStatus());
        }

        if (!existing.getEmployee().getEmpId().equals(empId)) {
            throw new RuntimeException("Access denied: Leave does not belong to this employee.");
        }

        existing.setStartDate(updatedRequest.getStartDate());
        existing.setEndDate(updatedRequest.getEndDate());
        existing.setReson(updatedRequest.getReson());
        existing.setLeaveType(updatedRequest.getLeaveType()); 
        return leaveRepo.save(existing);
    }
    
    // 7. Cancel leave
    @Override
    public String cancelLeave(Long empId, Long leaveId) {
        LeaveRequest existing = leaveRepo.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave not found"));

        if (!existing.getEmployee().getEmpId().equals(empId)) {
            throw new RuntimeException("Access denied: Leave does not belong to this employee.");
        }

        if (existing.getStatus() == LeaveStatus.APPROVED) {
            throw new RuntimeException("Cannot cancel an approved leave.");
        }

        existing.setStatus(LeaveStatus.CANCELLED);
        leaveRepo.save(existing);

        return "Leave request cancelled successfully.";
    }
    
    // 8. View profile
    @Override
    public Employee viewProfile(Long empId) {
        return empRepo.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    // 9. Update profile
    @Override
    public Employee updateProfile(Long empId, Employee updated) {
        Employee emp = empRepo.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        emp.setName(updated.getName());
        emp.setEmail(updated.getEmail());
        emp.setDepartment(updated.getDepartment());
        emp.setRole(updated.getRole());
        return empRepo.save(emp);
    } 
} 

 
















