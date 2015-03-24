package com.doritos.mtndew.gpacalculator;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.widget.ListView;
import android.widget.TextView;


import java.text.DecimalFormat;
import java.util.ArrayList;

public class SubjectDisplayActivity extends ActionBarActivity {


    private Button mAddSubjectButton;
    private EditText mSubjectName;
    private TextView mCombinedGPA;

    private DecimalFormat decimalFormatter;

    private ListView mLVSubject;

    private ArrayList<SubjectEntry> mSubjectArray;

    private ArrayAdapter<SubjectEntry> mSubjectAdapter;

    //vars to get current selected subject entry
    private SubjectEntry mSelectedSubjectEntry;
    private int mSelectedSubjectPosition;

    //request codes for Intents
    public static final int ADD_SUBJECT_REQUEST = 1;
    public static final int EDIT_SUBJECT_REQUEST = 2;

    //string keys for savedInstanceState
    public static final String SUBJECT_ENTRY_ARRAYLIST = "subject arraylist";
    public static final String SUBJECT_NAME = "subject name";
    public static final String SELECTED_SUBJECT_POSITION = "selected subject position";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_display);

        mAddSubjectButton = (Button)this.findViewById(R.id.addSubjectButton);
        mSubjectName = (EditText)this.findViewById(R.id.subject_name);
        mCombinedGPA = (TextView)this.findViewById(R.id.combined_gpa);

        decimalFormatter = new DecimalFormat("#.00");

        mLVSubject = (ListView)this.findViewById(R.id.subjectListView);

        //defines the subject in ListView that is selected
        mSelectedSubjectEntry = new SubjectEntry("",0.0,0.0,null);
        mSelectedSubjectPosition = 0;

        mSubjectArray = new ArrayList<>();


        if (savedInstanceState != null) {
            //retrieve saved SubjectEntry ArrayList, if any
            mSubjectArray = savedInstanceState.getParcelableArrayList(SUBJECT_ENTRY_ARRAYLIST);
            mSubjectName.setText(savedInstanceState.getString(SUBJECT_NAME));
            mSelectedSubjectPosition = savedInstanceState.getInt(SELECTED_SUBJECT_POSITION);
        } else {

            //or else, populate ListView with sample data
            populateSubjectList();

        }

        setUpSubjectView();
    }

    private void setUpSubjectView() {

        //override getView method of adapter to display text in item and sub-item fields of simple_list_item_2
        mSubjectAdapter = new ArrayAdapter<SubjectEntry>(this, android.R.layout.simple_list_item_2, android.R.id.text1, mSubjectArray) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView)view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                //display subject name, percentage and GPA
                text1.setText(mSubjectArray.get(position).getmSubject_name());
                text2.setText("Percentage: "+String.valueOf(mSubjectArray.get(position).getmPercentage())+"%\nGPA: "+String.valueOf(mSubjectArray.get(position).getmGPA()));
                return view;
            }
        };

        mLVSubject.setAdapter(mSubjectAdapter);

        //if mSubjectArray is populated, calculate GPA
        if (mSubjectArray.size() > 0) {
            calculateCombinedGPA();
        }

        //when add button is clicked
        mAddSubjectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){

                if (mSubjectName.getText().toString().length() == 0) {
                    mSubjectName.setError(getText(R.string.subject_name_empty_error));
                } else {
                    //starts intent to add a new subject
                    Intent addSubjectIntent = new Intent(SubjectDisplayActivity.this, GPADisplayActivity.class);

                    //send over subject name entered and request code
                    addSubjectIntent.putExtra(GPADisplayActivity.SUBJECT_NAME, mSubjectName.getText().toString());
                    addSubjectIntent.putExtra(GPADisplayActivity.REQUEST_CODE, ADD_SUBJECT_REQUEST);

                    //startActivityForResult so we can interpret results later
                    startActivityForResult(addSubjectIntent, ADD_SUBJECT_REQUEST);
                }
            }
        });

        //when a SubjectEntry in the ListView is clicked
        mLVSubject.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {

                //get the respective SubjectEntry, and store its position
                mSelectedSubjectEntry = (SubjectEntry)adapter.getItemAtPosition(position);
                mSelectedSubjectPosition = position;

                //starts intent to edit existing subject
                Intent editSubjectIntent = new Intent(SubjectDisplayActivity.this, GPADisplayActivity.class);

                //send over subject name, SubjectEntry and request code
                editSubjectIntent.putExtra(GPADisplayActivity.SUBJECT_NAME, mSelectedSubjectEntry.getmSubject_name());
                editSubjectIntent.putExtra(GPADisplayActivity.SUBJECT_ENTRY_EDIT, mSelectedSubjectEntry);
                editSubjectIntent.putExtra(GPADisplayActivity.REQUEST_CODE, EDIT_SUBJECT_REQUEST);

                startActivityForResult(editSubjectIntent, EDIT_SUBJECT_REQUEST);
            }
        });

        mLVSubject.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View v, int position, long id) {
                mSelectedSubjectEntry = (SubjectEntry)adapter.getItemAtPosition(position);
                mSelectedSubjectPosition = position;

                mSubjectArray.remove(mSelectedSubjectEntry);
                mSubjectAdapter.notifyDataSetChanged();

                calculateCombinedGPA();

                return false;
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

        //save mSubjectArray, mSubjectName EditText field and mSelectedSubjectPosition
        outState.putParcelableArrayList(SUBJECT_ENTRY_ARRAYLIST, mSubjectArray);
        outState.putString(SUBJECT_NAME,mSubjectName.getText().toString());
        outState.putInt(SELECTED_SUBJECT_POSITION,mSelectedSubjectPosition);
    }

    //interpret GPADisplayActivity results
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_SUBJECT_REQUEST) {
            //if initial request was to add a new subject
            if (resultCode == RESULT_OK) {

                //retrieve new SubjectEntry and add it to mSubjectArray
                SubjectEntry returnedSubject = data.getParcelableExtra(GPADisplayActivity.RETURNED_SUBJECT);
                mSubjectArray.add(returnedSubject);

                mSubjectName.setText("");
            }
        } else if (requestCode == EDIT_SUBJECT_REQUEST) {
            //if initial request was to edit existing subject
            if (resultCode == RESULT_OK) {

                //retrieve new edited subject
                SubjectEntry editedSubject = data.getParcelableExtra(GPADisplayActivity.EDITED_SUBJECT);

                //find the outdated SubjectEntry with the position saved earlier
                mSelectedSubjectEntry = mSubjectAdapter.getItem(mSelectedSubjectPosition);

                //update its parameters
                mSelectedSubjectEntry.setmPercentage(editedSubject.getmPercentage());
                mSelectedSubjectEntry.setmGPA(editedSubject.getmGPA());
                mSelectedSubjectEntry.setmGPA_Array(editedSubject.getmGPA_Array());
            }
        }

        //update subject ListView and recalculate GPA
        mSubjectAdapter.notifyDataSetChanged();
        calculateCombinedGPA();
    }

    //calculate average GPA of all subjects and update combined GPA TextView
    private void calculateCombinedGPA() {
        Double mFinalCombinedGPA = 0.0;
        int mSubjectNumber = 0;
        for (SubjectEntry mSubjectEntry : mSubjectArray) {
            mFinalCombinedGPA += mSubjectEntry.getmGPA();
            mSubjectNumber++;
        }
        mFinalCombinedGPA /= mSubjectNumber;
        mCombinedGPA.setText(String.valueOf(decimalFormatter.format(mFinalCombinedGPA)));
    }

    //define sample data and populate subject ListView
    private void populateSubjectList() {
        GPAEntry gpa_1 = new GPAEntry("Dank Memes Test 420", 10, 420, 420);
        GPAEntry gpa_2 = new GPAEntry("Quickscoping 101",10,0,101);
        GPAEntry gpa_3 = new GPAEntry("Weed agriculture",10, 99, 420);

        ArrayList<GPAEntry> SwegList = new ArrayList<>();
        SwegList.add(gpa_1);
        SwegList.add(gpa_2);
        SwegList.add(gpa_3);

        SubjectEntry SwegSubject = new SubjectEntry("Sweg",calcPercent(SwegList),calcGPA(calcPercent(SwegList)), SwegList);

        GPAEntry gpa_4 = new GPAEntry("Android Programming Test", 10, 1, 99);
        GPAEntry gpa_5 = new GPAEntry("Zammil", 10, 99, 99);
        GPAEntry gpa_6 = new GPAEntry("Investment banking", 10, 100, 1000);

        ArrayList<GPAEntry> CEPList = new ArrayList<>();
        CEPList.add(gpa_4);
        CEPList.add(gpa_5);
        CEPList.add(gpa_6);

        SubjectEntry CEPSubject = new SubjectEntry("CEP",calcPercent(CEPList),calcGPA(calcPercent(CEPList)),CEPList);

        mSubjectArray.add(SwegSubject);
        mSubjectArray.add(CEPSubject);

    }

    //calculate and return percentage with GPAEntry ArrayList
    private double calcPercent(ArrayList<GPAEntry> GPAArray) {
        Double percent = 0.0;
        Double totalWeightage = 0.0;
        DecimalFormat df = new DecimalFormat("#.0");
        for (GPAEntry entry : GPAArray) {
            totalWeightage += entry.getWeightage();
        }
        for (GPAEntry entry : GPAArray) {
            percent += entry.getWeightage() * entry.getScore_received() / (entry.getTotal_score()*totalWeightage * 0.01);
        }
        percent = Double.parseDouble(df.format(percent));

        return percent;
    }

    //calculate and return GPA based on percentage given
    private double calcGPA(Double percent) {
        Double finalGPA;

        //determine final GPA
        if (percent >= 80) {
            finalGPA = 4.0;
        } else if (percent >= 70 && percent < 80 ) {
            finalGPA = 3.6;
        } else if (percent >= 65 && percent < 70) {
            finalGPA = 3.2;
        } else if (percent >= 60 && percent < 65) {
            finalGPA = 2.8;
        } else if (percent >= 55 && percent < 60) {
            finalGPA = 2.4;
        } else if (percent >= 50 && percent < 55) {
            finalGPA = 2.0;
        } else if (percent >= 45 && percent < 50) {
            finalGPA = 1.6;
        } else if (percent >= 40 && percent < 45) {
            finalGPA = 1.2;
        } else {
            finalGPA = 0.8;
        }
        return finalGPA;
    }
}
