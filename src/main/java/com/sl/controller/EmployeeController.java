package com.sl.controller;

import java.net.HttpURLConnection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

	@PostMapping("/saveEmployee")
	public ResponseEntity<ResponseMessage> employeeRegister(@RequestBody UserRequestDto userRequestDto)
	{
		try {
			if(userRequestDto.getEmail()==null||userRequestDto.getEmail().isBlank()||userRequestDto.getEmail().isEmpty()||userRequestDto.getPassword()==null||userRequestDto.getPassword().isBlank()||userRequestDto.getPassword().isEmpty())
			{
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(HttpURLConnection.HTTP_BAD_GATEWAY,Constants.FAILED, "email and password can not be empty."));
			}
			
			Employee saveEmployee = employeeService.saveEmployee(userRequestDto);
			
			if(saveEmployee!=null) {
				return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseMessage(HttpURLConnection.HTTP_CREATED, Constants.SUCESS,"Employee save successfully", saveEmployee));
			}else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(HttpURLConnection.HTTP_INTERNAL_ERROR,Constants.FAILED,"Internal server error"));			
			}
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage(HttpURLConnection.HTTP_INTERNAL_ERROR,Constants.FAILED,"Internal server error"));			
		}
	}
	
	@PostMapping("/{empId}/apply-leave")
    public LeaveRequest applyLeave(@PathVariable Long empId, @RequestBody LeaveRequest request) {
        return employeeService.applyLeave(empId, request);
    }
	
	@GetMapping("/{empId}/view-leave-history")
	public List<LeaveRequest> viewAllLeaveRequest(@PathVariable Long empId)
	{
		return employeeService.viewLeaveHistory(empId);  
	} 
	
	   @GetMapping("/{empId}/leave-balance")
	    public LeaveBalance viewLeaveBalance(@PathVariable Long empId) {
	        return employeeService.viewLeaveBalance(empId);
	    } 
} 



 





