package com.bitnays.otobsngeldi.screens

import androidx.activity.OnBackPressedDispatcher
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(headerText: String, subHeaderText: String)
{
    TopAppBar(
        title = {
            Column {
                Text(text = headerText, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(10.dp))
                Text(text = subHeaderText, maxLines = 1, overflow = TextOverflow.Ellipsis,modifier = Modifier.padding(10.dp), fontSize = MaterialTheme.typography.labelSmall.fontSize)
            }
        }, actions = {IconButton(onClick = {}) { Icon(imageVector = Icons.Outlined.Menu, contentDescription = "Localized description") }}
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBarWithBack(
    headerText: String, subHeaderText: String, onBackPressedDispatcher: OnBackPressedDispatcher?
)
{
    TopAppBar(
        title = {
            Column {
                Text(text = headerText, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(10.dp))
                Text(text = subHeaderText, maxLines = 1, overflow = TextOverflow.Ellipsis,modifier = Modifier.padding(10.dp), fontSize = MaterialTheme.typography.labelSmall.fontSize)
            }
        }, actions = {IconButton(onClick = {}) { Icon(imageVector = Icons.Outlined.Menu, contentDescription = "Localized description") }},                navigationIcon = {
            IconButton(onClick = { onBackPressedDispatcher?.onBackPressed() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Localized description"
                )
            }
        }
    )
}