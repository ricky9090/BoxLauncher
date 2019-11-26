package ricky.boxlauncher.task;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import ricky.boxlauncher.entity.AppDir;
import ricky.boxlauncher.ui.LauncherContract;

import static ricky.boxlauncher.common.Const.LIST_FILE_NAME;

public class ListAppTask extends AsyncTask<Integer, Integer, Integer> {

    private WeakReference<Context> mContext;

    public ListAppTask(Context context) {
        this.mContext = new WeakReference<>(context);
    }

    @Override
    protected Integer doInBackground(Integer... integers) {
        Context context = mContext.get();
        if (context == null) {
            return 0;
        }
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> allApp = context.getPackageManager().getInstalledPackages(PackageManager.GET_ACTIVITIES);
        if (allApp == null || allApp.size() == 0) {
            return null;
        }
        AppDir.DirInfoFile infoFileObj = new AppDir.DirInfoFile();
        infoFileObj.appList = new ArrayList<>();

        for (PackageInfo info : allApp) {
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(info.packageName);
            if (launchIntent != null) {
                // TODO
                AppDir.AppInfo appInfo = new AppDir.AppInfo();
                try {
                    appInfo.appPackage = info.packageName;
                    appInfo.appName = pm.getApplicationLabel(pm.getApplicationInfo(info.packageName,PackageManager.GET_META_DATA)).toString();
                } catch (Exception e) {
                    appInfo.appName = "";
                }

                infoFileObj.appList.add(appInfo);
            }
        }

        Gson gson = new Gson();
        String json2Write = gson.toJson(infoFileObj);
        try {
            File appRootDir = context.getExternalFilesDir(null);
            File targetFile = new File(appRootDir.getAbsolutePath() + "/" + LIST_FILE_NAME);
            if (targetFile.exists()) {
                targetFile.delete();

            }
            targetFile.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(targetFile);

            outputStream.write(json2Write.getBytes());
            outputStream.close();
        } catch (Exception e) {

        }

        return null;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        if (mContext.get() instanceof LauncherContract.View) {
            ((LauncherContract.View) mContext.get()).listAppFinished();
        }
        mContext = null;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        mContext = null;
    }
}
