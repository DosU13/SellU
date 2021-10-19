package com.dosu.sellu.ui.products.util

import android.content.Context
import android.util.AttributeSet


class FitYCropXImageView: androidx.appcompat.widget.AppCompatImageView{
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if(drawable != null){
            val imageSideRatio = drawable.intrinsicWidth.toFloat()/drawable.intrinsicHeight.toFloat()
            val viewSideRatio = MeasureSpec.getSize(widthMeasureSpec).toFloat()/
                                MeasureSpec.getSize(heightMeasureSpec).toFloat()
            scaleType = if(imageSideRatio >= viewSideRatio) ScaleType.CENTER_CROP
                        else ScaleType.FIT_CENTER
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}