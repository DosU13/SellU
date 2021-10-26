package com.dosu.sellu.ui.products.recyclerview

import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.net.Uri
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.dosu.sellu.R
import com.dosu.sellu.util.loadImage
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.tabs.TabLayout


class ProductItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val name: TextView = itemView.findViewById(R.id.product_name)
    val image: ShapeableImageView = itemView.findViewById(R.id.product_image)
    val quantity: TextView = itemView.findViewById(R.id.quantity)
    val prize: TextView = itemView.findViewById(R.id.prize)
    val addBtn: Button = itemView.findViewById(R.id.add_btn)
    val expBtn: Button = itemView.findViewById(R.id.product_expand_btn)
    val details: LinearLayout = itemView.findViewById(R.id.details)

    private val carousel: ViewPager = itemView.findViewById(R.id.carousel)
    private val tabIndicator: TabLayout = itemView.findViewById(R.id.tab_indicator)
    val description: TextView = itemView.findViewById(R.id.description)
    val ownPrize: TextView = itemView.findViewById(R.id.own_prize)
    val todaySold: TextView = itemView.findViewById(R.id.today_sold)
    val todayIncome: TextView = itemView.findViewById(R.id.today_income)
    val changeBtn: Button = itemView.findViewById(R.id.change_btn)

    fun setImageViewPager(context: Context, images: List<String?>) {
        if (images.isEmpty()) carousel.visibility = View.GONE
        else {
            val adapter = MyAdapter(context, images)
            carousel.adapter = adapter
            if(images.size > 1)tabIndicator.setupWithViewPager(carousel)
        }
    }

    class MyAdapter(private val context: Context, private val images: List<String?>
    ): PagerAdapter() {
        override fun instantiateItem(container: View, position: Int): Any {
            val imageView = ImageView(context)
            (container as ViewPager).addView(imageView, 0)
            imageView.loadImage(context, Uri.parse(images[position]))
            return imageView
        }

        override fun getCount(): Int {
            return images.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == (`object` as View)
        }

        override fun destroyItem(container: View, position: Int, `object`: Any) {
        }
    }
}