package edu.jru.deleon.crislenard.examscheduler;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {
    FloatingActionButton btnAddExam;
    TextView txtTotalUpcoming;
    int count=0;
    DbHandler myDB;
    public ListView lvExamSched;
    int blue = Color.parseColor("#004D40");

    Calendar calendar = Calendar.getInstance();
    int day, min,month,year,hours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        myDB= new DbHandler(this);
        lvExamSched = (ListView)findViewById(R.id.lvExam);
        TextView txtTest = (TextView)findViewById(R.id.txtTest);
        TextView txtTest2 = (TextView)findViewById(R.id.txtTest2);

        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH)+1;
        year = calendar.get(Calendar.YEAR);
        hours = calendar.get(Calendar.HOUR_OF_DAY)-1;
        min = calendar.get(Calendar.MINUTE);
        String yearsystem,hoursystem;
        yearsystem= day+"/"+month+"/"+year;
        hoursystem=hours+":"+min;

        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
        Date date = null;
        try {
            date = fmt.parse(hoursystem);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat frmtOut = new SimpleDateFormat("hh:mm a");
        String formattedTime = frmtOut.format(date);

        txtTest.setText(formattedTime);
        txtTest2.setText(yearsystem);



    }


    public void populateListView() {
        //  Log.d(TAG, "populateListView: Displaying data in the ListView.");

        //get the data and append to a list
        Cursor data = myDB.getData();
        ArrayList<String> listData = new ArrayList<>();
        while(data.moveToNext()){
            //get the value from the database in column 1
            //then add it to the ArrayList
            listData.add(data.getString(1));
        }
        //create the list adapter and set the adapter
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        lvExamSched.setAdapter(adapter);

        //set an onItemClickListener to the ListView
        lvExamSched.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = adapterView.getItemAtPosition(i).toString();

                Cursor data = myDB.getItemID(name); //get the id associated with that name
                int itemID = -1;
                while(data.moveToNext()){
                    itemID = data.getInt(0);
                }
                if(itemID > -1){

                }

                else{}

            }
        });
    }

}
