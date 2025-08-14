package com.bitnays.otobsngeldi.screens

import android.util.Log
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowSizeClass
import com.bitnays.otobsngeldi.model.HatSeferSaatleri
import com.bitnays.otobsngeldi.viewmodel.DurakListViewModel
import com.bitnays.otobsngeldi.viewmodel.SeferSaatleriViewModel
import com.bitnays.otobsngeldi.viewmodel.SharedViewModel
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
fun SeferSaatleriScreen(viewModeShared: SharedViewModel) {
    val seferSaatleriViewModel: SeferSaatleriViewModel = viewModel()
    val hatkodu by viewModeShared.HatKodu
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        seferSaatleriViewModel.getSeferSaatleri(hatkodu,context)
    }
    var yon by rememberSaveable { mutableIntStateOf(0) }
    var gun by rememberSaveable { mutableIntStateOf(0) }

    val deCodeGun = when(gun) {
        0 -> "I"
        1 -> "C"
        else -> "P"
    }
    var filteredList = seferSaatleriViewModel.seferSaatleri.filter{ if(yon == 0)  it.SYON == "G" else it.SYON == "D"} as ArrayList<HatSeferSaatleri>
    filteredList = filteredList.filter { it.SGUNTIPI == deCodeGun } as ArrayList<HatSeferSaatleri>
    Column{
        AppBar(hatkodu)
        YonBUttons("donus","gidis", onCheckedChange = {yon = it})
        GunSecimiTab(modifier = Modifier.padding(0.dp), onSelectedChange = {gun = it}, filteredList)
        SeferSaatleriList(filteredList)
    }
}
@Composable
fun SeferSaatleriList(list: ArrayList<HatSeferSaatleri>) {
    val groupedList = list.groupBy { it.DT?.split(":")?.get(0) }
    LazyColumn(modifier = Modifier.navigationBarsPadding()){
        groupedList.forEach { (hour, itemsInHour) ->
            item {
                Row(modifier = Modifier.padding(5.dp)) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        ),
                    ){
                        Text(
                            text = "${hour?.split(":")?.get(0)?: "Bilinmeyen Saat"}:00",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(8.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(itemsInHour) { item ->
                            OutlinedCard(
                                colors = CardDefaults.cardColors(containerColor =  MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                            ) {
                                Text(
                                    text = item.DT?.split(":")?.get(1) ?: "Boş",
                                    modifier = Modifier.padding(8.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(hatkodu: String)
{    val backButton = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val sizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val maxItemCount =
        if (sizeClass.minWidthDp >= WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) {
            5
        } else {
            3
        }
    TopAppBar(
        title = {
            Column {
                Text("Sefer Saatleri", maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(hatkodu, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = MaterialTheme.typography.labelSmall.fontSize)
            }
        },
        navigationIcon = {
            IconButton(onClick = { backButton?.onBackPressed() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Localized description"
                )
            }
        },
        actions = {

        }
    )
}
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun YonBUttons(yon1: String, yon2: String, onCheckedChange: (Int) -> Unit)
{
    val options = listOf(yon1, yon2)
    val unCheckedIcons = listOf(Icons.Outlined.ArrowUpward, Icons.Outlined.ArrowDownward)
    val checkedIcons = listOf(Icons.Filled.ArrowUpward, Icons.Filled.ArrowDownward)
    var selectedIndex by remember { mutableIntStateOf(0) }
    Row(
        Modifier.padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
    ) {
        val modifiers = listOf(Modifier.weight(0.5f), Modifier.weight(0.5f))
        options.forEachIndexed { index, label ->
            ToggleButton(
                checked = selectedIndex == index,
                onCheckedChange = {
                    onCheckedChange(index)
                    selectedIndex = index
                },
                modifier = modifiers[index].semantics { role = Role.RadioButton },
                shapes =
                    when (index) {
                        0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                        options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                        else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                    }
            ) {
                Icon(
                    if (selectedIndex == index) checkedIcons[index] else unCheckedIcons[index],
                    contentDescription = "Localized description"
                )
                Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                Text(label)
            }
        }
    }
}
enum class Destination(val route: String, val label: String) {
    I("Ic", "Hafta İçi"),
    C("cumartesi", "Cumartesi"),
    P("pazar", "Pazar")
}
@Composable
fun GunSecimiTab(modifier: Modifier, onSelectedChange: (Int) -> Unit, arrayList: ArrayList<HatSeferSaatleri>) {
    val navController = rememberNavController()
    val startDestination = Destination.I
    var selectedDestination by remember { mutableIntStateOf(startDestination.ordinal) }
    Log.d("main3",selectedDestination.toString())
    Column {
        PrimaryTabRow(selectedTabIndex = selectedDestination, modifier = Modifier.padding()) {
            Destination.entries.forEachIndexed { index, destination ->
                Tab(
                    selected = selectedDestination == index,
                    onClick = {
                        selectedDestination = index
                        onSelectedChange(index)
                    },
                    text = {
                        Text(
                            text = destination.label,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
        }
    }
}
