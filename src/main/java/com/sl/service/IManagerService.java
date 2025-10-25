package com.sl.service;

import java.util.List;
import java.util.Map;

import com.sl.entity.LeaveRequest;
import com.sl.entity.Manager;
import com.sl.model.ManagerRequestDto;

public interface IManagerService {

	Manager saveManager(ManagerRequestDto managerRequestDto);

	LeaveRequest  approveLeave(Long requestId);

	LeaveRequest rejectLeave(Long requestId);

	Manager checkDetails(ManagerRequestDto managerDto);

	List<LeaveRequest> getPendingLeavesByManager(Long managerId);

	List<LeaveRequest> getLeaveHistoryOfTeam(Long managerId);

	Map<String, Object> getTeamLeaveSummary(Long managerId);

}
  