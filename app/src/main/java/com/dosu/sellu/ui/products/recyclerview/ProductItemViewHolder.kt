package com.dosu.sellu.ui.products.recyclerview

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.dosu.sellu.R
import com.dosu.sellu.ui.products.util.OnSwipeTouchListener
import com.dosu.sellu.util.dp
import com.google.android.material.imageview.ShapeableImageView


class ProductItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val name: TextView = itemView.findViewById(R.id.product_name)
    val image: ShapeableImageView = itemView.findViewById(R.id.product_image)
    val quantity: TextView = itemView.findViewById(R.id.quantity)
    val prize: TextView = itemView.findViewById(R.id.prize)
    val addBtn: Button = itemView.findViewById(R.id.add_btn)
    val expBtn: Button = itemView.findViewById(R.id.product_expand_btn)
    val details: LinearLayout = itemView.findViewById(R.id.details)

    private val carousel: LinearLayout = itemView.findViewById(R.id.carousel)
    val images = arrayOf<ImageView>(
        itemView.findViewById(R.id.image0),
        itemView.findViewById(R.id.image1),
        itemView.findViewById(R.id.image2),
        itemView.findViewById(R.id.image3),
        itemView.findViewById(R.id.image4))
    val description: TextView = itemView.findViewById(R.id.description)
    val ownPrize: TextView = itemView.findViewById(R.id.own_prize)
    val todaySold: TextView = itemView.findViewById(R.id.today_sold)
    val todayIncome: TextView = itemView.findViewById(R.id.today_income)
    val changeBtn: Button = itemView.findViewById(R.id.change_btn)

    private var imgIt = 0
    fun setImageSwipes(context: Context, parent: RecyclerView, imageCount: Int) {
        if(imageCount < 4) for(i in (imageCount)..4){
            images[i].visibility = View.GONE
        }
        when (imageCount) {
            0 -> carousel.visibility = View.GONE
            1 -> expandImg(0)
            else -> {
                val swipeTouchListener = object : OnSwipeTouchListener(context) {
                    override fun onSwipeLeft() {
                        if (imgIt > 0) {
                            shrinkImg(imgIt)
                            imgIt--
                            expandImg(imgIt)
                            val autoTransition = AutoTransition()
                            autoTransition.duration = 150
                            TransitionManager.beginDelayedTransition(parent, autoTransition)
                        }
                    }

                    override fun onSwipeRight() {
                        if (imgIt < imageCount - 1) {
                            shrinkImg(imgIt)
                            imgIt++
                            expandImg(imgIt)
                            val autoTransition = AutoTransition()
                            autoTransition.duration = 150
                            TransitionManager.beginDelayedTransition(parent, autoTransition)
                        }
                    }
                }
                carousel.setOnTouchListener(swipeTouchListener)
                for(i in 0 until imageCount) images[i].setOnTouchListener(swipeTouchListener)
            }
        }
    }

    fun setImageListeners(context: Context, parent: RecyclerView, imageCount: Int) {
        if(imageCount < 5) for(i in imageCount..4){
            images[i].visibility = View.GONE
        }
        when (imageCount) {
            0 -> carousel.visibility = View.GONE
            1 -> expandImg(0)
            else -> {
                for(i in 0 until imageCount) images[i].setOnClickListener {
                    if(imgIt != i){
                        shrinkImg(imgIt)
                        imgIt = i
                        expandImg(imgIt)
                        val autoTransition = AutoTransition()
                        autoTransition.duration = 300
                        TransitionManager.beginDelayedTransition(parent, autoTransition)
                    }
                }
            }
        }
    }

    private fun shrinkImg(index: Int){
        val imgView = images[index]
        //imgView.scaleType = ImageView.ScaleType.CENTER_CROP
        val params = imgView.layoutParams as LinearLayout.LayoutParams
        params.weight = 0f
        params.width = 10.0.dp.toInt()
        imgView.layoutParams = params
    }

    private fun expandImg(index: Int){
        val imgView = images[index]
        //imgView.scaleType = ImageView.ScaleType.FIT_CENTER
        val params = imgView.layoutParams as LinearLayout.LayoutParams
        params.weight = 1f
        params.width = 0
        imgView.layoutParams = params
    }
}