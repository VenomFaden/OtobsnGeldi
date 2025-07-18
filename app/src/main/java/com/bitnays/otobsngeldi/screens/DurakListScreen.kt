package com.bitnays.otobsngeldi.screens

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.SatelliteAlt
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bitnays.otobsngeldi.model.Durak
import com.bitnays.otobsngeldi.R
import com.bitnays.otobsngeldi.ui.theme.OtobüsünGeldiTheme
import com.bitnays.otobsngeldi.viewmodel.DurakListViewModel
import com.bitnays.otobsngeldi.viewmodel.SharedViewModel

enum class Direction(val short: String, val stringDirection: String){
    Gidis("G","Gidiş"),
    Donus("D","Dönüş")
}
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DurakListScreen()
{   val viewModel: DurakListViewModel = viewModel()
    val viewModelShared: SharedViewModel = viewModel()
    var permissionBoolean by remember { mutableStateOf(false) }

    GetLocationPermission(viewModel = viewModel, permissionBoolean = {permissionBoolean = it} )

    val navController = rememberNavController()
    val backButton = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val durakKodu by viewModel.hatKodu
    val durakList = viewModel.durakDetayList
    var selecttedDirectremember by remember { mutableIntStateOf(0) }
    val currentDurakList = durakList.filter {
        it.YON == Direction.entries[selecttedDirectremember].short
    }
    remember { mutableListOf("") }
    val yon1 = durakList.firstOrNull{ it.YON == Direction.entries[0].short }?.DURAKADI
    val yon2 = durakList.firstOrNull{ it.YON == Direction.entries[1].short }?.DURAKADI
    durakList.firstOrNull()?.DURAKADI
    LaunchedEffect(durakKodu) {
        viewModel.getDurakDetay(durakKodu)
    }

    OtobüsünGeldiTheme {
                NavHost(navController = navController, startDestination = "0"){
                    composable("0") {
                        Scaffold(topBar = { AppBarWithBack("Hat Durak Bilgisi",durakKodu,backButton) },
                            modifier = Modifier.navigationBarsPadding()) { innerPadding ->
                            Box(modifier = Modifier.padding(innerPadding).fillMaxWidth()) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.fillMaxWidth().padding(15.dp).wrapContentHeight()) {
                                        Box(contentAlignment = Alignment.TopStart){
                                            Column {
                                                if (permissionBoolean){
                                                    KalanDurakSayisi(viewModel.KalanDurakSayisi(currentDurakList))
                                                    EnYakinDurak(viewModel)
                                                }
                                            }
                                        }
                                        Box(contentAlignment = Alignment.TopEnd, modifier = Modifier.fillMaxWidth()){
                                            Column {
                                                    Button(onClick = {navController.navigate("1")}){
                                                        Text("Sefer Saatleri",fontSize =MaterialTheme.typography.labelSmall.fontSize)
                                                        Icon(Icons.Filled.DirectionsBus, contentDescription = "Localized description",
                                                            modifier = Modifier.size(15.dp))
                                                    }
                                                    Button(onClick = {navController.navigate("2")}){
                                                        Text("Duyurular",fontSize = MaterialTheme.typography.labelSmall.fontSize)
                                                        Icon(Icons.Filled.SatelliteAlt, contentDescription = "Localized description",
                                                            modifier = Modifier.size(15.dp))
                                                    }
                                            }
                                        }
                                    }
                                    OtobusCarousel(viewModel)
                                    SelectDirection(
                                        yon1.toString(),
                                        yon2.toString(),
                                        oncheckedChange = { selecttedDirectremember = it })
                                    DurakList(
                                        durakList.filter { it.YON == Direction.entries[selecttedDirectremember].short },
                                        viewModel
                                    )
                                }
                            }
                        }
                    }
                    composable(route = "1"){
                        viewModelShared.setHatKodu2(durakKodu)
                        SeferSaatleriScreen(viewModelShared)
                    }
                    composable(route = "2"){
                        DuyurularScreen(headerText = "Duyurular",
                            durakKodu = durakKodu,
                            onBackPressedDispatcher = backButton
                        )
                    }
                }
            }
        }
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SelectDirection(yon1: String,yon2: String, oncheckedChange: (Int) -> Unit)
{
    val options = listOf(yon1,yon2)
    var selectedIndex by remember { mutableIntStateOf(0) }
    Row(    modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center)
    {
        options.forEachIndexed { index, it ->
            ToggleButton(checked = selectedIndex == index,
                onCheckedChange = { selectedIndex = index
                                    oncheckedChange(index)
                }
            ) {
                Text(it)
            }
        }
    }
}
@Composable
private fun DurakList(list: List<Durak>,viewModel: DurakListViewModel)
{
    LazyColumn {
        items(items = list, key = { it.DURAKKODU }) { durak ->
            val (YON, SIRANO, DURAKKODU, DURAKADI, XKOORDINATI, YKOORDINATI, otobusVar) = durak
               Column {
                   Row {
                       Card(colors = CardDefaults.cardColors(
                           containerColor = MaterialTheme.colorScheme.surface,),
                           modifier = Modifier.padding(3.dp).width(50.dp),
                           border = BorderStroke(0.5.dp, Color.Gray)){
                           Column(modifier = Modifier.padding(10.dp)) {
                               Row {
                                   Text(text = SIRANO,maxLines = 1)
                               }
                           }
                       }
                       Card(colors = CardDefaults.cardColors(
                           containerColor = MaterialTheme.colorScheme.surface,),
                           modifier = Modifier.fillMaxWidth().padding(3.dp),
                           border = BorderStroke(0.5.dp, Color.Gray)){
                           Column(modifier = Modifier.padding(10.dp)) {
                               Row {
                                   if (otobusVar==true){
                                       Image(painter = painterResource(id = R.drawable.gidis), contentDescription = "gidis",
                                           modifier = Modifier
                                               .width(25.dp)
                                               .height(25.dp)
                                               .clip(CircleShape))
                                   }
                                   else{
                                       Image(colorFilter = ColorFilter.tint(Color.Black),painter = painterResource(id = R.drawable.gidis), contentDescription = "gidis",
                                           modifier = Modifier
                                               .width(25.dp)
                                               .height(25.dp)
                                               .clip(CircleShape))
                                   }

                                   Text(text = DURAKADI,maxLines = 1,)
                               }
                               if (DURAKADI == viewModel.nearDurak.value) // yakın durak
                               {
                                   Row{
                                       Box(modifier = Modifier
                                           .width(25.dp)
                                           .height(25.dp)
                                           .fillMaxWidth()
                                           .fillMaxHeight())
                                       Text(text = "Size en yakın durak",maxLines = 1,)
                                   }
                               }
                           }
                       }
                   }
               }
            }
        }
    }
