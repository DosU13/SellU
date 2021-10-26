package com.dosu.sellu.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dosu.sellu.data.network.product.ProductRepository
import com.dosu.sellu.data.network.selling.SellingRepository

class StatViewModelFactory(private val productRepository: ProductRepository,
                           private val sellingRepository: SellingRepository): ViewModelProvider.NewInstanceFactory(){
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return StatViewModel(productRepository,  sellingRepository) as T
    }
}