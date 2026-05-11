package com.bozidar.rpg.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "runs")
public class RunEntity {

    @Id
    private String runId;

    @Column(columnDefinition = "text", nullable = false)
    private String stateJson;

    public RunEntity() {}

    public RunEntity(String runId, String stateJson) {
        this.runId = runId;
        this.stateJson = stateJson;
    }

    public String getRunId() {
        return runId;
    }

    public String getStateJson() {
        return stateJson;
    }

    public void setStateJson(String stateJson) {
        this.stateJson = stateJson;
    }
}
