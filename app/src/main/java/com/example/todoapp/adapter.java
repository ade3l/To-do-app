package com.example.todoapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class adapter extends RecyclerView.Adapter<adapter.myViewHolder> {
    List<String> titles, descriptions, dates;
    Context context;
    List<Integer> task_number;
    public adapter(Context ct, List<String> s1, List<String> s2, List<Integer> index, List<String> dates_array){
        titles=s1;
        descriptions=s2;
        context=ct;
        task_number=index;
        dates=dates_array;
    }
    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.row,parent,false);
        return new myViewHolder(view);
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.title_text.setText(titles.get(position));
        //if the description is empty the it'll be hidden
        if (descriptions.get(position).length() != 0) {
            holder.desc_text.setVisibility(View.VISIBLE);
            holder.desc_text.setText(descriptions.get(position));
        }
        //if there isn't a due date then it'll be hidden
        if (dates.get(position).length() != 0 ) {
            holder.dueDate_text.setText("Due Date: " + dates.get(position));
        }

        //Code to delete that task
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(context)
                        .setTitle("Delete task")
                        .setMessage("This action is irreversible. Are you sure you want to delete the task")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ScrollingActivity.tasks.execSQL("DELETE FROM tasks WHERE task_num=" + task_number.get(position) + "");
                                ScrollingActivity.setList();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });


        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //initialisation code
                Cursor c = ScrollingActivity.tasks.rawQuery("SELECT task, description ,date FROM tasks WHERE task_num=" + task_number.get(position) + "", null);
                int description_index = c.getColumnIndex("description");
                int title_index = c.getColumnIndex("task");
                int date_index = c.getColumnIndex("date");
                final String[] description = {null},task = {null};
                final String[] date = {null};
                c.moveToFirst();
                try {
                    while (c != null) {
                        description[0] = c.getString(description_index);
                        task[0] = c.getString(title_index);
                        date[0] = c.getString(date_index);
                        c.moveToNext();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Edit Task");
                View viewInflated = LayoutInflater.from(context).inflate(R.layout.task_input, ScrollingActivity.vg, false);

                final EditText title_textView = (EditText) viewInflated.findViewById(R.id.title);
                final EditText description_textView = (EditText) viewInflated.findViewById(R.id.description);
                TextView date_textView = (TextView) viewInflated.findViewById(R.id.due_date);

                title_textView.setText(task[0]);
                description_textView.setText(description[0]);

                if(date[0].length() == 0){
                    date[0] ="No date set";
                }
                date_textView.setText(date[0]);

                builder.setView(viewInflated);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { dialog.cancel(); }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(title_textView.getText().toString().length()!=0) {
                            dialog.dismiss();
                            description[0] = description_textView.getText().toString();
                            task[0] = title_textView.getText().toString();
                            date[0] =date_textView.getText().toString();
                            if(date[0].equals("No date set")) { date[0]=""; }
                            ScrollingActivity.tasks.execSQL("UPDATE tasks SET task='" + task[0] + "', description='" + description[0] + "',date='"+date[0]+"'  WHERE task_num=" + task_number.get(position) + " ");
                            ScrollingActivity.setList();
                        }
                        else{
                            title_textView.setError(context.getString(R.string.title_blank));
                        }
                    }
                });
                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar c=Calendar.getInstance();
                        c.set(Calendar.YEAR,year); c.set(Calendar.MONTH,monthOfYear); c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
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
                        DatePickerDialog datePickerDialog = new DatePickerDialog(context, dateSetListener, this_year,this_month , this_day);
                        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                        datePickerDialog.show();
                        Log.i("mine","calendar opened");
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder{
        TextView title_text,desc_text,dueDate_text;
        ConstraintLayout layout;
        FloatingActionButton editButton,deleteButton;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            title_text=itemView.findViewById(R.id.title_text);
            desc_text=itemView.findViewById(R.id.desc_text);
            deleteButton=itemView.findViewById(R.id.delete);
            editButton=itemView.findViewById(R.id.edit);
            layout=itemView.findViewById(R.id.row_layout);
            dueDate_text=itemView.findViewById(R.id.dueDate);
        }
    }
}
