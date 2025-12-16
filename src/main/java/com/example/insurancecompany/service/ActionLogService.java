package com.example.insurancecompany.service;

import com.example.insurancecompany.dao.ActionLogDao;
import com.example.insurancecompany.model.ActionLog;
import java.time.LocalDateTime;
import java.util.List;

public class ActionLogService {
    private final ActionLogDao actionLogDao;

    public ActionLogService(ActionLogDao actionLogDao) {
        this.actionLogDao = actionLogDao;
    }

    public void logAction(Integer userId, String userRole, String actionType, 
                         String entityType, Integer entityId, String description, String ipAddress) throws Exception {
        ActionLog log = new ActionLog();
        log.setUserId(userId);
        log.setUserRole(userRole);
        log.setActionType(actionType);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDescription(description);
        log.setIpAddress(ipAddress);
        log.setCreatedAt(LocalDateTime.now());
        actionLogDao.create(log);
    }

    public List<ActionLog> getAllLogs() throws Exception {
        return actionLogDao.findAll();
    }

    public List<ActionLog> getLogsByUserId(int userId) throws Exception {
        return actionLogDao.findByUserId(userId);
    }

    public List<ActionLog> getLogsByActionType(String actionType) throws Exception {
        return actionLogDao.findByActionType(actionType);
    }

    public List<ActionLog> getLogsByEntityType(String entityType) throws Exception {
        return actionLogDao.findByEntityType(entityType);
    }

    public List<ActionLog> getRecentLogs(int limit) throws Exception {
        return actionLogDao.findRecent(limit);
    }
}

