# PinchImageView

这是一个手势体验极佳且使用简单的ImageView控件，实现了手势放大缩小，平移等功能。PinchImageView继承自ImageView，仅一个类文件，无依赖任何外部库，非常易于集成。

QQ交流群：1011201647

## Demo Video

![demo](demo/demo.gif)

完整Demo视频：[http://v.youku.com/v_show/id_XMTUyOTA0NzI3Ng==.html](http://v.youku.com/v_show/id_XMTUyOTA0NzI3Ng==.html)

APK下载：**[demo.apk](demo/demo.apk)**

### Demo说明

1. 手势控件与ViewPlayer结合，当图片不在当前页时加载一张缩略图，切换到当前页后更新成大图。
2. 点击缩略图切换到大图浏览使用放大动画过渡；当返回时从大图到缩略图使用动画过渡。无论大图到状态和小图的位置都保证过渡平滑。
3. 浏览超大尺寸图片时使用分片加载策略保证不OOM。

## Features

### 手势

1. 单指滑动
2. 单指滑动惯性
3. 滑动触及边界停止
4. 双击放大缩小
5. 双击放大时双击点会尽量移动到控件中心
6. 双手指手势放大缩小
7. 缩放模式下可以移动图片
8. 缩放模式下图片允许移动到边界之外
9. 缩放模式下允许图片放大缩小超过边界尺寸
10. 缩放模式下屏幕上有多于2个手指并且按照任意顺序抬起放下均不会引起错乱和不平滑
11. 缩放模式下最后一个手指抬起如果图片处于边界之外或者尺寸过大过小会使用动画回弹到最接近的正确位置
12. 回弹动画过程中允许立即通过手势进入缩放模式并且保持平滑

### 显示

1. 允许显示任何ImageView能显示的内容，使用ImageView相同的api设置显示内容
2. 允许任何情况下（包括手势进行中，动画进行中）替换显示内容（例如将低清图换成高清图），并且保持当前的缩放尺寸
3. 允许设置矩形遮罩

### 扩展性

1. 允许设置click，长按事件
2. 允许设置图片大小位置变化的事件监听
3. 允许获得图片当前变换过后的大小以及位置
4. 允许获得PinchImageView当前的手势状态
5. 允许通过api为图片执行缩放移动动画
6. 允许通过api为图片遮罩执行移动缩放动画
7. 允许通过override设置图片的最大放大尺寸
8. 允许通过override设置图片双击之后要放大或缩小到的比例

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

**2)** 接下来你可以直接把它作为ImageView取出来使用：

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
