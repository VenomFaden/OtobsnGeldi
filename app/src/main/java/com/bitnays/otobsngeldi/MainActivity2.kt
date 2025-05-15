package com.bitnays.otobsngeldi

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bitnays.otobsngeldi.databinding.ActivityMain2Binding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import androidx.core.graphics.toColorInt
import android.Manifest
import android.location.Location
import android.util.Log
import android.view.View
import androidx.annotation.RequiresPermission
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import kotlin.math.sqrt

class MainActivity2 : AppCompatActivity() {
    private lateinit var filterArrayList: ArrayList<Durak>
    private lateinit var binding: ActivityMain2Binding
    private var OtoHatKonumList = ArrayList<OtoHatKonum>()
    private lateinit var  HatKodu :String
    private val client = OkHttpClient()
    private var durakInfoList = ArrayList<Durak>()
    private var intentString: String? = null
    private var durakInfoListD =  ArrayList<Durak>()
    private var durakInfoListG = ArrayList<Durak>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationFine: Location? = null
    private var enYakinDurak: Durak? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        val requestPermissionLauncher = registerForActivityResult(RequestPermission())
        { isGranted: Boolean ->
                if (isGranted){
                    getLocation()
                    recreate()
                }
                else{
                    setInvisible()
                    Toast.makeText(this, "Konum izni verilmedi", Toast.LENGTH_SHORT).show()
                    Log.d("test","11")
                }
        }
        binding = ActivityMain2Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        var swiperefreshlayout: SwipeRefreshLayout =  binding.refreshLayout
        swiperefreshlayout.setOnRefreshListener {
            getXML()
            //recreate()
            swiperefreshlayout.setRefreshing(false);
        }
        var intent: Intent = getIntent()
        intentString = intent.getStringExtra("intentString")
        HatKodu = intent.getStringExtra("hatkodu").toString()
        getXML()
        binding.GidisDonusSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked)
            {
                durakList(durakInfoList)
                binding.GidisDonusSwitch.thumbTintList = ColorStateList.valueOf("#F44336".toColorInt())

            }
            else {
                durakList(durakInfoList)
                binding.GidisDonusSwitch.thumbTintList = ColorStateList.valueOf("#4CAF50".toColorInt())
            }
        }
        when {
            ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                if (locationFine == null)
                {
                    getLocation()
                }
                Log.d("test","3")
            }
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) -> {
                setInvisible()
                Snackbar.make(binding.main, "Konum izni gerekli", Snackbar.LENGTH_INDEFINITE).setAction("İzin ver"){
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }.show()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)

            }
        }
    }
    override fun onStart() {
        super.onStart()
    }
    override fun onPause() {
        super.onPause()
        durakInfoList.clear()
        durakInfoListD.clear()
        durakInfoListG.clear()
    }
    fun getXML()
    {
        CoroutineScope(Dispatchers.IO).launch {
            val postBody = """
                <?xml version='1.0' encoding='utf-8'?>
                    <soap:Envelope xmlns:soap='http://schemas.xmlsoap.org/soap/envelope/'>
                        <soap:Body>
                            <DurakDetay_GYY xmlns='http://tempuri.org/'>
                                <hat_kodu>${HatKodu}</hat_kodu>
                            </DurakDetay_GYY>
                        </soap:Body>
                    </soap:Envelope>
                """.trimIndent()
            val request = Request.Builder()
                .url("https://api.ibb.gov.tr/iett/ibb/ibb.asmx?wsdl")
                .post(postBody.toRequestBody(MEDIA_TYPE_XML))
                .build()
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful){
                    var responseBody = response.body?.string()
                    runOnUiThread {

                        xmlParser(responseBody.toString())
                    }
                }
                else{
                    runOnUiThread {
                        println("Hata: ${response.code}")
                    }
                }
            }
            catch (e: IOException) {
                runOnUiThread{
                    println("Hata: ${e}")
                }
            }
        }
    }
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun getLocation()
    {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                locationFine = location
            }
    }
    fun jsonDecode(text: String): ArrayList<OtoHatKonum>
    {
        return Json.decodeFromString<ArrayList<OtoHatKonum>>(text.toString()) as ArrayList<OtoHatKonum>
    }
    fun xmlParser(text:String)
    {
        durakInfoList.clear()
        durakInfoListD.clear()
        durakInfoListG.clear()
        var durakIsım:String = ""
        var durakKod:String = ""
        var siraNo: String = ""
        var yon: String = ""
        var YKOORDINATI: String = ""
        var XKOORDINATI: String = ""
        var factory = XmlPullParserFactory.newInstance()
        val parser = factory.newPullParser()
        parser.setInput(StringReader(text))
        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when(eventType)
            {
                XmlPullParser.START_TAG -> {
                    when (parser.name) {
                        "YON"->{
                            parser.next()
                            yon = parser.text
                        }
                        "SIRANO"->{
                            parser.next()
                            siraNo = parser.text
                        }
                        "DURAKKODU"->{
                            parser.next()
                             durakIsım = parser.text
                        }
                        "DURAKADI"->{
                            parser.next()
                            durakKod = parser.text
                        }
                        "XKOORDINATI"->{
                            parser.next()
                            XKOORDINATI = parser.text
                        }
                        "YKOORDINATI"->{
                            parser.next()
                            YKOORDINATI = parser.text
                            durakInfoList.add(Durak(yon,siraNo,durakIsım,durakKod,XKOORDINATI,YKOORDINATI))
                        }
                    }
                }
            }
            eventType = parser.next()
        }
        binding.refreshLayout.visibility = View.VISIBLE
        binding.linearLayout2.visibility = View.VISIBLE
        binding.linearLayout3.visibility = View.VISIBLE
        binding.progressBar.visibility = View.INVISIBLE
        //otobusListSlicer()
        createList()
        if (binding.GidisDonusSwitch.isChecked)
        {
            durakList(durakInfoListD)
        }
        else{
            durakList(durakInfoListG)
        }
    }
    companion object {
        val MEDIA_TYPE_XML = "text/xml; charset=utf-8".toMediaType()
    }
    fun otobusListSlicer()
    {
        var durakInfoListDA = durakInfoList.filter { it.YON == "D" }
        var durakInfoListGA = durakInfoList.filter { it.YON == "G" }
        durakInfoListDA.forEach { durak -> durakInfoListD.add(durak) }
        durakInfoListGA.forEach { durak -> durakInfoListG.add(durak) }
    }
    fun createList()
    {
        OtoHatKonumList = jsonDecode(intentString.toString())
        OtoHatKonumList.forEachIndexed {i, hat ->
            val durakName = durakInfoList.find { it.DURAKKODU ==  hat.yakinDurakKodu }
            hat.yakinDurakKodu = durakName?.DURAKADI.toString()
            println(OtoHatKonumList[i])
        }
        val adapter =  OtobusAdapter(OtoHatKonumList)
        //binding.recylervi.layoutManager = LinearLayoutManager(this)
        //binding.recylervi.adapter = adapter
    }
    fun durakList(arrayList: ArrayList<Durak>)
    {

        var sonDurak = arrayList.lastOrNull()?.DURAKADI
        OtoHatKonumList.forEach { oto ->
            val durakIndex = arrayList.indexOfFirst { it.DURAKADI == oto.yakinDurakKodu && sonDurak == oto.yon.toString() }
            if(durakIndex != -1 )
            {
                arrayList[durakIndex].otobusVar = true
            }
        }
        when(binding.GidisDonusSwitch.isChecked){
            true ->{
                filterArrayList = durakInfoList.filter { it.YON == "D" } as ArrayList<Durak>
            }
            else -> {
                filterArrayList = durakInfoList.filter { it.YON == "G" } as ArrayList<Durak>
            }
        }
        if (locationFine != null)
        {
            println(locationFine?.latitude)
            enYakinDurak = findNearestLocation(filterArrayList, locationFine!!)
            binding.yakinDurak.text ="Size en yakın durak: "+ enYakinDurak?.DURAKADI.toString()
            findNearestOtobus(filterArrayList)
        }
        else{
            setInvisible()
        }

        val adapter =  DurakAdapter(filterArrayList, enYakinDurak)
        binding.recylerview2.layoutManager = LinearLayoutManager(this)
        binding.recylerview2.adapter = adapter
    }
    fun findNearestOtobus(arrayList: ArrayList<Durak>,)/*: OtoHatKonum?*/ {
        var durakFarki: Int? = 0
        val filterArrayList = arrayList.filter { it.otobusVar == true &&  (enYakinDurak?.SIRANO?.toInt() ?: 0) - (it.SIRANO?.toInt() ?: 0) > 0 }
        val nearestOtobusDurak = filterArrayList.minByOrNull { durak ->
             (enYakinDurak?.SIRANO?.toInt() ?: 0) - durak.SIRANO.toInt()
        }.also { it ->
            durakFarki = (enYakinDurak?.SIRANO?.toInt() ?: 0) - (it?.SIRANO?.toInt() ?: 0 )
        }
        println(nearestOtobusDurak.toString()+"  "+ durakFarki)
        binding.nearOtobus.text = "Size yakın otobüs "+durakFarki+" durak geride".toString()
    }
    fun findNearestLocation(arrayList: ArrayList<Durak>, fineLocation: Location): Durak? {
        return arrayList.minByOrNull { durak ->
                val x = durak.XKOORDINATI.toDouble() - fineLocation.longitude.toDouble()
                val y = durak.YKOORDINATI.toDouble() - fineLocation.latitude.toDouble()
                sqrt(x * x + y * y) // double mesafe
        }
    }
    fun setInvisible()
    {
        binding.yakinDurak.visibility = View.GONE
        binding.nearOtobus.visibility = View.GONE
    }
}