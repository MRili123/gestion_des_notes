package com.example.GestionNote.service;

import com.example.GestionNote.model.ActivityLog;
import com.example.GestionNote.model.User;
import com.example.GestionNote.repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActivityLogService {
    @Autowired
    private ActivityLogRepository activityLogRepository;
    @Autowired
    private UserServices userServices;

    public void save(String action,  Integer userId) {
        // get the user
        User user = userServices.getUserById(userId);
        ActivityLog activityLog = new ActivityLog(action, user);
        activityLogRepository.save(activityLog);
    }
}
