import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import model.ProductsResponseItem

@Composable
fun App() {
    MaterialTheme {

        val data = remember {
            mutableStateListOf<ProductsResponseItem>()
        }

        LaunchedEffect(Unit) {
            data.addAll(getDataFromApi())
        }


        Column(modifier = Modifier
            .background(color = Color(0xffF3F3F3))
            .fillMaxSize()
        ) {
            LazyColumn(
                state = rememberLazyListState(),
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()) {
                items(data) {
                    productItemComposable(it)
                }
            }
        }
    }
}



val client = HttpClient {
    install(ContentNegotiation) {
        json()
    }
}

@Composable
fun productItemComposable(productsResponseItem: ProductsResponseItem) {
    Card(modifier = Modifier
        .padding(horizontal = 16.dp, vertical = 4.dp)
        .clip(shape = RoundedCornerShape(12))
        , elevation = 10.dp
        , backgroundColor = Color.White
    ) {

        Row (modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            KamelImage(
                asyncPainterResource(productsResponseItem.image ?: ""),
                null,
                modifier = Modifier
                    .width(60.dp)
                    .height(60.dp)
                    .padding(start = 8.dp, end = 8.dp), contentScale = ContentScale.Fit
                ,
            )

            Column(Modifier) {
                Text(
                    productsResponseItem.title ?: "",
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                        .height(18.dp)
                    ,
                    color = Color.Black,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    productsResponseItem.description ?: "",
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                        .wrapContentWidth(),
                    color = Color.LightGray,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    fontSize = 14.sp
                )
                Text(productsResponseItem.price.toString() ?: "",
                    modifier = Modifier
                        .padding(start = 8.dp, bottom = 4.dp, top = 4.dp)
                        .wrapContentWidth(),
                    color = Color.Red,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    fontSize = 12.sp
                )
            }
        }
    }
}


suspend fun getDataFromApi() : List<ProductsResponseItem> = client
    .get("https://fakestoreapi.com/products").body<List<ProductsResponseItem>>()

expect fun getPlatformName(): String