package com.bitnays.otobsngeldi

import android.os.Bundle
import android.util.Xml
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bitnays.otobsngeldi.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream
import java.io.StringReader

private val client = OkHttpClient()
private lateinit var binding: ActivityMainBinding
@Serializable
data class OtoHatKonum (val kapino: String, val boylam: String, val enlem: String, val hatkodu: String, val guzergahkodu: String, val hatad: String, val yon: String, val son_konum_zamani: String, val yakinDurakKodu: String)

class MainActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    private lateinit var binding: ActivityMainBinding
    private lateinit var HatKodu: String

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
                            //println("Başarılı: ${responseBody}")
                            //binding.stopName.text = responseBody.toString()
                            xml_parser(responseBody.toString())
                        }
                    } else {
                        // Hata durumunda UI thread üzerinde işlemek için
                        runOnUiThread {
                            println("Hata: ${response.code}")
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
    fun xml_parser(xmlString: String): String
    {
        var xml_data = xmlString
        var factory = XmlPullParserFactory.newInstance()
        var parser = factory.newPullParser()

        parser.setInput(StringReader(xml_data))
        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            var tag_name = parser.name
            when(eventType){

                XmlPullParser.START_TAG ->{
                    if(tag_name.equals("GetHatOtoKonum_jsonResult"))
                    {

                    }
                }
                XmlPullParser.TEXT ->{
                    var text = parser.text
                    println(text)
                    parseJSON(text)
                    return text
                }


            }
            eventType = parser.next()

        }
        return "null"
    }
    fun parseJSON(jsonStrings: String) {
        println("parsejson")

        jsonStrings.trimIndent()
        val OtoHatKonumList = Json.decodeFromString<List<OtoHatKonum>>(jsonStrings)
        OtoHatKonumList.forEach { oto ->
            println(oto.kapino)
            binding.textView.text = oto.toString();
        }
    }
    companion object {
        // XML için uygun media type
        val MEDIA_TYPE_XML = "text/xml; charset=utf-8".toMediaType()
    }
}
