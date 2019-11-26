package ricky.boxlauncher.task;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import ricky.boxlauncher.common.FileUtils;
import ricky.boxlauncher.common.StringUtils;
import ricky.boxlauncher.entity.AppDir;
import ricky.boxlauncher.ui.LauncherContract;

import static ricky.boxlauncher.common.Const.DIR_INFO_FILE;

public class ShowDirTask extends AsyncTask<File, Integer, AppDir> {

    private WeakReference<Context> mContext;

    public ShowDirTask(Context mContext) {
        this.mContext = new WeakReference<>(mContext);
    }

    @Override
    protected AppDir doInBackground(File... files) {
        if (files == null) {
            return null;
        }

        File targetDir = files[0];

        if (!targetDir.exists() || !targetDir.isDirectory()) {
            return null;
        }
        final String currentDir = targetDir.getAbsolutePath();

        AppDir result = new AppDir();
        result.self = targetDir;
        result.dirInfo = null;

        String jsonStr = FileUtils.getJson(new File(currentDir + "/" + DIR_INFO_FILE));
        Gson gson = new Gson();
        AppDir.DirInfoFile dirInfoFile = gson.fromJson(jsonStr, AppDir.DirInfoFile.class);

        if (dirInfoFile == null) {
            return result;
        }
        result.dirInfo = dirInfoFile;

        List<AppDir> dirList = new ArrayList<>();

        if (dirInfoFile.dirList != null) {
            for (String dirStr : dirInfoFile.dirList) {
                AppDir dirToAdd = new AppDir();
                if (StringUtils.isEmpty(dirStr)) {
                    dirToAdd.self = null;
                    dirToAdd.dirInfo = null;
                } else {
                    String subDir = currentDir + "/" + dirStr;
                    File subDirFile = new File(subDir);
                    dirToAdd.self = subDirFile;
                }
                dirList.add(dirToAdd);
            }
        }

        result.dirList.addAll(dirList);
        if (dirInfoFile.appList != null) {
            getApplicationInfo(dirInfoFile.appList);
            result.appList.addAll(dirInfoFile.appList);
        }
        return result;
    }

    private void getApplicationInfo(List<AppDir.AppInfo> appList) {
        Context context = mContext.get();
        if (context == null) {
            return;
        }
        for (AppDir.AppInfo info : appList) {
            try {
                PackageInfo pInfo = context.getPackageManager().getPackageInfo(info.appPackage, PackageManager.GET_ACTIVITIES);
                info.packageInfo = pInfo;
            } catch (Exception e) {
                info.packageInfo = null;
            }
        }
    }

    @Override
    protected void onPostExecute(AppDir appDir) {
        if (mContext.get() instanceof LauncherContract.View) {
            ((LauncherContract.View) mContext.get()).showDir(appDir);
            ((LauncherContract.View) mContext.get()).hideLoading();
        }
        mContext = null;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (mContext.get() instanceof LauncherContract.View) {
            ((LauncherContract.View) mContext.get()).hideLoading();
        }
        mContext = null;
    }
}
