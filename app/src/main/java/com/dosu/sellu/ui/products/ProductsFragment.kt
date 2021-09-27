package com.dosu.sellu.ui.products

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dosu.sellu.data.network.product.model.Product
import com.dosu.sellu.databinding.ProductsFragmentBinding
import com.dosu.sellu.ui.products.add_product.AddProductActivity
import com.dosu.sellu.ui.products.util.ImageListener
import com.dosu.sellu.ui.products.util.ProductsRecyclerViewAdapter
import com.dosu.sellu.ui.products.util.ProductsListener
import com.dosu.sellu.ui.products.viewmodel.ProductsViewModel
import com.dosu.sellu.ui.products.viewmodel.ProductsViewModelFactory
import com.dosu.sellu.util.ErrorResponse
import com.dosu.sellu.util.toDrawable
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.instance

class ProductsFragment : Fragment(), DIAware, ProductsListener, ImageListener {
    override val di: DI by closestDI()
    private val productsViewModelFactory: ProductsViewModelFactory by instance()
    private lateinit var productsViewModel: ProductsViewModel
    private var _binding: ProductsFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ProductsRecyclerViewAdapter // if it works make initializer here

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        productsViewModel = ViewModelProvider(this, productsViewModelFactory).get(ProductsViewModel::class.java)
        productsViewModel.setListener(this as ProductsListener)
        productsViewModel.setListener(this as ImageListener)
        _binding = ProductsFragmentBinding.inflate(inflater, container, false)

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ProductsRecyclerViewAdapter(context, productsViewModel)
        productsViewModel.getProducts()
        recyclerView.adapter = adapter

        binding.addBtn.setOnClickListener{addBtnClicked() }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        productsViewModel.getProducts()
    }

    private fun addBtnClicked() {
        val intent = Intent(context, AddProductActivity::class.java)
        intent.putExtra("isNewProduct", true)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun getProducts(products: List<Product>) {
        adapter.products = products
        Toast.makeText(context, products.size.toString(), Toast.LENGTH_LONG).show()
        adapter.notifyDataSetChanged()
    }

    override fun downloadImage(byteArray: ByteArray, productId: String, imagePos: Int) {
        val pos = adapter.products.indexOf(adapter.products.find {p -> p.productId==productId})
        binding.recyclerView.findViewHolderForAdapterPosition(pos)?.run{
            val viewHolder = this as ProductsRecyclerViewAdapter.ViewHolder
            viewHolder.image.setImageDrawable(byteArray.toDrawable(resources))
            viewHolder.images.setImageDrawable(byteArray.toDrawable(resources))
        }
    }

    override fun updateProductSucceed() {
        adapter.notifyDataSetChanged()
    }

    override fun anyError(code: Int?, responseBody: ErrorResponse?) {
        Toast.makeText(context, "any error called", Toast.LENGTH_LONG).show()
    }
}