package ricky.boxlauncher.ui;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ricky.boxlauncher.R;
import ricky.boxlauncher.common.Const;
import ricky.boxlauncher.entity.AppDir;

public class AppItemAdapter extends RecyclerView.Adapter<AppItemAdapter.AppItemViewHolder> {

    private AppDir currentDir;
    private Context mContext;
    private LauncherContract.ItemClickListener onItemClickListener;

    public AppItemAdapter(Context context) {
        mContext = context;
    }

    public AppDir getCurrentDir() {
        return currentDir;
    }

    public void setCurrentDir(AppDir dir) {
        if (dir == null) {
            this.currentDir = null;
            return;
        }
        this.currentDir = dir;
        currentDir.itemList.clear();
        if (currentDir.dirInfo == null) {
            return;
        }
        currentDir.itemList.addAll(currentDir.dirList);

        if (currentDir.dirList.size() != 0) {
            int emptyCount = Const.SPAN_COUNT - currentDir.dirList.size() % Const.SPAN_COUNT;
            if (emptyCount != 4) {  // 填充一些空元素，分隔文件夹与应用图标
                for (int i = 0; i < emptyCount; i++) {
                    currentDir.itemList.add(new AppDir());
                }
            }
        }

        currentDir.itemList.addAll(currentDir.appList);
    }

    public LauncherContract.ItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(LauncherContract.ItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public AppItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.layout_app_item, parent, false);
        return new AppItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AppItemViewHolder holder, int position) {
        if (currentDir == null) {
            return;
        }
        final LauncherContract.Item item = currentDir.itemList.get(position);
        try {
            if (item instanceof AppDir) {
                bindDir(holder, (AppDir) item);
            } else if (item instanceof AppDir.AppInfo) {
                bindApp(holder, (AppDir.AppInfo) item);
            }
        } catch (Exception e) {
            holder.imageView.setImageResource(R.mipmap.ic_launcher);
            holder.nameText.setText("");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener == null) {
                    return;
                }

                if (item instanceof AppDir) {
                    onItemClickListener.onDirClick((AppDir) item);
                } else if (item instanceof AppDir.AppInfo) {
                    onItemClickListener.onAppClick((AppDir.AppInfo) item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (currentDir == null) {
            return 0;
        }
        return currentDir.itemList.size();
    }

    private void bindDir(AppItemViewHolder holder, AppDir item) {
        if (item.self == null) {
            holder.imageView.setImageDrawable(null);
            holder.nameText.setText("");
            return;
        }
        holder.imageView.setImageResource(R.mipmap.icon_folder);
        holder.nameText.setText(item.self.getName());
    }

    private void bindApp(AppItemViewHolder holder, AppDir.AppInfo item) throws Exception {
        PackageInfo packageInfo = item.packageInfo;
        if (packageInfo == null) {  // 应用已卸载
            holder.imageView.setImageResource(R.mipmap.icon_downloads);
            holder.nameText.setText(item.appName);
        } else {
            try {
                Drawable icon = null;
                if (((MainActivity) mContext).getIconCache() != null) {
                    IconCache cache = ((MainActivity) mContext).getIconCache();
                    icon = cache.getIconForPackage(item.appPackage);
                }
                holder.imageView.setImageDrawable(icon);
            } catch (NullPointerException e) {
                holder.imageView.setImageDrawable(null);
            }
            holder.nameText.setText(item.appName);
        }
    }

    static class AppItemViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView nameText;

        public AppItemViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            nameText = itemView.findViewById(R.id.textView);
        }
    }
}
