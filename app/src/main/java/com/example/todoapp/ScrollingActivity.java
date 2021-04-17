package com.example.todoapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ScrollingActivity extends AppCompatActivity {
    SQLiteDatabase tasks;
    List<String> titles=new ArrayList<>();
    List<String> descriptions=new ArrayList<>();
    RecyclerView recycler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());

        tasks=this.openOrCreateDatabase("tasks data",MODE_PRIVATE,null);
        tasks.execSQL("CREATE TABLE IF NOT EXISTS tasks(task_num INT(4), task VARCHAR , description VARCHAR)");
        SharedPreferences pref=this.getSharedPreferences("com.example.todoapp", Context.MODE_PRIVATE);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(ScrollingActivity.this);
                builder.setTitle("New Task");
                View viewInflated = LayoutInflater.from(ScrollingActivity.this).inflate(R.layout.task_input,(ViewGroup) findViewById(android.R.id.content) , false);
                final EditText title_textView = (EditText) viewInflated.findViewById(R.id.title);
                final EditText description_textView = (EditText) viewInflated.findViewById(R.id.description);

                builder.setView(viewInflated);

                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String title = title_textView.getText().toString();
                        String description =description_textView.getText().toString();
                        int task_num=pref.getInt("index",1);
                        if (title.length()!=0){
                            tasks.execSQL("INSERT INTO tasks(task_num, task,description)  Values('"+task_num+"','"+title+"', '"+description+"')");
                            task_num+=1;
                            pref.edit().putInt("index", task_num).apply();
                            Toast.makeText(ScrollingActivity.this, "Task added successfully", Toast.LENGTH_SHORT).show();

                        }
                        else{
                            Toast.makeText(ScrollingActivity.this, "Title cannot be blank", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
        Cursor c=tasks.rawQuery("SELECT * FROM tasks", null);

        int desc_index=c.getColumnIndex("description");
        int title_index=c.getColumnIndex("task");

        Log.i("mine","hello");
        c.moveToFirst();
        try {
            while (c != null) {
                titles.add(c.getString(title_index));
                descriptions.add( c.getString(desc_index));
                c.moveToNext();
                Log.i("mine", "task: " + descriptions.toString());
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        recycler=findViewById(R.id.recycler);
        adapter my_adapter=new adapter(this, titles, descriptions);
        recycler.setAdapter(my_adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}