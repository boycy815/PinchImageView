package com.boycy815.pinchimageviewexample.images;

import java.io.Serializable;

/**
 * 图片数据源
 *
 * @author clifford
 */
public interface ImageSource extends Serializable {

    /**
     * 根据希望的宽高获取缩略图
     *
     * @param width 希望的宽
     * @param height 希望的高
     * @return 图片缩略图对象
     */
    ImageObject getThumb(int width, int height);

    /**
     * 获取原图
     */
    ImageObject getOrigin();
}