package com.bitnays.otobsngeldi.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf

import androidx.lifecycle.ViewModel
import com.bitnays.otobsngeldi.Durak
import com.bitnays.otobsngeldi.OtoHatKonum
import com.bitnays.otobsngeldi.repo.repo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DurakListViewModel: ViewModel() {
    private val getRepo = repo()
    private val _otohatkonumList = mutableStateListOf<OtoHatKonum>()
    val otoHatkonumList: List<OtoHatKonum> = _otohatkonumList
    private val _durakDetayList = mutableStateListOf<Durak>()
    val durakDetayList : List<Durak> = _durakDetayList
    private val _hatKodu = mutableStateOf("")
    val hatKodu: State<String> = _hatKodu
    fun setOtoHatKonumList(value: List<OtoHatKonum>) {
        _otohatkonumList.addAll(value)
    }
    fun setHatKodu(value: String) {
        _hatKodu.value = value
    }
    fun getDurakDetay(value: String){
        CoroutineScope(Dispatchers.IO).launch {
            val list = getRepo.DurakDetayJSON(value)
            Log.d("test123",list.toString())
            _durakDetayList.addAll(list as Collection<Durak>)
            println(durakDetayList)
        }
    }


}