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
import kotlinx.coroutines.withContext
import okhttp3.internal.filterList

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

            _durakDetayList.clear()
            _durakDetayList.addAll(list as Collection<Durak>)

            withContext(Dispatchers.Main) {
                        val durakGyon = _durakDetayList.lastOrNull { it.YON == "G" }?.DURAKADI //
                        val durakDyon = _durakDetayList.lastOrNull { it.YON == "D" }?.DURAKADI //
                        Log.d("123",durakGyon.toString())
                        Log.d("123",durakDyon.toString())
                        otoHatkonumList.forEachIndexed { index,oto ->
                       _durakDetayList.firstOrNull {it.DURAKKODU == oto.yakinDurakKodu && oto.yon == durakGyon }?.otobusVar = true
                       _durakDetayList.firstOrNull {it.DURAKKODU == oto.yakinDurakKodu && oto.yon == durakDyon }?.otobusVar = true
                   }
                }
            }

        }
    }