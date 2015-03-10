package com.doritos.mtndew.gpacalculator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainActivity extends Activity {
    private EditText mAssignment;       //name of assignment
    private EditText mWeightage;        //weightage in %
    private EditText mScoreReceived;   //score received
    private EditText mTotalScore;      //total score
    private Button mAddButton;          //"add" button
    private Button mCalculateButton;    //"calculate" button
    private Button mClearButton;         //"clear button
    private ListView mLVItem;           //items in the list

    private Double mTotalWeightage;    //total weightage of entries
    private DecimalFormat decimalFormatter; //DecimalFormat to allow for rounding

    private ArrayList<GPA_Entry> mGPA_Array_Raw;    //array of raw data as GPA_Entry

    private ArrayList<String> mGPA_Array_String;    //array of strings for the list items
    private ArrayAdapter<String> mGPA_Adapter_String;   //adapter for string array

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpView();

    }

    private void setUpView() {
        mAssignment = (EditText)this.findViewById(R.id.assignment);
        mWeightage = (EditText)this.findViewById(R.id.weightage);
        mScoreReceived = (EditText)this.findViewById(R.id.score_received);
        mTotalScore = (EditText)this.findViewById(R.id.total_score);
        mAddButton = (Button)this.findViewById(R.id.button_add);
        mCalculateButton = (Button)this.findViewById(R.id.calculate_button);
        mClearButton = (Button)this.findViewById(R.id.clear_button);
        mLVItem = (ListView)this.findViewById(R.id.listView_items);
        mTotalWeightage = 0.0;
        decimalFormatter = new DecimalFormat("#.0");    //set rounding to 1 dp


        mGPA_Array_Raw = new ArrayList<>(); //array of raw GPA_Entry items
        mGPA_Array_String = new ArrayList<>();  //array of GPA_Entry strings

        //attach adapter to array for strings
        mGPA_Adapter_String = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mGPA_Array_String);

        //set the adapter for the list items
        mLVItem.setAdapter(mGPA_Adapter_String);

        mAddButton.setOnClickListener(new View.OnClickListener() {

            //check for validity of entries
            public void onClick(View v) {
                 //check for empty fields
                if (mAssignment.getText().toString().length() == 0) {
                    mAssignment.setError(getText(R.string.empty_error));
                } else if (mWeightage.getText().toString().length() == 0) {
                    mWeightage.setError(getText(R.string.empty_error));
                } else if (mScoreReceived.getText().toString().length() == 0) {
                    mScoreReceived.setError(getText(R.string.empty_error));
                } else if (mTotalScore.getText().toString().length() == 0) {
                    mTotalScore.setError(getText(R.string.empty_error));
                }
                //check for invalid fields
                else if (!isDouble(mWeightage.getText().toString())) {
                    mWeightage.setError(getText(R.string.invalid_double_error));
                } else if (!isDouble(mScoreReceived.getText().toString())) {
                    mScoreReceived.setError(getText(R.string.invalid_double_error));
                } else if (!isDouble(mTotalScore.getText().toString())) {
                    mTotalScore.setError(getText(R.string.invalid_double_error));
                }
                //check if total score is 0
                else if (Double.parseDouble(mTotalScore.getText().toString()) == 0) {
                    mTotalScore.setError(getText(R.string.total_score_0_error));
                }
                //check if weightage > 100%
                else if (Double.parseDouble(mWeightage.getText().toString()) > 100) {
                    mWeightage.setError(getText(R.string.weightage_101_error));
                }
                //check if score received is more than total score
                else if (Double.parseDouble(mScoreReceived.getText().toString()) > Double.parseDouble(mTotalScore.getText().toString())) {
                    mScoreReceived.setError(getText(R.string.score_received_total_error));
                }
                //check if weightage in weightage field will exceed 100% if entered
                else if ((mTotalWeightage+Double.parseDouble(mWeightage.getText().toString())) > 100) {
                    //format value of remaining weightage to avoid showing long chain of decimals (value might be wrong by 0.1 if 2 dp weightages are entered :/ )
                    mWeightage.setError(getText(R.string.total_weightage_101_error)+" "+String.valueOf(Double.parseDouble(decimalFormatter.format(100-mTotalWeightage)))+"%");
                } else {
                    addEntry();
                }
            }
        });

        mCalculateButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //create error toast
                Toast toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);

                //check if there are items in the list, if not show error toast
                if (mGPA_Array_Raw.size() == 0) {
                    toast.setText(R.string.empty_array_error);
                    toast.show();
                } else {
                    calculateGPA();
                }
            }
        });

        //clear all entries
        mClearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clearData();
            }
        });
    }

    private void addEntry() {

        //add weightage to mTotalWeightage
        mTotalWeightage += Double.parseDouble(mWeightage.getText().toString());

        //convert entered data into GPA_Entry
        GPA_Entry Raw_Entry = new GPA_Entry(mAssignment.getText().toString(), Double.parseDouble(mWeightage.getText().toString()), Double.parseDouble(mScoreReceived.getText().toString()), Double.parseDouble(mTotalScore.getText().toString()));

        //add GPA_Entry to raw array
        mGPA_Array_Raw.add(Raw_Entry);

        //convert entered data into a string
        String Entry = mAssignment.getText().toString()+"\nWeightage: "+mWeightage.getText().toString()+"%\nScore: "+mScoreReceived.getText().toString()+"/"+mTotalScore.getText().toString();

        //add the converted string to the list items
        mGPA_Array_String.add(Entry);

        //update the adapter
        mGPA_Adapter_String.notifyDataSetChanged();



        //reset all the EditText fields
        mAssignment.setText("");
        mWeightage.setText("");
        mScoreReceived.setText("");
        mTotalScore.setText("");

    }

    private void calculateGPA() {
        Double mGPA_Percent = 0.0;
        Double mFinal_GPA;


        //calculate percentage based on weightage
        for (GPA_Entry mRaw : mGPA_Array_Raw) {

            mGPA_Percent += mRaw.getWeightage() * mRaw.getScore_received() / (mRaw.getTotal_score()*mTotalWeightage * 0.01);
        }

        //round GPA off to 1 decimal point
        mGPA_Percent = Double.parseDouble(decimalFormatter.format(mGPA_Percent));

        //determine final GPA
        if (mGPA_Percent >= 80) {
            mFinal_GPA = 4.0;
        } else if (mGPA_Percent >= 70 && mGPA_Percent < 80 ) {
            mFinal_GPA = 3.6;
        } else if (mGPA_Percent >= 65 && mGPA_Percent < 70) {
            mFinal_GPA = 3.2;
        } else if (mGPA_Percent >= 60 && mGPA_Percent < 65) {
            mFinal_GPA = 2.8;
        } else if (mGPA_Percent >= 55 && mGPA_Percent < 60) {
            mFinal_GPA = 2.4;
        } else if (mGPA_Percent >= 50 && mGPA_Percent < 55) {
            mFinal_GPA = 2.0;
        } else if (mGPA_Percent >= 45 && mGPA_Percent < 50) {
            mFinal_GPA = 1.6;
        } else if (mGPA_Percent >= 40 && mGPA_Percent < 45) {
            mFinal_GPA = 1.2;
        } else {
            mFinal_GPA = 0.8;
        }

        //spawn intent to show GPA
        Intent intent = new Intent(MainActivity.this, GPAActivity.class);
        //format total weightage to avoid showing long chain of decimals
        intent.putExtra(GPAActivity.FINAL_GPA,mFinal_GPA+"\n\nPercentage: "+mGPA_Percent+"%\nWeightage: "+Double.parseDouble(decimalFormatter.format(mTotalWeightage))+"%");
        startActivity(intent);

    }

    //function to clear arrays and update the list
    private void clearData() {

        mTotalWeightage = 0.0;

        mGPA_Array_Raw.clear();
        mGPA_Array_String.clear();

        mGPA_Adapter_String.notifyDataSetChanged();
    }

    //function to check if a string is a double
    private boolean isDouble(String str) {
        try {
            //attempt to parse string
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
