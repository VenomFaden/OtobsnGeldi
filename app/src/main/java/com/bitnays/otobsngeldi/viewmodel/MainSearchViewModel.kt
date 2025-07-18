package com.bitnays.otobsngeldi.viewmodel

import android.content.Context
import android.util.Log

import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bitnays.otobsngeldi.repo.repo
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainSearchViewModel: ViewModel() {
    private lateinit var auth: FirebaseAuth
    private val getRepo = repo()
    private val hatOtoKonumString = MutableLiveData("")
    val liveHatOtoKonumString : LiveData<String> = hatOtoKonumString
    fun getHatOtoKonumJSON(hatkodu: String)
    {
        CoroutineScope(Dispatchers.IO).launch {
            hatOtoKonumString.postValue(getRepo.HatOtoKonumJSON(hatkodu).toString())
        }
    }
    fun CredentialRequestBuilder(context: Context){
        val googleIdOption = GetGoogleIdOption.Builder()
            // Your server's client ID, not your Android client ID.
            .setServerClientId("931592929069-l2uqt5uqjqsio7okem89t86gqkc0i01d.apps.googleusercontent.com")
            // Only show accounts previously used to sign in.
            .setFilterByAuthorizedAccounts(true)
            .build()

        // Create the Credential Manager request
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
        CreadentialManager(request,context)
    }
    fun CreadentialManager(credentialRequest: GetCredentialRequest,context: Context)
    {
        val credentialManager = CredentialManager.create(context)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = credentialManager.getCredential(
                    // Use an activity-based context to avoid undefined system UI
                    // launching behavior.
                    context = context,
                    request = credentialRequest
                )
                handleSignIn(result)
            } catch (e: GetCredentialException) {
                // Handle failure
            }
        }
    }
    private fun handleSignIn(credential: GetCredentialResponse) {
        // Check if credential is of type Google ID
        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            // Create Google ID Token
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            // Sign in to Firebase with using the token
            firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
        } else {
            Log.w("TAG", "Credential is not of type Google ID!")
        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        auth = FirebaseAuth.getInstance()
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")
                    val user = auth.currentUser

                } else {
                    // If sign in fails, display a message to the user
                }
            }
    }


}