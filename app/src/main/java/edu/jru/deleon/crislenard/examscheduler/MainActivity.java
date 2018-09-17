package edu.jru.deleon.crislenard.examscheduler;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
FloatingActionButton btnAddExam;
    TextView txtTotalUpcoming,txtTotalDone;
    int count=0;
    DbHandler myDB;
    public ListView lvExamSched;
    int blue = Color.parseColor("#004D40");
    ImageButton imgPopulator;
    CardView cardView,cardView2;

    Calendar calendar = Calendar.getInstance();
    int day, min,month,year,hours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDB= new DbHandler(this);
        lvExamSched = (ListView)findViewById(R.id.lvSched);
        btnAddExam = (FloatingActionButton)findViewById(R.id.btnAddExam);
        txtTotalUpcoming = (TextView)findViewById(R.id.txtTotalComing);
        txtTotalDone = (TextView)findViewById(R.id.txtTotalDone);
        cardView = (CardView)findViewById(R.id.cardView);
        cardView2 = (CardView)findViewById(R.id.cardView2);
        imgPopulator = (ImageButton)findViewById(R.id.btnPopulateAll);

        service();
        alarmBefore();
        populateListView();
        addExam();
        checkLoad();
        countUpcomingScheds();
        populateListView();
        countDoneScheds();
        populateDone();
        populateUpcoming();
        populateAll();


    }
    public void addExam(){

        btnAddExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this,AddExamSched.class);
                startActivity(intent);
            }
        });
    }
    public void checkLoad(){
        Cursor cursor = myDB.getData();
        if(cursor.getCount()==0){
            Toast.makeText(this,"No Schedules yet",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"Exam Schedules has been loaded!",Toast.LENGTH_SHORT).show();
        }

    }
    public void countUpcomingScheds(){
        count = myDB.countUpcoming();
        txtTotalUpcoming.setText(Integer.toString(count));
    }
    public void countDoneScheds(){
        count = myDB.countDone();
        txtTotalDone.setText(Integer.toString(count));
    }


    public void showMessage(String message, String status, final int id) {
       /* AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.show();*/


        Context context = findViewById(android.R.id.content).getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View formview = inflater.inflate(R.layout.dialog_exam, null, false);
        Button btnMark = (Button) formview.findViewById(R.id.btnMark);
        Button btnBack = (Button) formview.findViewById(R.id.btnBack);
        TextView txtMessage = (TextView) formview.findViewById(R.id.txtMessage);
        LinearLayout parentLayout = (LinearLayout) formview.findViewById(R.id.parentLayout);

        if (status.equalsIgnoreCase("Upcoming")) {
            parentLayout.setBackgroundColor(Color.parseColor("#8BC34A"));
            btnMark.setText("MARK AS DONE");
        }else if (status.equalsIgnoreCase("Done")){
            parentLayout.setBackgroundColor(Color.parseColor("#FDD835"));
            btnMark.setVisibility(View.GONE);
        }
        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        ad.setView(formview);
        txtMessage.setText(message);
        final AlertDialog alertDialog = ad.show();
        btnMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDB.updateStatusDialog(id);
                alertDialog.cancel();
                countUpcomingScheds();
                populateListView();
                countDoneScheds();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
    }
    public void populateAll(){
        imgPopulator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                populateListView();
            }
        });
    }
    public void populateDone(){
        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                populateListViewDone();
            }
        });
    }
    public void populateUpcoming(){
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                populateListViewUpcoming();
            }
        });
    }

    public void populateListViewUpcoming(){
        //  Log.d(TAG, "populateListView: Displaying data in the ListView.");

        //get the data and append to a list
        Cursor data = myDB.getDataUpcoming();
        //    List<String> labels = myDB.getDataListView();
        //   ListAdapter dataAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,labels);
        //   lvExamSched.setAdapter(dataAdapter);
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
        lvExamSched.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = adapterView.getItemAtPosition(i).toString();

                Cursor data = myDB.getItemID(name); //get the id associated with that name
                int itemID = -1;
                while(data.moveToNext()){
                    itemID = data.getInt(0);
                }
                if(itemID > -1){

                    data = myDB.getAllData(name);
                    while(data.moveToNext()) {
                        Intent intent = new Intent(MainActivity.this, EditSched.class);
                        intent.putExtra("ID",data.getInt(0));
                        intent.putExtra("COURSE CODE",data.getString(1));
                        intent.putExtra("COURSE NAME",data.getString(2));
                        intent.putExtra("EXAM DATE",data.getString(3));
                        intent.putExtra("EXAM TIME",data.getString(4));
                        intent.putExtra("NOTES",data.getString(5));
                        intent.putExtra("ALARM",data.getString(6));
                        startActivity(intent);
                    }
                }
                else{
                    toastMessage("NO COURSECODE ASSOCIATED");
                }

                return false;
            }
        });
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
                    data = myDB.getAllData(name);
                    if(data.moveToNext()) {
                        StringBuffer buffer = new StringBuffer();
                        String status = data.getString(7);
                        int id = data.getInt(0);

                        buffer.append("COURSE CODE: " + data.getString(1) + "\n");
                        buffer.append("SUBJECT: " + data.getString(2) + "\n");
                        buffer.append("EXAM DATE: " + data.getString(3) + "\n");
                        buffer.append("EXAM TIME: " + data.getString(4) + "\n");
                        buffer.append("NOTES: " + data.getString(5) + "\n");
                        buffer.append("STATUS: " + data.getString(7) + "\n\n");
                        // buffer.append("ALARM TIME: " + data.getString(6) + "\n\n");
                        showMessage(buffer.toString(),status,id);
                    }
                }

                else{
                    toastMessage("NO COURSECODE ASSOCIATED");
                }
            }
        });
    }

    public void populateListViewDone(){
        //  Log.d(TAG, "populateListView: Displaying data in the ListView.");

        //get the data and append to a list
        Cursor data = myDB.getDataDone();
        //    List<String> labels = myDB.getDataListView();
        //   ListAdapter dataAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,labels);
        //   lvExamSched.setAdapter(dataAdapter);
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
        lvExamSched.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = adapterView.getItemAtPosition(i).toString();

                Cursor data = myDB.getItemID(name); //get the id associated with that name
                int itemID = -1;
                while(data.moveToNext()){
                    itemID = data.getInt(0);
                }
                if(itemID > -1){

                    data = myDB.getAllData(name);
                    while(data.moveToNext()) {
                        Intent intent = new Intent(MainActivity.this, EditSched.class);
                        intent.putExtra("ID",data.getInt(0));
                        intent.putExtra("COURSE CODE",data.getString(1));
                        intent.putExtra("COURSE NAME",data.getString(2));
                        intent.putExtra("EXAM DATE",data.getString(3));
                        intent.putExtra("EXAM TIME",data.getString(4));
                        intent.putExtra("NOTES",data.getString(5));
                        intent.putExtra("ALARM",data.getString(6));
                        startActivity(intent);
                    }
                }
                else{
                    toastMessage("NO COURSECODE ASSOCIATED");
                }

                return false;
            }
        });
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
                    data = myDB.getAllData(name);
                    if(data.moveToNext()) {
                        StringBuffer buffer = new StringBuffer();
                        String status = data.getString(7);
                        int id = data.getInt(0);

                        buffer.append("COURSE CODE: " + data.getString(1) + "\n");
                        buffer.append("SUBJECT: " + data.getString(2) + "\n");
                        buffer.append("EXAM DATE: " + data.getString(3) + "\n");
                        buffer.append("EXAM TIME: " + data.getString(4) + "\n");
                        buffer.append("NOTES: " + data.getString(5) + "\n");
                        buffer.append("STATUS: " + data.getString(7) + "\n\n");
                        // buffer.append("ALARM TIME: " + data.getString(6) + "\n\n");
                        showMessage(buffer.toString(),status,id);
                    }
                }

                else{
                    toastMessage("NO COURSECODE ASSOCIATED");
                }
            }
        });
    }

    public void populateListView() {
      //  Log.d(TAG, "populateListView: Displaying data in the ListView.");

        //get the data and append to a list
        Cursor data = myDB.getData();
    //    List<String> labels = myDB.getDataListView();
     //   ListAdapter dataAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,labels);
     //   lvExamSched.setAdapter(dataAdapter);
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
        lvExamSched.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = adapterView.getItemAtPosition(i).toString();

                Cursor data = myDB.getItemID(name); //get the id associated with that name
                int itemID = -1;
                while(data.moveToNext()){
                    itemID = data.getInt(0);
                }
                if(itemID > -1){

                    data = myDB.getAllData(name);
                    while(data.moveToNext()) {
                        Intent intent = new Intent(MainActivity.this, EditSched.class);
                        intent.putExtra("ID",data.getInt(0));
                        intent.putExtra("COURSE CODE",data.getString(1));
                        intent.putExtra("COURSE NAME",data.getString(2));
                        intent.putExtra("EXAM DATE",data.getString(3));
                        intent.putExtra("EXAM TIME",data.getString(4));
                        intent.putExtra("NOTES",data.getString(5));
                        intent.putExtra("ALARM",data.getString(6));
                        startActivity(intent);
                    }
                }
                else{
                    toastMessage("NO COURSECODE ASSOCIATED");
                }

                return false;
            }
        });
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
                    data = myDB.getAllData(name);
                    if(data.moveToNext()) {
                        StringBuffer buffer = new StringBuffer();
                        String status = data.getString(7);
                        int id = data.getInt(0);

                        buffer.append("COURSE CODE: " + data.getString(1) + "\n");
                        buffer.append("SUBJECT: " + data.getString(2) + "\n");
                        buffer.append("EXAM DATE: " + data.getString(3) + "\n");
                        buffer.append("EXAM TIME: " + data.getString(4) + "\n");
                        buffer.append("NOTES: " + data.getString(5) + "\n");
                        buffer.append("STATUS: " + data.getString(7) + "\n\n");
                       // buffer.append("ALARM TIME: " + data.getString(6) + "\n\n");
                        showMessage(buffer.toString(),status,id);
                    }
                }

                else{
                    toastMessage("NO COURSECODE ASSOCIATED");
                }
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
      /*Cursor cursor = myDB.notifExam();
try{
        if (cursor.moveToFirst()) {


           /* NotificationCompat.Builder notification = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                    .setContentTitle("YOU HAVE EXAM TODAY!")
                    .setContentText("God Bless to your Exam! Do your best!");

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, notification.build());

        }

        }catch (Exception e){
    e.printStackTrace();
}*/






}
