package com.bitnays.otobsngeldi.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bitnays.otobsngeldi.ui.theme.OtobüsünGeldiTheme
import com.bitnays.otobsngeldi.viewmodel.MainSearchViewModel
import com.bitnays.otobsngeldi.viewmodel.SharedViewModel


@Composable
@Preview(showBackground = true)
fun MainSearch() {
    val viewModel: MainSearchViewModel = viewModel()
    val sharedViewModel : SharedViewModel = viewModel()
    val SearchBarBoxPadding = remember { mutableStateOf(25.dp) }
    OtobüsünGeldiTheme {
        var favCardExpanded by remember { mutableStateOf(false) }
        var searchText = remember { mutableStateOf("") }
        var active: Boolean by remember { mutableStateOf(false) }
        Scaffold(topBar = { AppBar("Otobüsün Geldi","Merhaba") }) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding).fillMaxWidth().fillMaxHeight(), contentAlignment = Alignment.Center)
            {
                Column(){
                    Box(modifier = Modifier.padding(SearchBarBoxPadding.value).onFocusChanged{
                        focusState ->
                        if (focusState.isFocused){
                            SearchBarBoxPadding.value = 0.dp

                        }
                        else{
                            SearchBarBoxPadding.value = 25.dp
                        }
                    }){
                        Search(searchText.value, onQueryChange = {searchText.value = it},active =active,  onSearch = {active = it}, onActiveChange = {active = it},viewModel,sharedViewModel)
                    }
                    FavCard(onExpandedChange = {favCardExpanded = it}, expanded = favCardExpanded)
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Search(
    query: String,
    onQueryChange: (String) -> Unit,
    active: Boolean,
    onSearch: (Boolean) -> Unit,
    onActiveChange: (Boolean) -> Unit,
    viewModel: MainSearchViewModel,
    sharedViewModel: SharedViewModel
) {
    val context = LocalContext.current
    SearchBar(
        trailingIcon ={ if (active) Icon(modifier = Modifier.clickable{ onQueryChange("") }, imageVector = Icons.Filled.Close, contentDescription = "Localized description")},
        leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = "Localized description")},
        placeholder = { Text(text = "Otobüs Hattı Ara") },
        query = query,
        onQueryChange = { onQueryChange(it) },
        onSearch = {
            onSearch(it.toBoolean())
            viewModel.getHatOtoKonumJSON(it)

            viewModel.liveHatOtoKonumString.observeForever { it
                if (it=="404"){
                    Toast.makeText(context, "Böyle bir otobüs yok", Toast.LENGTH_LONG).show()
                }
            }
            sharedViewModel.setHatOtoKonumString(viewModel.liveHatOtoKonumString.value)
            sharedViewModel.setHatKodu(it)
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
