package ricky.boxlauncher.entity;

import android.content.pm.PackageInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ricky.boxlauncher.ui.LauncherContract;

public class AppDir implements LauncherContract.Item {
    public File self = null;
    public DirInfoFile dirInfo = null;

    // For Adapter
    public final List<LauncherContract.Item> itemList = new ArrayList<>();
    public final List<LauncherContract.Item> dirList = new ArrayList<>();
    public final List<LauncherContract.Item> appList = new ArrayList<>();

    public static class DirInfoFile {
        public List<String> dirList;
        public List<AppInfo> appList;
    }

    public static class AppInfo implements LauncherContract.Item {
        public String appName;
        public String appPackage;
        public PackageInfo packageInfo;
    }
}
