package com.bitnays.otobsngeldi.repo


import com.bitnays.otobsngeldi.BuildConfig
import com.bitnays.otobsngeldi.Durak
import com.bitnays.otobsngeldi.MainActivity.Companion.MEDIA_TYPE_XML
import com.bitnays.otobsngeldi.MainActivity2
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader

class repo (){
    fun DurakDetayJSON(durakkodu: String): Any?{
        val client = OkHttpClient()
        val postBody = """
                <?xml version='1.0' encoding='utf-8'?>
                    <soap:Envelope xmlns:soap='http://schemas.xmlsoap.org/soap/envelope/'>
                        <soap:Body>
                            <DurakDetay_GYY xmlns='http://tempuri.org/'>
                                <hat_kodu>${durakkodu}</hat_kodu>
                            </DurakDetay_GYY>
                        </soap:Body>
                    </soap:Envelope>
                """.trimIndent()
        val request = Request.Builder()
            .url(BuildConfig.ibb)
            .post(postBody.toRequestBody(MainActivity2.Companion.MEDIA_TYPE_XML))
            .build()
        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful){
                val responseBody = response.body?.string()
                return xmlParser2(responseBody.toString())
            }
            else{
                    return "404"
            }
        }
        catch (e: IOException) {
            return "404"
        }
    }
    fun HatOtoKonumJSON(hatkodu: String): Any?
    {
        val client = OkHttpClient()
        val postBody = """
                    <?xml version='1.0' encoding='utf-8'?> 
                    <soap:Envelope xmlns:soap='http://schemas.xmlsoap.org/soap/envelope/'>
                        <soap:Body>
                            <GetHatOtoKonum_json xmlns='http://tempuri.org/'>
                                <HatKodu>${hatkodu}</HatKodu>
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
            if(response.isSuccessful)
            {
                val responseString = response.body?.string()
                val json = xmlParser(responseString.toString())
                return json
            }
            else{
                return "404"
            }

        }catch (e: IOException){
            println("Ağ hatası: ${e.message}")
            return "404"
        }
    }
    private fun xmlParser(responseString: String): String? {
        var xmldata = responseString
        var factory = XmlPullParserFactory.newInstance()
        var parser = factory.newPullParser()
        parser.setInput(StringReader(xmldata))
        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            when(eventType){
                XmlPullParser.TEXT ->{
                    var text = parser.text
                    return text
                }
            }
            eventType = parser.next()
        }
        return ""
    }
    fun xmlParser2(text:String): ArrayList<Durak>
    {
        var  returnList: ArrayList<Durak> = ArrayList()
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
                            returnList.add(Durak(yon,siraNo,durakIsım,durakKod,XKOORDINATI,YKOORDINATI))
                        }
                    }
                }
            }
            eventType = parser.next()
        }
        return returnList
    }
    fun getLocation()
    {

    }
}