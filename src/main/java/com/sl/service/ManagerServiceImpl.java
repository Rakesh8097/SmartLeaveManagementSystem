package com.sl.service;

import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sl.entity.LeaveBalance;
import com.sl.entity.LeaveRequest;
import com.sl.entity.LeaveStatus;
import com.sl.entity.Manager;
import com.sl.model.ManagerRequestDto;
import com.sl.repository.LeaveBalanceRepository;
import com.sl.repository.LeaveRequestRepository;
import com.sl.repository.ManagerRepository;

@Service
public class ManagerServiceImpl implements IManagerService{

	@Autowired
	private ManagerRepository managerRepo;
	
	@Autowired
	private LeaveBalanceRepository leaveBalanceRepo;
	
	@Autowired
	private LeaveRequestRepository leaveRequestRepo;
	
    @Autowired
    private EmailService emailService;
	
	@Override
	public Manager saveManager(ManagerRequestDto managerRequestDto) {
	    Manager manager = new Manager();

	    try {
	    	manager.setName(managerRequestDto.getName());
	    	manager.setEmail(managerRequestDto.getEmail());
	    	manager.setPassword(managerRequestDto.getPassword());
	    	manager.setDepartment(managerRequestDto.getDepartment());

	        // Save employee first
	        managerRepo.save(manager);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return manager;  
	}

	@Override
	public LeaveRequest  approveLeave(Long requestId) {
		LeaveRequest request = leaveRequestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Leave Request not found"));

        if (request.getStatus() != LeaveStatus.PENDING) {
            throw new RuntimeException("Leave already processed!");
        }

        request.setStatus(LeaveStatus.APPROVED);
        leaveRequestRepo.save(request);

        // Update leave balance here
        LeaveBalance balance = leaveBalanceRepo.findByEmployeeEmpId(request.getEmployee().getEmpId());
        int requestedDays = (int) ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;
        balance.setUsedLeave(balance.getUsedLeave() + requestedDays);
        balance.setRemainingLeave(balance.getRemainingLeave() - requestedDays);
        leaveBalanceRepo.save(balance);
        
        
        try {
            String subject = "Leave Request Approved ";
            String body = "Hello " + request.getEmployee().getName() + ",\n\n" +
                    "Good news! Your leave request has been *approved* by your manager.\n\n" +
                    "Leave Type: " + request.getLeaveType().getTypeName() + "\n" +
                    "From: " + request.getStartDate() + " to " + request.getEndDate() + "\n" +
                    "Total Days: " + requestedDays + "\n" +
                    "Status: " + request.getStatus() + "\n\n" +
                    "Enjoy your time off!\n\n" +
                    "Best Regards,\nSmart Leave Management System";

            emailService.sendEmail(request.getEmployee().getEmail(), subject, body);
        } catch (Exception e) {
            System.err.println("Failed to send approval email: " + e.getMessage());
        }
        
        return request; 
	}

	@Override
    public LeaveRequest rejectLeave(Long requestId) {
        LeaveRequest request = leaveRequestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Leave Request not found"));
        if (request.getStatus() != LeaveStatus.PENDING) {
            throw new RuntimeException("Leave already processed!");
        }

        request.setStatus(LeaveStatus.REJECTED); 
        leaveRequestRepo.save(request); 
        
        
        try {
            String subject = "Leave Request Rejected ";
            String body = "Hello " + request.getEmployee().getName() + ",\n\n" +
                    "Unfortunately, your leave request has been *rejected* by your manager.\n\n" +
                    "Leave Type: " + request.getLeaveType().getTypeName() + "\n" +
                    "From: " + request.getStartDate() + " to " + request.getEndDate() + "\n" +
                    "Total Days: " + ((int) ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1) + "\n" +
                    "Status: " + request.getStatus() + "\n\n" +
                    "You may contact your manager for clarification.\n\n" +
                    "Best Regards,\nSmart Leave Management System";

            emailService.sendEmail(request.getEmployee().getEmail(), subject, body);
        } catch (Exception e) {
            System.err.println("Failed to send rejection email: " + e.getMessage());
        }
        
        
        return request;
    }
}
 







