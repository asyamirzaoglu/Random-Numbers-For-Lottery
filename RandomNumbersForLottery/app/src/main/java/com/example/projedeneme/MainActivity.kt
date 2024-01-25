package com.example.projedeneme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.projedeneme.ui.theme.ProjedenemeTheme
import java.util.Random
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LotteryApp()
            }
        }
    }
@Composable
fun LotteryApp() {
    var numbers by remember { mutableStateOf(emptyList<Int>()) }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LotteryNumbers(numbers)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            numbers = generateRandomNumbers()
            focusManager.clearFocus()
        }) {
            Text("Rastgele Sayılar")
        }
    }
}

@Composable
fun LotteryNumbers(numbers: List<Int>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Seçilen Sayılar:")
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            numbers.forEach { number ->
                NumberChip(number = number)
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
fun NumberChip(number: Int) {
    Card(
        modifier = Modifier
            .width(48.dp)
            .height(48.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = number.toString())
        }
    }
}

fun generateRandomNumbers(): List<Int> {
    val random = Random(System.currentTimeMillis())
    val selectedNumbers = mutableListOf<Int>()
    while (selectedNumbers.size < 6) {
        val randomNumber = random.nextInt(50)
        if (!selectedNumbers.contains(randomNumber)) {
            selectedNumbers.add(randomNumber)
        }
    }
    return selectedNumbers.sorted()
}