package com.dosu.sellu.ui.products.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dosu.sellu.data.network.product.ProductRepository

class ProductsViewModelFactory(private val productRepository: ProductRepository):
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("", "UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProductsViewModel(productRepository) as T
    }
}