package com.bitnays.otobsngeldi.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel: ViewModel() {
    val _hatOtoKonumString = MutableLiveData("")
    private val _hatKodu = MutableLiveData("")
    val hatOtoKonumString: LiveData<String> = _hatOtoKonumString
    val hatKodu: LiveData<String> = _hatKodu

    fun setHatOtoKonumString(value: String) {
        _hatOtoKonumString.value = value
    }
    fun setHatKodu(value: String) {
        _hatKodu.value = value
    }

}