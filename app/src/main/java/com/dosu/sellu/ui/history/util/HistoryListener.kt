package com.dosu.sellu.ui.history.util

import com.dosu.sellu.ui.history.model.HistorySelling
import com.dosu.sellu.util.ErrorResponse

interface HistoryListener {
    fun getSellingList(sellingList: List<HistorySelling>)
    fun anyError(errorCode: Int?, responseBody: ErrorResponse?)
}