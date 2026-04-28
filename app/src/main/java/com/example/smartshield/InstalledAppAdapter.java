package com.example.smartshield;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class InstalledAppAdapter extends RecyclerView.Adapter<InstalledAppAdapter.AppViewHolder> {
    private List<AppModel> appList;
    private List<String> whitelistedPackages;
    private OnWhitelistToggleListener toggleListener;
    private Context context;

    public interface OnWhitelistToggleListener {
        void onWhitelistToggle(String packageName, boolean isWhitelisted);
    }

    public InstalledAppAdapter(Context context, List<AppModel> appList, 
                              List<String> whitelistedPackages) {
        this.context = context;
        this.appList = appList;
        this.whitelistedPackages = whitelistedPackages;
    }

    public void setOnWhitelistToggleListener(OnWhitelistToggleListener listener) {
        this.toggleListener = listener;
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_installed_app, parent, false);
        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        AppModel app = appList.get(position);
        holder.appName.setText(app.getAppName());
        
        // Set a default icon for now
        holder.appIcon.setImageResource(R.drawable.ic_launcher_foreground);
        
        // Set toggle state
        boolean isWhitelisted = whitelistedPackages.contains(app.getPackageName());
        holder.whitelistToggle.setChecked(isWhitelisted);
        
        holder.whitelistToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (toggleListener != null) {
                toggleListener.onWhitelistToggle(app.getPackageName(), isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    static class AppViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;
        Switch whitelistToggle;

        AppViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.app_icon);
            appName = itemView.findViewById(R.id.app_name);
            whitelistToggle = itemView.findViewById(R.id.whitelist_toggle);
        }
    }
}
