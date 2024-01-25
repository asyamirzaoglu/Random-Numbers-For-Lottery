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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LotteryApp() {
    var bankoNumbers by remember { mutableStateOf(emptyList<Int>()) }
    var randomNumbers by remember { mutableStateOf(emptyList<Int>()) }
    var bankoInput1 by remember { mutableStateOf("") }
    var bankoInput2 by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var showBankoInputs by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Uğurlu sayı var mı? mesajı
        Text("Uğurlu sayınız var mı? Hemen banko sayı olarak belirleyin!")

        // Banko sayı giriş alanları, sadece gösterildiği zaman
        if (showBankoInputs) {
            TextField(
                value = bankoInput1,
                onValueChange = { input ->
                    // Ondalık, virgül, boşluk ve eksi işaretini temizle
                    bankoInput1 = input.filter { it.isDigit() }
                    errorMessage = "" // Hata mesajını temizle
                },
                label = { Text("Banko Sayı 1 (1-49)") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                singleLine = true
            )

            TextField(
                value = bankoInput2,
                onValueChange = { input ->
                    // Ondalık, virgül, boşluk ve eksi işaretini temizle
                    bankoInput2 = input.filter { it.isDigit() }
                    errorMessage = "" // Hata mesajını temizle
                },
                label = { Text("Banko Sayı 2 (1-49)") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                singleLine = true
            )

            // Hata mesajını gösterme
            if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Banko sayıları göster/gizle butonu
        Button(onClick = {
            showBankoInputs = !showBankoInputs
        }) {
            Text(if (showBankoInputs) "Banko Sayıları Gizle" else "Banko Sayı Belirle")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Banko sayıları ayıklama ve random sayıları üretme
            val bankoList1 = bankoInput1.split(",").mapNotNull { it.trim().toIntOrNull() }
            val bankoList2 = bankoInput2.split(",").mapNotNull { it.trim().toIntOrNull() }

            if (hasDuplicateNumbers(bankoList1 + bankoList2)) {
                errorMessage = "Aynı sayıları girmeyiniz!"
            } else if (bankoList1.any { it > 49 } || bankoList2.any { it > 49 }) {
                errorMessage = "Banko sayıları 49'dan büyük olamaz!"
            } else {
                bankoNumbers = (bankoList1 + bankoList2).filter { it in 1..49 }
                randomNumbers = generateRandomNumbers(bankoNumbers)
                focusManager.clearFocus()
            }
        }) {
            Text("Şanslı Numaraları Göster")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Banko sayıları gösterme
        if (bankoNumbers.isNotEmpty()) {
            BankoNumbers(bankoNumbers)
        }

        // Rastgele sayıları sadece belirlendiğinde göster
        if (randomNumbers.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            LotteryNumbers(randomNumbers)
        }
    }
}

fun hasDuplicateNumbers(numbers: List<Int>): Boolean {
    val uniqueNumbers = mutableSetOf<Int>()
    for (number in numbers) {
        if (!uniqueNumbers.add(number)) {
            return true
        }
    }
    return false
}

@Composable
fun BankoNumbers(bankoNumbers: List<Int>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Banko Sayılar:")
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            bankoNumbers.forEach { bankoNumber ->
                NumberChip(number = bankoNumber)
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
fun LotteryNumbers(randomNumbers: List<Int>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Şanslı Sayılar:")
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            randomNumbers.forEach { number ->
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

fun generateRandomNumbers(bankoNumbers: List<Int>): List<Int> {
    val random = Random(System.currentTimeMillis())
    val selectedNumbers = mutableListOf<Int>()

    // Banko sayıları göz önüne alarak random sayıları üretme
    while (selectedNumbers.size + bankoNumbers.size < 6) {
        val randomNumber = random.nextInt(49)+1
        if (!selectedNumbers.contains(randomNumber) && !bankoNumbers.contains(randomNumber)) {
            selectedNumbers.add(randomNumber)
        }
    }

    return (selectedNumbers + bankoNumbers).sorted()
}