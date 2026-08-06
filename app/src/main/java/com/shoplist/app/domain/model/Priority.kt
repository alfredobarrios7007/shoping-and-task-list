package com.shoplist.app.domain.model

enum class Priority {
    LOW,
    NORMAL,
    HIGH
}

fun Priority.next(): Priority = when (this) {
    Priority.LOW -> Priority.NORMAL
    Priority.NORMAL -> Priority.HIGH
    Priority.HIGH -> Priority.LOW
}
