package id.menitaja.app.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.menitaja.app.ui.theme.Background
import id.menitaja.app.ui.theme.Border
import id.menitaja.app.ui.theme.Coffee
import id.menitaja.app.ui.theme.CoffeeDark
import id.menitaja.app.ui.theme.MenitAjaTheme
import id.menitaja.app.ui.theme.Sage
import id.menitaja.app.ui.theme.SageLight
import id.menitaja.app.ui.theme.Surface
import id.menitaja.app.ui.theme.SurfaceWarm
import id.menitaja.app.ui.theme.TextPrimary
import id.menitaja.app.ui.theme.TextSecondary

private const val MAX_TASK_LENGTH = 120

// ponytail: enum state navigation holds for MVP placeholders; upgrade to Navigation Compose when multi-screen args/backstack needed.
private enum class HomeDestination {
    HOME,
    SESSION_PLACEHOLDER,
    HISTORY_PLACEHOLDER,
    SETTINGS_PLACEHOLDER,
}

@Composable
fun MenitAjaApp() {
    var destination by rememberSaveable { mutableStateOf(HomeDestination.HOME) }
    var taskInput by rememberSaveable { mutableStateOf("") }
    var activeTask by rememberSaveable { mutableStateOf("") }

    BackHandler(enabled = destination != HomeDestination.HOME) {
        destination = HomeDestination.HOME
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background,
    ) {
        AnimatedContent(
            targetState = destination,
            transitionSpec = {
                fadeIn(tween(200)) togetherWith fadeOut(tween(180))
            },
            label = "screen_transition",
        ) { screen ->
            when (screen) {
                HomeDestination.HOME -> HomeScreen(
                    taskText = taskInput,
                    onTaskTextChange = { updated ->
                        if (updated.length <= MAX_TASK_LENGTH) {
                            taskInput = updated
                        }
                    },
                    onStartClick = {
                        val trimmed = taskInput.trim()
                        if (trimmed.isNotEmpty()) {
                            activeTask = trimmed
                            destination = HomeDestination.SESSION_PLACEHOLDER
                        }
                    },
                    onHistoryClick = { destination = HomeDestination.HISTORY_PLACEHOLDER },
                    onSettingsClick = { destination = HomeDestination.SETTINGS_PLACEHOLDER },
                )

                HomeDestination.SESSION_PLACEHOLDER -> PlaceholderScreen(
                    badge = "Sesi 5 Menit",
                    title = activeTask,
                    message = "Fokus sebentar. Nggak perlu sempurna.\n(Layar timer akan hadir di Phase 3–4)",
                    onBack = { destination = HomeDestination.HOME },
                )

                HomeDestination.HISTORY_PLACEHOLDER -> PlaceholderScreen(
                    badge = "Riwayat",
                    title = "Langkah yang Pernah Dimulai",
                    message = "Belum ada sesi. Satu langkah kecil bisa dimulai kapan saja.",
                    onBack = { destination = HomeDestination.HOME },
                )

                HomeDestination.SETTINGS_PLACEHOLDER -> PlaceholderScreen(
                    badge = "Pengaturan",
                    title = "Preferensi",
                    message = "Pengaturan getaran dan suara akan tersedia di Phase 6.",
                    onBack = { destination = HomeDestination.HOME },
                )
            }
        }
    }
}

@Composable
fun HomeScreen(
    taskText: String,
    onTaskTextChange: (String) -> Unit,
    onStartClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val isTaskValid = taskText.trim().isNotEmpty()
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .systemBarsPadding()
            .imePadding(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            HomeTopBar(
                onHistoryClick = onHistoryClick,
                onSettingsClick = onSettingsClick,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                HomeHeaderSection()

                TaskInputCard(
                    taskText = taskText,
                    onTaskTextChange = onTaskTextChange,
                    onDone = {
                        focusManager.clearFocus()
                        if (isTaskValid) onStartClick()
                    },
                )

                StartSessionButton(
                    enabled = isTaskValid,
                    onClick = {
                        focusManager.clearFocus()
                        onStartClick()
                    },
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SupportingFooter()
        }
    }
}

@Composable
private fun HomeTopBar(
    onHistoryClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            color = SageLight,
            shape = RoundedCornerShape(50),
        ) {
            Text(
                text = "5-Menit Aja",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onHistoryClick,
                modifier = Modifier
                    .size(48.dp)
                    .semantics { contentDescription = "Buka Riwayat" },
            ) {
                HistoryVectorIcon(tint = TextSecondary)
            }

            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .size(48.dp)
                    .semantics { contentDescription = "Buka Pengaturan" },
            ) {
                SettingsVectorIcon(tint = TextSecondary)
            }
        }
    }
}

