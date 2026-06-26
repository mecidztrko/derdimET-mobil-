package com.derdimet.mobil.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derdimet.mobil.model.MeResponse
import com.derdimet.mobil.model.UserRole
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.components.DerdimTopBar
import com.derdimet.mobil.ui.components.FigmaSecondaryButton
import com.derdimet.mobil.ui.components.FigmaStyle
import com.derdimet.mobil.ui.theme.DerdimColors

@Composable
fun RoleProfileScreen(
    userRole: UserRole,
    marketService: MarketService,
    onLogout: () -> Unit,
    onSwitchRole: (UserRole) -> Unit = {},
    onOpenMyListings: () -> Unit = {},
    onOpenPurchases: () -> Unit = {},
    onOpenNotifications: () -> Unit = {},
    onOpenEditProfile: () -> Unit = {},
    onOpenFavorites: () -> Unit = {},
    onOpenSecuritySettings: () -> Unit = {},
    onOpenNotificationPreferences: () -> Unit = {},
) {
    var me by remember { mutableStateOf<MeResponse?>(null) }
    var offerCount by remember { mutableStateOf(0) }
    var messageCount by remember { mutableStateOf(0) }
    var favoriteCount by remember { mutableStateOf(0) }
    var showRoleSwitcher by remember { mutableStateOf(false) }
    var listingCount by remember { mutableStateOf(0) }

    LaunchedEffect(userRole) {
        me = marketService.fetchMe().data
        messageCount = marketService.fetchConversations().data?.sumOf { it.unreadCount } ?: 0
        favoriteCount = when (userRole) {
            UserRole.MEAT_BUYER -> marketService.fetchBuyerFavoriteMeatListings().data?.size ?: 0
            UserRole.ANIMAL_SELLER -> marketService.fetchFavoriteAnimalPurchaseRequests().data?.size ?: 0
            UserRole.SLAUGHTERHOUSE -> marketService.fetchFavoriteAnimalListings().data?.size ?: 0
            else -> 0
        }
        listingCount = when (userRole) {
            UserRole.MEAT_BUYER -> marketService.fetchMyPurchases().data?.size ?: 0
            UserRole.ANIMAL_SELLER -> marketService.fetchMySellerAnimalListings().data?.size ?: 0
            UserRole.SLAUGHTERHOUSE -> marketService.fetchMySlaughterhouseMeatSaleRequests().data?.size ?: 0
            else -> 0
        }
        offerCount = when (userRole) {
            UserRole.MEAT_BUYER -> marketService.fetchMyBuyerMeatOffers().data?.size ?: 0
            UserRole.ANIMAL_SELLER -> (marketService.fetchSellerIncomingListingOffers().data?.size ?: 0) +
                (marketService.fetchMyAnimalOffers().data?.size ?: 0)
            UserRole.SLAUGHTERHOUSE -> (marketService.fetchSlaughterhouseIncomingMeatOffers().data?.size ?: 0) +
                (marketService.fetchMySlaughterhouseListingOffers().data?.size ?: 0)
            else -> 0
        }
    }

    val roleLabel = when (userRole) {
        UserRole.MEAT_BUYER -> "Et Alıcı"
        UserRole.ANIMAL_SELLER -> "Hayvan Satıcı"
        UserRole.SLAUGHTERHOUSE -> "Kesimhane"
        else -> "Kullanıcı"
    }
    val roleBg = when (userRole) {
        UserRole.MEAT_BUYER -> DerdimColors.Secondary to Color(0xFF1D4ED8)
        UserRole.ANIMAL_SELLER -> Color(0xFFD1FAE5) to Color(0xFF047857)
        UserRole.SLAUGHTERHOUSE -> Color(0xFFF3E8FF) to Color(0xFF7E22CE)
        else -> DerdimColors.Muted to DerdimColors.MutedForeground
    }

    Column(Modifier.fillMaxSize().background(FigmaStyle.ScreenBg)) {
        DerdimTopBar(title = "Profil")
        Column(Modifier.verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White).border(1.dp, DerdimColors.Border.copy(0.5f), RoundedCornerShape(16.dp))) {
                Box(Modifier.fillMaxWidth().height(80.dp).background(Brush.horizontalGradient(listOf(DerdimColors.Primary, Color(0xFF3B82F6)))))
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                        Box(Modifier.size(64.dp).background(Color.White, RoundedCornerShape(16.dp)).border(4.dp, Color.White, RoundedCornerShape(16.dp)).padding(4.dp)) {
                            Box(Modifier.fillMaxSize().background(DerdimColors.Primary.copy(0.1f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Person, null, tint = DerdimColors.Primary, modifier = Modifier.size(32.dp))
                            }
                        }
                        FigmaSecondaryButton("Düzenle", onClick = onOpenEditProfile)
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(me?.name ?: "Kullanıcı", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        if (me?.businessVerified == true) Icon(Icons.Default.CheckCircle, null, tint = DerdimColors.Success, modifier = Modifier.padding(start = 6.dp).size(18.dp))
                    }
                    Text(roleLabel, modifier = Modifier.padding(top = 6.dp).background(roleBg.first, RoundedCornerShape(999.dp)).padding(horizontal = 10.dp, vertical = 4.dp), color = roleBg.second, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    me?.companyName?.let { Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Business, null, tint = DerdimColors.MutedForeground, modifier = Modifier.size(14.dp)); Text(it, fontSize = 13.sp, color = DerdimColors.MutedForeground, modifier = Modifier.padding(start = 6.dp)) } }
                    me?.city?.let { Row(Modifier.padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.LocationOn, null, tint = DerdimColors.MutedForeground, modifier = Modifier.size(14.dp)); Text(it, fontSize = 13.sp, color = DerdimColors.MutedForeground, modifier = Modifier.padding(start = 6.dp)) } }
                    Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ProfileStat("$listingCount", "İlan", Icons.Default.Description)
                        ProfileStat("$offerCount", "Teklif", Icons.Default.TrendingUp)
                        ProfileStat("$messageCount", "Mesaj", Icons.Default.Chat)
                        ProfileStat("$favoriteCount", "Favori", Icons.Default.Star)
                    }
                }
            }

            ProfileMenuSection(
                title = "Demo: Rol Değiştir",
                subtitle = "Farklı kullanıcı deneyimlerini test et",
                icon = Icons.Default.SwapHoriz,
                tint = Color(0xFF2563EB),
                expanded = showRoleSwitcher,
                onClick = { showRoleSwitcher = !showRoleSwitcher },
            )
            if (showRoleSwitcher) {
                val switchRoles = listOf(
                    UserRole.MEAT_BUYER to "Et Alıcı",
                    UserRole.ANIMAL_SELLER to "Hayvan Satıcı",
                    UserRole.SLAUGHTERHOUSE to "Kesimhane",
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    switchRoles.forEach { (role, label) ->
                        if (role != userRole) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color.White)
                                    .border(1.dp, DerdimColors.Border.copy(0.5f), RoundedCornerShape(14.dp))
                                    .clickable { onSwitchRole(role) }
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(label, fontWeight = FontWeight.Medium, fontSize = 14.sp, modifier = Modifier.weight(1f))
                                Icon(Icons.Default.ChevronRight, null, tint = DerdimColors.MutedForeground)
                            }
                        }
                    }
                }
            }

            SectionTitle("HESABIM")
            ProfileMenuItem(
                Icons.Default.Favorite,
                "Favorilerim",
                Color(0xFFF87171),
                favoriteCount.takeIf { it > 0 }?.toString(),
                onClick = onOpenFavorites,
            )
            if (userRole == UserRole.MEAT_BUYER) {
                ProfileMenuItem(Icons.Default.ShoppingBag, "Alışverişlerim", Color(0xFF3B82F6), onClick = onOpenPurchases)
            } else {
                ProfileMenuItem(Icons.Default.ShoppingBag, "Satış / Alışveriş", Color(0xFF3B82F6), onClick = onOpenPurchases)
                ProfileMenuItem(Icons.Default.Description, "İlanlarım", Color(0xFF8B5CF6), listingCount.takeIf { it > 0 }?.toString(), onClick = onOpenMyListings)
            }

            SectionTitle("AYARLAR")
            ProfileMenuItem(Icons.Default.Notifications, "Bildirimler", Color(0xFFF59E0B), messageCount.takeIf { it > 0 }?.toString(), onClick = onOpenNotifications)
            ProfileMenuItem(Icons.Default.Security, "Gizlilik ve Güvenlik", DerdimColors.Success, onClick = onOpenSecuritySettings)
            ProfileMenuItem(Icons.Default.Settings, "Bildirim Tercihleri", DerdimColors.MutedForeground, onClick = onOpenNotificationPreferences)
            ProfileMenuItem(Icons.Default.Help, "Yardım", DerdimColors.MutedForeground, onClick = { })

            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White).border(1.dp, DerdimColors.Border.copy(0.5f), RoundedCornerShape(16.dp)).clickable(onClick = onLogout).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(36.dp).background(DerdimColors.Red50, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                    Icon(Icons.AutoMirrored.Filled.Logout, null, tint = DerdimColors.Destructive)
                }
                Text("Çıkış Yap", color = DerdimColors.Destructive, fontWeight = FontWeight.Medium, modifier = Modifier.padding(start = 12.dp))
            }
        }
    }
}

