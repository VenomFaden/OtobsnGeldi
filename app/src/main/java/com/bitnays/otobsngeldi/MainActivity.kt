package com.bitnays.otobsngeldi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bitnays.otobsngeldi.databinding.ActivityMainBinding
import com.bitnays.otobsngeldi.model.OtoHatKonum
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import com.bitnays.otobsngeldi.screens.MainSearch
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
               MainSearch()
        }
        mainViewModel.liveHatOtoKonumString.observe(this){
            sharedViewModel.setHatOtoKonumString(it)
            if(it != "404" && it != "")
            {
                Log.d("test123","burasi m1"+it.toString())
                val hatkodu = sharedViewModel.hatKodu.value
                Log.d("test123","m"+hatkodu)
                var intent = Intent(this, MainActivity2::class.java)
                intent.putExtra("intentString", it)
                val json = Json.decodeFromString<ArrayList<OtoHatKonum>>(it) as ArrayList<OtoHatKonum>
                intent.putExtra("hatkodu",hatkodu)
                startActivity(intent)
            }
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
