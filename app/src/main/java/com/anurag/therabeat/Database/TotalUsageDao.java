package com.anurag.therabeat.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TotalUsageDao {

    @Query("SELECT * FROM TOTALUSAGE ORDER BY DATE DESC LIMIT 7")
    List<TotalUsage> getTotalUsage();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTotalUsage(TotalUsage person);

    @Update
    void updateTotalUsage(TotalUsage person);

    @Delete
    void delete(TotalUsage person);

    @Query("SELECT * FROM TOTALUSAGE WHERE date = :name")
    TotalUsage getTotalUsageByDate(String name);
}
