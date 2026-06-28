package com.resq254.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.resq254.app.ui.theme.NusuruTheme
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = FirebaseFirestore.getInstance()

        val data = hashMapOf<String, Any>(
            "message" to "Nusuru connected ✅",
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("test")
            .add(data)
            .addOnSuccessListener {
                println("✅ Firestore write SUCCESS")
            }
            .addOnFailureListener { e ->
                println("❌ Firestore write FAILED $e")
            }

        setContent {
            NusuruTheme {
                Greeting("Android")
            }
        }
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NusuruTheme {
        Greeting("Android")
    }
}