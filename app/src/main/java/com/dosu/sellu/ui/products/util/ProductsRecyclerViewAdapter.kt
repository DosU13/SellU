package com.dosu.sellu.ui.products.util

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.text.InputType
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.dosu.sellu.R
import com.dosu.sellu.data.network.product.model.Product
import com.dosu.sellu.ui.products.add_product.AddProductActivity
import com.dosu.sellu.ui.products.viewmodel.ProductsViewModel
import com.dosu.sellu.util.dp
import com.dosu.sellu.util.price
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily

class ProductsRecyclerViewAdapter(private val context: Context?, private val viewModel: ProductsViewModel)
    : RecyclerView.Adapter<ProductsRecyclerViewAdapter.ViewHolder>(){
    private val mInflater = LayoutInflater.from(context)
    var products: List<Product> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.fragment_product_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]

        val radius = 5.0.dp
        holder.image.shapeAppearanceModel = holder.image.shapeAppearanceModel.toBuilder().
            setTopLeftCorner(CornerFamily.ROUNDED, radius).setBottomLeftCorner(CornerFamily.ROUNDED, radius).build()

        holder.name.text = product.name
        if(product.numOfImages > 0) viewModel.downloadImage(product.productId, 0)
        holder.prize.text = product.prize.price
        val quantityStr = "${context?.getString(R.string.text_quantity)} ${product.quantity}"
        val quantity = SpannableString(quantityStr)
        quantity.setSpan(RelativeSizeSpan(1.618f), 5, quantityStr.length, 0)
        holder.quantity.text = quantity
        holder.addBtn.setOnClickListener { showDialog(product) }
        setExpandAction(position, holder)
    }

    private var expandedPos = -1
    @SuppressLint("NotifyDataSetChanged")
    private fun setExpandAction(pos: Int, holder: ViewHolder){
        val isExpanded = expandedPos == pos
        holder.details.visibility = if(isExpanded) VISIBLE else GONE
        holder.details.isActivated = isExpanded
        holder.expBtn.setOnClickListener {
            //notifyItemChanged(expandedPos)
            expandedPos = if(isExpanded) -1 else pos
            if(!isExpanded) updateDetails(pos, holder)
            val autoTransition = AutoTransition()
            autoTransition.duration = 150
            TransitionManager.beginDelayedTransition(recyclerView, autoTransition)
            //notifyItemChanged(pos)
            notifyDataSetChanged()
        }
    }

    private fun updateDetails(pos: Int, holder: ViewHolder) {
        holder.run {
            val p = products[pos]
            viewModel.downloadImage(products[pos].productId, 0)
            description.text = p.description
            ownPrize.text = p.ownPrize.toString()
            val todaySoldInt = viewModel.todaySold(p.productId)
            todaySold.text = todaySoldInt.toString()
            todayIncome.text = String.format("%.2f", (todaySoldInt.toDouble()*(p.prize-p.ownPrize)))+"сом"
            changeBtn.setOnClickListener {
                val intent = Intent(context, AddProductActivity::class.java)
                intent.putExtra("isNewProduct", false)
                intent.putExtra("productId", p.productId)
                context?.startActivity(intent)
            }
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
            viewModel.updateProductQuantity(product.productId, v)
        }
        builder.setNegativeButton(R.string.add_quantity_cancel) { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.product_name)
        val image: ShapeableImageView = itemView.findViewById(R.id.product_image)
        val quantity: TextView = itemView.findViewById(R.id.quantity)
        val prize: TextView = itemView.findViewById(R.id.prize)
        val addBtn: Button = itemView.findViewById(R.id.add_btn)
        val expBtn: Button = itemView.findViewById(R.id.product_expand_btn)
        val details: LinearLayout = itemView.findViewById(R.id.details)

        val images: ImageView = itemView.findViewById(R.id.images)
        val description: TextView = itemView.findViewById(R.id.description)
        val ownPrize: TextView = itemView.findViewById(R.id.own_prize)
        val todaySold: TextView = itemView.findViewById(R.id.today_sold)
        val todayIncome: TextView = itemView.findViewById(R.id.today_income)
        val graph: View = itemView.findViewById(R.id.graph)
        val changeBtn: Button = itemView.findViewById(R.id.change_btn)
    }

    private lateinit var recyclerView: RecyclerView
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }
}