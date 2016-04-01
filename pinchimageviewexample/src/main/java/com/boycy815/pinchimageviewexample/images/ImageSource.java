package com.boycy815.pinchimageviewexample.images;

import android.graphics.Point;

import java.io.Serializable;

/**
 * 图片数据源
 *
 * @author clifford
 */
public interface ImageSource extends Serializable {

    /**
     * 根据希望的宽高获取图片地址
     *
     * @param width 希望的宽
     * @param height 希望的高
     * @return 可请求的图片地址
     */
    String getUrl(int width, int height);

    /**
     * 获取请求的图片大小
     *
     * @param requestWidth 请求的图片宽
     * @param requestHeight 请求的图片高
     * @return 返回的图片真实宽高
     */
    Point getSize(int requestWidth, int requestHeight);

    /**
     * 图片原始宽度
     */
    int getOriginWidth();

    /**
     * 图片原始高度
     */
    int getOriginHeight();
}