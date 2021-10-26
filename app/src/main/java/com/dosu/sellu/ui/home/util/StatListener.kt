package com.dosu.sellu.ui.home.util

import com.dosu.sellu.ui.home.model.Stat
import com.dosu.sellu.util.ErrorResponse

interface StatListener{
    fun dataReady()
    fun singleStat(todayStat: Stat?, allStat: Stat)
    fun statsReady(stats: MutableList<Stat>)
    fun anyError(code: Int?, responseBody: ErrorResponse?)
}