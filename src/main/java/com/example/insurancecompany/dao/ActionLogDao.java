package com.example.insurancecompany.dao;

import com.example.insurancecompany.model.ActionLog;
import java.util.List;

public interface ActionLogDao {
    void create(ActionLog actionLog) throws Exception;
    List<ActionLog> findAll() throws Exception;
    List<ActionLog> findByUserId(int userId) throws Exception;
    List<ActionLog> findByActionType(String actionType) throws Exception;
    List<ActionLog> findByEntityType(String entityType) throws Exception;
    List<ActionLog> findRecent(int limit) throws Exception;
}

