package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.newValues
import dev.toolkt.reactive.cell.sample
import dev.toolkt.reactive.cell.updatedValues
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.emit
import dev.toolkt.reactive.event_stream.map
import dev.toolkt.reactive.event_stream.mapAt

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

            updateVerifier.verifyUpdateDidNotPropagate(
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

    /**
     * A tricky update verifier that triggers a corner case path, where the subject cell might be activated and pulled
     * at the same time.
     */
    data object Quick : Partial() {
        override fun <ValueT> begin(
            subjectCell: Cell<ValueT>,
        ): UpdateVerifier.Partial<ValueT> = object : UpdateVerifier.Partial<ValueT>() {
            override fun verifyUpdates(
                doTrigger: EmitterEventStream<Unit>,
                expectedUpdatedValue: ValueT,
            ) {
                val doReset = EmitterEventStream<Unit>()

                val helperOuterCell = MomentContext.execute {
                    Cell.define(
                        initialValue = Cell.of(subjectCell.sample()),
                        newValues = EventStream.merge2(
                            doTrigger.map { subjectCell },
                            doReset.mapAt { Cell.of(subjectCell.sampleExternally()) },
                        ),
                    )
                }

                val helperSwitchCell = Cell.switch(helperOuterCell)

                val helperUpdateVerifier = UpdateVerifier.observeActively(
                    subjectCell = helperSwitchCell,
                )

                helperUpdateVerifier.verifyUpdates(
                    doTrigger = doTrigger,
                    expectedUpdatedValue = expectedUpdatedValue,
                )

                doReset.emit()
            }
        }
    }

    abstract fun <ValueT> begin(
        subjectCell: Cell<ValueT>,
    ): UpdateVerifier<ValueT>
}
