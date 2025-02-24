package com.example.GestionNote.repository;

import com.example.GestionNote.model.ActivityLog;
import com.example.GestionNote.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Integer> {
    @Query("SELECT a FROM ActivityLog a JOIN a.user u WHERE u.role = :userRole")
    List<ActivityLog> getActivityLogsByUserRole(Role userRole);
}
