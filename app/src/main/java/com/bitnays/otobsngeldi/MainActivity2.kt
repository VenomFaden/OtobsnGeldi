package com.bitnays.otobsngeldi

import android.content.Intent
import android.os.Bundle
import android.util.Xml
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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

private lateinit var binding: ActivityMain2Binding
private var OtoHatKonumList = ArrayList<OtoHatKonum>()
private lateinit var  HatKodu :String
private val client = OkHttpClient()
private var durakInfoList = ArrayList<Durak>()
class MainActivity2 : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMain2Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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
                    runOnUiThread {
                        var responseBody = response.body?.string()
                        println(responseBody.toString())
                        xmlParser(responseBody.toString())
                        println(durakInfoList.get(1))
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
        var intent: Intent = getIntent()
        var intentString = intent.getStringExtra("intentString")
        HatKodu = intent.getStringExtra("hatkodu").toString()
        println(HatKodu)
        OtoHatKonumList = jsonDecode(intentString.toString())
        val adapter =  OtobusAdapter(OtoHatKonumList)
        binding.recylervi.layoutManager = LinearLayoutManager(this)
        binding.recylervi.adapter = adapter
    }
    override fun onStart() {
        super.onStart()


    }
    fun jsonDecode(text: String): ArrayList<OtoHatKonum>
    {
        return Json.decodeFromString<ArrayList<OtoHatKonum>>(text.toString()) as ArrayList<OtoHatKonum>
    }
    fun xmlParser(text:String)
    {
        var durakIsım:String = ""
        var durakKod:String = ""
        var siraNo: String = ""
        var factory = XmlPullParserFactory.newInstance()
        val parser = factory.newPullParser()
        parser.setInput(StringReader(text))
        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {

            var parserText: String = ""
            when(eventType)
            {
                XmlPullParser.START_TAG -> {
                    when (parser.name) {
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
                            durakInfoList.add(Durak(siraNo,durakIsım,durakKod))
                        }
                    }
                }
            }
            var parserName =  parser.name
            println(parserName+"  "+parserText)
            eventType = parser.next()
        }
    }
    companion object {
        val MEDIA_TYPE_XML = "text/xml; charset=utf-8".toMediaType()
    }

}