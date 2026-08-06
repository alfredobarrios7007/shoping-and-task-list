package com.shoplist.app.presentation.listdetail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shoplist.app.domain.model.FrequentProduct

@Composable
fun FrequentChipsRow(
    products: List<FrequentProduct>,
    onProductClick: (FrequentProduct) -> Unit,
    modifier: Modifier = Modifier
) {
    if (products.isEmpty()) return
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(products, key = { it.productId }) { product ->
            AssistChip(
                onClick = { onProductClick(product) },
                label = { Text(product.name) },
                leadingIcon = { Icon(Icons.Filled.Add, contentDescription = null) }
            )
        }
    }
}
