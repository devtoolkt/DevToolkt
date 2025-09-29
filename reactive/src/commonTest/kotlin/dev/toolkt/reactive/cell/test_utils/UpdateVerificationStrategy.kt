package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.newValues
import dev.toolkt.reactive.cell.sample
import dev.toolkt.reactive.cell.updatedValues
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.emit
import dev.toolkt.reactive.event_stream.map

sealed class UpdateVerificationStrategy {
    abstract class Total : UpdateVerificationStrategy() {
        abstract override fun <ValueT> begin(
            subjectCell: Cell<ValueT>,
        ): UpdateVerificationProcess.Total<ValueT>
    }

    data object Passive : Total() {
        override fun <ValueT> begin(
            subjectCell: Cell<ValueT>,
        ): UpdateVerificationProcess.Passive<ValueT> = UpdateVerificationProcess.observePassively(
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

        abstract override fun <ValueT> begin(
            subjectCell: Cell<ValueT>,
        ): UpdateVerificationProcess.Active<ValueT>
    }

    data object ViaUpdatedValues : Active() {
        override fun <ValueT> begin(
            subjectCell: Cell<ValueT>,
        ): UpdateVerificationProcess.Active<ValueT> = UpdateVerificationProcess.observeActivelyViaEventStream(
            subjectCell = subjectCell,
            extract = Cell<ValueT>::updatedValues,
        )
    }

    data object ViaNewValues : Active() {
        override fun <ValueT> begin(
            subjectCell: Cell<ValueT>,
        ): UpdateVerificationProcess.Active<ValueT> = UpdateVerificationProcess.observeActivelyViaEventStream(
            subjectCell = subjectCell,
            extract = Cell<ValueT>::newValues,
        )
    }

    data object ViaSwitch : Active() {
        override fun <ValueT> begin(
            subjectCell: Cell<ValueT>,
        ): UpdateVerificationProcess.Active<ValueT> = UpdateVerificationProcess.observeActivelyViaSwitch(
            subjectCell = subjectCell,
        )
    }

    abstract class Partial : UpdateVerificationStrategy() {
        companion object {
            val values by lazy {
                listOf(
                    ViaSimultaneousSwitch,
                )
            }
        }

        abstract override fun <ValueT> begin(
            subjectCell: Cell<ValueT>,
        ): UpdateVerificationProcess.Partial<ValueT>
    }

    data object ViaSimultaneousSwitch : Partial() {
        override fun <ValueT> begin(
            subjectCell: Cell<ValueT>,
        ): UpdateVerificationProcess.Partial<ValueT> = object : UpdateVerificationProcess.Partial<ValueT>() {
            override fun verifyUpdates(
                doUpdate: EmitterEventStream<Unit>,
                expectedUpdatedValue: ValueT,
            ) {
                val helperOuterCell = MomentContext.execute {
                    Cell.define(
                        initialValue = Cell.of(subjectCell.sample()),
                        newValues = doUpdate.map { subjectCell },
                    )
                }

                val helperSwitchCell = Cell.switch(helperOuterCell)

                val helperUpdateVerifier = UpdateVerificationProcess.observeActively(
                    subjectCell = helperSwitchCell,
                )

                doUpdate.emit()

                helperUpdateVerifier.verifyUpdates(
                    doUpdate = doUpdate,
                    expectedUpdatedValue = expectedUpdatedValue,
                )
            }
        }
    }

    abstract fun <ValueT> begin(
        subjectCell: Cell<ValueT>,
    ): UpdateVerificationProcess<ValueT>
}
