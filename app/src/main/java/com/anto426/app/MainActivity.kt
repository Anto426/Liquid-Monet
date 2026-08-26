package com.anto426.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import com.anto426.liquidmonet.components.navigation.LiquidTopBar
import com.anto426.liquidmonet.components.buttons.LiquidFloatingActionButton
import com.anto426.liquidmonet.components.navigation.LiquidNavigationBar
import com.anto426.liquidmonet.components.navigation.LiquidNavigationItem
import com.anto426.liquidmonet.components.inputs.LiquidSearchBar
import com.anto426.liquidmonet.components.navigation.LiquidTopBarAction
import com.anto426.liquidmonet.components.feedback.LiquidToastHost
import com.anto426.liquidmonet.components.feedback.LiquidToastType
import com.anto426.liquidmonet.components.feedback.rememberLiquidToastState
import com.anto426.liquidmonet.glass.LiquidBackgroundEffect
import com.anto426.liquidmonet.glass.LiquidGlassScene
import com.anto426.liquidmonet.glass.LiquidBackground
import com.anto426.liquidmonet.icons.LiquidIcons
import com.anto426.liquidmonet.motion.LiquidAnimatedNavContent
import com.anto426.liquidmonet.motion.LiquidNavTransition
import com.anto426.liquidmonet.theme.LiquidMonetTheme
import com.anto426.liquidmonet.theme.monet.LiquidMonetPresets
import com.anto426.liquidmonet.theme.monet.LiquidMonetSeed
import com.anto426.app.screens.ControlsInputHubScreen
import com.anto426.app.screens.ModalsFeedbackHubScreen
import com.anto426.app.screens.NavigationHubScreen
import com.anto426.app.screens.StudioHubScreen

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var selectedPresetIndex by remember { mutableIntStateOf(0) }
            var sliderVal by remember { mutableFloatStateOf(0.65f) }
            val currentSeed: LiquidMonetSeed = when (selectedPresetIndex) {
                0 -> LiquidMonetPresets.Sapphire
                1 -> LiquidMonetPresets.Emerald
                2 -> LiquidMonetPresets.Sunset
                else -> LiquidMonetPresets.Violet
            }

            LiquidMonetTheme(
                useMonetEngine = true,
                customMonetSeed = currentSeed,
                liquidIntensity = sliderVal
            ) {
                val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

                var selectedEffectIndex by remember { mutableIntStateOf(0) }
                val backgroundEffects = listOf(
                    LiquidBackgroundEffect.RadiantBeam,
                    LiquidBackgroundEffect.Aurora,
                    LiquidBackgroundEffect.MeshGlow,
                    LiquidBackgroundEffect.OrbitalPulse
                )

                val toastState = rememberLiquidToastState()
                var isSearchOpen by remember { mutableStateOf(false) }
                var searchQuery by remember { mutableStateOf("") }
                var isNavBarVisible by remember { mutableStateOf(true) }
                var currentTab by remember { mutableIntStateOf(0) }

                // Ogni categoria parte dalla propria intestazione espansa:
                // evita di ereditare lo stato collassato della categoria precedente.
                LaunchedEffect(currentTab) {
                    scrollBehavior.state.heightOffset = 0f
                    scrollBehavior.state.contentOffset = 0f
                }

                val navBarScrollConnection = remember {
                    object : NestedScrollConnection {
                        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                            if (available.y < -12f) {
                                isNavBarVisible = false
                            } else if (available.y > 12f) {
                                isNavBarVisible = true
                            }
                            return Offset.Zero
                        }
                    }
                }

                // La demo è organizzata in quattro aree principali; ogni hub
                // contiene le proprie sotto-schermate e non duplica la shell.
                val pageTitles = listOf("Studio", "Controlli", "Feedback", "Navigazione")
                val searchBarInset by animateDpAsState(
                    targetValue = if (isSearchOpen) 68.dp else 0.dp,
                    animationSpec = spring(dampingRatio = 0.75f, stiffness = 380f),
                    label = "demoTopBarSearchInset"
                )

                LiquidGlassScene(
                    modifier = Modifier.fillMaxSize(),
                    background = {
                        LiquidBackground(
                            effect = backgroundEffects[selectedEffectIndex],
                            monetSeed = currentSeed,
                            intensity = sliderVal
                        )
                    },
                    topBar = { contentBackdrop ->
                        LiquidTopBar(
                            title = pageTitles[currentTab],
                            subtitle = "Demo catalog",
                            backdropState = contentBackdrop,
                            scrollBehavior = scrollBehavior,
                            isSearchActive = isSearchOpen,
                            searchQuery = searchQuery,
                            onQueryChange = { searchQuery = it },
                            onSearchActiveChange = { isSearchOpen = it },
                            searchPlaceholder = "Cerca componenti, controlli, gesture...",
                            actionItems = listOf(
                                LiquidTopBarAction(
                                    icon = LiquidIcons.Search,
                                    label = "Cerca componenti",
                                    onClick = { isSearchOpen = !isSearchOpen }
                                ),
                                LiquidTopBarAction(
                                    icon = LiquidIcons.MoreVert,
                                    label = "Azioni",
                                    subItems = listOf(
                                        LiquidTopBarAction(
                                            icon = LiquidIcons.Refresh,
                                            label = "Ricarica catalogo",
                                            onClick = {
                                                toastState.show(
                                                    message = "Catalogo sincronizzato",
                                                    subtitle = "Interfaccia fluida a 120 FPS",
                                                    type = LiquidToastType.Info
                                                )
                                            }
                                        ),
                                        LiquidTopBarAction(
                                            icon = LiquidIcons.Share,
                                            label = "Condividi link",
                                            onClick = {
                                                toastState.show(
                                                    message = "Link copiato negli appunti",
                                                    subtitle = "https://github.com/Anto426/Antosdk",
                                                    type = LiquidToastType.Success
                                                )
                                            }
                                        ),
                                        LiquidTopBarAction(
                                            icon = LiquidIcons.Info,
                                            label = "Informazioni SDK",
                                            onClick = {
                                                toastState.show(
                                                    message = "Liquid Monet Liquid Monet 2.0",
                                                    subtitle = "AGSL Optical Snell Refraction Engine",
                                                    type = LiquidToastType.Info
                                                )
                                            }
                                        )
                                    )
                                )
                            )
                        )
                    },
                    bottomBar = { contentBackdrop ->
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                        ) {
                            LiquidNavigationBar(
                                selectedIndex = currentTab,
                                onItemSelected = { selectedNav ->
                                    currentTab = selectedNav
                                },
                                items = listOf(
                                    LiquidNavigationItem(label = "Studio", icon = LiquidIcons.Star),
                                    LiquidNavigationItem(label = "Controlli", icon = LiquidIcons.Edit),
                                    LiquidNavigationItem(label = "Feedback", icon = LiquidIcons.Notifications),
                                    LiquidNavigationItem(label = "Navigazione", icon = LiquidIcons.Share)
                                ),
                                visible = isNavBarVisible,
                                backdropState = contentBackdrop
                            )
                        }
                    },
                    overlay = { contentBackdrop ->
                        LiquidFloatingActionButton(
                            onClick = {
                                toastState.show(
                                    message = "Azione rapida eseguita!",
                                    subtitle = "Scheda attiva: ${pageTitles[currentTab]}",
                                    type = LiquidToastType.Success
                                )
                            },
                            visible = isNavBarVisible,
                            backdropState = contentBackdrop,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .navigationBarsPadding()
                                .padding(end = 16.dp, bottom = 92.dp)
                        ) {
                            Icon(
                                imageVector = LiquidIcons.Add,
                                contentDescription = "Nuovo",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        LiquidToastHost(
                            state = toastState,
                            backdropState = contentBackdrop,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 80.dp)
                        )
                    }
                ) { backdropState ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        Scaffold(
                            modifier = Modifier
                                .fillMaxSize()
                                .nestedScroll(scrollBehavior.nestedScrollConnection)
                                .nestedScroll(navBarScrollConnection),
                            containerColor = Color.Transparent,
                            topBar = {
                                Column(
                                    modifier = Modifier.clearAndSetSemantics { }
                                ) {
                                    LargeTopAppBar(
                                        title = {},
                                        colors = TopAppBarDefaults.topAppBarColors(
                                            containerColor = Color.Transparent,
                                            scrolledContainerColor = Color.Transparent
                                        ),
                                        scrollBehavior = scrollBehavior
                                    )
                                    Spacer(modifier = Modifier.height(searchBarInset))
                                }
                            }
                        ) { innerPadding ->
                            Box(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState())
                                        .padding(horizontal = 16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))

                                    // Hub Content Switcher with Expressive Directional Transitions
                                    LiquidAnimatedNavContent(
                                        targetState = currentTab,
                                        transition = LiquidNavTransition.AutoDirectional,
                                        label = "mainScreenTransition"
                                    ) { tab ->
                                        when (tab) {
                                            0 -> StudioHubScreen(
                                                selectedPresetIndex = selectedPresetIndex,
                                                onSelectPreset = { selectedPresetIndex = it },
                                                sliderVal = sliderVal,
                                                onSliderChange = { sliderVal = it },
                                                selectedEffect = backgroundEffects[selectedEffectIndex],
                                                onSelectEffect = { effect -> selectedEffectIndex = backgroundEffects.indexOf(effect) },
                                                backdropState = backdropState
                                            )
                                            1 -> ControlsInputHubScreen(
                                                backdropState = backdropState
                                            )
                                            2 -> ModalsFeedbackHubScreen(
                                                toastState = toastState,
                                                backdropState = backdropState
                                            )
                                            else -> NavigationHubScreen(
                                                toastState = toastState,
                                                backdropState = backdropState
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(115.dp))
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}
