package com.boycy815.pinchimageviewexample.images;

import java.io.Serializable;

/**
 * 图片数据源
 *
 * @author clifford
 */
public interface ImageSource extends Serializable {

    /**
     * 获取缩略图
     */
    ImageObject getThumb();

    /**
     * 获取原图
     */
    ImageObject getOrigin();
}