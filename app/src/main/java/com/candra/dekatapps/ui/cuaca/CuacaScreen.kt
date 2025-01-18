package com.candra.dekatapps.ui.cuaca

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.candra.dekatapps.data.common.Result
import com.candra.dekatapps.data.response.DataItem
import com.candra.dekatapps.data.vmf.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Composable
fun CuacaScreen(
    viewModel: CuacaViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    ),
    lifecycleOwner: LifecycleOwner
) {
    var dataCuacaList by remember {
        mutableStateOf<List<DataItem>>(emptyList())
    }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.getAllDataCuaca().observe(lifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    isLoading = true // Set loading state to true
                }
                is Result.Success -> {
                    dataCuacaList = result.data.data
                    isLoading = false // Set loading state to false
                    Log.d("CuacaScreen", "Data Cuaca List : $dataCuacaList")
                }
                is Result.Error -> {
                    isLoading = false // Set loading state to false on error
                    // Handle error if needed
                }
            }
        }
    }

    // Gunakan Box untuk menempatkan CircularProgressIndicator di tengah
    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            CuacaList(listCuaca = dataCuacaList)
        }
    }
}

@Composable
fun CuacaList(listCuaca: List<DataItem>) {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier
        ) {
            items(listCuaca.size) { index ->
                val data = listCuaca[index]

                // Box untuk menambahkan rounded corner dan shadow
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp) // Padding untuk jarak antar item
                        .shadow(4.dp, RoundedCornerShape(16.dp)) // Shadow dan rounded corner
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp)) // Rounded background
                        .padding(16.dp) // Padding untuk konten di dalam Box
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween // Menyusun item kiri dan kanan
                    ) {
                        // Bagian Kiri: Kecamatan dan CreatedAt
                        Column(
                            modifier = Modifier
                                .weight(1f) // Memberikan ruang proporsional di sebelah kiri
                        ) {
                            data.kecamatan?.let {
                                Text(
                                    text = capitalizeFirst(it),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    textAlign = TextAlign.Start,
                                )
                            }
                            data.createdAt?.let {
                                Text(
                                    text = convertToWIB(it),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 16.sp
                                    ),
                                    textAlign = TextAlign.Start
                                )
                            }
                        }

                        // Bagian Kanan: Kondisi dan Dampak
                        Column(
                            modifier = Modifier
                                .wrapContentWidth(Alignment.End) // Menempelkan ke sisi kanan
                        ) {
                            data.kondisi?.let {
                                Text(
                                    text = capitalizeFirst(it),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 16.sp
                                    ),
                                    textAlign = TextAlign.End
                                )
                            }
                            data.dampak?.let {
                                Text(
                                    text = capitalizeFirst(it),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 16.sp
                                    ),
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}



fun convertToWIB(dateString: String): String {
    // Format asli dari server
    val serverFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
    serverFormat.timeZone = TimeZone.getTimeZone("UTC")

    // Format tujuan
    val targetFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID"))
    targetFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")

    // Parse dan format ulang
    return try {
        val date = serverFormat.parse(dateString)
        date?.let { targetFormat.format(it) } ?: "Invalid Date"
    } catch (e: Exception) {
        "Invalid Date"
    }
}

fun capitalizeFirst(input: String): String {
    return input.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}
