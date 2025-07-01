package com.bitnays.otobsngeldi.screens

import android.graphics.drawable.Icon
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bitnays.otobsngeldi.ui.theme.OtobüsünGeldiTheme

@Composable
@Preview(showBackground = true)
fun MainSearch() {
    OtobüsünGeldiTheme {
        var favCardExpanded by remember { mutableStateOf(false) }
        var searchText = remember { mutableStateOf("") }
        var active: Boolean by remember { mutableStateOf(false) }
        Scaffold(topBar = { AppBar() }) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding).fillMaxWidth().fillMaxHeight(), contentAlignment = Alignment.Center)
            {
                Column() {
                    Search(searchText.value, onQueryChange = {searchText.value = it},active =active,  onSearch = {active = it}, onActiveChange = {active = it})
                    FavCard(onExpandedChange = {favCardExpanded = it}, expanded = favCardExpanded)
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar()
{
    TopAppBar(
        title = {
            Column {
                Text("Otobüsün Geldi", maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(10.dp))
                Text("Merhaba", maxLines = 1, overflow = TextOverflow.Ellipsis,modifier = Modifier.padding(10.dp), fontSize = MaterialTheme.typography.labelSmall.fontSize)
            }
        }, actions = {IconButton(onClick = {}) { Icon(imageVector = Icons.Outlined.Menu, contentDescription = "Localized description") }}
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Search(
    query: String,
    onQueryChange: (String) -> Unit,
    active: Boolean,
    onSearch: (Boolean) -> Unit,
    onActiveChange: (Boolean) -> Unit
) {
    SearchBar(
        trailingIcon ={ if (active) Icon(modifier = Modifier.clickable{ onQueryChange("") }, imageVector = Icons.Filled.Close, contentDescription = "Localized description")},
        leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = "Localized description")},
        placeholder = { Text(text = "Otobüs Hattı Ara") },
        query = query,

        onQueryChange = { onQueryChange(it) },
        onSearch = { onSearch(it.toBoolean())
                    println(it.toBoolean())
                   },
        active = active,
        onActiveChange = { onActiveChange(it) }
    ) {

    }
}
@Composable
fun FavCard(onExpandedChange: (Boolean) -> Unit, expanded: Boolean)
{
    OutlinedCard(modifier = Modifier.clickable{ onExpandedChange(!expanded) }.padding(10.dp), )
    {
        Row(modifier = Modifier.padding(10.dp)){Text("Daha önce aranan seferler.")
            when(expanded){
            true ->  Icon(Icons.Filled.ArrowDownward, contentDescription = "Localized description")
            false ->  Icon(Icons.Filled.ArrowUpward, contentDescription = "Localized description")
            }
        }
            AnimatedVisibility(visible = expanded,modifier = Modifier.padding(10.dp)) {
                Text("Eski:")
            }
    }
}
