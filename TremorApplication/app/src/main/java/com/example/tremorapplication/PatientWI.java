package com.example.tremorapplication;

public class PatientWI {

    private String PatientNoi;
    private  String WeekNoi;
    private String FrequencyRatei;
    private String TremorRatei;


    public PatientWI() {
    }

    public PatientWI(String patientNo, String weekNo, String frequencyRate, String tremorRate) {
        PatientNoi = patientNo;
        WeekNoi = weekNo;
        FrequencyRatei = frequencyRate;
        TremorRatei = tremorRate;
    }

    public String getPatientNo() {
        return PatientNoi;
    }

    public void setPatientNo(String patientNo) {
        PatientNoi = patientNo;
    }

    public String getWeekNo() {
        return WeekNoi;
    }

    public void setWeekNo(String weekNo) {
        WeekNoi = weekNo;
    }

    public String getFrequencyRate() {
        return FrequencyRatei;
    }

    public void setFrequencyRate(String frequencyRate) {
        FrequencyRatei = frequencyRate;
    }

    public String getTremorRate() {
        return TremorRatei;
    }

    public void setTremorRate(String tremorRate) {
        TremorRatei = tremorRate;
    }
}

