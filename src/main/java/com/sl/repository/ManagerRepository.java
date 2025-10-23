package com.sl.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sl.entity.Manager;

public interface ManagerRepository extends JpaRepository<Manager, Long>{

}
