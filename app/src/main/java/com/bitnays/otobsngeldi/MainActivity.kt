package com.bitnays.otobsngeldi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bitnays.otobsngeldi.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader

private val client = OkHttpClient()
private lateinit var binding: ActivityMainBinding
private lateinit var HatKodu: String
private lateinit var fusedLocationClient: FusedLocationProviderClient

class MainActivity : AppCompatActivity() {
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your app.
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.button.setOnClickListener {
            HatKodu = binding.editTextText.text.toString()
            CoroutineScope(Dispatchers.IO).launch {
                val postBody = """
                    <?xml version='1.0' encoding='utf-8'?> 
                    <soap:Envelope xmlns:soap='http://schemas.xmlsoap.org/soap/envelope/'>
                        <soap:Body>
                            <GetHatOtoKonum_json xmlns='http://tempuri.org/'>
                                <HatKodu>${HatKodu}</HatKodu>
                            </GetHatOtoKonum_json>
                        </soap:Body>
                    </soap:Envelope>
                """.trimIndent()
                val request = Request.Builder()
                    .url("https://api.ibb.gov.tr/iett/FiloDurum/SeferGerceklesme.asmx?wsdl")
                    .post(postBody.toRequestBody(MEDIA_TYPE_XML))
                    .build()
                try {
                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        // Sonucu UI thread üzerinde işlemek için
                        runOnUiThread {
                            var responseBody = response.body?.string()
                            println("Başarılı: ${responseBody}")
                            xml_parser(responseBody.toString())
                        }
                    } else {
                        // Hata durumunda UI thread üzerinde işlemek için
                        runOnUiThread {
                            //println("Hata: ${response.code}")
                            Toast.makeText(applicationContext, "Böyle bir otobüs yok HATA: ${response.code}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: IOException) {
                    // Hata durumunda UI thread üzerinde işlemek için
                    runOnUiThread {
                        println("Ağ hatası: ${e.message}")
                        Toast.makeText(applicationContext, "Ağ hatası HATA: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onStart() {
        super.onStart()
        getLocationPermission(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                Log.d("location", location.toString())
            }
    }
    fun getLocationPermission(view: View)
    {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("permission", "1")
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.ACCESS_FINE_LOCATION) -> {
                Snackbar.make(view, "Permission is needed", Snackbar.LENGTH_INDEFINITE).setAction(
                    "İzin ver", View.OnClickListener {
                        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                ).show()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                Log.d("permission", "3")
            }
        }
    }
    fun xml_parser(xmlString: String)
    {
        var xml_data = xmlString
        var factory = XmlPullParserFactory.newInstance()
        var parser = factory.newPullParser()
        parser.setInput(StringReader(xml_data))

        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            //println("saa"+parser.columnNumber+" "+parser.text)
            when(eventType){

                XmlPullParser.TEXT ->{
                    var text = parser.text
                    sendIntentToList(text)
                }

            }
            eventType = parser.next()
        }
    }
    fun sendIntentToList(intentString: String)
    {
        var intent: Intent = Intent(this, MainActivity2::class.java)
        intent.putExtra("intentString", intentString)
        intent.putExtra("hatkodu",HatKodu)
        startActivity(intent)
    }
    companion object {
        val MEDIA_TYPE_XML = "text/xml; charset=utf-8".toMediaType()
    }
}
