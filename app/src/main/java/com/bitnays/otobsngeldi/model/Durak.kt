package com.bitnays.otobsngeldi.model

import kotlinx.serialization.Serializable

@Serializable
data class Durak (
    val YON: String,
    val SIRANO: String,
    val DURAKKODU : String,
    val DURAKADI: String,
    val XKOORDINATI: String,
    val YKOORDINATI: String,
    var otobusVar: Boolean? = null
)