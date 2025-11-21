package com.example.uaswebmobile.util;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.example.uaswebmobile.R;

public class NotificationHelper {
    
    public static void showSuccess(Activity activity, String title, String message) {
        showDialog(activity, title, message, R.drawable.ic_success, R.color.success);
    }
    
    public static void showError(Activity activity, String title, String message) {
        showDialog(activity, title, message, R.drawable.ic_error, R.color.error);
    }
    
    public static void showWarning(Activity activity, String title, String message) {
        showDialog(activity, title, message, R.drawable.ic_warning, R.color.warning);
    }
    
    public static void showInfo(Activity activity, String title, String message) {
        showDialog(activity, title, message, R.drawable.ic_info, R.color.info);
    }
    
    public static void showConfirm(Activity activity, String title, String message, 
                                   Runnable onConfirm) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_confirm, null);
        
        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        TextView tvMessage = dialogView.findViewById(R.id.tvMessage);
        Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        ImageView ivIcon = dialogView.findViewById(R.id.ivIcon);
        
        tvTitle.setText(title);
        tvMessage.setText(message);
        ivIcon.setImageResource(R.drawable.ic_warning);
        ivIcon.setColorFilter(activity.getResources().getColor(R.color.warning));
        
        AlertDialog dialog = builder.create();
        dialog.setView(dialogView);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        
        btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            if (onConfirm != null) onConfirm.run();
        });
        
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
    }
    
    private static void showDialog(Activity activity, String title, String message, 
                                  int iconRes, int colorRes) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_notification, null);
        
        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        TextView tvMessage = dialogView.findViewById(R.id.tvMessage);
        Button btnOk = dialogView.findViewById(R.id.btnOk);
        ImageView ivIcon = dialogView.findViewById(R.id.ivIcon);
        
        tvTitle.setText(title);
        tvMessage.setText(message);
        ivIcon.setImageResource(iconRes);
        ivIcon.setColorFilter(activity.getResources().getColor(colorRes));
        
        AlertDialog dialog = builder.create();
        dialog.setView(dialogView);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        
        btnOk.setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
    }
}

