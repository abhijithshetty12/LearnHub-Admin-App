package com.example.learnhubadminapp.model;

public class ParentModel {
    private  String parentName , parentPassword , stdName,stdEmail,stdClass,parentId;

    public ParentModel(String parentName, String parentPassword, String stdName, String stdEmail, String stdClass) {
        this.parentName = parentName;
        this.parentPassword = parentPassword;
        this.stdName = stdName;
        this.stdEmail = stdEmail;
        this.stdClass = stdClass;
    }

    public ParentModel(String parentName, String parentPassword, String stdName, String stdEmail, String stdClass, String parentId) {
        this.parentName = parentName;
        this.parentPassword = parentPassword;
        this.stdName = stdName;
        this.stdEmail = stdEmail;
        this.stdClass = stdClass;
        this.parentId = parentId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public ParentModel() {
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentPassword() {
        return parentPassword;
    }

    public void setParentPassword(String parentPassword) {
        this.parentPassword = parentPassword;
    }

    public String getStdName() {
        return stdName;
    }

    public void setStdName(String stdName) {
        this.stdName = stdName;
    }

    public String getStdEmail() {
        return stdEmail;
    }

    public void setStdEmail(String stdEmail) {
        this.stdEmail = stdEmail;
    }

    public String getStdClass() {
        return stdClass;
    }

    public void setStdClass(String stdClass) {
        this.stdClass = stdClass;
    }
}
