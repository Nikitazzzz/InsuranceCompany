package com.example.insurancecompany.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InsuranceCase {
    private int id;
    private int policyId;
    private int ownerId;
    private int statId;
    private LocalDateTime incidentDate;
    private String incidentDescription;
    private String descriptionDamage;
    private BigDecimal gradeDamage;
    private String adminComment;
    private LocalDateTime createDate;
    private LocalDateTime decisionDate;
    private boolean active;

    public InsuranceCase() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPolicyId() {
        return policyId;
    }

    public void setPolicyId(int policyId) {
        this.policyId = policyId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getStatId() {
        return statId;
    }

    public void setStatId(int statId) {
        this.statId = statId;
    }

    public LocalDateTime getIncidentDate() {
        return incidentDate;
    }

    public void setIncidentDate(LocalDateTime incidentDate) {
        this.incidentDate = incidentDate;
    }

    public String getIncidentDescription() {
        return incidentDescription;
    }

    public void setIncidentDescription(String incidentDescription) {
        this.incidentDescription = incidentDescription;
    }

    public String getDescriptionDamage() {
        return descriptionDamage;
    }

    public void setDescriptionDamage(String descriptionDamage) {
        this.descriptionDamage = descriptionDamage;
    }

    public BigDecimal getGradeDamage() {
        return gradeDamage;
    }

    public void setGradeDamage(BigDecimal gradeDamage) {
        this.gradeDamage = gradeDamage;
    }

    public String getAdminComment() {
        return adminComment;
    }

    public void setAdminComment(String adminComment) {
        this.adminComment = adminComment;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public LocalDateTime getDecisionDate() {
        return decisionDate;
    }

    public void setDecisionDate(LocalDateTime decisionDate) {
        this.decisionDate = decisionDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
