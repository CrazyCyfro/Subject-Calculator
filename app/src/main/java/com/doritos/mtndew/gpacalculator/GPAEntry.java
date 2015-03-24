package com.doritos.mtndew.gpacalculator;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Yan Wei on 24/2/2015.
 */
public class GPAEntry implements Parcelable{

    private String mAssignment;
    private double mWeightage;
    private double mScore_received;
    private double mTotal_score;

    public GPAEntry(String assignment, double weightage, double score_received, double total_score){
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


    //implement Parcelable to allow for transfer via Intents
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mAssignment);
        out.writeDouble(mWeightage);
        out.writeDouble(mScore_received);
        out.writeDouble(mTotal_score);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<GPAEntry> CREATOR = new Parcelable.Creator<GPAEntry>() {
        public GPAEntry createFromParcel(Parcel in) {
            return new GPAEntry(in);
        }

        public GPAEntry[] newArray(int size) {
            return new GPAEntry[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private GPAEntry(Parcel in) {
        mAssignment = in.readString();
        mWeightage = in.readDouble();
        mScore_received = in.readDouble();
        mTotal_score = in.readDouble();
    }


}
