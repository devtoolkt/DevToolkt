package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.newValues
import dev.toolkt.reactive.cell.updatedValues
import dev.toolkt.reactive.event_stream.EmitterEventStream

sealed class CellVerificationStrategy {
    abstract class Total : CellVerificationStrategy() {
        abstract override fun <ValueT> begin(
            subjectCell: Cell<ValueT>,
        ): CellVerifier.Total<ValueT>
    }

    data object Passive : Total() {
        override fun <ValueT> begin(
            subjectCell: Cell<ValueT>,
        ): CellVerifier.Passive<ValueT> = CellVerifier.observePassively(
            subjectCell = subjectCell,
        )
    }

    abstract class Active : Total() {
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

            verifier.end()

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

    abstract class Partial : CellVerificationStrategy() {
        abstract override fun <ValueT> begin(
            subjectCell: Cell<ValueT>,
        ): CellVerifier.Partial<ValueT>
    }

    data object Quick : Partial() {
        override fun <ValueT> begin(
            subjectCell: Cell<ValueT>,
        ): CellVerifier.Partial<ValueT> = CellVerifier.observeQuick(
            subjectCell = subjectCell,
        )
    }

    abstract fun <ValueT> begin(
        subjectCell: Cell<ValueT>,
    ): CellVerifier<ValueT>
}
