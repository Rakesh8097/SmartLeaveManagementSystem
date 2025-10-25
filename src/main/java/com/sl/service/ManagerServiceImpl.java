package com.sl.service;

import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sl.entity.Employee;
import com.sl.entity.LeaveBalance;
import com.sl.entity.LeaveRequest;
import com.sl.entity.LeaveStatus;
import com.sl.entity.Manager;
import com.sl.model.ManagerRequestDto;
import com.sl.repository.EmployeeRepository;
import com.sl.repository.LeaveBalanceRepository;
import com.sl.repository.LeaveRequestRepository;
import com.sl.repository.ManagerRepository;

@Service
public class ManagerServiceImpl implements IManagerService{

	@Autowired
	private ManagerRepository managerRepo;
	
	@Autowired 
	private EmployeeRepository employeeRepo;
	
	@Autowired
	private LeaveBalanceRepository leaveBalanceRepo;
	
	@Autowired
	private LeaveRequestRepository leaveRequestRepo;
	
    @Autowired
    private EmailService emailService;
	
    // 1. Manger Register
	@Override
	public Manager saveManager(ManagerRequestDto managerRequestDto) {
	    Manager manager = new Manager();

	    try {
	    	manager.setName(managerRequestDto.getName());
	    	manager.setEmail(managerRequestDto.getEmail());
	    	manager.setPassword(Base64.getEncoder().encodeToString(managerRequestDto.getPassword().getBytes()));  
	    	manager.setDepartment(managerRequestDto.getDepartment());

	        // Save employee first
	        managerRepo.save(manager);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return manager;  
	}

	// 2. Manager Login
	@Override
	public Manager checkDetails(ManagerRequestDto managerDto) {
		
		Manager byEmail = managerRepo.findByEmail(managerDto.getEmail());
		if(byEmail!=null)
		{
			String decode = new String(Base64.getDecoder().decode(byEmail.getPassword()));
			if(decode.equals(managerDto.getPassword()))
				return byEmail;
		}
		return byEmail;
	}
	
	// 3. Approve Leave
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

	
	//  4. Reject Leave
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

	// 5. Get Pending Leave
	public List<LeaveRequest> getPendingLeavesByManager(Long managerId) {
	    Manager manager = managerRepo.findById(managerId)
	            .orElseThrow(() -> new RuntimeException("Manager not found"));

	    List<Employee> teamMembers = employeeRepo.findByDepartment(manager.getDepartment());
	    List<Long> empIds = teamMembers.stream()
	            .map(Employee::getEmpId)
	            .collect(Collectors.toList());

	    return leaveRequestRepo.findByEmployee_EmpIdInAndStatus(empIds, LeaveStatus.PENDING); 
	}


	// 6. Get Leave History
	public List<LeaveRequest> getLeaveHistoryOfTeam(Long managerId) {
	    Manager manager = managerRepo.findById(managerId)
	            .orElseThrow(() -> new RuntimeException("Manager not found"));

	    List<Employee> team = employeeRepo.findByDepartment(manager.getDepartment());
	    List<Long> empIds = team.stream().map(Employee::getEmpId).collect(Collectors.toList());

	    return leaveRequestRepo.findByEmployeeEmpIdIn(empIds);  
	}

	
	// 7. Get Team Leave Summary
	public Map<String, Object> getTeamLeaveSummary(Long managerId) {
	    Manager manager = managerRepo.findById(managerId)
	            .orElseThrow(() -> new RuntimeException("Manager not found"));

	    List<Employee> team = employeeRepo.findByDepartment(manager.getDepartment());
	    List<Long> empIds = team.stream().map(Employee::getEmpId).collect(Collectors.toList());
	    List<LeaveRequest> allLeaves = leaveRequestRepo.findByEmployeeEmpIdIn(empIds);

	    long totalApproved = allLeaves.stream()
	            .filter(lr -> lr.getStatus() == LeaveStatus.APPROVED)
	            .count();

	    long totalRejected = allLeaves.stream()
	            .filter(lr -> lr.getStatus() == LeaveStatus.REJECTED)
	            .count();

	    Map<String, Object> summary = new HashMap<>();
	    summary.put("totalEmployees", team.size());
	    summary.put("approvedLeaves", totalApproved);
	    summary.put("rejectedLeaves", totalRejected);
	    summary.put("avgLeavesPerEmployee", (double) allLeaves.size() / team.size());
	    return summary;
	}

}
  

























