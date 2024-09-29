package firman.music.app

import firman.music.app.helper.RequestNotificationPermissions
import firman.music.app.model.NavigationItems
import firman.music.app.presentation.component.DrawerItem
import firman.music.app.presentation.screen.AlbumScreen
import firman.music.app.presentation.screen.PlayerScreen
import firman.music.app.ui.theme.NotificationsTypesTheme
import firman.music.app.viewmodel.AlbumViewModel
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import firman.music.app.viewmodel.PlayerViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import firman.music.app.R

/**
 * Activity responsible for handling notifications and displaying notification-related UI.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /**
     * Called when the activity is starting.
     */
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            //Remember Clicked item state
            var selectedItemIndex by rememberSaveable {
                mutableStateOf(0)
            }

            //Remember the State of the drawer. Closed/ Opened
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

            val scope = rememberCoroutineScope()

            val items = listOf(
                NavigationItems(
                    title = "About App",
                    selectedIcon = Icons.Filled.Info,
                    unselectedIcon = Icons.Outlined.Info
                ),
                NavigationItems(
                    title = "Privacy Policy",
                    selectedIcon = ImageVector.vectorResource(id = R.drawable.ic_privacy_police),
                    unselectedIcon = ImageVector.vectorResource(id = R.drawable.ic_privacy_police)
                ),
                NavigationItems(
                    title = "Rate This App",
                    selectedIcon = Icons.Filled.Star,
                    unselectedIcon = Icons.Outlined.Star,
                    //badgeCount = 105
                ),
                NavigationItems(
                    title = "Request Lagu",
                    selectedIcon = Icons.Filled.Email,
                    unselectedIcon = Icons.Outlined.Email
                ),
                NavigationItems(
                    title = "Share App Lagu",
                    selectedIcon = Icons.Filled.Share,
                    unselectedIcon = Icons.Outlined.Share
                )
            )
            val albumViewModel = hiltViewModel<AlbumViewModel>()
            val playerViewModel = hiltViewModel<PlayerViewModel>()
            val navController = rememberNavController()
            NotificationsTypesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            ModalDrawerSheet {

                                Spacer(modifier = Modifier.height(16.dp))
                                Box(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp),
                                    contentAlignment = Alignment.Center) {
                                    Image(
                                        painter = painterResource(id = R.drawable.background_header_menu),
                                        contentDescription = null,
                                        Modifier
                                            .clip(
                                                shape = RoundedCornerShape(10.dp),
                                            )
                                            .fillMaxWidth()
                                            .height(130.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                    Text(text = resources.getString(R.string.app_name), color = Color.White,
                                        fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.height(16.dp)) //space (margin) from top
                                items.forEachIndexed { index, item ->
                                    DrawerItem(item, index, selectedItemIndex) {
                                        selectedItemIndex = index
                                        scope.launch {
                                            drawerState.close()
                                        }
                                    }
                                }
                            }
                        },
                        gesturesEnabled = true,
                        content = {
                            Scaffold {
                                NavHost(
                                    navController = navController,
                                    startDestination = ScreenA
                                ) {
                                    composable<ScreenA> {
                                        AlbumScreen(
                                            viewModel = albumViewModel,
                                            drawerState = drawerState,
                                            onSelectAlbum = { entry ->
                                                val name = entry.title.`$t`
                                                playerViewModel.setPlayList(
                                                    name, entry.content.`$t`
                                                )
                                                navController.navigate(ScreenB("1", 2))
                                            }) {
                                            scope.launch {
                                                drawerState.open()
                                            }
                                        }
                                    }
                                    composable<ScreenB> {
                                        PlayerScreen(viewModel = playerViewModel)
                                    }
                                }
                            }
                        }
                    )

                }



                RequestNotificationPermissions()
            }
        }
    }

    @Serializable
    object ScreenA

    @Serializable
    data class ScreenB(
        val name: String?,
        val age: Int
    )
}
