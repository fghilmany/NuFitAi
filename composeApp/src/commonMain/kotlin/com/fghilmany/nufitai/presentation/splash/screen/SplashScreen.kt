package com.fghilmany.nufitai.presentation.splash.screen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fghilmany.nufitai.ui.theme.AccentBrightGreen
import com.fghilmany.nufitai.ui.theme.AccentSoftGreen
import com.fghilmany.nufitai.ui.theme.DecorativeGradient1
import com.fghilmany.nufitai.ui.theme.PrimaryDark
import com.fghilmany.nufitai.ui.theme.PrimaryLight
import com.fghilmany.nufitai.ui.theme.PrimaryMuted
import com.fghilmany.nufitai.ui.theme.SurfaceWhite
import com.fghilmany.nufitai.ui.theme.TextMuted
import com.fghilmany.nufitai.presentation.splash.viewmodel.SplashState
import com.fghilmany.nufitai.presentation.splash.viewmodel.SplashViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit,
    viewModel: SplashViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    // Navigate when splash is ready
    LaunchedEffect(state) {
        if (state is SplashState.Ready) {
            onSplashComplete()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "splash")

    // Animated dots
    val dot1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "dot1",
    )
    val dot2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing, delayMillis = 200),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "dot2",
    )
    val dot3Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing, delayMillis = 400),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "dot3",
    )
    val dot4Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing, delayMillis = 600),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "dot4",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceWhite),
    ) {
        // Decorative blur elements
        Box(
            modifier = Modifier
                .size(256.dp)
                .offset(x = (-96).dp, y = (-96).dp)
                .blur(32.dp)
                .background(DecorativeGradient1, CircleShape),
        )
        Box(
            modifier = Modifier
                .size(256.dp)
                .offset(x = 134.dp, y = 628.dp)
                .blur(32.dp)
                .background(DecorativeGradient1, CircleShape),
        )

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Logo placeholder (128x128, rounded 24px)
            Box(
                modifier = Modifier
                    .size(128.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurfaceWhite),
                contentAlignment = Alignment.Center,
            ) {
                // TODO: Replace with actual NuFit AI logo resource
                Text(
                    text = "🏋️",
                    fontSize = 48.sp,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // App name
            Text(
                text = "NuFit AI",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryDark,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline
            Text(
                text = "Personal trainer di kantongmu",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = PrimaryLight.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
            )
        }

        // Bottom loading indicator
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 64.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Animated dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .graphicsLayer { alpha = dot1Alpha }
                        .clip(CircleShape)
                        .background(AccentSoftGreen),
                )
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .graphicsLayer { alpha = dot2Alpha }
                        .clip(CircleShape)
                        .background(PrimaryDark),
                )
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .graphicsLayer { alpha = dot3Alpha }
                        .clip(CircleShape)
                        .background(PrimaryMuted),
                )
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .graphicsLayer { alpha = dot4Alpha }
                        .clip(CircleShape)
                        .background(AccentBrightGreen),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Loading text
            Text(
                text = "MEMULAI PERJALANANMU",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.2.sp,
                color = TextMuted.copy(alpha = 0.4f),
                textAlign = TextAlign.Center,
            )
        }
    }
}
