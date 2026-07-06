package com.example.crazycam

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CrazyCamTheme {
                CameraScreen()
            }
        }
    }
}

@Composable
fun CameraScreen() {
    var processedBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Load last used camera from SharedPreferences
    val prefs = remember {
        context.getSharedPreferences("camera_prefs", Context.MODE_PRIVATE)
    }
    var lensFacing by rememberSaveable {
        mutableStateOf(
            prefs.getInt("last_lens_facing", CameraSelector.LENS_FACING_BACK)
        )
    }

    // Save camera selection when it changes
    LaunchedEffect(lensFacing) {
        prefs.edit().putInt("last_lens_facing", lensFacing).apply()
    }

    // Camera binding function
    fun bindCamera(lens: Int) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build()
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            val cameraExecutor = Executors.newSingleThreadExecutor()

            imageAnalysis.setAnalyzer(cameraExecutor, CameraProcessor { bitmap ->
                processedBitmap = bitmap
            })

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(lens)
                .build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(context))
    }

    // Rebind camera when lensFacing changes
    LaunchedEffect(lensFacing) {
        bindCamera(lensFacing)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (lensFacing == CameraSelector.LENS_FACING_BACK) "Back Camera" else "Front Camera",
                style = MaterialTheme.typography.headlineSmall
            )

            IconButton(
                onClick = {
                    lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                        CameraSelector.LENS_FACING_FRONT
                    } else {
                        CameraSelector.LENS_FACING_BACK
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Cameraswitch,
                    contentDescription = "Switch Camera"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        processedBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Processed camera frame",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
            )
        } ?: Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Text("Waiting for camera...", modifier = Modifier.align(Alignment.Center))
        }
    }
}