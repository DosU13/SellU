package com.dosu.sellu.ui.selling.util

import com.dosu.sellu.util.ErrorResponse

interface AddSellingListener {
    fun addSellingSucceed()
    fun getSummaryPrize(prize: Double)
    fun anyError(code: Int?, responseBody: ErrorResponse?)
}