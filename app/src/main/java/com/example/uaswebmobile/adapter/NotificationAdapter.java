package com.example.uaswebmobile.adapter;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uaswebmobile.R;
import com.example.uaswebmobile.entity.Notification;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    public interface NotificationActionListener {
        void onMarkAsRead(Notification notification);
    }

    private List<Notification> notifications = new ArrayList<>();
    private final NotificationActionListener actionListener;

    public NotificationAdapter(NotificationActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void submitList(List<Notification> items) {
        this.notifications = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        holder.bind(notifications.get(position));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivIcon;
        private final TextView tvTitle;
        private final TextView tvMessage;
        private final TextView tvTimestamp;
        private final TextView tvType;
        private final View viewUnreadIndicator;
        private final MaterialButton btnMarkRead;

        NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvType = itemView.findViewById(R.id.tvType);
            viewUnreadIndicator = itemView.findViewById(R.id.viewUnreadIndicator);
            btnMarkRead = itemView.findViewById(R.id.btnMarkRead);
        }

        void bind(Notification notification) {
            tvTitle.setText(notification.title != null ? notification.title : "Notifikasi");
            tvMessage.setText(notification.message != null ? notification.message : "");
            tvTimestamp.setText(notification.timestamp != null ? notification.timestamp : "-");

            String typeLabel = formatTypeLabel(notification.type);
            tvType.setText(typeLabel);
            applyTypeStyle(notification.type);

            viewUnreadIndicator.setVisibility(notification.isRead ? View.GONE : View.VISIBLE);
            btnMarkRead.setVisibility(notification.isRead ? View.GONE : View.VISIBLE);
            itemView.setAlpha(notification.isRead ? 0.6f : 1f);

            btnMarkRead.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onMarkAsRead(notification);
                }
            });
        }

        private String formatTypeLabel(String type) {
            if (type == null || type.isEmpty()) {
                return "Info";
            }
            return type.substring(0, 1).toUpperCase(Locale.getDefault()) +
                    type.substring(1).toLowerCase(Locale.getDefault());
        }

        private void applyTypeStyle(String type) {
            int iconRes = getIconRes(type);
            int colorRes = getColorRes(type);
            ivIcon.setImageResource(iconRes);
            ivIcon.setColorFilter(ContextCompat.getColor(itemView.getContext(), colorRes));

            if (tvType.getBackground() instanceof GradientDrawable) {
                GradientDrawable drawable = (GradientDrawable) tvType.getBackground().mutate();
                drawable.setColor(ContextCompat.getColor(itemView.getContext(), colorRes));
            }
        }

        private int getIconRes(String type) {
            if ("success".equalsIgnoreCase(type)) {
                return R.drawable.ic_success;
            } else if ("warning".equalsIgnoreCase(type)) {
                return R.drawable.ic_warning;
            } else if ("error".equalsIgnoreCase(type)) {
                return R.drawable.ic_error;
            }
            return R.drawable.ic_info;
        }

        private int getColorRes(String type) {
            if ("success".equalsIgnoreCase(type)) {
                return R.color.success;
            } else if ("warning".equalsIgnoreCase(type)) {
                return R.color.warning;
            } else if ("error".equalsIgnoreCase(type)) {
                return R.color.error;
            }
            return R.color.info;
        }
    }
}

