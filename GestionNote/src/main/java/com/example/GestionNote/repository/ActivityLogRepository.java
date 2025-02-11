package com.example.GestionNote.repository;

import com.example.GestionNote.model.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Integer> {
}
