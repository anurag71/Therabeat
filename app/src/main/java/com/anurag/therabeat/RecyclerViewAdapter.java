package com.anurag.therabeat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    private OnNoteListener onNoteListener;
    public ArrayList<Playlist> myValues;
    public RecyclerViewAdapter (ArrayList<Playlist> myValues,OnNoteListener onNoteListener){
        this.myValues= myValues;
        this.onNoteListener = onNoteListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        return new MyViewHolder(listItem,this.onNoteListener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.myTextView.setText(myValues.get(position).getName());
    }

    public interface OnNoteListener{
        void onNoteClick(int position);
    }


    @Override
    public int getItemCount() {
        return myValues.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView myTextView;
        OnNoteListener onNoteListener;
        public MyViewHolder(View itemView,OnNoteListener onNoteListener) {
            super(itemView);
            myTextView = (TextView)itemView.findViewById(R.id.text_cardview);
            itemView.setOnClickListener(this);
            this.onNoteListener = onNoteListener;
        }

        @Override
        public void onClick(View v) {
            this.onNoteListener.onNoteClick(getAdapterPosition());
        }


    }
}