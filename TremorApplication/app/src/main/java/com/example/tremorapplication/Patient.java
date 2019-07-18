package com.example.tremorapplication;

import java.util.Calendar;

public class Patient {

    private String patientNo;
    private String hand;
    private String tremorRate;
    private long date;


    public Patient() {
    }


    public Patient(String patientNo, String hand, String tremorRate, long date) {
        this.patientNo = patientNo;
        this.hand = hand;
        this.tremorRate = tremorRate;
        this.date = date;
    }

    public String getPatientNo() {
        return patientNo;
    }

    public void setPatientNo(String patientNo) {
        this.patientNo = patientNo;
    }

    public String getHand() {
        return hand;
    }

    public void setHand(String hand) {
        this.hand = hand;
    }

    public String getTremorRate() {
        return tremorRate;
    }

    public void setTremorRate(String tremorRate) {
        this.tremorRate = tremorRate;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
