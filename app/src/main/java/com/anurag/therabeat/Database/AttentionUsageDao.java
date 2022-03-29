package com.anurag.therabeat.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AttentionUsageDao {

    @Query("SELECT * FROM ATTENTIONUSAGE ORDER BY DATE DESC LIMIT 7")
    List<AttentionUsage> getAttentionUsage();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAttentionUsage(AttentionUsage attentionUsage);

    @Update
    void updateAttentionUsage(AttentionUsage attentionUsage);

    @Delete
    void delete(AttentionUsage attentionUsage);

    @Query("SELECT * FROM ATTENTIONUSAGE WHERE date = :name")
    AttentionUsage getAttentionUsageByDate(String name);
}
