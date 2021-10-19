package com.dosu.sellu.ui.stat.model

data class Stat(
    val date: String,
    var money: Double = 0.0,
    var outcome: Double = 0.0,
    var count: Int = 0
)