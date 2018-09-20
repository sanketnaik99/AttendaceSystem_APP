package com.echo.attendacesystem;

import android.app.DatePickerDialog;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;

public class LectureDetails extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private Button dateButton, startTimeButton, endTimeButton;
    private TextView dateText, startTimeText, endTimeText;
    public String lectureDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_details);

        initviews();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        lectureDate = DateFormat.getDateInstance(DateFormat.SHORT).format(c.getTime());

        dateText.setText(lectureDate);
    }

    private void initviews(){
        dateButton = findViewById(R.id.dateSelectButton);
        dateText = findViewById(R.id.dateText);

        startTimeText = findViewById(R.id.startTimeText);
        startTimeButton = findViewById(R.id.startTimeButton);

        endTimeText = findViewById(R.id.endTimeText);
        endTimeButton = findViewById(R.id.endTimeButton);
    }

    public void dateSelect(View v){
        DialogFragment datePicker = new datePickerFragment();
        datePicker.show(getSupportFragmentManager(), "Date Picker");
    }
}
