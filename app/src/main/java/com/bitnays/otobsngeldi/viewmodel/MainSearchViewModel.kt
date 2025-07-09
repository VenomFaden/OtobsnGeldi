package com.bitnays.otobsngeldi.viewmodel

import android.content.Intent
import androidx.activity.viewModels
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bitnays.otobsngeldi.Durak
import com.bitnays.otobsngeldi.MainActivity
import com.bitnays.otobsngeldi.MainActivity2
import com.bitnays.otobsngeldi.OtoHatKonum
import com.bitnays.otobsngeldi.repo.repo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class MainSearchViewModel: ViewModel() {
    private val getRepo = repo()
    private val hatOtoKonumString = MutableLiveData("")
    val liveHatOtoKonumString : LiveData<String> = hatOtoKonumString
    fun getHatOtoKonumJSON(hatkodu: String)
    {
        CoroutineScope(Dispatchers.IO).launch {
            hatOtoKonumString.postValue(getRepo.HatOtoKonumJSON(hatkodu).toString())
        }
    }

}