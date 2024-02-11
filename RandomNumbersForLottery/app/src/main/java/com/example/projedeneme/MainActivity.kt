package com.example.projedeneme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.util.Random

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
    var bankoInput1 by remember { mutableStateOf("") }
    var bankoInput2 by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var showBankoInputs by remember { mutableStateOf(false) }
    var additionalSets by remember { mutableStateOf(1) }
    var showLotteryNumbers by remember { mutableStateOf(false) }
    var shouldUpdateLotteryNumbers by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Uğurlu sayı var mı? mesajı
        Text(
            text = "Uğurlu sayınız var mı? Hemen banko sayı olarak belirleyin!",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(vertical = 32.dp).align(Alignment.CenterHorizontally)
        )
        // Banko sayı giriş alanları, sadece gösterildiği zaman
        Button(onClick = {
            showBankoInputs = !showBankoInputs
        }) {
            Text(if (showBankoInputs) "Banko Sayıları Gizle" else "Banko Sayı Belirle")
        }

        Spacer(modifier = Modifier.height(16.dp))
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
            Spacer(modifier = Modifier.height(16.dp))
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
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = errorMessage,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 4.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Kolon sayısı giriş alanı
        TextField(
            value = additionalSets.toString(),
            onValueChange = { input ->
                additionalSets = input.toIntOrNull() ?: 1
                shouldUpdateLotteryNumbers = false // Kolon sayısı değiştiğinde, butona basılmadan önce yeniden hesaplanması gerektiğini belirten bayrak
            },
            label = { Text("Kolon Sayısı") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (additionalSets > 0) {
                    showLotteryNumbers = true
                    shouldUpdateLotteryNumbers = true // Butona basıldığında şanslı numaraların güncellenmesi gerektiğini belirten bayrak
                    val bankoList1 = bankoInput1.split(",").mapNotNull { it.trim().toIntOrNull() }
                    val bankoList2 = bankoInput2.split(",").mapNotNull { it.trim().toIntOrNull() }

                    if (hasDuplicateNumbers(bankoList1 + bankoList2)) {
                        errorMessage = "Aynı sayıları girmeyiniz!"
                    } else if (bankoList1.any { it > 49 } || bankoList2.any { it > 49 }) {
                        errorMessage = "Banko sayıları 49'dan büyük olamaz!"
                    } else if (bankoList1.any { it < 1 } || bankoList2.any { it < 1 }) {
                        errorMessage = "Banko sayıları 1'den küçük olamaz!"
                    } else {
                        bankoNumbers = (bankoList1 + bankoList2).filter { it in 1..49 }
                        focusManager.clearFocus()
                    }
                }
            },
            enabled = additionalSets > 0
        ) {
            Text("Şanslı Numaraları Göster")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Banko sayıları gösterme
        if (bankoNumbers.isNotEmpty()) {
            BankoNumbers(bankoNumbers)
        }

        // Rastgele sayıları gösterme
        if (showLotteryNumbers && shouldUpdateLotteryNumbers) {
            for (i in 1..additionalSets) {
                Spacer(modifier = Modifier.height(8.dp))
                LotteryNumbers(generateRandomNumbersWithBanko(bankoNumbers, System.currentTimeMillis() + i))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
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

fun generateRandomNumbersWithBanko(bankoNumbers: List<Int>, seed: Long): List<Int> {
    val random = Random(seed)
    val selectedNumbers = mutableListOf<Int>()

    // Banko sayıları rastgele seçilen 6'lı içinde olmalı
    selectedNumbers.addAll(bankoNumbers)

    // Rastgele sayıları belirleme
    while (selectedNumbers.size < 6) {
        val randomNumber = random.nextInt(49) + 1
        if (!selectedNumbers.contains(randomNumber)) {
            selectedNumbers.add(randomNumber)
        }
    }

    return selectedNumbers.sorted()
}
