package com.example.robot;

public class Robot {

    //xmpp robot login info
    private String robotname;
    private String password;
    private String nicknme;
    private String robotjid;
    
    //Association to turing robot
    private String turingId;
    private String turingKey;
    
    public String getRobotname() {
        return robotname;
    }
    public void setRobotname(String robotname) {
        this.robotname = robotname;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getNicknme() {
        return nicknme;
    }
    public void setNicknme(String nicknme) {
        this.nicknme = nicknme;
    }
    public String getUserjid() {
        return robotjid;
    }
    public void setUserjid(String robotjid) {
        this.robotjid = robotjid;
    }
    public String getTuringId() {
        return turingId;
    }
    public void setTuringId(String turingId) {
        this.turingId = turingId;
    }
    public String getTuringKey() {
        return turingKey;
    }
    public void setTuringKey(String turingKey) {
        this.turingKey = turingKey;
    }
    
    
    
}