@Composable
private fun HomeHeaderSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        CozyCupIllustration()

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Pelan-pelan aja.",
            color = TextPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 34.sp,
        )

        Text(
            text = "Nggak harus selesai semuanya hari ini.",
            color = TextSecondary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 24.sp,
        )
    }
}

@Composable
private fun TaskInputCard(
    taskText: String,
    onTaskTextChange: (String) -> Unit,
    onDone: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Surface,
        border = BorderStroke(1.dp, Border),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = "Mau mulai apa hari ini?",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )

            OutlinedTextField(
                value = taskText,
                onValueChange = onTaskTextChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "Contoh: Baca materi kuliah...",
                        color = TextSecondary.copy(alpha = 0.75f),
                        fontSize = 16.sp,
                        lineHeight = 22.sp,
                    )
                },
                textStyle = TextStyle(
                    color = TextPrimary,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                ),
                minLines = 2,
                maxLines = 3,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Background.copy(alpha = 0.45f),
                    unfocusedContainerColor = Background.copy(alpha = 0.25f),
                    focusedBorderColor = Coffee,
                    unfocusedBorderColor = Border,
                    cursorColor = Coffee,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                ),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onDone() },
                ),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Cukup mulai selama lima menit.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                )

                Text(
                    text = "${taskText.length}/$MAX_TASK_LENGTH",
                    color = TextSecondary,
                    fontSize = 12.sp,
                )
            }
        }
    }
}

@Composable
private fun StartSessionButton(
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.98f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "button_press_scale",
    )

    Button(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPressed) CoffeeDark else Coffee,
            contentColor = Surface,
            disabledContainerColor = SurfaceWarm,
            disabledContentColor = TextSecondary.copy(alpha = 0.7f),
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            disabledElevation = 0.dp,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .scale(scale),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 4.dp),
        ) {
            PlayVectorIcon(
                tint = if (enabled) Surface else TextSecondary.copy(alpha = 0.7f),
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Mulai 5 Menit",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun SupportingFooter() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(width = 28.dp, height = 3.dp)
                .background(color = Border, shape = CircleShape),
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Sedikit langkah tetap sebuah langkah.",
            color = TextSecondary,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp,
        )
    }
}

@Composable
private fun CozyCupIllustration(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.size(64.dp),
        shape = RoundedCornerShape(18.dp),
        color = SurfaceWarm,
        border = BorderStroke(1.dp, Border),
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
                .semantics { contentDescription = "Ilustrasi cangkir hangat" },
        ) {
            val w = size.width
            val h = size.height

            // Soft sage leaf/coaster accent at base
            drawRoundRect(
                color = Sage,
                topLeft = Offset(w * 0.14f, h * 0.84f),
                size = Size(w * 0.64f, h * 0.09f),
                cornerRadius = CornerRadius(h * 0.05f, h * 0.05f),
            )

            // Cup handle
            drawArc(
                color = Coffee,
                startAngle = -65f,
                sweepAngle = 130f,
                useCenter = false,
                topLeft = Offset(w * 0.58f, h * 0.44f),
                size = Size(w * 0.28f, h * 0.28f),
                style = Stroke(width = w * 0.08f, cap = StrokeCap.Round),
            )

            // Cup body
            val cupPath = Path().apply {
                moveTo(w * 0.18f, h * 0.40f)
                lineTo(w * 0.74f, h * 0.40f)
                lineTo(w * 0.68f, h * 0.74f)
                quadraticTo(w * 0.66f, h * 0.82f, w * 0.56f, h * 0.82f)
                lineTo(w * 0.36f, h * 0.82f)
                quadraticTo(w * 0.26f, h * 0.82f, w * 0.24f, h * 0.74f)
                close()
            }
            drawPath(path = cupPath, color = Coffee)

            // Gentle steam lines
            val steamStroke = Stroke(width = w * 0.065f, cap = StrokeCap.Round)
            val leftSteam = Path().apply {
                moveTo(w * 0.36f, h * 0.28f)
                quadraticTo(w * 0.30f, h * 0.19f, w * 0.37f, h * 0.10f)
            }
            val rightSteam = Path().apply {
                moveTo(w * 0.54f, h * 0.28f)
                quadraticTo(w * 0.60f, h * 0.19f, w * 0.53f, h * 0.10f)
            }
            drawPath(path = leftSteam, color = Sage, style = steamStroke)
            drawPath(path = rightSteam, color = TextSecondary.copy(alpha = 0.6f), style = steamStroke)
        }
    }
}

