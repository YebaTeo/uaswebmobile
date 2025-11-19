package com.example.uaswebmobile.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;
import com.example.uaswebmobile.entity.Bookmark;
import java.util.List;

@Dao
public interface BookmarkDao {
    @Insert
    long insertBookmark(Bookmark bookmark);
    
    @Delete
    void deleteBookmark(Bookmark bookmark);
    
    @Query("SELECT * FROM bookmarks WHERE jobSeekerId = :jobSeekerId")
    List<Bookmark> getBookmarksByJobSeeker(int jobSeekerId);
    
    @Query("SELECT * FROM bookmarks WHERE jobId = :jobId AND jobSeekerId = :jobSeekerId")
    Bookmark getBookmarkByJobAndSeeker(int jobId, int jobSeekerId);
    
    @Query("SELECT jobs.* FROM jobs INNER JOIN bookmarks ON jobs.id = bookmarks.jobId WHERE bookmarks.jobSeekerId = :jobSeekerId")
    List<com.example.uaswebmobile.entity.Job> getBookmarkedJobs(int jobSeekerId);
}

