package com.bitnays.otobsngeldi.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class Duyuru(
    val HATKODU: String,
    val HAT: String,
    val TIP: String,
    val GUNCELLEME_SAATI: String,
    val MESAJ: String)