@Composable
private fun PlayVectorIcon(tint: Color) {
    Canvas(modifier = Modifier.size(16.dp)) {
        val path = Path().apply {
            moveTo(size.width * 0.22f, size.height * 0.15f)
            lineTo(size.width * 0.85f, size.height * 0.50f)
            lineTo(size.width * 0.22f, size.height * 0.85f)
            close()
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(
                width = size.width * 0.12f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
            ),
        )
        drawPath(path = path, color = tint)
    }
}

@Composable
private fun HistoryVectorIcon(tint: Color) {
    Canvas(modifier = Modifier.size(22.dp)) {
        val strokeWidth = size.width * 0.09f
        drawCircle(
            color = tint,
            radius = size.minDimension * 0.40f,
            style = Stroke(width = strokeWidth),
        )
        // Clock hands
        drawLine(
            color = tint,
            start = center,
            end = Offset(center.x, center.y - size.height * 0.20f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = center,
            end = Offset(center.x + size.width * 0.16f, center.y + size.height * 0.08f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
    }
}

@Composable
private fun SettingsVectorIcon(tint: Color) {
    Canvas(modifier = Modifier.size(22.dp)) {
        val stroke = size.width * 0.09f
        val y1 = size.height * 0.32f
        val y2 = size.height * 0.68f
        // Top slider line
        drawLine(
            color = tint,
            start = Offset(size.width * 0.16f, y1),
            end = Offset(size.width * 0.84f, y1),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        drawCircle(
            color = Background,
            radius = size.width * 0.12f,
            center = Offset(size.width * 0.62f, y1),
        )
        drawCircle(
            color = tint,
            radius = size.width * 0.12f,
            center = Offset(size.width * 0.62f, y1),
            style = Stroke(width = stroke),
        )

        // Bottom slider line
        drawLine(
            color = tint,
            start = Offset(size.width * 0.16f, y2),
            end = Offset(size.width * 0.84f, y2),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        drawCircle(
            color = Background,
            radius = size.width * 0.12f,
            center = Offset(size.width * 0.38f, y2),
        )
        drawCircle(
            color = tint,
            radius = size.width * 0.12f,
            center = Offset(size.width * 0.38f, y2),
            style = Stroke(width = stroke),
        )
    }
}

@Composable
private fun PlaceholderScreen(
    badge: String,
    title: String,
    message: String,
    onBack: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .systemBarsPadding()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = 440.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Surface,
            border = BorderStroke(1.dp, Border),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                Surface(
                    color = SageLight,
                    shape = RoundedCornerShape(50),
                ) {
                    Text(
                        text = badge,
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    )
                }

                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 28.sp,
                )

                Text(
                    text = message,
                    color = TextSecondary,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedButton(
                    onClick = onBack,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Border),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = SurfaceWarm,
                        contentColor = TextPrimary,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp),
                ) {
                    Text(
                        text = "Kembali ke Beranda",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun HomeScreenSmallPreview() {
    MenitAjaTheme {
        HomeScreen(
            taskText = "",
            onTaskTextChange = {},
            onStartClick = {},
            onHistoryClick = {},
            onSettingsClick = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 892)
@Composable
private fun HomeScreenFilledPreview() {
    MenitAjaTheme {
        HomeScreen(
            taskText = "Baca materi kuliah bab 1",
            onTaskTextChange = {},
            onStartClick = {},
            onHistoryClick = {},
            onSettingsClick = {},
        )
    }
}
