package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.MomentContext

sealed interface Cell<out ValueT> {
    companion object {
        fun <ValueT1, ValueT2, ResultT> map2(
            cell1: Cell<ValueT1>,
            cell2: Cell<ValueT2>,
            transform: (ValueT1, ValueT2) -> ResultT,
        ): Cell<ResultT> = TODO()

        fun <ValueT1, ValueT2, ValueT3, ResultT> map3(
            cell1: Cell<ValueT1>,
            cell2: Cell<ValueT2>,
            cell3: Cell<ValueT3>,
            transform: (ValueT1, ValueT2, ValueT3) -> ResultT,
        ): Cell<ResultT> = TODO()

        fun <ValueT> of(
            value: ValueT,
        ): Cell<ValueT> = TODO()

        fun <ValueT> switch(
            outerCell: Cell<Cell<ValueT>>,
        ): Cell<ValueT> = TODO()

        fun <ValueT> divert(
            outerCell: Cell<EventStream<ValueT>>,
        ): EventStream<ValueT> = TODO()
    }
}

context(momentContext: MomentContext) fun <ValueT> Cell<ValueT>.sample(): ValueT = TODO()

fun <ValueT, TransformedValueT> Cell<ValueT>.map(
    transform: (ValueT) -> TransformedValueT,
): Cell<TransformedValueT> = TODO()

val <ValueT> Cell<ValueT>.newValues: EventStream<ValueT>
    get() = TODO()

val <ValueT> Cell<ValueT>.updatedValues: EventStream<ValueT>
    get() = TODO()
