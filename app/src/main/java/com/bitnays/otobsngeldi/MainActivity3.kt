package com.bitnays.otobsngeldi

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material.icons.outlined.MarkEmailUnread
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AppBarRow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.window.core.layout.WindowSizeClass
import com.bitnays.otobsngeldi.ui.theme.OtobüsünGeldiTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.chromium.net.CronetEngine
import org.chromium.net.UploadDataProvider
import org.chromium.net.UploadDataProviders
import org.chromium.net.UrlRequest
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class MainActivity3 : ComponentActivity() {
    private lateinit var hatkodu: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        hatkodu = intent.getStringExtra("hatkodu").toString()
        val context = this
        lifecycleScope.launch(Dispatchers.IO)
        {
            getSeferSaatleri(context,"251")
            xmlParser("")
        }
        setContent {
            OtobüsünGeldiTheme {
                SeferSaatleri()
            }
        }
    }
}
@Composable
fun SeferSaatleri() {
    Column(){
        AppBar()

    }
}

fun getSeferSaatleri(context: Context, hatkodu: String)
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
        RequestCallback(),
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
    Scaffold(
        topBar = {
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
        },
        content = { innerPadding ->
            LazyColumn(
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val list = (0..75).map { it.toString() }
                items(count = list.size) {
                    Text(
                        text = list[it],
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }
            }
        }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediumTopAppBarExample() {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        "Sefer Saatleri",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
    ) { innerPadding ->
        ScrollContent(innerPadding)
    }
}

@Composable
fun ScrollContent(innerPadding: PaddingValues) {
    val range = 0..1
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = innerPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(range.count()) { index ->
            Text(
                text = "- List item number ${index + 1}"//,
                //modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.onBackground)
            )
        }
    }
}

