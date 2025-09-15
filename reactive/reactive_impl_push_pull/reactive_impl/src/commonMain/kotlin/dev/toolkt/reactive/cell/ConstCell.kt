package dev.toolkt.reactive.cell

data class ConstCell<ValueT>(
    val value: ValueT,
) : Cell<ValueT>