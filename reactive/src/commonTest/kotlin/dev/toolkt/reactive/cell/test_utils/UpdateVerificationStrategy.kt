package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.newValues
import dev.toolkt.reactive.cell.updatedValues
import dev.toolkt.reactive.event_stream.EmitterEventStream

sealed class UpdateVerificationStrategy {
    abstract class Total : UpdateVerificationStrategy() {
        abstract override fun <ValueT> begin(
            subjectCell: Cell<ValueT>,
        ): UpdateVerifier.Total<ValueT>
    }

    data object Passive : Total() {
        override fun <ValueT> begin(
            subjectCell: Cell<ValueT>,
        ): UpdateVerifier.Passive<ValueT> = UpdateVerifier.observePassively(
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
            val updateVerifier = begin(
                subjectCell = subjectCell,
            )

            updateVerifier.end()

            updateVerifier.verifyUpdateDoesNotPropagate(
                doTrigger = doTrigger,
            )
        }

        abstract override fun <ValueT> begin(
            subjectCell: Cell<ValueT>,
        ): UpdateVerifier.Active<ValueT>
    }

    data object ViaUpdatedValues : Active() {
        override fun <ValueT> begin(
            subjectCell: Cell<ValueT>,
        ): UpdateVerifier.Active<ValueT> = UpdateVerifier.observeActivelyViaEventStream(
            subjectCell = subjectCell,
            extract = Cell<ValueT>::updatedValues,
        )
    }

    data object ViaNewValues : Active() {
        override fun <ValueT> begin(
            subjectCell: Cell<ValueT>,
        ): UpdateVerifier.Active<ValueT> = UpdateVerifier.observeActivelyViaEventStream(
            subjectCell = subjectCell,
            extract = Cell<ValueT>::newValues,
        )
    }

    data object ViaSwitch : Active() {
        override fun <ValueT> begin(
            subjectCell: Cell<ValueT>,
        ): UpdateVerifier.Active<ValueT> = UpdateVerifier.observeActivelyViaSwitch(
            subjectCell = subjectCell,
        )
    }

    abstract class Partial : UpdateVerificationStrategy() {
        abstract override fun <ValueT> begin(
            subjectCell: Cell<ValueT>,
        ): UpdateVerifier.Partial<ValueT>
    }

    data object Quick : Partial() {
        override fun <ValueT> begin(
            subjectCell: Cell<ValueT>,
        ): UpdateVerifier.Partial<ValueT> = UpdateVerifier.observeQuick(
            subjectCell = subjectCell,
        )
    }

    abstract fun <ValueT> begin(
        subjectCell: Cell<ValueT>,
    ): UpdateVerifier<ValueT>
}
