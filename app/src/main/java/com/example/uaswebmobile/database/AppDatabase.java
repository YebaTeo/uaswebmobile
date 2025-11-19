package com.example.uaswebmobile.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.uaswebmobile.dao.ApplicationDao;
import com.example.uaswebmobile.dao.BookmarkDao;
import com.example.uaswebmobile.dao.JobDao;
import com.example.uaswebmobile.dao.UserDao;
import com.example.uaswebmobile.entity.Application;
import com.example.uaswebmobile.entity.Bookmark;
import com.example.uaswebmobile.entity.Job;
import com.example.uaswebmobile.entity.User;

@Database(entities = {User.class, Job.class, Application.class, Bookmark.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract JobDao jobDao();
    public abstract ApplicationDao applicationDao();
    public abstract BookmarkDao bookmarkDao();
    
    private static AppDatabase INSTANCE;
    
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "job_search_database")
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

