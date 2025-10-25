package com.sl.controller;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

	// 1. Manager Register
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
    
    // 2. Manager Login
    @PostMapping("/login")
    public ResponseEntity<ResponseMessage> loginManager(@RequestBody ManagerRequestDto managerDto)
    {
    	try {
    		if(managerDto.getEmail()==null||managerDto.getEmail().isBlank()||managerDto.getEmail().isBlank()||managerDto.getPassword()==null||managerDto.getPassword().isBlank()||managerDto.getPassword().isEmpty())
        	{
        		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(HttpURLConnection.HTTP_BAD_GATEWAY, Constants.FAILED, "email and password can not be emplty or blank"));
        	}
        	
        	Manager checkDetails = managerService.checkDetails(managerDto);
        	
        	if(checkDetails!=null) {
        		return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ResponseMessage(HttpURLConnection.HTTP_OK, Constants.SUCESS, "Manager login sucessfully"));
        	}
        	 else
    		 {
    			 return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_BAD_REQUEST, Constants.FAILED, "Invalid user name and password"));
    		 }
    	}catch(Exception e) {
			 return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_INTERNAL_ERROR, Constants.FAILED, "Internal server error")); 
    	}    	
    }
    
    
 // 3. Approve Leave
    @PutMapping("/approve/{requestId}")
    public ResponseEntity<ResponseMessage> approveLeave(@PathVariable Long requestId) {
        try {
            LeaveRequest updated = managerService.approveLeave(requestId);
            return ResponseEntity.ok(
                    new ResponseMessage(HttpURLConnection.HTTP_OK, Constants.SUCESS,
                            "Leave approved successfully for Request ID: " + updated.getRequestId()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage(HttpURLConnection.HTTP_BAD_REQUEST, Constants.FAILED, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage(HttpURLConnection.HTTP_INTERNAL_ERROR, Constants.FAILED, "Internal server error"));
        }
    }

    // 4. Reject Leave
    @PutMapping("/reject/{requestId}")
    public ResponseEntity<ResponseMessage> rejectLeave(@PathVariable Long requestId) {
        try {
            LeaveRequest updated = managerService.rejectLeave(requestId);
            return ResponseEntity.ok(
                    new ResponseMessage(HttpURLConnection.HTTP_OK, Constants.SUCESS,
                            "Leave rejected successfully for Request ID: " + updated.getRequestId()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage(HttpURLConnection.HTTP_BAD_REQUEST, Constants.FAILED, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage(HttpURLConnection.HTTP_INTERNAL_ERROR, Constants.FAILED, "Internal server error"));
        }
    }

    // 5. View Pending Leaves
    @GetMapping("/{managerId}/pending-leaves")
    public ResponseEntity<ResponseMessage> getPendingLeaves(@PathVariable Long managerId) {
        try {
            List<LeaveRequest> leaves = managerService.getPendingLeavesByManager(managerId);
            return ResponseEntity.ok(
                    new ResponseMessage(HttpURLConnection.HTTP_OK, Constants.SUCESS, "Pending leaves fetched successfully", leaves));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage(HttpURLConnection.HTTP_INTERNAL_ERROR, Constants.FAILED, "Internal server error"));
        }
    }

    // 6. View Team Leave History
    @GetMapping("/{managerId}/team-leave-history")
    public ResponseEntity<ResponseMessage> getLeaveHistory(@PathVariable Long managerId) {
        try {
            List<LeaveRequest> history = managerService.getLeaveHistoryOfTeam(managerId);
            return ResponseEntity.ok(
                    new ResponseMessage(HttpURLConnection.HTTP_OK, Constants.SUCESS, "Team leave history fetched successfully", history));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage(HttpURLConnection.HTTP_INTERNAL_ERROR, Constants.FAILED, "Internal server error"));
        }
    }

    // 7. Team Summary (Approved, Rejected, Pending)
    @GetMapping("/{managerId}/team-summary")
    public ResponseEntity<ResponseMessage> getTeamSummary(@PathVariable Long managerId) {
        try {
            Map<String, Object> summary = managerService.getTeamLeaveSummary(managerId);
            return ResponseEntity.ok(
                    new ResponseMessage(HttpURLConnection.HTTP_OK, Constants.SUCESS, "Team summary fetched successfully", summary));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage(HttpURLConnection.HTTP_INTERNAL_ERROR, Constants.FAILED, "Internal server error"));
        }
    }

}











