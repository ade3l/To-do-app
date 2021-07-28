package com.example.todoapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    List<Task> tasks = new ArrayList<>();
    Context context;
    HashMap<String,Task> taskMap;
    List<String> keys;
    public MyAdapter(Context ct, List<Task> tasks, HashMap<String,Task> taskMap){
        this.context = ct;
        this.tasks = tasks;
        this.taskMap=taskMap;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        //Inflate the row xml file
        View view = inflater.inflate(R.layout.row,parent,false);

        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String key=keys.get(position);
        Task task=taskMap.get(key);
        holder.title_text.setText(task.getTitle());
        //if the description is empty the it'll be hidden
        if (task.getDesc().length() != 0) {
            holder.desc_text.setVisibility(View.VISIBLE);
            holder.desc_text.setText(task.getDesc());
        }
        //if there isn't a due date then it'll be hidden
        if (task.getDate().length() != 0 ) {
            holder.dueDate_text.setText("Due Date: " +task.getDate());
        }
    }

    @Override
    public int getItemCount() {
        Collection<String> kc= taskMap.keySet();
        keys= new ArrayList<>(kc);
        return taskMap.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title_text,desc_text,dueDate_text;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title_text=itemView.findViewById(R.id.title_text);
            desc_text=itemView.findViewById(R.id.desc_text);
            dueDate_text=itemView.findViewById(R.id.dueDate);

//            myImage=itemView.findViewById(R.id.logo);


        }
    }
}
