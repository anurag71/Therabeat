package com.anurag.therabeat.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "totalusage")
public class TotalUsage {
    @PrimaryKey
    @NonNull
    String date;
    Integer timeUsed;


    public TotalUsage(String date, Integer timeUsed) {
        this.date = date;
        this.timeUsed = timeUsed;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getTimeUsed() {
        return timeUsed;
    }

    public void setTimeUsed(Integer timeUsed) {
        this.timeUsed = timeUsed;
    }
}