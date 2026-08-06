package com.shoplist.app.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.shoplist.app.R
import com.shoplist.app.domain.model.RecurrenceInterval

@Composable
fun RecurrenceInterval.displayName(): String = stringResource(
    when (this) {
        RecurrenceInterval.DAILY -> R.string.interval_daily
        RecurrenceInterval.WEEKLY -> R.string.interval_weekly
        RecurrenceInterval.MONTHLY -> R.string.interval_monthly
    }
)
