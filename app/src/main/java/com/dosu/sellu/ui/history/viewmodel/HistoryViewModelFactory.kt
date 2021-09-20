package com.dosu.sellu.ui.history.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dosu.sellu.data.network.product.ProductRepository
import com.dosu.sellu.data.network.selling.SellingRepository

class HistoryViewModelFactory(private val sellingRepository: SellingRepository, private val productRepository: ProductRepository)
    : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HistoryViewModel(sellingRepository, productRepository) as T
    }
}