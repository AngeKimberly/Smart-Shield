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
import java.util.function.Consumer;

public class AppManagementAdapter extends RecyclerView.Adapter<AppManagementAdapter.AppViewHolder> {
    private List<AppModel> appList;
    private Consumer<AppModel> onDeleteCallback;
    private Context context;

    public AppManagementAdapter(List<AppModel> appList, Consumer<AppModel> onDeleteCallback) {
        this.appList = appList;
        this.onDeleteCallback = onDeleteCallback;
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_app_management, parent, false);
        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        AppModel app = appList.get(position);
        holder.appName.setText(app.getAppName());
        holder.packageName.setText(app.getPackageName());
        
        // Set a default icon for now
        holder.appIcon.setImageResource(R.drawable.ic_launcher_foreground);
        
        holder.deleteButton.setOnClickListener(v -> {
            if (onDeleteCallback != null) {
                onDeleteCallback.accept(app);
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

    public List<AppModel> getApps() {
        return appList;
    }

    static class AppViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;
        TextView packageName;
        TextView deleteButton;

        AppViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.app_icon);
            appName = itemView.findViewById(R.id.app_name);
            packageName = itemView.findViewById(R.id.package_name);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}