@Composable
fun GetLocationPermission(viewModel: DurakListViewModel, permissionBoolean: (Boolean) -> Unit){
    val context = LocalContext.current
    var requestPermission by remember { mutableStateOf(true) } // ilk açıldığında çalışsın
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->

            permissionBoolean(isGranted)
            if (isGranted){
                viewModel.getLocation(context)
            }
            Log.d("test","Permission granted: $isGranted")
        }
    LaunchedEffect(requestPermission) {
        when{
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                viewModel.getLocation(context)
                permissionBoolean(true)
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                context as Activity, Manifest.permission.ACCESS_FINE_LOCATION) -> { requestPermission = false

                }
            else -> {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtobusCarousel(viewModel: DurakListViewModel)
{
    val stateIcons = listOf(Icons.Filled.ArrowDownward, Icons.Filled.ArrowUpward)
    val expanded = remember { mutableStateOf(false) }
    val item = remember { viewModel.otoHatkonumList }
    OutlinedCard(modifier = Modifier.clickable{expanded.value = !expanded.value}){
        Column(modifier = Modifier.padding(10.dp).fillMaxWidth()) {
            Box(modifier = Modifier.fillMaxWidth()){
                Row {
                    Text("Seferde olan otobüsler (${item.size})")
                    Icon(stateIcons[if (expanded.value) 1 else 0], contentDescription = "Localized description")
                }
            }

            AnimatedVisibility(expanded.value) {
                HorizontalMultiBrowseCarousel(state = rememberCarouselState { item.count() },
                    preferredItemWidth = 180.dp) { it->
                    val item = item[it]
                    Box(modifier = Modifier.fillMaxWidth().background(Color.Gray)
                        .height(150.dp).clickable {  }){
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Kapı no:"+item.kapino)
                            Text("Yon"+item.yon)
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun EnYakinDurak(viewModel: DurakListViewModel)
{
    Text("Size en yakın durak ${viewModel.nearDurak.value}")
}
@Composable
fun KalanDurakSayisi(int: Int?)
{
    if (int != -1)
    {
        Text("Kalan Durak Sayısı $int")
    }
}

@Preview(showBackground = true)
@Composable
fun ScreenPreview()
{
    DurakListScreen()
}