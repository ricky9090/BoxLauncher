package ricky.boxlauncher.ui

import android.content.Context
import ricky.boxlauncher.task.ListAppTask
import ricky.boxlauncher.task.ShowDirTask
import ricky.boxlauncher.common.Const
import java.io.File

class LauncherPresenter(context: Context?): LauncherContract.Presenter {

    var mContext = context
    var loadDirTask: ShowDirTask? = null
    var listAppTask: ListAppTask? = null

    var currentDir: File? = null

    override fun listApp() {
        listAppTask?.cancel(true)
        listAppTask = ListAppTask(mContext)
        listAppTask?.execute(null)
    }

    override fun loadDir(dirFile: File?) {
        (mContext as LauncherContract.View).showLoading()
        loadDirTask?.cancel(true)
        loadDirTask = ShowDirTask(mContext)
        loadDirTask?.execute(dirFile)

        currentDir = dirFile
    }

    override fun canGoBack(): Boolean {
        if (currentDir == null) {
            return false
        }
        var reachRoot = currentDir?.name?.equals(Const.ROOT_DIR_NAME)
        reachRoot?.let {
            return !it
        }
        return false
    }

    override fun goBack() {
        if (currentDir == null) {
            return
        }
        var parent = currentDir?.parentFile
        parent?.let {
            loadDir(it)
        }
    }

    override fun onDestroy() {
        loadDirTask?.cancel(true)
        listAppTask?.cancel(true)
        mContext = null
    }
}