# BoxLauncher
使用 JSON 文件配置的Android应用启动器。<br>
每次换手机，都要重新整理桌面，不同厂商桌面操作还有差异。应用装的多了，在很多个Tab间切换寻找也很麻烦。所以写了这个小启动器。<br>
不需要替换系统的Launcher（本身也没有配置成Launcher应用），只需要把图标放到桌面下边Dock中即可。<br><br>

写成 JSON 配置一个是很简单，还有更换手机时直接把旧文件拷贝过来就可以。

## 配置文件
在 **appDir** 目录下进行配置。通过文件夹进行分类，文件夹下创建 **info.json** 配置文件。文件夹列表可以用空值指定空位。<br>
配置完成后将 **appDir** 目录拷贝到手机应用缓存 ``xxx/Android/data/ricky.boxlauncher/files`` 路径下。
```json
{
  "dirList": ["应用市场","网购","工具","出行","影音","阅读","学习","游戏","","社区","聊天","浏览器"],
  "appList": [
    {
      "appName": "相机",
      "appPackage": "com.huawei.camera"
    },
    {
      "appName": "图库",
      "appPackage": "com.android.gallery3d"
    },
    {
      "appName": "文件管理",
      "appPackage": "com.huawei.hidisk"
    },
    {
      "appName": "设置",
      "appPackage": "com.android.settings"
    }
  ]
}
```
