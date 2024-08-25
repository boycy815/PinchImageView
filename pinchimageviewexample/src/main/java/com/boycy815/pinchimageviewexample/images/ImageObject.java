package com.boycy815.pinchimageviewexample.images;

import java.io.Serializable;

/**
 * 图片信息结构体
 *
 * @author clifford
 */
public class ImageObject implements Serializable {
    public int width;
    public int height;
    public String url;

    public ImageObject(String u, int w, int h) {
        this.width = w;
        this.height = h;
        this.url = u;
    }
}