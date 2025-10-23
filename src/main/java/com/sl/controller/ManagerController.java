package com.sl.controller;

import java.net.HttpURLConnection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sl.entity.LeaveRequest;
import com.sl.entity.Manager;
import com.sl.model.ManagerRequestDto;
import com.sl.model.ResponseMessage;
import com.sl.service.IManagerService;
import com.sl.utility.Constants;

@RestController
@RequestMapping("/manager")
public class ManagerController {

	@Autowired
    private IManagerService managerService;

    @PostMapping("/register")
    public ResponseEntity<ResponseMessage> registerManager(@RequestBody ManagerRequestDto managerRequestDto) {
    	try {
			if(managerRequestDto.getEmail()==null||managerRequestDto.getEmail().isBlank()||managerRequestDto.getEmail().isEmpty()||managerRequestDto.getPassword()==null||managerRequestDto.getPassword().isBlank()||managerRequestDto.getPassword().isEmpty())
			{
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(HttpURLConnection.HTTP_BAD_GATEWAY,Constants.FAILED, "email and password can not be empty."));
			}
			 
			Manager saveManager = managerService.saveManager(managerRequestDto);
			
			if(saveManager!=null) {
				return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseMessage(HttpURLConnection.HTTP_CREATED, Constants.SUCESS,"Manager save successfully", saveManager));
			}else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(HttpURLConnection.HTTP_INTERNAL_ERROR,Constants.FAILED,"Internal server error"));			
			}
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage(HttpURLConnection.HTTP_INTERNAL_ERROR,Constants.FAILED,"Internal server error"));			
		}
    }
    
    @PutMapping("/approve/{requestId}")
    public ResponseEntity<LeaveRequest> approveLeave(@PathVariable Long requestId) {
        return ResponseEntity.ok(managerService.approveLeave(requestId));
    } 
    
    @PutMapping("/reject/{requestId}")
    public ResponseEntity<LeaveRequest> rejectLeave(@PathVariable Long requestId) {
        return ResponseEntity.ok(managerService.rejectLeave(requestId));
    }
}











