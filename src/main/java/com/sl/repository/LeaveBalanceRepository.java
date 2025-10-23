package com.sl.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sl.entity.LeaveBalance;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long>{

	LeaveBalance findByEmployeeEmpId(Long empId);

} 
