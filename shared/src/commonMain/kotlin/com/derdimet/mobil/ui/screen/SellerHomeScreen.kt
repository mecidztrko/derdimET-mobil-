package com.derdimet.mobil.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import com.derdimet.mobil.model.AnimalPurchaseRequestDto
import com.derdimet.mobil.model.SellerAnimalOfferItemDto
import com.derdimet.mobil.repository.AnimalCategoryFilter
import com.derdimet.mobil.viewmodel.SellerViewModel

@Composable
fun SellerHomeScreen(
    viewModel: SellerViewModel,
    selectedFilter: AnimalCategoryFilter
) {
    val requests by viewModel.requests.collectAsState()
    val offers by viewModel.offers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val filteredRequests = remember(requests, selectedFilter) {
        when (selectedFilter) {
            AnimalCategoryFilter.ALL -> requests
            AnimalCategoryFilter.KUCUKBAS -> requests.filter { it.animalCategory?.name == AnimalCategoryFilter.KUCUKBAS.name }
            AnimalCategoryFilter.BUYUKBAS -> requests.filter { it.animalCategory?.name == AnimalCategoryFilter.BUYUKBAS.name }
        }
    }
    val filteredOffers = remember(offers, selectedFilter) {
        when (selectedFilter) {
            AnimalCategoryFilter.ALL -> offers
            AnimalCategoryFilter.KUCUKBAS -> offers.filter { it.request.animalCategory?.name == AnimalCategoryFilter.KUCUKBAS.name }
            AnimalCategoryFilter.BUYUKBAS -> offers.filter { it.request.animalCategory?.name == AnimalCategoryFilter.BUYUKBAS.name }
        }
    }

    if (isLoading && filteredRequests.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            item {
                Text(text = "Hayvan alış ilanları", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "Yönetici tarafından açılan taleplere teklif verebilirsiniz.",
                    modifier = Modifier.padding(top = 8.dp, bottom = 20.dp),
                    color = Color.Gray
                )
            }

            error?.let {
                item {
                    Text(text = it, color = Color.Red, modifier = Modifier.padding(bottom = 12.dp))
                }
            }

            item {
                Text(text = "Açık ilanlar", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
            }

            if (filteredRequests.isEmpty()) {
                item { Text(text = "Şu an açık ilan yok.", color = Color.Gray) }
            } else {
                items(filteredRequests) { request ->
                    RequestCard(request, onOfferClick = { /* Show offer dialog */ })
                }
            }

            item {
                Text(text = "Verdiğim teklifler", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 28.dp, bottom = 8.dp))
            }

            if (filteredOffers.isEmpty()) {
                item { Text(text = "Henüz teklif yok.", color = Color.Gray) }
            } else {
                items(filteredOffers) { offer ->
                    OfferCard(offer)
                }
            }
        }
    }
}

@Composable
fun RequestCard(request: AnimalPurchaseRequestDto, onOfferClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = request.title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text(text = "Tür: ${request.animalCategory}", fontSize = 14.sp)
            request.quantity?.let { Text(text = "Adet: $it", fontSize = 14.sp) }
            request.description?.let { Text(text = it, fontSize = 14.sp, modifier = Modifier.padding(top = 6.dp)) }
            
            Button(
                onClick = onOfferClick,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            ) {
                Text("Teklif ver")
            }
        }
    }
}

@Composable
fun OfferCard(offer: SellerAnimalOfferItemDto) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = offer.request.title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text(text = "${offer.pricePerKg} ₺/kg", fontSize = 14.sp)
            Text(text = "Durum: ${offer.status}", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(text = offer.createdAt, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
        }
    }
}
