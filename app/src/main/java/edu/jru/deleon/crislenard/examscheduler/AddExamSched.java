package edu.jru.deleon.crislenard.examscheduler;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddExamSched extends AppCompatActivity {
    Button btnExamSched;
    EditText etCourseName, etCourseCode, etNotes;
    String date,coursecode,coursename,notes,status,time,alarmtime;

    DbHandler myDB;
    TextView tvExamDate,tvTime,txtAlarmTime;
    int day, month, year, hour, min,tt;
    Calendar mCurrentDate;
    String newdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exam_sched);
        myDB= new DbHandler(this);
        btnExamSched = (Button)findViewById(R.id.btnAdd);
        etCourseName = (EditText)findViewById(R.id.etCourseName);
        etCourseCode = (EditText)findViewById(R.id.etCourseCode);
        etNotes = (EditText)findViewById(R.id.etNotes);
        tvExamDate = (TextView)findViewById(R.id.tvDate);
        tvTime = (TextView)findViewById(R.id.tvTime);
        txtAlarmTime = (TextView)findViewById(R.id.txtAlarmTime);
        mCurrentDate = Calendar.getInstance();

        day = mCurrentDate.get(Calendar.DAY_OF_MONTH);
        month = mCurrentDate.get(Calendar.MONTH);
        year = mCurrentDate.get(Calendar.YEAR);
        hour = mCurrentDate.get(Calendar.HOUR_OF_DAY);

        service();
        alarmBefore();
        getExamDate();
        getExamTime();

        coursecode = etCourseCode.getText().toString();
        coursename = etCourseName.getText().toString();
        notes = etNotes.getText().toString();
        status = "Upcoming";
        date = tvExamDate.getText().toString();
        time = tvTime.getText().toString();

     //   getterData();
        addExamSched();
    }




    public void addExamSched(){
        btnExamSched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( myDB.addData(etCourseCode.getText().toString(), etCourseName.getText().toString(),tvExamDate.getText().toString(),
                        tvTime.getText().toString(), etNotes.getText().toString(),status,txtAlarmTime.getText().toString())){

                    Toast.makeText(AddExamSched.this,"EXAM SCHEDULE ADDED",Toast.LENGTH_SHORT).show();

                }
                else{
                    Toast.makeText(AddExamSched.this,"EXAM SCHEDULE NOT ADDED",Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent(AddExamSched.this,MainActivity.class);
                startActivity(intent);

            }
        });

    }


    public void getExamDate(){
        tvExamDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddExamSched.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        monthOfYear = monthOfYear+1;
                        tvExamDate.setText(dayOfMonth+"/"+monthOfYear+"/"+year);

                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });
    }

    public void getExamTime(){
        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddExamSched.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int mins) {

                        String time = hour+":"+mins;
                        int beforehour = hour - 1;
                        String beforetime = beforehour+":"+mins;

                        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
                        Date date = null;
                        Date before = null;
                        try{
                            date = fmt.parse(time);
                            before = fmt.parse(beforetime);

                        }catch (ParseException e){
                            e.printStackTrace();
                        }

                        SimpleDateFormat frmtOut = new SimpleDateFormat("hh:mm aa");
                        String formattedTime = frmtOut.format(date);
                        tvTime.setText(formattedTime);
                        alarmtime = frmtOut.format(before);
                        txtAlarmTime.setText(alarmtime);
                    }
                },hour,min,false);
               // newdate = hour+":"+min;
                timePickerDialog.show();
            }
        });

    }
    public void service() {
        Intent intent = new Intent(getApplicationContext(), MyAlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, MyAlarmReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = 1000; //first run of alarm is immediate // aranca la palicacion
        int intervalMillis = 1 * 2 * 1000; //2 segundos
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, intervalMillis, pIntent);

    }
    public void alarmBefore(){
        Intent intent1 = new Intent(getApplicationContext(), MyAlarmReceiver2.class);
        final PendingIntent pIntent1 = PendingIntent.getBroadcast(this, MyAlarmReceiver2.REQUEST_CODE, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis1 = 1000; //first run of alarm is immediate // aranca la palicacion
        int intervalMillis1 = 1 * 2 * 1000; //2 segundos
        AlarmManager alarm1 = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm1.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis1, intervalMillis1, pIntent1);

    }


}
