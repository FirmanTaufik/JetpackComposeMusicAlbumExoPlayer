package firman.music.app.presentation.component

import firman.music.app.helper.ExtentionHelper.fetchImage
import firman.music.app.model.album.Entry
import firman.music.app.ui.theme.BasicColor
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage


@Composable
fun AlbumItem(entry: Entry,   onSelectAlbum: (Entry)->Unit ={}) {
    Box (
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding( 10.dp, ),){
        Card(
            modifier = Modifier
                .clickable {
                    onSelectAlbum(entry)
                }
                .fillMaxWidth()
                .wrapContentHeight(),
            elevation = CardDefaults.cardElevation(2.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .wrapContentHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = BasicColor,
                                shape = RoundedCornerShape(
                                    bottomEndPercent = 20,
                                    bottomStartPercent = 20
                                )
                            )
                            .width(100.dp)
                            .height(50.dp)
                    ) {

                    }
                    AsyncImage(
                        model = entry.content.`$t`.fetchImage(), contentDescription = null,
                        modifier = Modifier
                            .width(80.dp)
                            .height(120.dp).clip(RoundedCornerShape(  8.dp )) ,
                        contentScale = ContentScale.Crop,
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = entry.title.`$t`, color = BasicColor,
                    textAlign = TextAlign.Center
                )
            }
        }
    }


}
@Preview(showSystemUi = true)
@Composable
fun preview() {
    AlbumItem(entry = Entry())
}
