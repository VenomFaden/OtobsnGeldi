package com.bitnays.otobsngeldi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.MarkEmailUnread
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AppBarRow
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowSizeClass
import com.bitnays.otobsngeldi.ui.theme.OtobüsünGeldiTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.chromium.net.CronetEngine
import org.chromium.net.CronetException
import org.chromium.net.UploadDataProvider
import org.chromium.net.UploadDataProviders
import org.chromium.net.UrlRequest
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class MainActivity3 : ComponentActivity() {
    private lateinit var hatkodu: String
    private var hatSeferSaatleriList: (MutableList<HatSeferSaatleri>) = mutableStateListOf<HatSeferSaatleri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        hatkodu = intent.getStringExtra("hatkodu").toString()
        val context = this
        getSeferSaatleri(context,hatkodu, onResponse = {
            val parseredText = xmlParser(it)
            hatSeferSaatleriList.clear()
            hatSeferSaatleriList.addAll(Json.decodeFromString<List<HatSeferSaatleri>>(parseredText.toString()))
            }, onError = {
                Toast.makeText(this,"Hata",Toast.LENGTH_LONG).show()
            })

        setContent {
            OtobüsünGeldiTheme {
                SeferSaatleriScreen()
            }
        }
    }
fun getSeferSaatleri(context: Context, hatkodu: String ,
                     onResponse: (String) -> Unit,
                     onError: (CronetException?) -> Unit) {
    val postBody = """
        <?xml version="1.0" encoding="utf-8"?>
        <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
        <soap:Body>
        <GetPlanlananSeferSaati_json  xmlns="http://tempuri.org/">
            <HatKodu>${hatkodu}</HatKodu>
        </GetPlanlananSeferSaati_json>
        </soap:Body>
        </soap:Envelope>
    """.trimIndent()
    val myBuilder = CronetEngine.Builder(context)
    val cronetEngine: CronetEngine = myBuilder.build()
    val executor: Executor = Executors.newSingleThreadExecutor()
    val url = BuildConfig.PlanlananSeferSaatleri
    val requestBuilder = cronetEngine.newUrlRequestBuilder(
        url,
        RequestCallback(onResponse,onError), executor)
    val uploadDataProvider : UploadDataProvider = UploadDataProviders.create(postBody.toByteArray())
    requestBuilder
        .setHttpMethod("POST")
        .addHeader("Content-Type","text/xml; charset=utf-8")
        .addHeader("SOAPAction","http://tempuri.org/GetPlanlananSeferSaati_json")
        .setUploadDataProvider(uploadDataProvider,executor)
    val request: UrlRequest = requestBuilder.build()
    request.start()
}
fun xmlParser(stringdata: String): String?
{
    val factory = XmlPullParserFactory.newInstance().newPullParser()
    factory.setInput(StringReader(stringdata))
    var eventType = factory.eventType
    while (eventType != XmlPullParser.END_DOCUMENT)
    {
        when(eventType)
        {
            XmlPullParser.TEXT -> {
                val text = factory.text
                return text
            }
        }
        eventType = factory.next()
    }
    return "null"
}
@Composable
fun SeferSaatleriScreen() {

    var yon by rememberSaveable { mutableIntStateOf(0) }
    var gun by rememberSaveable { mutableIntStateOf(0) }
    val deCodeGun = when(gun) {
        0 -> "I"
        1 -> "C"
        else -> "P"
    }
    var filteredList = hatSeferSaatleriList.filter{ if(yon == 0)  it.SYON == "G" else it.SYON == "D"} as ArrayList<HatSeferSaatleri>
    filteredList = filteredList.filter { it.SGUNTIPI == deCodeGun } as ArrayList<HatSeferSaatleri>
    Column{
            AppBar()
            YonBUttons(intent.getStringExtra("donus").toString(),intent.getStringExtra("gidis").toString(), onCheckedChange = {yon = it})
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
                Column(modifier = Modifier.padding(5.dp)) {
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
fun AppBar()
{
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
                    IconButton(onClick = {  onBackPressedDispatcher.onBackPressed()   }) {
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
                onCheckedChange = { onCheckedChange(index)
                    selectedIndex = index },
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
}