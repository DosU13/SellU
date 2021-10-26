package com.dosu.sellu.ui.selling

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.dosu.sellu.R
import com.dosu.sellu.data.network.product.model.Product
import com.dosu.sellu.databinding.SellingFragmentBinding
import com.dosu.sellu.ui.products.util.ProductsListener
import com.dosu.sellu.ui.products.viewmodel.ProductsViewModel
import com.dosu.sellu.ui.products.viewmodel.ProductsViewModelFactory
import com.dosu.sellu.ui.selling.util.AddSellingListener
import com.dosu.sellu.ui.selling.util.SellingRecyclerViewAdapter
import com.dosu.sellu.ui.selling.viewmodel.SellingViewModel
import com.dosu.sellu.util.ErrorResponse
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.instance

class SellingFragment : Fragment(), DIAware, AddSellingListener, ProductsListener{
    private var _binding: SellingFragmentBinding? = null
    private val binding get() = _binding!!
    override val di: DI by closestDI()
    private val sellingViewModel: SellingViewModel by instance()
    private val productsViewModelFactory: ProductsViewModelFactory by instance()
    private lateinit var productsViewModel: ProductsViewModel

    private lateinit var adapter: SellingRecyclerViewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = SellingFragmentBinding.inflate(inflater, container, false)
        sellingViewModel.setListener(this)
        sellingViewModel.getSummaryPrize()
        productsViewModel = ViewModelProvider(this, productsViewModelFactory).get(ProductsViewModel::class.java)
        productsViewModel.setListener(this as ProductsListener)

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter =  SellingRecyclerViewAdapter(context, sellingViewModel)
        recyclerView.adapter = adapter
        productsViewModel.getProducts()
        binding.toggleBtn.setOnCheckedChangeListener{ _, checked ->
            updateToggleBtn(checked)
        }
        binding.sellBtn.setOnClickListener{sellBtnPressed()}
        return binding.root
    }

    private lateinit var anim: AnimatedVectorDrawable

    private fun updateToggleBtn(checked: Boolean){
        binding.reasonContainer.visibility = if(checked)View.VISIBLE else View.GONE
        binding.prizeContainer.setBackgroundResource(if(checked)R.drawable.tile_top else R.drawable.tile)
        binding.prize.isEnabled = checked
        if(checked) binding.prize.requestFocus()
        else sellingViewModel.getSummaryPrize()
        binding.reason.isEnabled = checked
        anim = AppCompatResources.getDrawable(requireContext(),
            if(checked) R.drawable.animated_edit_to_cancel else R.drawable.animated_cancel_to_edit)
                as AnimatedVectorDrawable
        binding.toggleBtn.background = anim
        val autoTransition = AutoTransition()
        autoTransition.duration = 150
        TransitionManager.beginDelayedTransition(binding.root, autoTransition)
        anim.start()
    }

    private fun sellBtnPressed(){
        if(binding.toggleBtn.isChecked){
            val prize = binding.prize.text.toString().toDouble()
            sellingViewModel.sell(prize, prize, binding.reason.text.toString())
        }else {
            sellingViewModel.sell(summaryPrize, null, null)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val currentNightMode = newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val nightMode = currentNightMode == Configuration.UI_MODE_NIGHT_YES
        adapter.notifyDataSetChanged()
    }

    override fun addSellingSucceed() {
        Toast.makeText(context, getString(R.string.selling_succeed), Toast.LENGTH_SHORT).show()
        adapter.notifyDataSetChanged()
        getSummaryPrize(0.0)
    }

    override fun getProducts(products: List<Product>) {
        adapter.products = products
        adapter.notifyDataSetChanged()
    }

    override fun updateProductSucceed() {
        adapter.notifyDataSetChanged()
    }

    private var summaryPrize = 0.0
    @SuppressLint("SetTextI18n")
    override fun getSummaryPrize(prize: Double) {
        summaryPrize = prize
        if(!binding.toggleBtn.isChecked) binding.prize.setText(prize.toString())
    }

    override fun anyError(code: Int?, responseBody: ErrorResponse?) {
        Toast.makeText(context, "Error adding selling or getting products", Toast.LENGTH_LONG).show()
    }
}
