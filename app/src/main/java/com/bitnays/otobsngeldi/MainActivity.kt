package com.bitnays.otobsngeldi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.lifecycleScope
import com.bitnays.otobsngeldi.databinding.ActivityMainBinding
import com.bitnays.otobsngeldi.model.OtoHatKonum
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
import com.bitnays.otobsngeldi.screens.MainSearch
import com.bitnays.otobsngeldi.ui.theme.OtobüsünGeldiTheme
import com.bitnays.otobsngeldi.viewmodel.MainSearchViewModel
import com.bitnays.otobsngeldi.viewmodel.SharedViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
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
        //auth = FirebaseAuth.getInstance()
        //// Instantiate a Google sign-in request
        //val googleIdOption = GetGoogleIdOption.Builder()
        //    // Your server's client ID, not your Android client ID.
        //    .setServerClientId("931592929069-l2uqt5uqjqsio7okem89t86gqkc0i01d.apps.googleusercontent.com")
        //    // Only show accounts previously used to sign in.
        //    .setFilterByAuthorizedAccounts(false)
        //    .build()
//
        //// Create the Credential Manager request
        //val request = GetCredentialRequest.Builder()
        //    .addCredentialOption(   googleIdOption)
        //    .build()



      //val credentialManager = CredentialManager.create(this)

      //// Bu kısım coroutine içinde olmalı!
      //lifecycleScope.launch {
      //    try {
      //        // 🔽 Kullanıcıdan Google hesabı seçmesi istenir
      //        val result = credentialManager.getCredential(this@MainActivity, request)

      //        // ✅ Credential geldi → handleSignIn'e parametre olarak ver
      //        val credential = result.credential
      //        handleSignIn(credential) // ← İstediğin yer burası!

      //    } catch (e: Exception) {
      //        Log.e("aaac", "Google giriş hatası", e)
      //    }
      //}
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
    //private fun handleSignIn(credential: Credential) {
    //    // Check if credential is of type Google ID
    //    if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
    //        // Create Google ID Token
    //        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
//
    //        // Sign in to Firebase with using the token
    //        firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
    //    } else {
    //        Log.w("123", "Credential is not of type Google ID!")
    //    }
    //}
    //private fun firebaseAuthWithGoogle(idToken: String) {
    //    val credential = GoogleAuthProvider.getCredential(idToken, null)
    //    auth.signInWithCredential(credential)
    //        .addOnCompleteListener(this) { task ->
    //            if (task.isSuccessful) {
    //                // Sign in success, update UI with the signed-in user's information
    //                Log.d("aaac", "signInWithCredential:success")
    //                val user = auth.currentUser
    //                //updateUI(user)
    //            } else {
    //                // If sign in fails, display a message to the user
    //                Log.w("aaac", "signInWithCredential:failure", task.exception)
    //                //updateUI(null)
    //            }
    //        }
    //}
    override fun onResume() {
        super.onResume()

    }
    override fun onStart() {
        super.onStart()
        //val currentUser = auth.currentUser
        //updateUI(currentUser)
    }
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
        val json = Json.decodeFromString<ArrayList<OtoHatKonum>>(intentString) as ArrayList<OtoHatKonum>
        val hatkodu = json.firstOrNull()?.hatkodu
        intent.putExtra("hatkodu",hatkodu)
    }
    companion object {
        val MEDIA_TYPE_XML = "text/xml; charset=utf-8".toMediaType()
    }

}
