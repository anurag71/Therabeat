package com.anurag.therabeat.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MemoryUsageDao {

    @Query("SELECT * FROM MEMORYUSAGE ORDER BY DATE DESC LIMIT 7")
    List<MemoryUsage> getMemoryUsage();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMemoryUsage(MemoryUsage memoryUsage);

    @Update
    void updateMemoryUsage(MemoryUsage memoryUsage);

    @Delete
    void delete(MemoryUsage memoryUsage);

    @Query("SELECT * FROM MEMORYUSAGE WHERE date = :name")
    MemoryUsage getMemoryUsageByDate(String name);
}
