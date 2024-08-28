# PinchImageView

English | [简体中文](README.zh-CN.md)

A gesture-friendly and easy-to-use image pinch-zoom control. It extends `ImageView`, is contained in a single class file, has no external dependencies, and is lightweight and easy to integrate.

## Demo

![demo](demo/demo.gif)

### Demo Description

1. Integrated with ViewPlayer, preloads thumbnail images when the image is not on the current page, and switches to high-definition images when the page is in view.
2. Transition animations between thumbnail and full-size image viewing modes.
3. Loads extremely large images in segments.
4. Long image browsing mode.

## Features

### Gestures

1. Single-finger swipe
2. Single-finger swipe inertia
3. Swipe stops at the boundary
4. Double-tap to zoom in and out
5. When double-tapping to zoom in, the tap point is centered as much as possible
6. Pinch-zoom with two fingers to enter zoom mode
7. In zoom mode, the image can be moved
8. In zoom mode, the image can be moved beyond the boundaries
9. In zoom mode, the image can be zoomed in or out beyond the boundary size
10. Exit zoom mode (all fingers lifted), and the image rebounds to the nearest appropriate position

### Display

1. Compatible with ImageView API
2. Supports image resolution switching
3. Rectangular mask overlay

### Extensibility

1. Click and long-press events
2. Listeners for image size and position changes
3. Get current size and position
4. Get current gesture state
5. API to perform image zoom and move animations
6. API to perform mask move and zoom animations
7. Override to set the maximum zoom size for images
8. Override to set the scale for zooming in or out after double-tap

## Quick Start

**1)** Copy `PinchImageView.java` into your project.

**2)** Add the following code to your layout file; it can already display an image from resources:

    <RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
        <com.boycy815.pinchimageview.PinchImageView
            android:id="@+id/pic"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:src="@drawable/my_pic"/>
    </RelativeLayout>

**3)** Use it directly as an `ImageView`:

    // Retrieve it as an ImageView
    ImageView imageView = (ImageView) findViewById(R.id.pic);
    // You can set the image using any method supported by ImageView
    imageView.setImageResource(R.drawable.my_pic);
    // or...
    imageView.setImageBitmap(bitmap);
    // or...
    imageView.setImageDrawable(drawable);
    // or you can even use third-party image loading libraries, like ImageLoader
    imageLoader.displayImage("http://host.com/my_pic.jpg", imageView);
