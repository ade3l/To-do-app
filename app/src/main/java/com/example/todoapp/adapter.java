package com.example.todoapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class adapter extends RecyclerView.Adapter<adapter.myViewHolder> {
    List<String> titles, descriptions;
    Context context;
    List<Integer> task_number;
    public adapter(Context ct, List<String> s1, List<String> s2, List<Integer> index){
        titles=s1;
        descriptions=s2;
        context=ct;
        task_number=index;
        Log.i("mine",task_number.toString());
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

                Toast.makeText(context, String.valueOf(task_number.get(position)), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder{
        TextView title_text,desc_text;
        Button deleteButton;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            title_text=itemView.findViewById(R.id.title_text);
            desc_text=itemView.findViewById(R.id.desc_text);
            deleteButton=itemView.findViewById(R.id.delete);
        }
    }
}
