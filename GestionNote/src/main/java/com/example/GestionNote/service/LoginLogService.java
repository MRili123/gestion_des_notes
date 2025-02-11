package com.example.GestionNote.service;

import com.example.GestionNote.model.LoginLog;
import com.example.GestionNote.model.User;
import com.example.GestionNote.repository.LoginLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginLogService {
    @Autowired
    private LoginLogRepository loginLogRepository;
    @Autowired
    private UserServices userServices;

    public void save(Boolean success, String reason, String ipAddress,  Integer userId) {
        // get the user
        User user = userServices.getUserById(userId);
        LoginLog loginLog = new LoginLog(success, reason, ipAddress, user);
        loginLogRepository.save(loginLog);
    }
}
