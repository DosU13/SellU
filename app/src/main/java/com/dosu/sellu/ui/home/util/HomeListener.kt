package com.dosu.sellu.ui.home.util

import com.dosu.sellu.ui.home.model.Stat
import com.dosu.sellu.util.ErrorResponse

interface HomeListener{
    fun dataReady()
    fun singleStat(singleStat: Stat)
    fun statsReady(stats: MutableList<Stat>)
    fun anyError(code: Int?, responseBody: ErrorResponse?)
}