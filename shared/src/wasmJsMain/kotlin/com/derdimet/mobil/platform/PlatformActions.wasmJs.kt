package com.derdimet.mobil.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberShareTextAction(): (String) -> Unit = remember { { } }

@Composable
actual fun rememberDialPhoneAction(): (String) -> Unit = remember { { } }

@Composable
actual fun rememberImagePickerLauncher(onImagePicked: (ByteArray, String, String) -> Unit): () -> Unit =
    remember { { } }
