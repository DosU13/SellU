package com.dosu.sellu.ui.selling.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dosu.sellu.R
import com.dosu.sellu.data.network.product.model.Product
import com.dosu.sellu.ui.products.viewmodel.ProductsViewModel
import com.dosu.sellu.ui.selling.viewmodel.SellingViewModel
import com.dosu.sellu.util.dp
import com.dosu.sellu.util.price
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily

class SellingRecyclerViewAdapter(
    private val context: Context?,
    private val productsViewModel: ProductsViewModel,
    private val sellingViewModel: SellingViewModel)
                            : RecyclerView.Adapter<SellingRecyclerViewAdapter.ViewHolder>() {
    private val mInflater = LayoutInflater.from(context)
    var products: List<Product> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.fragment_product_item_in_selling, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val p = products[position]
        val radius = 5.0.dp
        holder.image.shapeAppearanceModel = holder.image.shapeAppearanceModel.toBuilder().
            setTopLeftCorner(CornerFamily.ROUNDED, radius).setBottomLeftCorner(CornerFamily.ROUNDED, radius).build()
        productsViewModel.downloadImage(p.productId, 0)
        holder.productName.text = p.name
        holder.prize.text = p.prize.price
        holder.quantityMinus.setOnClickListener {
            sellingViewModel.decreaseQuantity(p.productId)
            holder.quantity.text = sellingViewModel.getSellingQuantity(p.productId).toString()
            sellingViewModel.getSummaryPrize()
        }
        holder.quantityPlus.setOnClickListener {
            sellingViewModel.increaseQuantity(p.productId)
            holder.quantity.text = sellingViewModel.getSellingQuantity(p.productId).toString()
            sellingViewModel.getSummaryPrize()
        }
        holder.quantity.text = sellingViewModel.getSellingQuantity(p.productId).toString()
    }

    override fun getItemCount(): Int {
        return products.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.product_name)
        val image: ShapeableImageView = itemView.findViewById(R.id.product_image)
        val prize: TextView = itemView.findViewById(R.id.prize)
        val quantityMinus: ImageView = itemView.findViewById(R.id.quantity_minus)
        val quantityPlus: ImageView = itemView.findViewById(R.id.quantity_plus)
        val quantity: TextView = itemView.findViewById(R.id.quantity)
    }
}