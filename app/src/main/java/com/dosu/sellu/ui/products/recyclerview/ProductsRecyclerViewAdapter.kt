package com.dosu.sellu.ui.products.recyclerview

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.text.InputType
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.dosu.sellu.R
import com.dosu.sellu.data.network.product.model.Product
import com.dosu.sellu.ui.products.viewmodel.ProductsViewModel

abstract class ProductsRecyclerViewAdapter(private val context: Context?,
                                           private val productsViewModel: ProductsViewModel)
    : RecyclerView.Adapter<ProductItemViewHolder>() {
    private val mInflater = LayoutInflater.from(context)
    var products: MutableList<Product> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductItemViewHolder {
        val view = mInflater.inflate(R.layout.fragment_product_item, parent, false)
        return ProductItemViewHolder(context!!, view)
    }

    override fun onBindViewHolder(holder: ProductItemViewHolder, position: Int) {
        val product = products[position]
        holder.update(product)
        setExpandAction(position, holder)
        holder.addBtn.setOnClickListener { showDialog(product, position)}
        holder.editBtn.setOnClickListener {editProduct(product.productId, position)}
    }

    private var expandedPos = -1
    private var expandedHolder: ProductItemViewHolder? = null
    @SuppressLint("NotifyDataSetChanged")
    private fun setExpandAction(pos: Int, holder: ProductItemViewHolder){
        val isExpanded = expandedPos == pos
        if(isExpanded) holder.updateDetails(products[pos])
        holder.details.visibility = if(isExpanded) VISIBLE else GONE
        holder.details.isActivated = isExpanded
        holder.expBtn.setOnClickListener {
            expandedPos = if(isExpanded) -1 else pos
            expandedHolder = if(isExpanded) null else holder
            if(!isExpanded) holder.updateDetails(products[pos])
            val autoTransition = AutoTransition()
            autoTransition.duration = 150
            TransitionManager.beginDelayedTransition(recyclerView, autoTransition)
            notifyDataSetChanged()
        }
    }

    abstract fun editProduct(productId: String, position: Int)

    override fun getItemCount(): Int {
        return products.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private fun showDialog(product: Product, position: Int){
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context?.getString(R.string.add_quantity_dialog_title)+" "+product.name)
        val input = EditText(context)
        input.setHint(R.string.add_quantity_dialog_hint)
        input.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
        builder.setView(input)
        builder.setPositiveButton(R.string.add_quantity_ok) {_, _ ->
            val v = input.text.toString().toInt()
            productsViewModel.updateProductQuantity(product.productId, v, position)
        }
        builder.setNegativeButton(R.string.add_quantity_cancel) { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private lateinit var recyclerView: RecyclerView
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }
}