@Composable private fun SectionTitle(text: String) = Text(text, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = DerdimColors.MutedForeground, modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp))
@Composable private fun RowScope.ProfileStat(value: String, label: String, icon: ImageVector) {
    Column(Modifier.weight(1f).background(DerdimColors.Muted.copy(0.5f), RoundedCornerShape(12.dp)).padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = DerdimColors.Primary, modifier = Modifier.size(16.dp))
        Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(label, fontSize = 10.sp, color = DerdimColors.MutedForeground)
    }
}
@Composable private fun ProfileMenuSection(title: String, subtitle: String, icon: ImageVector, tint: Color, expanded: Boolean, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White).border(1.dp, DerdimColors.Border.copy(0.5f), RoundedCornerShape(16.dp)).clickable(onClick = onClick).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(36.dp).background(tint.copy(0.12f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) { Icon(icon, null, tint = tint) }
        Column(Modifier.weight(1f).padding(horizontal = 12.dp)) { Text(title, fontWeight = FontWeight.Medium, fontSize = 14.sp); Text(subtitle, fontSize = 12.sp, color = DerdimColors.MutedForeground) }
        Icon(Icons.Default.ChevronRight, null, tint = DerdimColors.MutedForeground)
    }
}
@Composable private fun ProfileMenuItem(icon: ImageVector, label: String, tint: Color, badge: String? = null, onClick: () -> Unit = {}) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White).border(1.dp, DerdimColors.Border.copy(0.5f), RoundedCornerShape(16.dp)).clickable(onClick = onClick).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(36.dp).background(DerdimColors.Muted.copy(0.5f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) { Icon(icon, null, tint = tint) }
        Text(label, fontWeight = FontWeight.Medium, fontSize = 14.sp, modifier = Modifier.weight(1f).padding(horizontal = 12.dp))
        badge?.let { Text(it, modifier = Modifier.background(DerdimColors.Primary.copy(0.1f), RoundedCornerShape(999.dp)).padding(horizontal = 8.dp, vertical = 2.dp), color = DerdimColors.Primary, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
        Icon(Icons.Default.ChevronRight, null, tint = DerdimColors.MutedForeground)
    }
}
