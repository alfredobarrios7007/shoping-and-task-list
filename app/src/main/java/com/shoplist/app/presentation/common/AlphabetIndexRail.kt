package com.shoplist.app.presentation.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

private val ALPHABET = ('A'..'Z').toList()

/**
 * Contacts-app-style A-Z fast-scroll rail. [names] must be in the same order as the
 * items rendered by [listState]'s LazyColumn (starting at [indexOffset], to account
 * for any header items placed before the indexed items).
 */
@Composable
fun AlphabetIndexRail(
    names: List<String>,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    indexOffset: Int = 0
) {
    val scope = rememberCoroutineScope()

    val letterToIndex = remember(names) {
        val map = mutableMapOf<Char, Int>()
        names.forEachIndexed { index, name ->
            val firstChar = name.firstOrNull()?.uppercaseChar() ?: return@forEachIndexed
            if (firstChar in 'A'..'Z' && firstChar !in map) {
                map[firstChar] = index
            }
        }
        map
    }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        ALPHABET.forEach { letter ->
            val hasEntries = letter in letterToIndex
            Text(
                text = letter.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = if (hasEntries) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outlineVariant
                },
                modifier = Modifier
                    .clickable {
                        val targetIndex = letterToIndex[letter] ?: nearestIndexedLetter(letter, letterToIndex)
                        targetIndex?.let { idx ->
                            scope.launch { listState.animateScrollToItem(idx + indexOffset) }
                        }
                    }
                    .padding(horizontal = 4.dp, vertical = 0.5.dp)
            )
        }
    }
}

private fun nearestIndexedLetter(letter: Char, map: Map<Char, Int>): Int? {
    if (map.isEmpty()) return null
    val pos = ALPHABET.indexOf(letter)
    for (distance in 1 until ALPHABET.size) {
        val forward = pos + distance
        val backward = pos - distance
        if (forward < ALPHABET.size && ALPHABET[forward] in map) return map.getValue(ALPHABET[forward])
        if (backward >= 0 && ALPHABET[backward] in map) return map.getValue(ALPHABET[backward])
    }
    return null
}
