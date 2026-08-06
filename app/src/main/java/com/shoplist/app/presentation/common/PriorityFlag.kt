package com.shoplist.app.presentation.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.shoplist.app.R
import com.shoplist.app.domain.model.Priority

@Composable
fun PriorityFlag(
    priority: Priority,
    onClick: () -> Unit
) {
    val (icon, tint, labelRes) = when (priority) {
        Priority.LOW -> Triple(Icons.Outlined.Flag, MaterialTheme.colorScheme.outline, R.string.priority_low)
        Priority.NORMAL -> Triple(Icons.Filled.Flag, MaterialTheme.colorScheme.primary, R.string.priority_normal)
        Priority.HIGH -> Triple(Icons.Filled.Flag, MaterialTheme.colorScheme.error, R.string.priority_high)
    }
    IconButton(onClick = onClick) {
        Icon(
            imageVector = icon,
            contentDescription = stringResource(R.string.cd_priority, stringResource(labelRes)),
            tint = tint
        )
    }
}
