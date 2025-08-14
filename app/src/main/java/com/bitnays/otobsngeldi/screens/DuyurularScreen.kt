package com.bitnays.otobsngeldi.screens

import androidx.activity.OnBackPressedDispatcher
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bitnays.otobsngeldi.viewmodel.DuyurularViewModel

@Composable
fun DuyurularScreen(onBackPressedDispatcher: OnBackPressedDispatcher?, headerText: String, durakKodu: String)
{
    val viewModel: DuyurularViewModel = viewModel()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.getDuyurular(context)
    }
        Scaffold(topBar = { AppBarWithBack(headerText,durakKodu, onBackPressedDispatcher) }) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)){
            Column {
                CurrentHatDuyurularList(viewModel,durakKodu)
            }
        }
    }
}
@Composable
fun CurrentHatDuyurularList(viewModel: DuyurularViewModel, durakKodu: String)
{
    Text("$durakKodu hatındaki duyurular")
    LazyColumn {
        items(items = viewModel.duyurular.filter { it.HATKODU == durakKodu }, key = { it.MESAJ }){ duyuru ->
            val (HATKODU,HAT,TIP,GUNCELLEME_SAATI,MESAJ)  = duyuru
            Column(modifier = Modifier.animateItem(tween(durationMillis = 1000))) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.padding(5.dp).animateItem(tween(durationMillis = 1000)),
                    border = BorderStroke(0.5.dp, Color.Gray))
                {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("$HAT", fontSize = MaterialTheme.typography.labelMedium.fontSize)
                        Text("$MESAJ", fontSize = MaterialTheme.typography.labelMedium.fontSize)
                        Text("$GUNCELLEME_SAATI", fontSize = MaterialTheme.typography.labelMedium.fontSize)
                    }
                }
            }
        }
    }
}