package com.dosu.sellu.ui.custom_view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView

class SquareImageView : ImageView {
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int):
            super(context, attrs, defStyleAttr, defStyleRes)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, ): super(context, attrs)
    constructor(context: Context?): super(context)
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measuredWidth
        setMeasuredDimension(width, width)
    }
}