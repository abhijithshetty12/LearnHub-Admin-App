package com.example.learnhubadminapp.model;

import java.util.ArrayList;
import java.util.List;

public class UserModel {
    String name , email,password,usertype;
    List<String> selectedClass,selectedSubject;

    public UserModel(String name, String email, String password, String usertype, List<String> selectedClass,List<String> selectedSubject) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.usertype = usertype;
        this.selectedClass = selectedClass;
        this.selectedSubject = selectedSubject;

    }

    public UserModel(String name, String usertype, List<String> selectedClass) {
        this.name = name;
        this.usertype = usertype;
        this.selectedClass = selectedClass;
    }

    public UserModel(String name, String email, String password, String usertype) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.usertype = usertype;
    }

    public UserModel() {
    }

    public List<String> getSelectedSubject() {
        return selectedSubject;
    }

    public void setSelectedSubject(List<String> selectedSubject) {
        this.selectedSubject = selectedSubject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public List<String> getSelectedClass() {
        return selectedClass;
    }


    public void setSelectedClass(List<String> selectedClass) {
        this.selectedClass = selectedClass;
    }
}
