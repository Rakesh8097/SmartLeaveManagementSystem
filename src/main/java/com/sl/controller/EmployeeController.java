package com.sl.controller;

import java.net.HttpURLConnection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sl.entity.Employee;
import com.sl.entity.LeaveBalance;
import com.sl.entity.LeaveRequest;
import com.sl.model.ResponseMessage;
import com.sl.model.UserRequestDto;
import com.sl.service.IEmployeeService;
import com.sl.utility.Constants;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
	
	@Autowired
	private IEmployeeService employeeService;

	// 1. Register
	@PostMapping("/saveEmployee")
	public ResponseEntity<ResponseMessage> employeeRegister(@RequestBody UserRequestDto userRequestDto)
	{
		try {
	        if (userRequestDto.getEmail() == null || userRequestDto.getEmail().isBlank() ||
	            userRequestDto.getPassword() == null || userRequestDto.getPassword().isBlank()) {

	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body(new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constants.FAILED,
	                            "Email and password cannot be empty"));
	        }

	        Employee savedEmployee = employeeService.saveEmployee(userRequestDto);

	        if (savedEmployee != null) {
	            return ResponseEntity.status(HttpStatus.CREATED)
	                    .body(new ResponseMessage(HttpStatus.CREATED.value(), Constants.SUCESS,
	                            "Employee saved successfully", savedEmployee));
	        }

	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.FAILED,
	                        "Unable to save employee"));
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.FAILED,
	                        "Internal server error: " + e.getMessage()));
	    }
	}
	
	
	// 2. Employee login
		 @PostMapping("/emplogin")
		 public ResponseEntity<ResponseMessage> checkUserLogin(@RequestBody UserRequestDto dto)
		 {
			 try {
				 
			 if(dto.getEmail()==null||dto.getEmail().isBlank()||dto.getEmail().isEmpty()||dto.getPassword()==null||dto.getPassword().isBlank()||dto.getPassword().isEmpty())
			 {
				 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(HttpURLConnection.HTTP_BAD_GATEWAY,Constants.FAILED,"email and password cant not be empty"));
			 }
			 
			 Employee checkUserDetails = employeeService.checkUserDetails(dto);
			 
			 if(checkUserDetails!=null)
			 {
				 return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_OK, Constants.SUCESS, "Login successfully",checkUserDetails));
			 }
			 else
			 {
				 return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_BAD_REQUEST, Constants.FAILED, "Invalid user name and password"));
			 }
			 }
			 catch(Exception e) {
				 e.printStackTrace();
				 return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_INTERNAL_ERROR, Constants.FAILED, "Internal server error"));
			 }
		 }
	
	
	 // 3. Apply Leave
    @PostMapping("/{empId}/apply-leave")
    public ResponseEntity<ResponseMessage> applyLeave(@PathVariable Long empId, @RequestBody LeaveRequest request) {
        try {
            LeaveRequest leaveRequest = employeeService.applyLeave(empId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseMessage(HttpStatus.CREATED.value(), Constants.SUCESS,
                            "Leave applied successfully", leaveRequest));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.FAILED,
                            "Error applying leave: " + e.getMessage()));
        }
    }

    // 4. View Leave History
    @GetMapping("/{empId}/view-leave-history")
    public ResponseEntity<ResponseMessage> viewAllLeaveRequest(@PathVariable Long empId) {
        try {
            List<LeaveRequest> leaveList = employeeService.viewLeaveHistory(empId);
            if (leaveList == null || leaveList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseMessage(HttpStatus.NOT_FOUND.value(), Constants.FAILED,
                                "No leave history found for employee ID " + empId));
            }
            return ResponseEntity.ok(new ResponseMessage(HttpStatus.OK.value(), Constants.SUCESS,
                    "Leave history fetched successfully", leaveList));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.FAILED,
                            "Error fetching leave history: " + e.getMessage()));
        }
    }

    // 5. View Leave Balance
    @GetMapping("/{empId}/leave-balance")
    public ResponseEntity<ResponseMessage> viewLeaveBalance(@PathVariable Long empId) {
        try {
            LeaveBalance balance = employeeService.viewLeaveBalance(empId);
            if (balance == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseMessage(HttpStatus.NOT_FOUND.value(), Constants.FAILED,
                                "Leave balance not found for employee ID " + empId));
            }
            return ResponseEntity.ok(new ResponseMessage(HttpStatus.OK.value(), Constants.SUCESS,
                    "Leave balance fetched successfully", balance));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.FAILED,
                            "Error fetching leave balance: " + e.getMessage()));
        }
    }

    // 6. Edit / Update Leave
    @PutMapping("/{empId}/edit-leave/{leaveId}")
    public ResponseEntity<ResponseMessage> updateLeave(@PathVariable Long empId,
                                                       @PathVariable Long leaveId,
                                                       @RequestBody LeaveRequest request) {
        try {
            LeaveRequest updatedLeave = employeeService.updateLeave(empId, leaveId, request);
            return ResponseEntity.ok(new ResponseMessage(HttpStatus.OK.value(), Constants.SUCESS,
                    "Leave request updated successfully", updatedLeave));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constants.FAILED, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.FAILED,
                            "Error updating leave: " + e.getMessage()));
        }
    }

    // 7. Cancel Leave
    @DeleteMapping("/{empId}/cancel-leave/{leaveId}")
    public ResponseEntity<ResponseMessage> cancelLeave(@PathVariable Long empId, @PathVariable Long leaveId) {
        try {
            String message = employeeService.cancelLeave(empId, leaveId);
            return ResponseEntity.ok(new ResponseMessage(HttpStatus.OK.value(), Constants.SUCESS, message));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constants.FAILED, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.FAILED,
                            "Error cancelling leave: " + e.getMessage()));
        }
    }
    
 // 8. View Profile
    @GetMapping("/{empId}/profile")
    public ResponseEntity<ResponseMessage> viewProfile(@PathVariable Long empId) {
        try {
            Employee employee = employeeService.viewProfile(empId);
            if (employee == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseMessage(HttpStatus.NOT_FOUND.value(), Constants.FAILED,
                                "Employee profile not found for ID: " + empId));
            }
            return ResponseEntity.ok(new ResponseMessage(HttpStatus.OK.value(), Constants.SUCESS,
                    "Employee profile fetched successfully", employee));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constants.FAILED, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.FAILED,
                            "Error fetching profile: " + e.getMessage()));
        }
    }

    // 9. Update Profile
    @PutMapping("/{empId}/update-profile")
    public ResponseEntity<ResponseMessage> updateProfile(@PathVariable Long empId,
                                                         @RequestBody Employee updated) {
        try {
            Employee updatedEmployee = employeeService.updateProfile(empId, updated);
            return ResponseEntity.ok(new ResponseMessage(HttpStatus.OK.value(), Constants.SUCESS,
                    "Profile updated successfully", updatedEmployee));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constants.FAILED, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.FAILED,
                            "Error updating profile: " + e.getMessage()));
        }
    }
 
    // 10. 
    
} 


 
 





