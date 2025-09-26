package dev.toolkt.reactive.cell.test_utils

sealed class UpdateVerifier<out ValueT> {
    abstract fun verifyUpdated(): ValueT
}

abstract class TotalUpdateVerifier<out ValueT> : UpdateVerifier<ValueT>() {
    abstract fun verifyDidNotUpdate()
}
