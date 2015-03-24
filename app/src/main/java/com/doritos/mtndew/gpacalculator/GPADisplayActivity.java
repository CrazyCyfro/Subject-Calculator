package com.doritos.mtndew.gpacalculator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class GPADisplayActivity extends Activity {
    private EditText mAssignment;       //name of assignment
    private EditText mWeightage;        //weightage in %
    private EditText mScoreReceived;   //score received
    private EditText mTotalScore;      //total score
    private Button mAddButton;          //"add" button
    private Button mCalculateButton;    //"calculate" button
    private Button mClearButton;         //"clear button
    private Button mSubmitButton;       //"submit" button
    private ListView mLVGPA;           //items in the list

    private Double mTotalWeightage;    //total weightage of entries
    private DecimalFormat decimalFormatter; //DecimalFormat to allow for rounding

    private Double mGPAPercent;
    private Double mFinalGPA;

    private GPAEntry mSelectedGPAEntry;
    private int mSelectedGPAPosition;

    private Toast toast;

    private ArrayList<GPAEntry> mGPAArray;    //array of raw data as GPAEntry

    private ArrayAdapter<GPAEntry> mGPAAdapter;   //adapter for string array

    //string keys for subject name and SubjectEntry sent over from SubjectDisplayActivity
    public static final String SUBJECT_NAME = "com.doritos.mtndew.gpacalculator.subject_name";
    public static final String SUBJECT_ENTRY_EDIT = "com.doritos.mtndew.gpacalculator.subject_entry_edit";

    //string keys for returning and editing subjects
    public static final String RETURNED_SUBJECT = "com.doritos.mtndew.gpacalculator.returned_subject";
    public static final String EDITED_SUBJECT = "com.doritos.mtndew.gpacalculator.edited_subject";

    //request code
    public static final String REQUEST_CODE = "com.doritos.mtndew.gpacalculator.request_code";

    //string keys for savedInstanceState
    public static final String GPA_ENTRY_ARRAYLIST = "gpa entry arraylist";
    public static final String SELECTED_GPA_POSITION = "selected gpa position";
    public static final String ASSIGNMENT_NAME = "assignment name";
    public static final String ASSIGNMENT_WEIGHTAGE = "assignment weightage";
    public static final String ASSIGNMENT_SCORE_RECEIVED = "assignment score received";
    public static final String ASSIGNMENT_TOTAL_SCORE = "assignment total score";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpa_display);

        mAssignment = (EditText)this.findViewById(R.id.assignmentEditText);
        mWeightage = (EditText)this.findViewById(R.id.weightageEditText);
        mScoreReceived = (EditText)this.findViewById(R.id.scoreEditText);
        mTotalScore = (EditText)this.findViewById(R.id.maxScoreEditText);
        mAddButton = (Button)this.findViewById(R.id.add_button);
        mCalculateButton = (Button)this.findViewById(R.id.calculate_button);
        mClearButton = (Button)this.findViewById(R.id.clear_button);
        mSubmitButton = (Button)this.findViewById(R.id.submit_button);
        mLVGPA = (ListView)this.findViewById(R.id.listView_items);

        mTotalWeightage = 0.0;
        decimalFormatter = new DecimalFormat("#.0");    //set rounding to 1 dp

        mGPAPercent = 0.0;
        mFinalGPA = 0.0;

        mSelectedGPAEntry = new GPAEntry("",0.0,0.0,0.0);
        mSelectedGPAPosition = 0;

        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);

        mGPAArray = new ArrayList<>(); //array of raw GPAEntry items

        //override getView method of adapter to display text in item and sub-item fields of simple_list_item_2
        mGPAAdapter = new ArrayAdapter<GPAEntry>(this, android.R.layout.simple_list_item_2, android.R.id.text1, mGPAArray) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView)view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                text1.setText(mGPAArray.get(position).getAssignment());
                text2.setText("Weightage: "+String.valueOf(mGPAArray.get(position).getWeightage())+"%\nScore: "+String.valueOf(mGPAArray.get(position).getScore_received())+"/"+String.valueOf(mGPAArray.get(position).getTotal_score()));
                return view;
            }
        };

        //set the adapter for the list items
        mLVGPA.setAdapter(mGPAAdapter);

        if (savedInstanceState != null) {
            //restore mGPAArray and mTotalWeightage by iterating through the saved version
            ArrayList<GPAEntry> tempGPAArray = savedInstanceState.getParcelableArrayList(GPA_ENTRY_ARRAYLIST);

            for (GPAEntry mTemp:tempGPAArray) {
                mGPAArray.add(mTemp);
            }

            calculateWeightage();

            mSelectedGPAPosition = savedInstanceState.getInt(SELECTED_GPA_POSITION);
            mSelectedGPAEntry = mGPAArray.get(mSelectedGPAPosition);

            //restore EditText fields
            mAssignment.setText(savedInstanceState.getString(ASSIGNMENT_NAME));
            mWeightage.setText(savedInstanceState.getString(ASSIGNMENT_WEIGHTAGE));
            mScoreReceived.setText(savedInstanceState.getString(ASSIGNMENT_SCORE_RECEIVED));
            mTotalScore.setText(savedInstanceState.getString(ASSIGNMENT_TOTAL_SCORE));

        } else {
            //if new Activity is started to edit existing SubjectEntry, populate GPAEntry ListView with its items
            if (getIntent().getIntExtra(GPADisplayActivity.REQUEST_CODE, 0) == 2) {
                displaySubjectForEdit();
            }
        }

        setUpView();

    }

    private void setUpView() {

        mLVGPA.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                mSelectedGPAEntry = (GPAEntry)adapter.getItemAtPosition(position);
                mSelectedGPAPosition = position;

                mAssignment.setText(mSelectedGPAEntry.getAssignment());
                mWeightage.setText(String.valueOf(mSelectedGPAEntry.getWeightage()));
                mScoreReceived.setText(String.valueOf(mSelectedGPAEntry.getScore_received()));
                mTotalScore.setText(String.valueOf(mSelectedGPAEntry.getTotal_score()));

            }
        });

        mLVGPA.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View v, int position, long id) {
                mSelectedGPAEntry = (GPAEntry)adapter.getItemAtPosition(position);
                mSelectedGPAPosition = position;

                mGPAArray.remove(mSelectedGPAEntry);
                mGPAAdapter.notifyDataSetChanged();

                calculateWeightage();

                return false;
            }
        });

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
                else if (mAssignment.getText().toString().equals(mSelectedGPAEntry.getAssignment())) {
                    if ((mTotalWeightage - mSelectedGPAEntry.getWeightage()+Double.parseDouble(mWeightage.getText().toString())) <= 100) {
                        mSelectedGPAEntry.setWeightage(Double.parseDouble(mWeightage.getText().toString()));
                        mSelectedGPAEntry.setScore_received(Double.parseDouble(mScoreReceived.getText().toString()));
                        mSelectedGPAEntry.setTotal_score(Double.parseDouble(mTotalScore.getText().toString()));

                        mGPAAdapter.notifyDataSetChanged();
                        calculateWeightage();
                    } else {
                        mWeightage.setError(getText(R.string.weightage_101_error));
                    }
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

                //check if there are items in the list, if not show error toast
                if (mGPAArray.size() == 0) {
                    displayEmptyError();
                } else {
                //launch Intent to show subject GPA, percentage and completed weightage
                    calculateGPA();

                    startGPAIntent();
                }
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                if (mGPAArray.size() == 0) {
                    displayEmptyError();
                } else {

                    if (getIntent().getIntExtra(GPADisplayActivity.REQUEST_CODE, 0) == 1) {
                        //if request is new subject, launch submit Intent
                        calculateGPA();
                        startSubmitIntent();
                    } else if (getIntent().getIntExtra(GPADisplayActivity.REQUEST_CODE,0) == 2) {
                        //else if request is an edit of existing subject, launch edit Intent
                        calculateGPA();
                        startEditIntent();
                    }
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_subject, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //save current GPAEntry ArrayList and text in all EditText fields
        outState.putParcelableArrayList(GPA_ENTRY_ARRAYLIST, mGPAArray);
        outState.putInt(SELECTED_GPA_POSITION, mSelectedGPAPosition);
        outState.putString(ASSIGNMENT_NAME,mAssignment.getText().toString());
        outState.putString(ASSIGNMENT_WEIGHTAGE, mWeightage.getText().toString());
        outState.putString(ASSIGNMENT_SCORE_RECEIVED, mScoreReceived.getText().toString());
        outState.putString(ASSIGNMENT_TOTAL_SCORE, mTotalScore.getText().toString());
    }

    private void addEntry() {


        //convert entered data into GPAEntry
        GPAEntry Raw_Entry = new GPAEntry(mAssignment.getText().toString(), Double.parseDouble(mWeightage.getText().toString()), Double.parseDouble(mScoreReceived.getText().toString()), Double.parseDouble(mTotalScore.getText().toString()));

        //add GPAEntry to raw array
        mGPAArray.add(Raw_Entry);

        //update the adapter
        mGPAAdapter.notifyDataSetChanged();

        //reset all the EditText fields
        mAssignment.setText("");
        mWeightage.setText("");
        mScoreReceived.setText("");
        mTotalScore.setText("");

        calculateWeightage();

    }

    private void calculateWeightage() {
        mTotalWeightage = 0.0;

        //recalculate weightage
        for (GPAEntry mRaw : mGPAArray) {
            mTotalWeightage += mRaw.getWeightage();
        }
    }

    //calculates GPA from scratch and updates all relevant fields (mGPAPercent, mFinalGPA, mTotalWeightage)
    private void calculateGPA() {
        calculateWeightage();

        mGPAPercent = 0.0;
        mFinalGPA = 0.0;

        //calculate percentage based on weightage
        for (GPAEntry mRaw : mGPAArray) {

            mGPAPercent += mRaw.getWeightage() * mRaw.getScore_received() / (mRaw.getTotal_score()*mTotalWeightage * 0.01);
        }

        //round GPA off to 1 decimal point
        mGPAPercent = Double.parseDouble(decimalFormatter.format(mGPAPercent));

        //determine final GPA
        if (mGPAPercent >= 80) {
            mFinalGPA = 4.0;
        } else if (mGPAPercent >= 70 && mGPAPercent < 80 ) {
            mFinalGPA = 3.6;
        } else if (mGPAPercent >= 65 && mGPAPercent < 70) {
            mFinalGPA = 3.2;
        } else if (mGPAPercent >= 60 && mGPAPercent < 65) {
            mFinalGPA = 2.8;
        } else if (mGPAPercent >= 55 && mGPAPercent < 60) {
            mFinalGPA = 2.4;
        } else if (mGPAPercent >= 50 && mGPAPercent < 55) {
            mFinalGPA = 2.0;
        } else if (mGPAPercent >= 45 && mGPAPercent < 50) {
            mFinalGPA = 1.6;
        } else if (mGPAPercent >= 40 && mGPAPercent < 45) {
            mFinalGPA = 1.2;
        } else {
            mFinalGPA = 0.8;
        }
    }

    private void startGPAIntent () {
        //spawn intent to show GPA
        Intent intent = new Intent(GPADisplayActivity.this, GPACalculationActivity.class);

        //send string containing info
        intent.putExtra(GPACalculationActivity.FINAL_GPA,mFinalGPA+"\n\nPercentage: "+mGPAPercent+"%\nWeightage: "+Double.parseDouble(decimalFormatter.format(mTotalWeightage))+"%");
        startActivity(intent);
    }

    //sends result Intent back to SubjectDisplayActivity to add a new subject
    private void startSubmitIntent() {
        //create the SubjectEntry to be returned
        SubjectEntry returnedSubject = new SubjectEntry(getIntent().getStringExtra(GPADisplayActivity.SUBJECT_NAME), mGPAPercent, mFinalGPA, mGPAArray);

        //send it to SubjectDisplayActivity
        Intent returnSubjectIntent = new Intent();
        returnSubjectIntent.putExtra(GPADisplayActivity.RETURNED_SUBJECT, returnedSubject);

        setResult(RESULT_OK,returnSubjectIntent);
        finish();
    }

    //sends result Intent back to SubjectDisplayActivity to edit an existing subject
    private void startEditIntent() {

        //create edited subject
        SubjectEntry editedSubject = new SubjectEntry(getIntent().getStringExtra(GPADisplayActivity.SUBJECT_NAME), mGPAPercent, mFinalGPA, mGPAArray);

        //send it to SubjectDisplayActivity
        Intent editSubjectIntent = new Intent();
        editSubjectIntent.putExtra(GPADisplayActivity.EDITED_SUBJECT, editedSubject);

        setResult(RESULT_OK, editSubjectIntent);
        finish();

    }

    //displays existing subject when selected from SubjectDisplayActivity's ListView
    private void displaySubjectForEdit() {
        //retrieve SubjectEntry
        SubjectEntry subjectEntryForEdit = getIntent().getParcelableExtra(GPADisplayActivity.SUBJECT_ENTRY_EDIT);

        //Populate mGPAArray with the SubjectEntry's GPAEntry ArrayList and total up the weightage
        for (GPAEntry mEntry: subjectEntryForEdit.getmGPA_Array()) {
            mGPAArray.add(mEntry);
            mTotalWeightage += mEntry.getWeightage();
        }
    }

    //function to clear arrays and update the list
    private void clearData() {

        mTotalWeightage = 0.0;

        mGPAArray.clear();

        mGPAAdapter.notifyDataSetChanged();
    }

    //shows error toast when attempting to calculate or submit with no GPAEntry's
    private void displayEmptyError() {

        toast.setText(R.string.empty_array_error);
        toast.show();
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
