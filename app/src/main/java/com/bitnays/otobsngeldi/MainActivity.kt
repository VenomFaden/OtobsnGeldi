package com.bitnays.otobsngeldi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bitnays.otobsngeldi.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import com.bitnays.otobsngeldi.screens.MainSearch
import com.bitnays.otobsngeldi.ui.theme.OtobüsünGeldiTheme
import com.bitnays.otobsngeldi.viewmodel.MainSearchViewModel
import com.bitnays.otobsngeldi.viewmodel.SharedViewModel
import kotlinx.serialization.json.Json
import kotlin.getValue

class MainActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    private lateinit var HatKodu: String
    private lateinit var binding: ActivityMainBinding

    private val mainViewModel : MainSearchViewModel by viewModels<MainSearchViewModel>()
    private val sharedViewModel: SharedViewModel by viewModels<SharedViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var intent = Intent(this, MainActivity3::class.java)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setContent {
            OtobüsünGeldiTheme{
               MainSearch()
            }
        }

        mainViewModel.liveHatOtoKonumString.observe(this){
            sharedViewModel.setHatOtoKonumString(it)
            if(it != "404" && it != "")
            {
                val hatkodu = sharedViewModel.hatKodu.value
                Log.d("test123","m"+hatkodu)
                var intent = Intent(this, MainActivity2::class.java)
                intent.putExtra("intentString", it)
                val json = Json.decodeFromString<ArrayList<OtoHatKonum>>(it) as ArrayList<OtoHatKonum>
                intent.putExtra("hatkodu",hatkodu)
                startActivity(intent)
            }
        }
        sharedViewModel.hatOtoKonumString.observe(this){ hatOtoKonumString->

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
                    .url(BuildConfig.SeferGerceklesme)
                    .post(postBody.toRequestBody(MEDIA_TYPE_XML))
                    .build()
                try {
                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        // Sonucu UI thread üzerinde işlemek için
                        var responseBody = response.body?.string()
                        runOnUiThread {
                            xmlParser(responseBody.toString())
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

    override fun onResume() {
        super.onResume()

    }
    ///@RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun xmlParser(xmlString: String)
    {
        var xmldata = xmlString
        var factory = XmlPullParserFactory.newInstance()
        var parser = factory.newPullParser()
        parser.setInput(StringReader(xmldata))

        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            when(eventType){
                XmlPullParser.TEXT ->{
                    var text = parser.text
                    Log.d("test",text)
                    sendIntentToList(text)
                }
            }
            eventType = parser.next()
        }
    }
    fun sendIntentToList(intentString: String)
    {
        var intent = Intent(this, MainActivity2::class.java)
        intent.putExtra("intentString", intentString)
        val json = Json.decodeFromString<ArrayList<OtoHatKonum>>(intentString) as ArrayList<OtoHatKonum>
        val hatkodu = json.firstOrNull()?.hatkodu
        intent.putExtra("hatkodu",hatkodu)
    }
    companion object {
        val MEDIA_TYPE_XML = "text/xml; charset=utf-8".toMediaType()
    }

}
