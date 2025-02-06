package com.example.GestionNote.DTO;

import java.util.List;

public class LevelDTO {
    private String title;
    private String alias;
    private Integer filiereId;
    private List<Integer> nextLevels;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Integer getFiliereId() {
        return filiereId;
    }

    public void setFiliereId(Integer filiereId) {
        this.filiereId = filiereId;
    }

    public List<Integer> getNextLevels() {
        return nextLevels;
    }

    public void setNextLevels(List<Integer> nextLevels) {
        this.nextLevels = nextLevels;
    }
}
