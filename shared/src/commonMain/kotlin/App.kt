import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.darkrockstudios.libraries.mpfilepicker.MultipleFilePicker
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import model.ProductsResponseItem
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE

@Composable
fun App() {
    MaterialTheme {

        val data = remember {
            mutableStateListOf<ProductsResponseItem>()
        }

        var authResponse = remember {
            mutableStateOf(AuthResponse())
        }


        LaunchedEffect(Unit) {
            authResponse.value = register("helloWorld@gmail.com", "12345678")

        }


        Column(modifier = Modifier
            .background(color = Color(0xffF3F3F3))
            .fillMaxSize()
        ) {

            Text(text = "Response"+authResponse.value)

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



//val client = HttpClient {
//    install(ContentNegotiation) {
//        json()
//    }
//}

val client = HttpClient() {
    install(ContentNegotiation) {
        json(Json {
            isLenient = true
            prettyPrint = true
            ignoreUnknownKeys = true
        })
    }
    install(Logging) {
        logger = Logger.SIMPLE
        level = LogLevel.BODY
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



private val json = Json { ignoreUnknownKeys = true }

@Serializable
data class AuthResponse(
    val idToken: String? = null,
    val email: String? = null,
    val refreshToken: String? = null,
    val expiresIn: String? = null,
    val localId: String? = null
)
val apiKey = "AIzaSyAl3oQcoVQaVsoNw6XTjligURO85X--9fU"

suspend fun register(email: String, password: String): AuthResponse {
    val response = client.post("https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=$apiKey") {
        contentType(ContentType.Application.Json)
        setBody(
            mapOf(
                "email" to email,
                "password" to password,
                "returnSecureToken" to true
            )
        )
    }
    println(response.bodyAsText())
    return json.decodeFromString(AuthResponse.serializer(), response.bodyAsText())
}

suspend fun login(email: String, password: String): AuthResponse {
    val response = client.post("https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=$apiKey") {
        contentType(ContentType.Application.Json)
        setBody(
            mapOf(
                "email" to email,
                "password" to password,
                "returnSecureToken" to true
            )
        )
    }
    return json.decodeFromString(AuthResponse.serializer(), response.bodyAsText())
}

expect fun getPlatformName(): String
