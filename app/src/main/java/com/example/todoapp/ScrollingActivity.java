package com.example.todoapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ScrollingActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    static SQLiteDatabase tasks;
    static List<String> titles_array =new ArrayList<>();
    static List<String> descriptions_array =new ArrayList<>();
    static List<Integer> indexes_array = new ArrayList<Integer>();
    static List<String> dates_array = new ArrayList<>();
    SharedPreferences pref;
    static ViewGroup vg;
    static RecyclerView recycler;

    FirebaseDatabase mDatabase;
    DatabaseReference mRef;
    private static Context context;
    public static Context getAppContext() {
        //This function is just to get the context.
        //I need to call setList() from the adapter class to refresh the recycler view
        //but getApplicationContext() and 'this' cannot be made static. And hence this function
        //the variable 'context' is assigned the context value in the on create method
        return ScrollingActivity.context;
    }
    static void setList(){
        titles_array.clear();
        descriptions_array.clear();
        indexes_array.clear();
        dates_array.clear();
        Cursor c=tasks.rawQuery("SELECT * FROM tasks", null);
        int desc_index=c.getColumnIndex("description");
        int title_index=c.getColumnIndex("task");
        int task_index=c.getColumnIndex("task_num");
        int date_index=c.getColumnIndex("date");
        c.moveToFirst();
        try {
            while (c != null) {
                titles_array.add(c.getString(title_index));
                descriptions_array.add( c.getString(desc_index));
                indexes_array.add(c.getInt(task_index));
                dates_array.add(c.getString(date_index));
                c.moveToNext();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        adapter my_adapter=new adapter(getAppContext(), titles_array, descriptions_array, indexes_array, dates_array);
        recycler.setAdapter(my_adapter);
        recycler.setLayoutManager(new LinearLayoutManager(getAppContext()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());

        pref=this.getSharedPreferences("com.example.todoapp", Context.MODE_PRIVATE);
        tasks=this.openOrCreateDatabase("tasks data",MODE_PRIVATE,null);
        tasks.execSQL("CREATE TABLE IF NOT EXISTS tasks(task_num INT(4), task VARCHAR , description VARCHAR, date VARCHAR)");
        recycler=findViewById(R.id.recyclerView);
        //Assigning the context to the variable
        ScrollingActivity.context =this;
        vg=(ViewGroup) findViewById(android.R.id.content);
        mDatabase=FirebaseDatabase.getInstance();
        mRef=mDatabase.getReference().child("Tasks");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_task);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //New task input dialog is made and inflated
                AlertDialog.Builder builder = new AlertDialog.Builder(ScrollingActivity.this);
                builder.setTitle("New Task");
                View viewInflated = LayoutInflater.from(ScrollingActivity.this).inflate(R.layout.task_input,(ViewGroup) findViewById(android.R.id.content) , false);

                final EditText title_textView = (EditText) viewInflated.findViewById(R.id.title);
                final EditText description_textView = (EditText) viewInflated.findViewById(R.id.description);
                final TextView date_textView = (TextView) viewInflated.findViewById(R.id.due_date);

                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        Log.i("mine","date set");

                        Calendar c=Calendar.getInstance();
                        c.set(Calendar.YEAR,year);
                        c.set(Calendar.MONTH,monthOfYear);
                        c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                        date_textView.setText(String.valueOf(dayOfMonth)+"/"+String.valueOf(monthOfYear)+"/"+String.valueOf(year));
                    }
                };

                //FAB button to inflate the date picker and set the due date
                FloatingActionButton calendar_button=viewInflated.findViewById(R.id.calendar);
                calendar_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar c=Calendar.getInstance();
                        int this_year=c.get(Calendar.YEAR),this_month=c.get(Calendar.MONTH),this_day=c.get(Calendar.DAY_OF_MONTH);
                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                context, dateSetListener, this_year,this_month , this_day);
                        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                        datePickerDialog.show();
                        Log.i("mine","calendar opened");

                    }
                });
                builder.setView(viewInflated);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //the onclick here is overridden and taken over by the dialog coming up next
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                //creating a dialog of this so that the positive button can be overridden
                AlertDialog dialog=builder.create();
                dialog.show();
                //overriding the ok button so that it only closes if the title is not empty
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String title = title_textView.getText().toString();
                        String description =description_textView.getText().toString();
                        String date=date_textView.getText().toString();
                        if(date.equals("No date set")){date="";}
                        int task_num=pref.getInt("index",1);
                        if (title.length()!=0){
                            dialog.dismiss();
                            tasks.execSQL("INSERT INTO tasks(task_num, task,description,date)  Values('"+task_num+"','"+title+"', '"+description+"','"+date+"')");
                            task_num+=1;
                            pref.edit().putInt("index", task_num).apply();
                            Toast.makeText(ScrollingActivity.this, "Task added successfully", Toast.LENGTH_SHORT).show();
                            Task task = new Task(title,description,date);
                            mRef.push().setValue(task);
                            setList();
                        }
                        else{
                            title_textView.setError(getString(R.string.title_blank));
                        }
                    }
                });
            }
        });
        setList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
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
            tasks.execSQL("DROP TABLE IF EXISTS tasks");
            pref=this.getSharedPreferences("com.example.todoapp", Context.MODE_PRIVATE);
            pref.edit().putInt("index", 1).apply();
            setList();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }
}