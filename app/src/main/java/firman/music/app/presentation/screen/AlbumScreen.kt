package firman.music.app.presentation.screen

import firman.music.app.core.state.ScreenState
import firman.music.app.helper.ExtentionHelper.fetchImage
import firman.music.app.model.album.Entry
import firman.music.app.presentation.component.AlbumItem
import firman.music.app.ui.theme.BasicColor
import firman.music.app.viewmodel.AlbumViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumScreen(
    viewModel: AlbumViewModel,
    drawerState: DrawerState,
    onSelectAlbum: (Entry) -> Unit,
    isShowDrawer: (Boolean) -> Unit
) {
    var text by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            modifier = Modifier.fillMaxWidth()
            ,query = text, onQueryChange = {
            text = it
            if (text.isEmpty()) {
                expanded = false
            }
            viewModel.searchList(text)
        }, onSearch = {}, active = expanded, onActiveChange = {
            expanded = it
        }, trailingIcon = {
            Icon(Icons.Filled.Search, contentDescription = null)
        }, leadingIcon = {
            Icon(Icons.Filled.Menu, contentDescription = null, modifier = Modifier.clickable {
                drawerState.apply {
                    isShowDrawer(!isClosed)
                }
            })
        },
            placeholder = { Text(text = "Cari di Sini...") }) {

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(viewModel.listSearchResult.value) {
                    ListItem(
                        headlineContent = { Text(it.title.`$t`) },
                        //                        supportingContent = { Text("Additional info") },
                        leadingContent = {
                            AsyncImage(
                                model = it.content.`$t`.fetchImage(), contentDescription = null,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(50))
                            ) /*Icon(Icons.Filled.Star, contentDescription = null)*/
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        modifier =
                        Modifier
                            .clickable {
                                text = it.title.`$t`
                                expanded = false
                                onSelectAlbum(it)
                            }
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
        when (uiState) {

            is ScreenState.OnLoading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = BasicColor)
                }
            }

            is ScreenState.OnError -> {
                val message = (uiState as ScreenState.OnError).message
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = message ?: "Terjadi Kesalahan")
                    Button(onClick = { viewModel.getData() }) {
                        Text(text = "Coba Lagi")
                    }
                }
            }

            else -> {
                val data = (uiState as ScreenState.OnSuccess).items

                Spacer(modifier = Modifier.height(10.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                ) {
                    data?.entry?.apply {
                        items(this.size) {
                            AlbumItem(data.entry?.get(it) ?: Entry(), onSelectAlbum)
                        }
                    }
                }
            }
        }
    }
}