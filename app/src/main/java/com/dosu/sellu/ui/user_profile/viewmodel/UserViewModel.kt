package com.dosu.sellu.ui.user_profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dosu.sellu.data.network.NetworkResponse
import com.dosu.sellu.data.network.user.UserRepository
import com.dosu.sellu.ui.user_profile.util.UserListener
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    private lateinit var listener: UserListener

    fun setListener(listener: UserListener){
        this.listener = listener
    }

    fun downloadImage() = viewModelScope.launch {
        when(val response = userRepository.downloadUserImage()){
            is NetworkResponse.Success -> listener.downloadImage(response.value)
            is NetworkResponse.Failure -> listener.anyError(response.errorCode, response.errorBody)
        }
    }

    fun uploadImage(byteArray: ByteArray) = viewModelScope.launch{
        when(val response = userRepository.uploadUserImage(byteArray)){
            is NetworkResponse.Success -> listener.uploadImageSucceed()
            is NetworkResponse.Failure -> listener.anyError(response.errorCode, response.errorBody)
        }
    }
}