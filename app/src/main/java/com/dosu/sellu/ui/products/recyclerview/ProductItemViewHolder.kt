package com.dosu.sellu.ui.products.recyclerview

import android.content.Context
import android.net.Uri
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.dosu.sellu.R
import com.dosu.sellu.data.network.product.model.Product
import com.dosu.sellu.util.SellU
import com.dosu.sellu.util.dp
import com.dosu.sellu.util.loadImage
import com.dosu.sellu.util.price
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.tabs.TabLayout


class ProductItemViewHolder(private val context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {
    val name: TextView = itemView.findViewById(R.id.product_name)
    private val image: ShapeableImageView = itemView.findViewById(R.id.product_image)
    private val quantity: TextView = itemView.findViewById(R.id.quantity)
    val prize: TextView = itemView.findViewById(R.id.prize)
    val addBtn: Button = itemView.findViewById(R.id.add_btn)
    val expBtn: Button = itemView.findViewById(R.id.product_expand_btn)
    val details: LinearLayout = itemView.findViewById(R.id.details)

    private val carousel: ViewPager = itemView.findViewById(R.id.carousel)
    private val tabIndicator: TabLayout = itemView.findViewById(R.id.tab_indicator)
    private val description: TextView = itemView.findViewById(R.id.description)
    private val ownPrize: TextView = itemView.findViewById(R.id.own_prize)
    private val todaySold: TextView = itemView.findViewById(R.id.today_sold)
    private val todayIncome: TextView = itemView.findViewById(R.id.today_income)
    val editBtn: Button = itemView.findViewById(R.id.edit_btn)

    fun update(product: Product){
        val radius = 5.0.dp
        image.shapeAppearanceModel = image.shapeAppearanceModel.toBuilder().
        setTopLeftCorner(CornerFamily.ROUNDED, radius).setBottomLeftCorner(CornerFamily.ROUNDED, radius).build()
        name.text = product.name
        product.images.let{if(it.isNotEmpty()) if(it[0]!=null) image.loadImage(context, Uri.parse(it[0]))}// it was thumbnail
        prize.text = product.prize.price
        val quantityStr = "${context.getString(R.string.text_quantity)} ${product.quantity}"
        val quantitySpan = SpannableString(quantityStr)
        quantitySpan.setSpan(RelativeSizeSpan(1.618f), 5, quantityStr.length, 0)
        quantity.text = quantitySpan
    }

    fun updateDetails(product: Product) {
        description.text = product.description ?: SellU.res.getString(R.string.empty)
        ownPrize.text = product.ownPrize.price
        val todaySoldStr = SpannableString(product.lastDaySold.toString())
        todaySoldStr.setSpan(RelativeSizeSpan(1.618f), 0, todaySoldStr.length, 0)
        todaySold.text = todaySoldStr
        todayIncome.text = ((product.prize - product.ownPrize)*product.lastDaySold).price
        setImageViewPager(context, product.images)
    }

    private fun setImageViewPager(context: Context, images: List<String?>) {
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