package com.anurag.therabeat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.anurag.therabeat.connectors.SongListDiffCallback;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    private OnNoteListener onNoteListener;
    public ArrayList<Song> myValues = new ArrayList<>();

    public RecyclerViewAdapter(ArrayList<Song> myValues, OnNoteListener onNoteListener) {
        this.myValues.addAll(myValues);
        this.onNoteListener = onNoteListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        return new MyViewHolder(listItem, this.onNoteListener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.myTextView.setText(myValues.get(position).getName());
        holder.buttonViewOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //creating a popup menu
                PopupMenu popup = new PopupMenu(view.getContext(), holder.buttonViewOption);
                //inflating menu from xml resource
                popup.inflate(R.menu.recycler_view_options_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.addToPlayistOption:
                                //handle menu1 click
                                Log.d("RecyclerView", "Add to playlist clicked");
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();

            }
        });
    }

    public interface OnNoteListener {
        void onNoteClick(int position);
    }


    @Override
    public int getItemCount() {
        return myValues.size();
    }

    public void updateEmployeeListItems(List<Song> employees) {
        final SongListDiffCallback diffCallback = new SongListDiffCallback(this.myValues, employees);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        this.myValues.clear();
        this.myValues.addAll(employees);
        diffResult.dispatchUpdatesTo(this);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView myTextView;
        private TextView buttonViewOption;
        OnNoteListener onNoteListener;

        public MyViewHolder(View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            myTextView = (TextView) itemView.findViewById(R.id.text_cardview);
            buttonViewOption = itemView.findViewById(R.id.textViewOptions);
            itemView.setOnClickListener(this);
            this.onNoteListener = onNoteListener;
        }

        @Override
        public void onClick(View v) {
            this.onNoteListener.onNoteClick(getAdapterPosition());
        }


    }
}