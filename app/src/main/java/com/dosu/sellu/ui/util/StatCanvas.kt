package com.dosu.sellu.ui.util

import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Paint.DITHER_FLAG
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import com.dosu.sellu.R
import com.dosu.sellu.ui.home.model.Stat

class StatCanvas(
    context: Context,
    attrs: AttributeSet? = null)
    : View(context, attrs, 0){

    private lateinit var stats: List<Stat>

    fun setValues(stats: List<Stat>){
        this.stats = stats
        invalidate()
    }

    private val textPaint = Paint(ANTI_ALIAS_FLAG or DITHER_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.text)
        textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20f, resources.displayMetrics)
        textAlign = Paint.Align.LEFT
    }
    private val barMoneyPaint = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 2f
        color = ContextCompat.getColor(context, R.color.money)
    }
    private val barIncomePaint = Paint(barMoneyPaint).apply {
        color = ContextCompat.getColor(context, R.color.income)
    }
    private val linePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.color_primary)
        strokeWidth = 4f
    }
    private var textWidth = 0f
    private var barWidthUnit = 0f
    private var barHeight = 0f
    private var rowHeight = 0f
    private var layWidth = 0f
    private var layHeight = 0f
    private fun initValues(){
        layWidth = measuredWidth.toFloat()
        val templateText = "00/00/00-"
        val bounds = Rect()
        textPaint.getTextBounds(templateText, 0, templateText.length, bounds)
        textWidth = bounds.width().toFloat()
        rowHeight = bounds.height()*2.618f
        barHeight = bounds.height()*1.618f
        val barMaxWidth = layWidth - textWidth
        val maxCash = stats.maxOf { stat -> stat.money }
        barWidthUnit = barMaxWidth/maxCash.toFloat()
        this.minimumHeight = y(stats.size).toInt()
    }

    override fun onDraw(canvas: Canvas) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            isForceDarkAllowed = false
        }
        super.onDraw(canvas)
        if(!::stats.isInitialized) return
        initValues()
        canvas.apply {
            drawBackground(this)
            drawTexts(this)
            drawBars(this)
            //drawLine(textWidth, 0f, textWidth, y(stats.size), linePaint)
        }
    }

    private fun drawBackground(canvas: Canvas){
    }

    private fun drawTexts(canvas: Canvas){
        for((i, stat) in stats.withIndex()){
            canvas.drawText(stat.date, 0f, y(i)+barHeight*0.5f - (textPaint.descent() +textPaint.ascent())/2, textPaint)
        }
    }

    private fun drawBars(canvas: Canvas){
        canvas.apply {
            for(i in stats.indices){
                drawRect(textWidth, y(i), textWidth+barMoneyW(i), y(i)+barHeight, barMoneyPaint)
                drawRect(textWidth, y(i), textWidth+barIncomeW(i), y(i)+barHeight, barIncomePaint)
            }
        }
    }

    private fun barMoneyW(i: Int): Float {
        return (barWidthUnit*stats[i].money).toFloat()
    }

    private fun barIncomeW(i: Int): Float{
        return (barWidthUnit*(stats[i].money-stats[i].outcome)).toFloat().run {
            if(this > 0) this
            else 0f
        }
    }

    private fun y(i: Int): Float{
        return i*rowHeight
    }
}