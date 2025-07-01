package com.bitnays.otobsngeldi

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.window.core.layout.WindowSizeClass
import com.bitnays.otobsngeldi.screens.MainSearch
import com.bitnays.otobsngeldi.ui.theme.OtobüsünGeldiTheme

class MainActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    private lateinit var HatKodu: String
    private lateinit var binding: ActivityMainBinding
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
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
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
        intent.putExtra("hatkodu",HatKodu)
        startActivity(intent)
    }
    companion object {
        val MEDIA_TYPE_XML = "text/xml; charset=utf-8".toMediaType()
    }
}
