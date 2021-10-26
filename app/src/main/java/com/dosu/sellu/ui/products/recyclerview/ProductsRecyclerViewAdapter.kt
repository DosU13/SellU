package com.dosu.sellu.ui.products.recyclerview

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.InputType
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
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
import com.dosu.sellu.ui.products.add_product.AddProductActivity
import com.dosu.sellu.ui.products.viewmodel.ProductsViewModel
import com.dosu.sellu.util.SellU
import com.dosu.sellu.util.dp
import com.dosu.sellu.util.loadImage
import com.dosu.sellu.util.price
import com.google.android.material.shape.CornerFamily

class ProductsRecyclerViewAdapter(private val context: Context?,
                                  private val productsViewModel: ProductsViewModel)
    : RecyclerView.Adapter<ProductItemViewHolder>() {
    private val mInflater = LayoutInflater.from(context)
    var products: List<Product> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductItemViewHolder {
        val view = mInflater.inflate(R.layout.fragment_product_item, parent, false)
        return ProductItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductItemViewHolder, position: Int) {
        val product = products[position]
        val radius = 5.0.dp
        holder.image.shapeAppearanceModel = holder.image.shapeAppearanceModel.toBuilder().
            setTopLeftCorner(CornerFamily.ROUNDED, radius).setBottomLeftCorner(CornerFamily.ROUNDED, radius).build()
        holder.name.text = product.name
        product.thumbnail?.let{holder.image.loadImage(context!!, Uri.parse(it))}
        holder.prize.text = product.prize.price
        val quantityStr = "${context!!.getString(R.string.text_quantity)} ${product.quantity}"
        val quantity = SpannableString(quantityStr)
        quantity.setSpan(RelativeSizeSpan(1.618f), 5, quantityStr.length, 0)
        holder.quantity.text = quantity
        holder.addBtn.setOnClickListener { showDialog(product) }
        setExpandAction(position, holder)
    }

    private var expandedPos = -1
    private var expandedHolder: ProductItemViewHolder? = null
    @SuppressLint("NotifyDataSetChanged")
    private fun setExpandAction(pos: Int, holder: ProductItemViewHolder){
        val isExpanded = expandedPos == pos
        holder.details.visibility = if(isExpanded) VISIBLE else GONE
        holder.details.isActivated = isExpanded
        holder.expBtn.setOnClickListener {
            expandedPos = if(isExpanded) -1 else pos
            expandedHolder = if(isExpanded) null else holder
            if(!isExpanded) updateDetails(pos, holder)
            val autoTransition = AutoTransition()
            autoTransition.duration = 150
            TransitionManager.beginDelayedTransition(recyclerView, autoTransition)
            notifyDataSetChanged()
        }
    }

    private fun updateDetails(pos: Int, holder: ProductItemViewHolder) {
        holder.run {
            val p = products[pos]
            description.text = p.description ?: SellU.res.getString(R.string.empty)
            ownPrize.text = p.ownPrize.price
            val todaySoldInt = productsViewModel.todaySold(p)
            val todaySoldStr = SpannableString(todaySoldInt.toString())
            todaySoldStr.setSpan(RelativeSizeSpan(1.618f), 0, todaySoldStr.length, 0)
            todaySold.text = todaySoldStr
            todayIncome.text = ((p.prize - p.ownPrize)*todaySoldInt).price
            changeBtn.setOnClickListener {
                val intent = Intent(context, AddProductActivity::class.java)
                intent.putExtra("isNewProduct", false)
                intent.putExtra("productId", p.productId)
                context?.startActivity(intent)
            }
            setImageViewPager(context!!, p.images)
        }
    }

    override fun getItemCount(): Int {
        return products.size
    }

    private fun showDialog(product: Product){
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context?.getString(R.string.add_quantity_dialog_title)+" "+product.name)
        val input = EditText(context)
        input.setHint(R.string.add_quantity_dialog_hint)
        input.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
        builder.setView(input)
        builder.setPositiveButton(R.string.add_quantity_ok) {_, _ ->
            val v = input.text.toString().toInt()
            productsViewModel.updateProductQuantity(product.productId, v)
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
