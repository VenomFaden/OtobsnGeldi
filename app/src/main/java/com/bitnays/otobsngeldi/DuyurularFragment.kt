package com.bitnays.otobsngeldi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bitnays.otobsngeldi.databinding.FragmentDuyurularBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader

class DuyurularFragment : Fragment() {
    private val client = OkHttpClient()
    private var _binding: FragmentDuyurularBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getXML()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_duyurular, container, false)
    }

    fun getXML()
    {
        CoroutineScope(Dispatchers.IO).launch {
            val postBody = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <GetDuyurular_json  xmlns="http://tempuri.org/">
                    
                </GetDuyurular_json >
              </soap:Body>
            </soap:Envelope>
            """.trimIndent()
                val request = Request.Builder()
                    .url("https://api.ibb.gov.tr/iett/UlasimDinamikVeri/Duyurular.asmx?wsdl")
                    .post(postBody.toRequestBody(MEDIA_TYPE_XML))
                    .build()
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful)
                {
                    var responseBody = response.body?.string()
                    var xmldata = responseBody
                    var factory = XmlPullParserFactory.newInstance()
                    var parser = factory.newPullParser()
                    parser.setInput(StringReader(xmldata))

                    var eventType = parser.eventType
                    while (eventType != XmlPullParser.END_DOCUMENT)
                    {
                        when(eventType){
                            XmlPullParser.TEXT ->{
                                var text = parser.text
                            }
                        }
                        eventType = parser.next()
                    }
                }
            }catch (e: Exception)
            {

            }
        }
    }

    companion object {
        val MEDIA_TYPE_XML = "text/xml; charset=utf-8".toMediaType()
    }
}