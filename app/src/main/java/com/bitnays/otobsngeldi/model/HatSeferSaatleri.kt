package com.bitnays.otobsngeldi.model

import kotlinx.serialization.Serializable

@Serializable
data class HatSeferSaatleri (
    val SHATKODU: String,
    val HATADI: String?,
    val SGUZERAH: String?,
    val SYON: String?,
    val SGUNTIPI: String?,
    val GUZERGAH_ISARETI: String?,
    val SSERVISTIPI: String?,
    val DT: String?,
)