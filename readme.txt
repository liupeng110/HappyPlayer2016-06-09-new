重要信息：该版本不再维护，如需要可移步到新版本：https://github.com/zhangliangming/HappyPlayer5.git




2015-08-12
 ## 新创建了一个项目
 ## 移植了之前项目的启动界面/主界面/皮肤界面/扫描歌曲界面/乐乐印象界面
 ## 将swipeBackLayout添加进项目的依赖中
 ## 在主题中设置<item name="android:windowIsTranslucent">true</item>
 ## 在主题中设置 <item name="android:windowNoTitle">true</item>
 ## 修改Util.convertActivityToTranslucent 使得能适配4.0 - 5.0+所有的设备
 ## 将slidingmenu_library添加进项目的依赖中
 ## 解决viewpager与slidingmenu的冲突
 ## slidingmenu 与 swipeBackLayout 添加右滑动添加遮罩层
 ## 另外，mSwipeBackLayout.setEdgeSize(100);这个滑动删除的效果只能从边界滑动才有效果，
 ## 如果要扩大touch的范围，可以调用但是上面这个方法不太好用，效果不明显，
 ## 推荐修改ViewDragHelper.java这个类源码中的getEdgeTouched(int x, int y)方法
 
 2015-08-13
 ## 修改了getEdgeTouched方法后，发现它与 listview的下拉功能冲突了，所以再次切换回之前的方法
 ## 修改了设置页面的样式
 ## 添加了功能介绍界面和内容【该页面主要是以webview的形式呈现】
 
 2015-08-14 
 ## 找到最新的框架，创建新的工具项目
 ## 引入SlidingMenu项目依赖，项目路径为:https://github.com/jfeinstein10/SlidingMenu
 ## 引入新的Swipebacklayout项目依赖，项目路径为 :https://github.com/teze/swipebacklayout
 ## 引入RoundedImageView项目依赖，项目路径为:http://www.cnblogs.com/tianzhijiexian/p/3856391.html
 ## 简化了主页面的弹出菜单，删除了不必要的菜单
 ## 重新修改了皮肤界面，关于该页面的界面还要重新好好设计
 ## 修改了关于页面的内容，把邮箱和Qq号去除了
 ## 设置页面添加了wifi联网、桌面歌词、锁屏歌词等菜单
 ## 删除主页面的 我的歌单、最近播放 两个菜单
 ## 关于界面添加一个新版本检测功能，具体思路暂时没有实现
 
 2015-08-16
 ## 修改了皮肤的界面和item的样式
 ## 修改了乐乐印象的item样式
 ## 完善了主界面的侧菜单，为侧菜单添加了背景图片
 ## 修复皮肤切换时不流畅问题
 
 2015-08-17
 ## 修复主页面的弹出菜单快速点击时，出现多个activity的情况
 ## 功能介绍添加了几项
 ## 添加了主界面弹出菜单的播放模式功能
 ## 完成了设置的设置选项功能
 
 2015-08-18
 ## 添加了判断本地内存卡和外置内存卡的路径判断
 ## 添加了手机全盘扫描mp3歌曲功能
 ## 将本地皮肤的gridView修改为RecyclerView
 
 2015-08-20
 ## 修改了开机启动页面和默认的皮肤主页面
 ## 修改皮肤的界面布局，将导航条放置到标题栏
 ## 设置界面添加多一个 辅助操控设置item菜单
 
 2015-08-22
 ## ImageUtil类修改为单任务下载和添加LruCache缓存
 ## 添加了 Android-Universal-Image-Loader开源框架 
 ## 开源项目地址 https://github.com/nostra13/Android-Universal-Image-Loader
 ## 开源项目讲解参考 http://blog.csdn.net/vipzjyno1/article/details/23206387
 ## 对于网络图片和lisview等加载多个图片时，统一使用Android-Universal-Image-Loader的 ImageLoadUtil 来实现
 ## 对于加载本地图片，则使用ImageUtil来实现
 
 2015-08-24
 ## 添加了版本更新请求
 
 2015-08-25
 ## 添加了下载功能，具体还没有实现下载。
 ## 添加了加载弹出窗口
 
 2015-08-27
 ## 添加了两个按钮的弹出窗口，和一个按钮的弹出窗口
 ## 初步完成了apk包的下载功能
 ## 添加了版本更新下载功能，当然有bug,但是应该不影响使用
 
 2015-09-03
 ## 将版本更新修改为通知栏显示进度条，并将下载app的线程任务交给mainactivity来处理。
 ## 将下载皮肤的进度条，由矩形进度条修改为扇形进度条
 
 2015-09-04
 ## 添加了皮肤的下载和不同皮肤的加载
 
 2015-09-05
 ## 修复搜索窗口和歌词窗口的背景图片加载慢的问题
 ## 将歌词窗口修改为类似酷狗的歌词窗口
 
 2015-09-06
 ## 添加了类似酷狗的歌词窗口界面
 ## https://github.com/zhaozhentao/KugouLayout
 ## 并对KugouLayout做了一定的修改
 ## KugouLayout的窗口打开时，并没有旋转，不知怎样弄。。。

  2015-10-11 -  2015-10-15 
 ## 完成了本地歌曲列表的显示和推荐歌曲列表的显示【待完善】
 
   2015-10-16 -  2015-10-25 
 ## 本地歌曲列表，添加喜欢歌曲和删除歌曲
 ## 我的喜欢歌曲列表，添加喜欢歌曲和删除歌曲
 ## 歌曲列表添加列表索引
 2015-10-26 - 2015-11-10
 ## 添加了修改状态栏颜色
 ## 添加了歌手头像、歌手写真等
 
 2015-11-10 - 2015-11-17
 ## 添加多行歌词。歌词界面的左右滑动与歌词视图的冲突了，暂时没有想到方法解决
 ## 去掉歌词界面的酷狗界面框架，使用左滑关闭界面框架，解决与歌词视图冲突问题。
 ## 添加通知栏播放界面、歌词设置菜单
 
 2015-11-18 - 2015-11-20
 ## 添加了桌面歌词
 ## 歌曲的添加删除功能
 ## 添加了上一首，下一首歌曲功能，删除歌曲功能
 ## 添加了播放模式功能判断
 ## 添加了桌面歌词锁定功能  针对小米系统的悬浮窗口，弹出设置权限窗口
 ## 耳机拔出监听
 ## 短信来电监听
 ## 锁屏界面
 注意:
 ## 线控监听 在写线控代码时，如果多次没法监听 耳机的点击事件，请将应用先删除，再重新安装。
 
 使用方式
 
 资源下载路径：http://pan.baidu.com/s/1eQz4S0q
   下载后，解压到 手机的储存卡里面，再运行应用，扫描歌曲，找到对应的歌词，播放，就可以看到效果了。
 
 具体简介：
 http://www.eoeandroid.com/forum.php?mod=viewthread&tid=917012&extra=page%3D1&_dsign=576de398
 
 ##优化了皮肤下载、APP更新(多线程下载)
 ##添加了在线歌曲播放
