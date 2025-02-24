package com.example.GestionNote.DTO;

public class StudentDTO {
    private String firstName;
    private String lastName;
    private String cne;
    private Integer CurrentLevelId;

    // Getters and setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCne() {
        return cne;
    }

    public void setCne(String cne) {
        this.cne = cne;
    }

    public Integer getCurrentLevelId() {
        return CurrentLevelId;
    }

    public void setCurrentLevelId(Integer CurrentLevelId) {
        this.CurrentLevelId = CurrentLevelId;
    }
}
