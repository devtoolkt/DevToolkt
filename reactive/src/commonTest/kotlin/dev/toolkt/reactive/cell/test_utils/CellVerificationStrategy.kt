package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.newValues
import dev.toolkt.reactive.cell.updatedValues
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.emit

sealed class CellVerificationStrategy {
    data object Passive : CellVerificationStrategy() {
        override fun <ValueT> begin(
            subjectCell: Cell<ValueT>,
        ): CellVerifier.Passive<ValueT> = CellVerifier.observePassively(
            subjectCell = subjectCell,
        )
    }

    abstract class Active : CellVerificationStrategy() {
        companion object {
            val values by lazy {
                listOf(
                    ViaUpdatedValues,
                    ViaNewValues,
                    ViaSwitch,
                )
            }
        }

        fun <ValueT> verifyDeactivation(
            subjectCell: Cell<ValueT>,
            doTrigger: EmitterEventStream<Unit>,
        ) {
            val verifier = begin(
                subjectCell = subjectCell,
            )

            verifier.deactivate()

            verifier.verifyUpdateDoesNotPropagate(
                doTrigger = doTrigger,
            )
        }

        abstract override fun <ValueT> begin(
            subjectCell: Cell<ValueT>,
        ): CellVerifier.Active<ValueT>
    }

    data object ViaUpdatedValues : Active() {
        override fun <ValueT> begin(
            subjectCell: Cell<ValueT>,
        ): CellVerifier.Active<ValueT> = CellVerifier.observeActivelyViaEventStream(
            subjectCell = subjectCell,
            extract = Cell<ValueT>::updatedValues,
        )
    }

    data object ViaNewValues : Active() {
        override fun <ValueT> begin(
            subjectCell: Cell<ValueT>,
        ): CellVerifier.Active<ValueT> = CellVerifier.observeActivelyViaEventStream(
            subjectCell = subjectCell,
            extract = Cell<ValueT>::newValues,
        )
    }

    data object ViaSwitch : Active() {
        override fun <ValueT> begin(
            subjectCell: Cell<ValueT>,
        ): CellVerifier.Active<ValueT> = CellVerifier.observeActivelyViaSwitch(
            subjectCell = subjectCell,
        )
    }

    data object Quick : CellVerificationStrategy() {
        override fun <ValueT> begin(
            subjectCell: Cell<ValueT>,
        ): CellVerifier<ValueT> = CellVerifier.observeQuick(
            subjectCell = subjectCell,
        )
    }

    fun <ValueT> verifyCompleteFreeze(
        subjectCell: Cell<ValueT>,
        doFreeze: EmitterEventStream<Unit>,
        expectedFrozenValue: ValueT,
    ) {
        val verifier = begin(
            subjectCell = subjectCell,
        )

        doFreeze.emit()

        verifier.verifyCurrentValue(
            expectedCurrentValue = expectedFrozenValue,
        )
    }

    abstract fun <ValueT> begin(
        subjectCell: Cell<ValueT>,
    ): CellVerifier<ValueT>
}
