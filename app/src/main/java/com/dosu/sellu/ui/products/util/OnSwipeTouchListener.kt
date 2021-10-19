package com.dosu.sellu.ui.products.util

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

open class OnSwipeTouchListener(context: Context): View.OnTouchListener {
    private val gestureDetector: GestureDetector = GestureDetector(context, GestureListener(this))

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(p1)
    }



    open fun onSwipeRight() {}

    open fun onSwipeLeft() {}

    class GestureListener(private val onSwipeTouchListener: OnSwipeTouchListener): GestureDetector.SimpleOnGestureListener() {
        companion object{
            const val SWIPE_THRESHOLD = 100
            const val SWIPE_VELOCITY_THRESHOLD = 100
        }

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            var result = false
            try {
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.y
                if(abs(diffX) > abs(diffY)){
                    if(abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD){
                        if(diffX > 0) onSwipeTouchListener.onSwipeRight()
                        else onSwipeTouchListener.onSwipeLeft()
                        result = true
                    }
                }
            }catch (e: Exception) {e.printStackTrace()}
            return result
        }
    }
}