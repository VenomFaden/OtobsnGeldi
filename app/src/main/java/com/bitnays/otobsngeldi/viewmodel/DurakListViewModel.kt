package com.bitnays.otobsngeldi.viewmodel

import android.Manifest
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.bitnays.otobsngeldi.model.Durak
import com.bitnays.otobsngeldi.model.OtoHatKonum
import com.bitnays.otobsngeldi.repo.repo
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.sqrt

class DurakListViewModel: ViewModel() {
    private lateinit var auth: FirebaseAuth
    private var _nearDurak = mutableStateOf("")
    val nearDurak : State<String> =  _nearDurak
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val getRepo = repo()
    private val _otohatkonumList = mutableStateListOf<OtoHatKonum>()
    val otoHatkonumList: List<OtoHatKonum> = _otohatkonumList
    private val _durakDetayList = mutableStateListOf<Durak>()
    val durakDetayList: List<Durak> = _durakDetayList
    private val _hatKodu = mutableStateOf("")
    val hatKodu: State<String> = _hatKodu
    private val _location = mutableStateOf(Location(""))
    val location: State<Location> = _location
    fun getAuth()
    {
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        Log.d("current", currentUser?.photoUrl.toString())
    }
    fun setLocation(value: Location?) {
        if (value != null) {
            _location.value = value
             Log.d("test",_location.value.toString())
        }
    }
    fun setOtoHatKonumList(value: List<OtoHatKonum>) {
        _otohatkonumList.addAll(value)
    }
    fun setHatKodu(value: String) {
        _hatKodu.value = value
    }
    fun getDurakDetay2(value: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val list = getRepo.DurakDetayJSON(value)
            _durakDetayList.clear()
            _durakDetayList.addAll(list as Collection<Durak>)
            withContext(Dispatchers.Main) {
                val durakGyon = _durakDetayList.lastOrNull { it.YON == "G" }?.DURAKADI //
                val durakDyon = _durakDetayList.lastOrNull { it.YON == "D" }?.DURAKADI //
                otoHatkonumList.forEachIndexed { index, oto ->
                    _durakDetayList.firstOrNull {
                        it.DURAKKODU == oto.yakinDurakKodu && oto.yon == durakGyon }?.otobusVar = true
                    _durakDetayList.firstOrNull {
                        it.DURAKKODU == oto.yakinDurakKodu && oto.yon == durakDyon }?.otobusVar = true
                }
                NearestDurak()
            }
        }
    }
    fun getDurakDetay(value: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val list = getRepo.DurakDetayJSON(value)
            _durakDetayList.clear()
            _durakDetayList.addAll(list as Collection<Durak>)
            withContext(Dispatchers.Main) {
                otoHatkonumList.forEachIndexed {index, oto ->
                    _durakDetayList.firstOrNull { it.DURAKKODU == oto.yakinDurakKodu && it.YON == oto.guzergahkodu.split("_").get(1)}?.otobusVar = true
                }
            }
            NearestDurak()
        }
    }
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun getLocation(context: Context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                setLocation(location)
            } else {

            }
        }
    }
    fun NearestDurak() {
        val nearest = _durakDetayList.minByOrNull { durak ->
                val x = durak.XKOORDINATI.toDouble() - location.value.longitude
                val y = durak.YKOORDINATI.toDouble() - location.value.latitude
                sqrt(x * x + y * y)
            }
        _nearDurak.value = nearest?.DURAKADI.toString()
        Log.d("test",_nearDurak.value)
    }
    fun KalanDurakSayisi(list: List<Durak>): Int?{
        val nearestDurakSiraNo =  list.firstOrNull{ it.DURAKADI == nearDurak.value }?.SIRANO?.toInt() ?: null
        val kalanDurak: Int?

        //kalanDurak = list.filter { it.otobusVar == true }.minByOrNull { it.SIRANO.toInt() - nearestDurakIndex }?.let {
        //    it.SIRANO.toInt() - nearestDurakIndex
        //}
        if (nearestDurakSiraNo != null){
            kalanDurak = list.filter {
                it.otobusVar == true && it.SIRANO.toInt() < nearestDurakSiraNo
            }.minByOrNull { abs(it.SIRANO.toInt() - nearestDurakSiraNo) }?.let {
                return  abs(it.SIRANO.toInt() - nearestDurakSiraNo)
            }
        }
        return -1
    }

}

