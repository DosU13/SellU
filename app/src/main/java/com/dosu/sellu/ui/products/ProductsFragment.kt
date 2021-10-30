package com.dosu.sellu.ui.products

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
        adapter = object : ProductsRecyclerViewAdapter(context, productsViewModel){
            override fun editProduct(productId: String, position: Int) {
                val intent = Intent(context, AddProductActivity::class.java)
                intent.putExtra("isNewProduct", false)
                intent.putExtra("productId", productId)
                intent.putExtra("product_position", position)
                addProductLauncher.launch(intent)
            }
        }
        adapter.setHasStableIds(true)
        recyclerView.adapter = adapter
        productsViewModel.getProducts()
        binding.addBtn.setOnClickListener{addBtnClicked() }
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private val addProductLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        Log.e("Here", "result")
        if(result.resultCode == Activity.RESULT_OK){
            Log.e("Here", "result ok")
            result.data?.run{
                Log.e("Here", this.toString())
                val productId = getStringExtra("productId")
                val productPos = getIntExtra("product_position", -1)
                if(productPos==-1) productsViewModel.getProducts()
                else productId?.let { productsViewModel.getProduct(it, productPos) }
                Log.e("Here", "$productId $productPos")
            }
        }
    }

    private fun addBtnClicked() {
        val intent = Intent(context, AddProductActivity::class.java)
        intent.putExtra("isNewProduct", true)
        addProductLauncher.launch(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun getProducts(products: List<Product>) {
        Toast.makeText(context, "products returned", Toast.LENGTH_SHORT).show() //TODO()
        adapter.products = products as MutableList<Product>
        adapter.notifyDataSetChanged()
    }

    override fun getProduct(product: Product, productPos: Int) {
        adapter.products[productPos] = product
        adapter.notifyItemChanged(productPos)
    }

    override fun updateProductSucceed(position: Int, product: Product) {
        adapter.products[position] = product
        adapter.notifyItemChanged(position)
    }

    override fun anyError(code: Int?, responseBody: ErrorResponse?) {
        Toast.makeText(context, responseBody?.detail, Toast.LENGTH_LONG).show()
    }
}