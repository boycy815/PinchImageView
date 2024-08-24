# PinchImageView

手势体验极佳且使用简单的图片双指缩放控件。继承自ImageView，仅一个类文件，无外部库依赖，轻量，易于集成。

QQ交流群：1011201647

## Demo Video

![demo](demo/demo.gif)

完整Demo视频：[http://v.youku.com/v_show/id_XMTUyOTA0NzI3Ng==.html](http://v.youku.com/v_show/id_XMTUyOTA0NzI3Ng==.html)

APK下载：**[demo.apk](demo/demo.apk)**

### Demo说明

1. 与ViewPlayer结合，图片不在当前页时预加载缩略图，切换到当前页后切换成高清图。
2. 缩略图与大图浏览状态切换动画过渡。
3. 超大尺寸图分片加载。

## Features

### 手势

1. 单指滑动
2. 单指滑动惯性
3. 滑动触及边界停止
4. 双击放大缩小
5. 双击放大时双击点尽量移动到控件中心
6. 双指手势放大缩小，进入缩放模式
7. 缩放模式下可以移动图片
8. 缩放模式下图片允许移动到边界之外
9. 缩放模式下允许图片放大缩小超过边界尺寸
10. 退出缩放模式（所有手指抬起），回弹至最近合理位置

### 显示

1. 兼容ImageView API
2. 支持图片清晰度切换
3. 矩形遮罩

### 扩展性

1. click，长按事件
2. 图片大小位置变化监听
3. 获得当前大小以及位置
4. 获得当前手势状态
5. api执行图片缩放移动动画
6. api执行遮罩移动缩放动画
7. override设置图片最大放大尺寸
8. override设置图片双击之后要放大或缩小到的比例

## Quick start

**1)** 将PinchImageView.java复制到项目中

**2)** 在布局文件中添加如下代码，它已经能显示一张资源中的图片了：

    <RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
        <com.boycy815.pinchimageview.PinchImageView
            android:id="@+id/pic"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:src="@drawable/my_pic"/>
    </RelativeLayout>

**2)** 直接把它作为ImageView使用：

    //作为ImageView取出来
    ImageView imageView = (ImageView) findViewById(R.id.pic);
    //可以使用任何ImageView支持的方式设置图片
    imageView.setImageResource(R.drawable.my_pic);
    //or...
    imageView.setImageBitmap(bitmap);
    //or...
    imageView.setImageDrawable(drawable);
    //or 你还能使用第三方图片加载库加载图片，如ImageLoader
    imageLoader.displayImage("http://host.com/my_pic.jpg", imageView);

end
