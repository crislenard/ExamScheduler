package edu.jru.deleon.crislenard.examscheduler;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class EditSched extends AppCompatActivity {

    EditText etNotes, etCourseName;
    TextView tvTime, tvExamDate,txtAlarmTime,etCourseCode;
    Button btnUpdate, btnDelete;
    int day, month, year, hour, min,tt,id,defVal=0;
    String date,coursecode,coursename,time,notes,alarmtime;
    Calendar mCurrentDate;
    String newdate;
    DbHandler myDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_sched);
        btnUpdate = (Button)findViewById(R.id.btnUpdate);
        btnDelete = (Button)findViewById(R.id.btnDelete);
        tvExamDate = (TextView)findViewById(R.id.tvDate1);
        tvTime = (TextView)findViewById(R.id.tvTime1);
        etCourseCode = (TextView)findViewById(R.id.etCourseCode1);
        etCourseName = (EditText)findViewById(R.id.etCourseName1);
        etNotes = (EditText)findViewById(R.id.etNotes1);
        mCurrentDate = Calendar.getInstance();
        day = mCurrentDate.get(Calendar.DAY_OF_MONTH);
        month = mCurrentDate.get(Calendar.MONTH);
        year = mCurrentDate.get(Calendar.YEAR);
        hour = mCurrentDate.get(Calendar.HOUR_OF_DAY);
        txtAlarmTime = (TextView)findViewById(R.id.txtAlarmTime1);
        myDb = new DbHandler(this);

        Intent intent = getIntent();
        coursecode = intent.getStringExtra("COURSE CODE");
        coursename = intent.getStringExtra("COURSE NAME");
        notes = intent.getStringExtra("NOTES");
        date = intent.getStringExtra("EXAM DATE");
        time = intent.getStringExtra("EXAM TIME");
        id = intent.getIntExtra("ID", -1);
        alarmtime = intent.getStringExtra("ALARM");

        etNotes.setText(notes);
        etCourseName.setText(coursename);
        etCourseCode.setText(coursecode);
        tvTime.setText(time);
        tvExamDate.setText(date);
        txtAlarmTime.setText(alarmtime);
        getExamDate();
        getExamTime();
        updateSched();
        deleteSched();
        service();
        alarmBefore();


    }

    public void getExamDate(){
        tvExamDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditSched.this, new DatePickerDialog.OnDateSetListener() {
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(EditSched.this, new TimePickerDialog.OnTimeSetListener() {
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
                newdate = hour+":"+min;
                timePickerDialog.show();
            }
        });

    }


    public void updateSched(){
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               // myDb.updateExam(id,etCourseCode.getText().toString(),tvExamDate.getText().toString());
                String newCourseCode = etCourseCode.getText().toString();
                String  newCourseName = etCourseName.getText().toString();
                String newNotes = etNotes.getText().toString();
                myDb.updateSched2(id,coursecode,etCourseCode.getText().toString(),etCourseName.getText().toString(),
                        tvExamDate.getText().toString(),tvTime.getText().toString(),etNotes.getText().toString(),
                        txtAlarmTime.getText().toString());


              /*  myDb.updateSched(id,coursecode,etCourseCode.getText().toString(),etCourseName.getText().toString(),
                        tvExamDate.getText().toString(),tvTime.getText().toString(),etNotes.getText().toString(),
                        txtAlarmTime.getText().toString()); */
                Intent intent = new Intent(EditSched.this,MainActivity.class);
                startActivity(intent);
                toastMessage("SCHEDULE UPDATED!");
            }
        });
    }

    public void deleteSched(){
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDb.deleteSched(id,coursecode);
                Intent intent = new Intent(EditSched.this,MainActivity.class);
                startActivity(intent);
                toastMessage("SCHEDULE DELETED!");
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

    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}
