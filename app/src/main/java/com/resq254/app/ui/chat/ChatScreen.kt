package com.resq254.app.ui.chat

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.resq254.app.data.AppData
import com.resq254.app.data.ChatMessage
import com.resq254.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    alertTitle: String,
    myRole: String,                    // "reporter" | "responder"
    messages: List<ChatMessage>,
    onSendText: (String) -> Unit,
    onSendPhoto: (Uri) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentBg = MaterialTheme.colorScheme.background
    val currentSurface = MaterialTheme.colorScheme.surface
    val currentText = MaterialTheme.colorScheme.onBackground

    var draft by remember { mutableStateOf("") }
    var showAttachSheet by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    val photoPicker = rememberPhotoPickerLaunchers(onPhotoReady = onSendPhoto)

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    Column(modifier = modifier.fillMaxSize().background(currentBg)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack, "Back",
                tint = AccentGreen,
                modifier = Modifier.size(18.dp).clickable { onBack() }
            )
            Column {
                Text(alertTitle, color = currentText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    if (myRole == "reporter") "Chatting with responder" else "Chatting with reporter",
                    color = TextSecondary, fontSize = 11.sp
                )
            }
        }

        HorizontalDivider(color = BorderColor)

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (messages.isEmpty()) {
                item {
                    Text(
                        "No messages yet. Share updates or a photo of the scene.",
                        color = TextSecondary, fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                    )
                }
            }
            items(messages, key = { it.id }) { msg ->
                val isMine = msg.senderRole == myRole
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start) {
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isMine) AccentGreen else currentSurface)
                            .padding(10.dp)
                            .widthIn(max = 240.dp)
                    ) {
                        if (msg.photoUri != null) {
                            AsyncImage(
                                model = msg.photoUri,
                                contentDescription = "Scene photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(8.dp))
                            )
                            if (msg.text != null) Spacer(modifier = Modifier.height(6.dp))
                        }
                        msg.text?.let {
                            Text(it, color = if (isMine) Color.White else currentText, fontSize = 13.sp)
                        }
                        Text(
                            AppData.timeAgo(msg.timestampMs),
                            color = if (isMine) Color.White.copy(alpha = 0.7f) else TextSecondary,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = BorderColor)

        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = { showAttachSheet = true }) {
                Icon(Icons.Default.CameraAlt, "Attach photo", tint = AccentGreen)
            }
            OutlinedTextField(
                value = draft,
                onValueChange = { draft = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Message...") },
                shape = RoundedCornerShape(20.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = androidx.compose.foundation.text.KeyboardActions(onSend = {
                    if (draft.isNotBlank()) { onSendText(draft.trim()); draft = "" }
                }),
                singleLine = true
            )
            IconButton(onClick = {
                if (draft.isNotBlank()) { onSendText(draft.trim()); draft = "" }
            }) {
                Icon(Icons.AutoMirrored.Filled.Send, "Send", tint = AccentGreen)
            }
        }
    }

    if (showAttachSheet) {
        ModalBottomSheet(onDismissRequest = { showAttachSheet = false }) {
            Column(modifier = Modifier.padding(16.dp).padding(bottom = 24.dp)) {
                Text("Share a photo of the scene", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().clickable {
                        showAttachSheet = false
                        photoPicker.launchCamera()
                    }.padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.CameraAlt, null, tint = AccentGreen)
                    Text("Take a photo", fontSize = 14.sp)
                }
                Row(
                    modifier = Modifier.fillMaxWidth().clickable {
                        showAttachSheet = false
                        photoPicker.launchGallery()
                    }.padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.Image, null, tint = AccentGreen)
                    Text("Choose from gallery", fontSize = 14.sp)
                }
            }
        }
    }
}