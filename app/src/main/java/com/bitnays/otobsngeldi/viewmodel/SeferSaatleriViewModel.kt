package com.bitnays.otobsngeldi.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.bitnays.otobsngeldi.model.HatSeferSaatleri
import com.bitnays.otobsngeldi.repo.repo
import kotlinx.serialization.json.Json
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader

class SeferSaatleriViewModel: ViewModel() {
    val repo = repo()
    private val _seferSaatleri = mutableStateListOf<HatSeferSaatleri>()
    val seferSaatleri: List<HatSeferSaatleri> = _seferSaatleri

    fun getSeferSaatleri(hatkodu: String, context: Context) {

        repo.HatSeferSaatleri(
            hatkodu,
            context = context,
            onResponse = {
                _seferSaatleri.clear()
                val parseredText = xmlParser(it)
                _seferSaatleri.addAll(Json.decodeFromString<List<HatSeferSaatleri>>(parseredText.toString()))
            },
            onError = {

            }
        )
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