package com.dosu.sellu.data.network.selling

import com.dosu.sellu.data.network.FirebaseService
import com.dosu.sellu.data.network.selling.model.SellingWithoutId
import com.dosu.sellu.util.BaseRepository

class SellingRepository(private val firebaseService: FirebaseService): BaseRepository() {
    suspend fun getSellingList() = safeApiCall {
        firebaseService.getSellingList()
    }

    suspend fun addSelling(selling: SellingWithoutId) = safeApiCall {
        firebaseService.addSelling(selling)
    }
}