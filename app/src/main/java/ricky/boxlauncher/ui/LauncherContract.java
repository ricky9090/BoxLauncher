package ricky.boxlauncher.ui;

import java.io.File;

import ricky.boxlauncher.entity.AppDir;

public interface LauncherContract {

    interface View {
        void listAppFinished();

        void showDir(AppDir appDir);

        void showLoading();

        void hideLoading();
    }

    interface Presenter {
        void listApp();

        void loadDir(File dirFile);

        boolean canGoBack();

        void goBack();

        void onDestroy();
    }

    interface ItemClickListener {
        void onDirClick(AppDir item);

        void onAppClick(AppDir.AppInfo item);
    }

    interface Item {
    }
}
