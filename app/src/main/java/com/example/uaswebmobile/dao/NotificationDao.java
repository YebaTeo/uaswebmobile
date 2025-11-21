package com.example.uaswebmobile.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.uaswebmobile.entity.Notification;
import java.util.List;

@Dao
public interface NotificationDao {
    @Insert
    long insertNotification(Notification notification);
    
    @Update
    void updateNotification(Notification notification);
    
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY timestamp DESC")
    List<Notification> getNotificationsByUser(int userId);
    
    @Query("SELECT * FROM notifications WHERE userId = :userId AND isRead = 0 ORDER BY timestamp DESC")
    List<Notification> getUnreadNotificationsByUser(int userId);
    
    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isRead = 0")
    int getUnreadCount(int userId);
    
    @Query("UPDATE notifications SET isRead = 1 WHERE id = :notificationId")
    void markAsRead(int notificationId);
    
    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    void markAllAsRead(int userId);
}

