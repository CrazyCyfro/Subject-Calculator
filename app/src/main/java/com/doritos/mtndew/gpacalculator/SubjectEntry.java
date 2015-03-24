package com.doritos.mtndew.gpacalculator;

import android.os.Parcelable;
import android.os.Parcel;

import java.util.ArrayList;

/**
 * Created by Yan on 17/3/2015.
 */
public class SubjectEntry implements Parcelable{

    private String mSubject_name;
    private double mPercentage;
    private double mGPA;
    private ArrayList<GPAEntry> mGPA_Array;

    public SubjectEntry(String mSubject_name, double mPercentage, double mGPA, ArrayList<GPAEntry> mGPA_Array) {
        this.mSubject_name = mSubject_name;
        this.mPercentage = mPercentage;
        this.mGPA = mGPA;
        this.mGPA_Array = mGPA_Array;
    }

    public String getmSubject_name() {
        return mSubject_name;
    }

    public void setmSubject_name(String mSubject_name) {
        this.mSubject_name = mSubject_name;
    }

    public double getmPercentage() {
        return mPercentage;
    }

    public void setmPercentage(double mPercentage) {
        this.mPercentage = mPercentage;
    }

    public double getmGPA() {
        return mGPA;
    }

    public void setmGPA(double mGPA) {
        this.mGPA = mGPA;
    }

    public ArrayList<GPAEntry> getmGPA_Array() {
        return mGPA_Array;
    }

    public void setmGPA_Array(ArrayList<GPAEntry> mGPA_Array) {
        this.mGPA_Array = mGPA_Array;
    }

    //implement Parcelable to allow for transfer via Intents
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mSubject_name);
        out.writeDouble(mPercentage);
        out.writeDouble(mGPA);
        out.writeTypedList(mGPA_Array);
    }

    public static final Parcelable.Creator<SubjectEntry> CREATOR = new Parcelable.Creator<SubjectEntry>() {
        public SubjectEntry createFromParcel(Parcel in) {
            return new SubjectEntry(in);
        }

        public SubjectEntry[] newArray(int size) {
            return new SubjectEntry[size];
        }
    };

    private SubjectEntry(Parcel in){

        mSubject_name = in.readString();
        mPercentage = in.readDouble();
        mGPA = in.readDouble();

        mGPA_Array = new ArrayList<>();
        in.readTypedList(mGPA_Array, GPAEntry.CREATOR);
    }
}
