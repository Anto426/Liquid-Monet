package com.anto426.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import com.anto426.antoui.components.navigation.AntoExpressiveTopBar
import com.anto426.antoui.components.buttons.AntoFloatingActionButton
import com.anto426.antoui.components.navigation.AntoFluidNavigationBar
import com.anto426.antoui.components.navigation.AntoNavItemData
import com.anto426.antoui.components.inputs.AntoSearchBar
import com.anto426.antoui.components.navigation.AntoTopBarAction
import com.anto426.antoui.components.feedback.AntoToastHost
import com.anto426.antoui.components.feedback.AntoToastType
import com.anto426.antoui.components.feedback.rememberAntoToastState
import com.anto426.antoui.glass.AntoBackgroundEffect
import com.anto426.antoui.glass.AntoGlassScene
import com.anto426.antoui.glass.AntoLiquidBackground
import com.anto426.antoui.icons.AntoIcons
import com.anto426.antoui.motion.AntoAnimatedNavContent
import com.anto426.antoui.motion.AntoNavTransition
import com.anto426.antoui.theme.LiquidMonetTheme
import com.anto426.antoui.theme.monet.AntoMonetPresets
import com.anto426.antoui.theme.monet.AntoMonetSeed
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
            val currentSeed: AntoMonetSeed = when (selectedPresetIndex) {
                0 -> AntoMonetPresets.Sapphire
                1 -> AntoMonetPresets.Emerald
                2 -> AntoMonetPresets.Sunset
                else -> AntoMonetPresets.Violet
            }

            LiquidMonetTheme(
                useMonetEngine = true,
                customMonetSeed = currentSeed,
                liquidIntensity = sliderVal
            ) {
                val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

                var selectedEffectIndex by remember { mutableIntStateOf(0) }
                val backgroundEffects = listOf(
                    AntoBackgroundEffect.RadiantBeam,
                    AntoBackgroundEffect.Aurora,
                    AntoBackgroundEffect.MeshGlow,
                    AntoBackgroundEffect.OrbitalPulse
                )

                val toastState = rememberAntoToastState()
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

                AntoGlassScene(
                    modifier = Modifier.fillMaxSize(),
                    background = {
                        AntoLiquidBackground(
                            effect = backgroundEffects[selectedEffectIndex],
                            monetSeed = currentSeed,
                            intensity = sliderVal
                        )
                    },
                    bottomBar = { contentBackdrop ->
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                        ) {
                            AntoFluidNavigationBar(
                                selectedIndex = currentTab,
                                onItemSelected = { selectedNav ->
                                    currentTab = selectedNav
                                },
                                items = listOf(
                                    AntoNavItemData(AntoIcons.Star, "Studio"),
                                    AntoNavItemData(AntoIcons.Edit, "Controlli"),
                                    AntoNavItemData(AntoIcons.Notifications, "Feedback"),
                                    AntoNavItemData(AntoIcons.Share, "Navigazione")
                                ),
                                visible = isNavBarVisible,
                                backdropState = contentBackdrop
                            )
                        }
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
                                AntoExpressiveTopBar(
                                    title = pageTitles[currentTab],
                                    subtitle = "Demo catalog",
                                    backdropState = backdropState,
                                    scrollBehavior = scrollBehavior,
                                    isSearchActive = isSearchOpen,
                                    searchQuery = searchQuery,
                                    onQueryChange = { searchQuery = it },
                                    onSearchActiveChange = { isSearchOpen = it },
                                    searchPlaceholder = "Cerca componenti, controlli, gesture...",
                                    actionItems = listOf(
                                        AntoTopBarAction(
                                            icon = AntoIcons.Search,
                                            label = "Cerca componenti",
                                            onClick = { isSearchOpen = !isSearchOpen }
                                        ),
                                        AntoTopBarAction(
                                            icon = AntoIcons.MoreVert,
                                            label = "Azioni",
                                            subItems = listOf(
                                                AntoTopBarAction(
                                                    icon = AntoIcons.Refresh,
                                                    label = "Ricarica catalogo",
                                                    onClick = {
                                                        toastState.show(
                                                            message = "Catalogo sincronizzato",
                                                            subtitle = "Interfaccia fluida a 120 FPS",
                                                            type = AntoToastType.Info
                                                        )
                                                    }
                                                ),
                                                AntoTopBarAction(
                                                    icon = AntoIcons.Share,
                                                    label = "Condividi link",
                                                    onClick = {
                                                        toastState.show(
                                                            message = "Link copiato negli appunti",
                                                            subtitle = "https://github.com/Anto426/Antosdk",
                                                            type = AntoToastType.Success
                                                        )
                                                    }
                                                ),
                                                AntoTopBarAction(
                                                    icon = AntoIcons.Info,
                                                    label = "Informazioni SDK",
                                                    onClick = {
                                                        toastState.show(
                                                            message = "AntoUI Liquid Monet 2.0",
                                                            subtitle = "AGSL Optical Snell Refraction Engine",
                                                            type = AntoToastType.Info
                                                        )
                                                    }
                                                )
                                            )
                                        )
                                    )
                                )
                            },
                            floatingActionButton = {
                                AntoFloatingActionButton(
                                    onClick = {
                                        toastState.show(
                                            message = "Azione rapida eseguita!",
                                            subtitle = "Scheda attiva: ${pageTitles[currentTab]}",
                                            type = AntoToastType.Success
                                        )
                                    },
                                    visible = isNavBarVisible,
                                    backdropState = backdropState,
                                    modifier = Modifier.padding(bottom = 76.dp)
                                ) {
                                    Icon(
                                        imageVector = AntoIcons.Add,
                                        contentDescription = "Nuovo",
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(24.dp)
                                    )
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
                                    AntoAnimatedNavContent(
                                        targetState = currentTab,
                                        transition = AntoNavTransition.AutoDirectional,
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

                        // Toast Notification Layer
                        AntoToastHost(
                            state = toastState,
                            backdropState = backdropState,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 80.dp)
                        )
                    }
                }
            }
        }
    }
}
