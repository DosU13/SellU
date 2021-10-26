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
import com.dosu.sellu.ui.products.util.ProductsListener
import com.dosu.sellu.ui.products.recyclerview.ProductsRecyclerViewAdapter
import com.dosu.sellu.ui.products.viewmodel.ProductsViewModel
import com.dosu.sellu.ui.products.viewmodel.ProductsViewModelFactory
import com.dosu.sellu.util.ErrorResponse
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.instance

class ProductsFragment : Fragment(), DIAware, ProductsListener{
    override val di: DI by closestDI()
    private val productsViewModelFactory: ProductsViewModelFactory by instance()
    private lateinit var productsViewModel: ProductsViewModel
    private var _binding: ProductsFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ProductsRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        productsViewModel = ViewModelProvider(this, productsViewModelFactory).get(ProductsViewModel::class.java)
        productsViewModel.setListener(this as ProductsListener)
        _binding = ProductsFragmentBinding.inflate(inflater, container, false)

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ProductsRecyclerViewAdapter(context, productsViewModel)
        recyclerView.adapter = adapter

        productsViewModel.getProducts()
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
        adapter.notifyDataSetChanged()
    }

    override fun updateProductSucceed() {
        adapter.notifyDataSetChanged()
    }

    override fun anyError(code: Int?, responseBody: ErrorResponse?) {
        Toast.makeText(context, responseBody?.detail, Toast.LENGTH_LONG).show()
    }
}