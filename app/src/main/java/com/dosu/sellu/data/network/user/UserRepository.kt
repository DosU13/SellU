package com.dosu.sellu.data.network.user

import com.dosu.sellu.data.network.FirebaseService
import com.dosu.sellu.util.BaseRepository
import com.google.firebase.auth.FirebaseAuth

class UserRepository(private val firebaseService: FirebaseService): BaseRepository() {
    private val userId get() = FirebaseAuth.getInstance().currentUser!!.uid

    suspend fun uploadUserImage(byteArray: ByteArray) = safeApiCall {
        val refString = "users/$userId.jpg"
        firebaseService.uploadImage(refString, byteArray)
    }

    suspend fun downloadUserImage() = safeApiCall {
        val refString = "users/$userId.jpg"
        firebaseService.downloadImage(refString)
    }
}