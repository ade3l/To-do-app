package com.example.todoapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class adapter extends RecyclerView.Adapter<adapter.myViewHolder> {
    List<String> titles, descriptions;
    Context context;
    List<Integer> task_number;

    List<String> titles_array =new ArrayList<>();
    List<String> descriptions_array =new ArrayList<>();
    List<Integer> indexes_array = new ArrayList<Integer>();
    SharedPreferences pref;
    RecyclerView recycler;
    Activity activity;

    public adapter(Context ct, List<String> s1, List<String> s2, List<Integer> index){
        titles=s1;
        descriptions=s2;
        context=ct;
        task_number=index;
    }
    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.row,parent,false);

        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.title_text.setText(titles.get(position));
        holder.desc_text.setText(descriptions.get(position));

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(context)
                        .setTitle("Delete task")
                        .setMessage("This action is irreversible. Are you sure you want to delete the task")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ScrollingActivity.tasks.execSQL("DELETE FROM tasks WHERE task_num="+task_number.get(position)+"");
                                ScrollingActivity.setList();
                            }
                        })
                        .setNegativeButton("No",null)
                        .show();
            }
        });
//SELECT DESCRIPTION FROM TASKS WHERE TASK_==
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor c=ScrollingActivity.tasks.rawQuery("SELECT task, description FROM tasks WHERE task_num="+task_number.get(position)+"",null);
                int description_index=c.getColumnIndex("description");
                int title_index=c.getColumnIndex("task");
                final String[] description = {null};
                final String[] task = {null};
                c.moveToFirst();
                try {
                    while (c != null) {
                        description[0] = c.getString(description_index);
                        task[0] =c.getString(title_index);
                        c.moveToNext();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Edit Task");
                View viewInflated = LayoutInflater.from(context).inflate(R.layout.task_input,ScrollingActivity.vg , false);
                final EditText title_textView = (EditText) viewInflated.findViewById(R.id.title);
                final EditText description_textView = (EditText) viewInflated.findViewById(R.id.description);
                title_textView.setText(task[0]);
                description_textView.setText(description[0]);
                builder.setView(viewInflated);

                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        description[0] =description_textView.getText().toString();
                        task[0] =title_textView.getText().toString();
                        ScrollingActivity.tasks.execSQL("UPDATE tasks SET task='"+task[0]+"', description='"+description[0]+"' WHERE task_num="+task_number.get(position)+" ");
                        ScrollingActivity.setList();
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
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder{
        TextView title_text,desc_text;
        Button deleteButton ;
        ConstraintLayout layout;
        FloatingActionButton editButton;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            title_text=itemView.findViewById(R.id.title_text);
            desc_text=itemView.findViewById(R.id.desc_text);
            deleteButton=itemView.findViewById(R.id.delete);
            editButton=itemView.findViewById(R.id.edit);
            layout=itemView.findViewById(R.id.row_layout);
        }
    }
}
