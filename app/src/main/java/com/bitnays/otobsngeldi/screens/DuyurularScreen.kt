package com.bitnays.otobsngeldi.screens

import androidx.activity.OnBackPressedDispatcher
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DuyurularScreen(onBackPressedDispatcher: OnBackPressedDispatcher?, headerText: String, durakKodu: String)
{
    Scaffold(topBar = { AppBarWithBack(headerText,durakKodu, onBackPressedDispatcher) }) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding))
        {

        }
    }
}