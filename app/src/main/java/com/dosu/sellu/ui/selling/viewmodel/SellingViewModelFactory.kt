package com.dosu.sellu.ui.selling.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dosu.sellu.data.network.product.ProductRepository
import com.dosu.sellu.data.network.selling.SellingRepository

class SellingViewModelFactory(private val sellingRepository: SellingRepository, private val productRepository: ProductRepository)
                            : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SellingViewModel(sellingRepository, productRepository) as T
    }
}