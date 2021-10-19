package com.dosu.sellu.ui.stat.util

import com.dosu.sellu.ui.stat.model.Stat
import com.dosu.sellu.util.ErrorResponse

interface StatListener{
    fun dataReady()
    fun singleStat(singleStat: Stat)
    fun statsReady(stats: MutableList<Stat>)
    fun anyError(code: Int?, responseBody: ErrorResponse?)
}