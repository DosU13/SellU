package com.dosu.sellu.ui.user_profile.util

import android.net.Uri
import com.dosu.sellu.util.ErrorResponse

interface UserListener {
    fun uploadImageSucceed(uri: Uri)
    fun anyError(code: Int?, responseBody: ErrorResponse?)
}