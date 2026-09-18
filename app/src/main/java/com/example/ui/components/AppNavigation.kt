package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.OutlineLight
import com.example.ui.theme.PrimaryEmerald
import com.example.ui.theme.SleekBackground
import com.example.ui.theme.TextSubtle
import com.example.ui.viewmodel.Screen

data class NavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector,
    val testTag: String
)

val primaryNavItems = listOf(
    NavItem(Screen.Home, "Home", Icons.Default.Home, "nav_home"),
    NavItem(Screen.Translate, "Translate", Icons.Default.Translate, "nav_translate"),
    NavItem(Screen.Voice, "Voice", Icons.Default.Mic, "nav_voice"),
    NavItem(Screen.Materials, "Materials", Icons.Default.Folder, "nav_materials"),
    NavItem(Screen.Profile, "Profile", Icons.Default.Person, "nav_profile")
)

@Composable
fun AppBottomBar(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.White,
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 12.dp)
            .testTag("app_bottom_bar")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            // Subtle top border line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(OutlineLight)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home
                SleekNavItem(
                    label = "Home",
                    icon = Icons.Default.Home,
                    isSelected = currentScreen == Screen.Home,
                    testTag = "nav_home",
                    onClick = { onNavigate(Screen.Home) }
                )

                // Translate
                SleekNavItem(
                    label = "Translate",
                    icon = Icons.Default.Translate,
                    isSelected = currentScreen == Screen.Translate,
                    testTag = "nav_translate",
                    onClick = { onNavigate(Screen.Translate) }
                )

                // Central Floating Voice Button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .offset(y = (-14).dp)
                        .testTag("nav_voice")
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(56.dp)
                            .shadow(elevation = 8.dp, shape = CircleShape)
                            .border(width = 3.5.dp, color = SleekBackground, shape = CircleShape)
                            .clip(CircleShape)
                            .background(PrimaryEmerald)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(bounded = true, color = Color.White)
                            ) {
                                onNavigate(Screen.Voice)
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Translation",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // Materials
                SleekNavItem(
                    label = "Materials",
                    icon = Icons.Default.Folder,
                    isSelected = currentScreen == Screen.Materials || currentScreen == Screen.Learn ||
                            currentScreen == Screen.Worksheets || currentScreen == Screen.Quiz ||
                            currentScreen == Screen.Flashcards,
                    testTag = "nav_materials",
                    onClick = { onNavigate(Screen.Materials) }
                )

                // Profile
                SleekNavItem(
                    label = "Profile",
                    icon = Icons.Default.Person,
                    isSelected = currentScreen == Screen.Profile,
                    testTag = "nav_profile",
                    onClick = { onNavigate(Screen.Profile) }
                )
            }
        }
    }
}

@Composable
private fun SleekNavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false, radius = 24.dp)
            ) { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag(testTag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) PrimaryEmerald else TextSubtle,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) PrimaryEmerald else TextSubtle
        )
    }
}

@Composable
fun AppNavigationRail(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationRail(
        modifier = modifier.fillMaxHeight(),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        primaryNavItems.forEach { item ->
            val selected = currentScreen == item.screen
            NavigationRailItem(
                selected = selected,
                onClick = { onNavigate(item.screen) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text(item.label) },
                modifier = Modifier.testTag(item.testTag)
            )
        }
    }
}

