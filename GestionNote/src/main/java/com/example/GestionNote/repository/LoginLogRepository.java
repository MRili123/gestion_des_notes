package com.example.GestionNote.repository;

import com.example.GestionNote.model.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginLogRepository extends JpaRepository<LoginLog, Integer> {
}
