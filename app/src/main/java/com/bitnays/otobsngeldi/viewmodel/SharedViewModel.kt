package com.bitnays.otobsngeldi.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel: ViewModel() {

    private val _HatKodu = mutableStateOf("")
    val HatKodu: State<String> = _HatKodu
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
    fun setHatKodu2(value: String) {
        _HatKodu.value = value
    }
}