package com.candra.dekatapps

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.candra.dekatapps.ui.cuaca.CuacaViewModel
import com.candra.dekatapps.ui.theme.DEKATAPPSTheme
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DEKATAPPSTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val viewModel: CuacaViewModel = viewModel(
                        factory = ViewModelFactory.getInstance(this@MainActivity)
                    )

                    Column {
                        AppsName(
                            name = "DEKAT",
                            fullname = "Dampak dan Kondisi Terkini",
                            modifier = Modifier.padding(innerPadding),
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppsName(
    name: String,
    fullname: String,
    modifier: Modifier = Modifier,
    viewModel: CuacaViewModel
) {
    val context = LocalContext.current
    val lifecycleOwner = context as LifecycleOwner
    var showDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<DataItem?>(null) }
    var isDeleting by remember { mutableStateOf(false) } // Untuk menampilkan indikator loading saat menghapus
    var isLoading by remember { mutableStateOf(false) } // State untuk loading indikator
    var dataCuacaList by remember { mutableStateOf<List<DataItem>>(emptyList()) } // Data cuaca

    // Mengamati data dari ViewModel
    LaunchedEffect(viewModel) {
        viewModel.getAllDataCuaca().observe(lifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    isLoading = true
                }

                is Result.Success -> {
                    isLoading = false
                    dataCuacaList = result.data.data
                }

                is Result.Error -> {
                    isLoading = false
                    Toast.makeText(context, "Error loading data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        textAlign = TextAlign.Start,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    text = "($fullname)",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        textAlign = TextAlign.Start,
                        fontSize = 18.sp
                    )
                )
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    text = "Dibuat oleh Candra - Universitas MDP", style = MaterialTheme.typography.bodyMedium.copy(
                        textAlign = TextAlign.Start, fontSize = 12.sp, fontWeight = FontWeight.Bold
                    )
                )
            }

            // Tombol refresh
            IconButton(
                onClick = {
                    Toast.makeText(context, "Memuat data terbaru...", Toast.LENGTH_SHORT).show()
                    viewModel.getAllDataCuaca().observe(lifecycleOwner) { result ->
                        when (result) {
                            is Result.Loading -> {
                                isLoading = true
                            }

                            is Result.Success -> {
                                isLoading = false
                                dataCuacaList = result.data.data
                            }

                            is Result.Error -> {
                                isLoading = false
                                Toast.makeText(context, "Error loading data", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        confirmButton = {
                            if (!isDeleting) { // Hanya tampilkan tombol Hapus jika tidak sedang menghapus
                                TextButton(onClick = {
                                    selectedItem?.id?.let { id ->
                                        isDeleting = true
                                        viewModel.deleteDataCuaca(id).observeForever { result ->
                                            when (result) {
                                                is Result.Loading -> {
                                                    isDeleting = true
                                                }

                                                is Result.Success -> {
                                                    // Berhasil dihapus
                                                    isDeleting = false
                                                    viewModel.getAllDataCuaca().observe(lifecycleOwner) { result2 ->
                                                        when (result2) {
                                                            is Result.Loading -> {
                                                                isLoading = true
                                                            }

                                                            is Result.Success -> {
                                                                isLoading = false
                                                                dataCuacaList = result2.data.data
                                                            }

                                                            is Result.Error -> {
                                                                isLoading = false
                                                                Toast.makeText(context, "Error loading data", Toast.LENGTH_SHORT).show()
                                                            }
                                                        }
                                                    }
                                                    showDialog = false
                                                }

                                                is Result.Error -> {
                                                    // Gagal menghapus
                                                    isDeleting = false
                                                    showDialog = false
                                                }
                                            }
                                        }
                                    }
                                }) {
                                    Text("Hapus")
                                }
                            }
                        },
                        dismissButton = {
                            if (!isDeleting) { // Hanya tampilkan tombol Batal jika tidak sedang menghapus
                                TextButton(onClick = { showDialog = false }) {
                                    Text("Batal")
                                }
                            }
                        },
                        title = { Text("Konfirmasi Hapus") },
                        text = {
                            Text(
                                if (isDeleting) "Menghapus item..."
                                else "Apakah Anda ingin menghapus item ${selectedItem?.kecamatan ?: ""}?"
                            )
                        }
                    )
                }


                Column(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(16.dp),
                        modifier = Modifier
                    ) {
                        items(dataCuacaList.size) { index ->
                            val data = dataCuacaList[index]

                            // Box untuk menambahkan rounded corner dan shadow
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(4.dp, RoundedCornerShape(16.dp)) // Shadow dan rounded corner
                                    .combinedClickable(
                                        onClick = {
                                            // Logika jika ada klik biasa
                                        },
                                        onLongClick = {
                                            // Menampilkan dialog konfirmasi
                                            selectedItem = data
                                            showDialog = true
                                        }
                                    )
                                    .padding(2.dp) // Padding untuk jarak antar item
                                    .background(
                                        MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp)
                                    ) // Rounded background
                                    .padding(16.dp) // Padding untuk konten di dalam Box
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween // Menyusun item kiri dan kanan
                                ) {
                                    // Bagian Kiri: Kecamatan dan CreatedAt
                                    Column(
                                        modifier = Modifier.weight(1f) // Memberikan ruang proporsional di sebelah kiri
                                    ) {
                                        data.kecamatan?.let {
                                            Text(
                                                text = capitalizeFirst(it),
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontSize = 16.sp, fontWeight = FontWeight.Bold
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
                                        modifier = Modifier.wrapContentWidth(Alignment.End) // Menempelkan ke sisi kanan
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
        }
    }
}


fun convertToWIB(dateString: String): String {
    val serverFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
    serverFormat.timeZone = TimeZone.getTimeZone("UTC")

    val targetFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID"))
    targetFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")

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