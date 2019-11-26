package ricky.boxlauncher.ui

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import ricky.boxlauncher.R
import ricky.boxlauncher.entity.AppDir
import java.io.File

class MainActivity : AppCompatActivity(), LauncherContract.View,
    LauncherContract.ItemClickListener {

    var iconCache: IconCache? = null
    var appAdapter: AppItemAdapter? = null
    var presenter: LauncherContract.Presenter? = null
    var loading: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // https://stackoverflow.com/questions/1362723/how-can-i-get-a-dialog-style-activity-window-to-fill-the-screen
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window.attributes?.let {
            it.gravity = Gravity.BOTTOM
        }

        listApp_btn.setOnClickListener {
            listApp_btn.isEnabled = false
            presenter?.listApp()
        }
        // 隐藏获取应用列表按钮
        listApp_btn.visibility = View.GONE

        iconCache = IconCache(this)
        presenter = LauncherPresenter(this)

        appAdapter = AppItemAdapter(this)
        appAdapter?.onItemClickListener = this
        main_list.layoutManager = GridLayoutManager(this, 4)
        main_list.adapter = appAdapter
    }

    override fun onResume() {
        super.onResume()
        loadPackage()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.onDestroy()
        presenter = null
    }

    override fun onBackPressed() {
        if (loading) {
            return
        }
        var canBoBack = presenter?.canGoBack()
        canBoBack?.let {
            if (it) {
                presenter?.goBack()
            } else {
                super.onBackPressed()
            }
            return
        }
        super.onBackPressed()
    }

    private fun loadPackage() {
        if (loading) {
            return
        }
        var rootDir = "${getExternalFilesDir(null)?.absolutePath}/appDir"
        var rootDirFile = File(rootDir)
        if (!rootDirFile.exists()) {
            rootDirFile.mkdir()
        }
        presenter?.loadDir(File(rootDir))
    }

    override fun listAppFinished() {
        Toast.makeText(this, "获取应用列表成功！", Toast.LENGTH_SHORT).show()
        listApp_btn.isEnabled = true
    }

    override fun showDir(appDir: AppDir?) {
        appAdapter?.currentDir = appDir
        appAdapter?.notifyDataSetChanged()
    }

    override fun onDirClick(item: AppDir?) {
        item?.let { dir ->
            if (dir.self != null) {
                presenter?.loadDir(dir.self)
            }
        }
    }

    override fun onAppClick(item: AppDir.AppInfo?) {
        item?.let { appInfo ->
            var launchIntent =
                this@MainActivity.packageManager.getLaunchIntentForPackage(appInfo.appPackage)
            if (launchIntent != null) {
                startActivity(launchIntent)
                finish()
            } else {

            }
        }
    }

    override fun showLoading() {
        loading = true
    }

    override fun hideLoading() {
        loading = false
    }
}
