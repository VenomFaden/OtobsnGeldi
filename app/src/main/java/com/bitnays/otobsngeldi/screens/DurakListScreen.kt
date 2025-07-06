package com.bitnays.otobsngeldi.screens

import android.util.Log
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bitnays.otobsngeldi.Durak
import com.bitnays.otobsngeldi.OtoHatKonum
import com.bitnays.otobsngeldi.R
import com.bitnays.otobsngeldi.ui.theme.OtobüsünGeldiTheme
import com.bitnays.otobsngeldi.viewmodel.DurakListViewModel
enum class Direction(val short: String, val stringDirection: String){
    Gidis("G","Gidiş"),
    Donus("D","Dönüş")
}
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DurakListScreen()
{
    val navController = rememberNavController()
    val backButton = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val viewModel: DurakListViewModel = viewModel()
    val durakKodu by viewModel.hatKodu
    val durakList = viewModel.durakDetayList
    var selecttedDirectremember by remember { mutableIntStateOf(0) }
    var directionWay = remember { mutableListOf("") }
    val yon1 = durakList.firstOrNull{ it.YON == Direction.entries[0].short }?.DURAKADI
    val yon2 = durakList.firstOrNull{ it.YON == Direction.entries[1].short }?.DURAKADI
    durakList.firstOrNull()?.DURAKADI
    Log.d("123",directionWay[0])
    LaunchedEffect(durakKodu) {
        viewModel.getDurakDetay(durakKodu)
    }
    OtobüsünGeldiTheme {
                NavHost(navController = navController, startDestination = "0"){
                    composable("0") {
                        Scaffold(topBar = { AppBarWithBack("Hat Durak Bilgisi",durakKodu,backButton) },) { innerPadding ->
                            Box(modifier = Modifier.padding(innerPadding).fillMaxWidth()) {
                                Button(onClick = { navController.navigate("1") }) { Text("sa") }
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    OtobusCard(viewModel.otoHatkonumList.toCollection(mutableListOf()))
                                    SelectDirection(
                                        yon1.toString(),
                                        yon2.toString(),
                                        oncheckedChange = { selecttedDirectremember = it })
                                    DurakList(
                                        durakList.filter { it.YON == Direction.entries[selecttedDirectremember].short },
                                        returnFirstDurak = { directionWay = it },
                                        null
                                    )
                                    Button(onClick = { navController.navigate("1") }) { Text("sa") }
                                }
                            }
                        }
                    }
                    composable(route = "1") {
                        Button(onClick = { navController.navigate("0")}) {Text("as") }
                    }
                }
            }
        }

@Composable
fun OtobusCard(otobusSeferList: MutableList<OtoHatKonum>) {


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
private fun DurakList(list: List<Durak>, returnFirstDurak: (ArrayList<String>)-> Unit,yakınDurak: Durak?)
{
    val nearDurak: Durak
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
                                   if (DURAKADI == null) // yakın durak
                                   {
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