package com.bitnays.otobsngeldi.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.bitnays.otobsngeldi.model.Duyuru
import com.bitnays.otobsngeldi.model.HatSeferSaatleri
import com.bitnays.otobsngeldi.repo.repo
import kotlinx.serialization.json.Json
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader

class DuyurularViewModel: ViewModel() {
    val repo = repo()
    private val _duyurular = mutableStateListOf<Duyuru>()
    val duyurular: List<Duyuru> = _duyurular
    fun getDuyurular(context: Context){
        repo.Duyurular(
            context = context,
            onResponse ={
                _duyurular.clear()
                val duyurular = xmlParser(it)
                _duyurular.addAll(Json.decodeFromString<List<Duyuru>>(duyurular.toString()))

            },
            onError = {
                Log.d("duyurular", "hata")
            })
    }
    fun xmlParser(stringdata: String): String?
    {
        val factory = XmlPullParserFactory.newInstance().newPullParser()
        factory.setInput(StringReader(stringdata))
        var eventType = factory.eventType
        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            when(eventType)
            {
                XmlPullParser.TEXT -> {
                    val text = factory.text
                    return text
                }
            }
            eventType = factory.next()
        }
        return "null"
    }
}