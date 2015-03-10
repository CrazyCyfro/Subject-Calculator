package com.doritos.mtndew.gpacalculator;

/**
 * Created by Yan Wei on 24/2/2015.
 */
public class GPA_Entry {

    private String mAssignment;
    private double mWeightage;
    private double mScore_received;
    private double mTotal_score;

    public GPA_Entry(String assignment, double weightage, double score_received, double total_score){
        mAssignment = assignment;
        mWeightage = weightage;
        mScore_received = score_received;
        mTotal_score = total_score;
    }

    public String getAssignment() {
        return mAssignment;
    }

    public void setAssignment(String assignment) {
        mAssignment = assignment;
    }

    public double getWeightage() {
        return mWeightage;
    }

    public void setWeightage(double weightage) {
        mWeightage = weightage;
    }

    public double getScore_received() {
        return mScore_received;
    }

    public void setScore_received(double score_received) {
        mScore_received = score_received;
    }

    public double getTotal_score() {
        return mTotal_score;
    }

    public void setTotal_score(double total_score) {
        mTotal_score = total_score;
    }




}
