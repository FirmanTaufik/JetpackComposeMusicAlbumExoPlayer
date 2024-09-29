package firman.music.app.presentation.component

import firman.music.app.model.NavigationItems
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun DrawerItem(item :NavigationItems, index :Int, selectedItemIndex :Int , onClick : () ->Unit ){
    NavigationDrawerItem(
        label = { Text(text = item.title) },
        selected = index == selectedItemIndex,
        onClick = {
            onClick()
        },
        icon = {
            Icon(
                imageVector = if (index == selectedItemIndex) {
                    item.selectedIcon
                } else item.unselectedIcon,
                contentDescription = item.title
            )
        },
        badge = {  // Show Badge
            item.badgeCount?.let {
                Text(text = item.badgeCount.toString())
            }
        },
        modifier = Modifier
            .padding(NavigationDrawerItemDefaults.ItemPadding) //padding between items
    )

}