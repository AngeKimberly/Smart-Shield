package com.example.smartshield;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppViewHolder> {
    private List<AppModel> appList;
    private Context context;
    private OnAppClickListener onAppClickListener;

    public interface OnAppClickListener {
        void onAppClick(AppModel app);
    }

    public AppAdapter(Context context, List<AppModel> appList) {
        this.context = context;
        this.appList = appList;
    }

    public void setOnAppClickListener(OnAppClickListener listener) {
        this.onAppClickListener = listener;
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_app, parent, false);
        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        AppModel app = appList.get(position);
        holder.appName.setText(app.getAppName());
        
        // Set a default icon for now
        holder.appIcon.setImageResource(R.drawable.ic_launcher_foreground);
        
        holder.itemView.setOnClickListener(v -> {
            if (onAppClickListener != null) {
                onAppClickListener.onAppClick(app);
            }
        });
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    public void updateApps(List<AppModel> newApps) {
        this.appList = newApps;
        notifyDataSetChanged();
    }

    static class AppViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;

        AppViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.app_icon);
            appName = itemView.findViewById(R.id.app_name);
        }
    }
}
