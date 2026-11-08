package id.menitaja.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.Modifier
import id.menitaja.app.ui.home.MenitAjaApp
import id.menitaja.app.ui.theme.MenitAjaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            MenitAjaTheme {
                MenitAjaApp()
            }
        }
    }
}
