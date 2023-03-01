package com.example.composechatapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.pubnub.api.PubNub
import com.pubnub.components.DefaultDatabase
import com.pubnub.components.asPubNub
import com.pubnub.components.chat.provider.ChatProvider
import com.pubnub.components.data.Database

private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200
)

private val LightColorPalette = lightColors(
    primary = Purple500,
    primaryVariant = Purple700,
    secondary = Teal200

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun ComposeChatAppTheme(
    pubNub: PubNub,
    database: DefaultDatabase = Database.initialize(LocalContext.current),
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        //content = content
    ) {
        ChatProvider(pubNub = pubNub, database.asPubNub()) {
            content()
        }
    }
}