package com.sl.service;

import com.sl.entity.LeaveRequest;
import com.sl.entity.Manager;
import com.sl.model.ManagerRequestDto;

public interface IManagerService {

	Manager saveManager(ManagerRequestDto managerRequestDto);

	LeaveRequest  approveLeave(Long requestId);

	LeaveRequest rejectLeave(Long requestId);

}
  