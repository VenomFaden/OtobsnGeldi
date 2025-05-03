package com.bitnays.otobsngeldi

import kotlinx.serialization.Serializable

@Serializable
data class OtoHatKonum (val kapino: String,
                        val boylam: String,
                        val enlem: String,
                        val hatkodu: String,
                        val guzergahkodu: String,
                        var hatad: String,
                        val yon: String,
                        val son_konum_zamani: String,
                        var yakinDurakKodu: String)