package com.dosu.sellu.ui.products.util

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

class ProductsRecyclerViewAdapter(context: Context?, private val viewModel: ProductsViewModel)
    : RecyclerView.Adapter<ProductsRecyclerViewAdapter.ViewHolder>(){
    private val mInflater = LayoutInflater.from(context)
    var products: List<Product> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.fragment_product_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        holder.name.text = product.name
        if(product.numOfImages > 0) viewModel.downloadImage(product.productId, 0)
        holder.quantity.text = product.quantity.toString()
        holder.prize.text = product.prize.toString()
    }

    override fun getItemCount(): Int {
        return products.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.product_name)
        val image: ImageView = itemView.findViewById(R.id.product_image)
        val quantity: TextView = itemView.findViewById(R.id.quantity)
        val prize: TextView = itemView.findViewById(R.id.prize)
    }
}