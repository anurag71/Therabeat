package com.anurag.therabeat.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AnxietyUsageDao {

    @Query("SELECT * FROM ANXIETYUSAGE ORDER BY DATE DESC LIMIT 7")
    List<AnxietyUsage> getAnxietyUsage();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAnxietyUsage(AnxietyUsage memory);

    @Update
    void updateAnxietyUsage(AnxietyUsage memory);

    @Delete
    void delete(AnxietyUsage memory);

    @Query("SELECT * FROM ANXIETYUSAGE WHERE date = :name")
    AnxietyUsage getAnxietyUsageByDate(String name);
}
