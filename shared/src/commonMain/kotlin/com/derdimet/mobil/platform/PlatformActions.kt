package com.derdimet.mobil.platform

import androidx.compose.runtime.Composable

@Composable
expect fun rememberShareTextAction(): (String) -> Unit

@Composable
expect fun rememberDialPhoneAction(): (String) -> Unit

@Composable
expect fun rememberImagePickerLauncher(onImagePicked: (ByteArray, String, String) -> Unit): () -> Unit
