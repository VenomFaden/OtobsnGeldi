package com.bitnays.otobsngeldi

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bitnays.otobsngeldi.databinding.ActivityMain2Binding
import kotlinx.serialization.json.Json
private lateinit var binding: ActivityMain2Binding
var OtoHatKonumList = ArrayList<OtoHatKonum>()
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
        var intent: Intent = getIntent()
        var intentString = intent.getStringExtra("intentString")

        OtoHatKonumList = Json.decodeFromString<ArrayList<OtoHatKonum>>(intentString.toString()) as ArrayList<OtoHatKonum>
        val adapter =  OtobusAdapter(OtoHatKonumList)
        binding.recylervi.layoutManager = LinearLayoutManager(this)
        binding.recylervi.adapter = adapter
        println(OtoHatKonumList[0])

    }
}