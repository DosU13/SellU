package com.dosu.sellu.ui.user_profile.util

import android.net.Uri
import com.dosu.sellu.util.ErrorResponse

interface UserListener {
    fun downloadImage(byteArray: ByteArray)
    fun uploadImageSucceed()
    fun anyError(code: Int?, responseBody: ErrorResponse?)
}