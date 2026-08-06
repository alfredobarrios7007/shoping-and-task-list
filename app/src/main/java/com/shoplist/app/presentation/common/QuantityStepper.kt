package com.shoplist.app.presentation.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shoplist.app.R

@Composable
fun QuantityStepper(
    quantity: Double,
    onQuantityChange: (Double) -> Unit,
    modifier: Modifier = Modifier,
    step: Double = 1.0,
    minValue: Double = 1.0
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { onQuantityChange((quantity - step).coerceAtLeast(minValue)) }) {
            Icon(Icons.Filled.Remove, contentDescription = stringResource(R.string.cd_decrease_quantity))
        }
        Text(
            text = if (quantity == quantity.toLong().toDouble()) quantity.toLong().toString() else quantity.toString(),
            modifier = Modifier.width(32.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        IconButton(onClick = { onQuantityChange(quantity + step) }) {
            Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.cd_increase_quantity))
        }
    }
}
