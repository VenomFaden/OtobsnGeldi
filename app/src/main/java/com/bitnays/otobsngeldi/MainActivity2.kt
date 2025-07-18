package com.bitnays.otobsngeldi

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
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
import androidx.core.graphics.toColorInt
import android.Manifest
import android.location.Location
import android.util.Log
import android.view.View
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresPermission
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bitnays.otobsngeldi.model.Durak
import com.bitnays.otobsngeldi.model.OtoHatKonum
import com.bitnays.otobsngeldi.screens.DurakListScreen
import com.bitnays.otobsngeldi.ui.theme.OtobüsünGeldiTheme
import com.bitnays.otobsngeldi.viewmodel.DurakListViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlin.getValue
import kotlin.math.sqrt

class MainActivity2 : AppCompatActivity() {
    private val durakListViewModel : DurakListViewModel by viewModels<DurakListViewModel>()
    private lateinit var binding: ActivityMain2Binding
    private lateinit var  HatKodu :String
    private var intentString: String? = null
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
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
            var intent: Intent = getIntent()
            intentString = intent.getStringExtra("intentString")
            durakListViewModel.setOtoHatKonumList(jsonDecode(intentString.toString()))
            HatKodu = intent.getStringExtra("hatkodu").toString()
            durakListViewModel.setHatKodu(HatKodu)

        setContent {
            OtobüsünGeldiTheme{
                DurakListScreen()
            }
        }
    }
    fun jsonDecode(text: String): ArrayList<OtoHatKonum>
    {
        return Json.decodeFromString<ArrayList<OtoHatKonum>>(text.toString()) as ArrayList<OtoHatKonum>
    }
    companion object {
        val MEDIA_TYPE_XML = "text/xml; charset=utf-8".toMediaType()
    }
}