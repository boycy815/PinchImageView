package com.boycy815.pinchimageviewexample;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.boycy815.pinchimageview.PinchImageView;

public class CustomPinchImageView extends PinchImageView {

    public CustomPinchImageView(Context context) {
        super(context);
    }

    public CustomPinchImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomPinchImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public Matrix getInnerMatrix(Matrix matrix) {
        if (matrix == null) {
            matrix = new Matrix();
        } else {
            matrix.reset();
        }
        if (isReady()) {
            //原图大小
            RectF tempSrc = MathUtils.rectFTake(0, 0, getDrawable().getIntrinsicWidth(), getDrawable().getIntrinsicHeight());
            //控件大小
            RectF tempDst = MathUtils.rectFTake(0, 0, getWidth(), getHeight());

            if (tempSrc.height() / tempSrc.width() > tempDst.height() / tempDst.width()) {
                //长图模式
                matrix.setRectToRect(tempSrc, tempDst, Matrix.ScaleToFit.START);
                float scale = Math.max(tempDst.width() / tempSrc.width(), tempDst.height() / tempSrc.height());
                matrix.setScale(scale, scale);
            } else {
                //普通模式
                //计算fit center矩阵
                matrix.setRectToRect(tempSrc, tempDst, Matrix.ScaleToFit.CENTER);
            }
            //释放临时对象
            MathUtils.rectFGiven(tempDst);
            MathUtils.rectFGiven(tempSrc);
        }
        return matrix;
    }
}
