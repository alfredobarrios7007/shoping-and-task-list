package com.shoplist.app.presentation.listdetail.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import com.shoplist.app.R
import com.shoplist.app.domain.model.ShoppingListItem
import com.shoplist.app.presentation.common.PriorityFlag
import com.shoplist.app.presentation.common.QuantityStepper

@Composable
fun ItemRow(
    item: ShoppingListItem,
    onToggleChecked: (Boolean) -> Unit,
    onQuantityChange: (Double) -> Unit,
    onCyclePriority: () -> Unit,
    onRemove: () -> Unit
) {
    ListItem(
        modifier = Modifier.fillMaxWidth(),
        leadingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = item.isChecked, onCheckedChange = onToggleChecked)
                PriorityFlag(priority = item.priority, onClick = onCyclePriority)
            }
        },
        headlineContent = {
            Text(
                text = item.productName,
                textDecoration = if (item.isChecked) TextDecoration.LineThrough else null
            )
        },
        supportingContent = item.unit?.let { { Text(it) } },
        trailingContent = {
            Row {
                QuantityStepper(quantity = item.quantity, onQuantityChange = onQuantityChange)
                IconButton(onClick = onRemove) {
                    Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.cd_remove_item, item.productName))
                }
            }
        }
    )
}
