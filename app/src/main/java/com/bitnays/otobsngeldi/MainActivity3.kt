package com.bitnays.otobsngeldi

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MarkEmailUnread
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.AppBarRow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
    private lateinit var hatSeferSaatleriDirectionFiltered : ArrayList<HatSeferSaatleri>
    private lateinit var hatkodu: String
    private var hatSeferSaatleriList = ArrayList<HatSeferSaatleri>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false) // Sistem çubuklarını açıkça yönet
        enableEdgeToEdge()
        hatkodu = intent.getStringExtra("hatkodu").toString()
        val context = this
        lifecycleScope.launch(Dispatchers.IO)
        {
            getSeferSaatleri(context,"251", onSuccess = {
                val parseredText = xmlParser(it)
                hatSeferSaatleriList = Json.decodeFromString<ArrayList<HatSeferSaatleri>>(parseredText.toString()) as ArrayList<HatSeferSaatleri>

                Log.d("123", hatSeferSaatleriList.get(1).HATADI.toString())
            }, onError = {
                Log.d("saaa",it.toString())
            })
        }
        setContent {
            OtobüsünGeldiTheme {
                //HelloScreen()
                SeferSaatleriScreen()
            }
        }
    }




    @Composable
    fun HelloScreen() {
        var name1 by rememberSaveable { mutableStateOf("d") }
        Log.d("main3",name1)
        HelloContent(
            name = name1,
            onNameChange = { name1 = it }
        )
    }

    @Composable
    fun HelloContent(name: String, onNameChange: (String) -> Unit) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Hello, $name",
                modifier = Modifier.padding(bottom = 8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Name") }
            )
        }
    }

fun getSeferSaatleri(context: Context, hatkodu: String ,
                     onSuccess: (String) -> Unit,
                     onError: (CronetException?) -> Unit)
{
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
    val requestBuilder = cronetEngine.newUrlRequestBuilder(
        "https://api.ibb.gov.tr/iett/UlasimAnaVeri/PlanlananSeferSaati.asmx?wsdl",
        RequestCallback(onSuccess,onError),
        executor)

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

    var Yon by rememberSaveable { mutableIntStateOf(0) }
    var Gun by rememberSaveable { mutableIntStateOf(0) }
    val deCodeGun =when(Gun) {
        0 -> "I"
        1 -> "C"
        else -> "P"
    }
    Log.d("main33","decode"+deCodeGun.toString())
    var filteredList = hatSeferSaatleriList.filter{ if(Yon == 0)  it.SYON == "G" else it.SYON == "D"} as ArrayList<HatSeferSaatleri>
    filteredList = filteredList.filter { it.SGUNTIPI == deCodeGun } as ArrayList<HatSeferSaatleri>

    Column(){
        AppBar()
        YonBUttons("gidiş","donüş", onCheckedChange = {Yon = it})
        GunSecimiTab(modifier = Modifier.padding(0.dp), onSelectedChange = {Gun = it}, filteredList)
        LazyColumn() {
            items(filteredList.count()) { index ->
                Text(text = "Item: "+filteredList.get(index).DT, color = Color(0xFFFFFFFF), fontSize = MaterialTheme.typography.bodyMedium.fontSize)
                // HorizontalDivider(thickness = 2.dp)
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
                        Text("Hat İsmi", maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = MaterialTheme.typography.labelSmall.fontSize)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
                actions = {
                    AppBarRow(
                        maxItemCount = maxItemCount,
                        overflowIndicator = {
                            IconButton(onClick = { it.show() }) {
                                Icon(
                                    imageVector = Icons.Filled.MoreVert,
                                    contentDescription = "Localized description"
                                )
                            }
                        }
                    ) {
                        clickableItem(
                            onClick = {},
                            icon = {
                                Icon(
                                    imageVector = Icons.Filled.Attachment,
                                    contentDescription = null
                                )
                            },
                            label = "Attachment"
                        )
                        clickableItem(
                            onClick = {},
                            icon = {
                                Icon(imageVector = Icons.Filled.Edit, contentDescription = null)
                            },
                            label = "Edit"
                        )
                        clickableItem(
                            onClick = {},
                            icon = {
                                Icon(imageVector = Icons.Outlined.Star, contentDescription = null)
                            },
                            label = "Favorite"
                        )
                        clickableItem(
                            onClick = {},
                            icon = {
                                Icon(imageVector = Icons.Filled.Snooze, contentDescription = null)
                            },
                            label = "Alarm"
                        )
                        clickableItem(
                            onClick = {},
                            icon = {
                                Icon(
                                    imageVector = Icons.Outlined.MarkEmailUnread,
                                    contentDescription = "Localized description"
                                )
                            },
                            label = "Email"
                        )
                    }
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

@Composable
fun ScrollContent(
    modifier: Modifier = Modifier,

    ) {
    val range = 0..1
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(range.count()) { index ->

        }
    }
}
}