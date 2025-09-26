package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.newValues
import dev.toolkt.reactive.cell.sample
import dev.toolkt.reactive.cell.updatedValues
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.map

sealed class UpdateVerificationStrategy {
    abstract class Total : UpdateVerificationStrategy() {
        companion object {
            val values by lazy {
                listOf(ViaUpdatedValues, ViaNewValues)
            }
        }

        abstract override fun <ValueT> begin(
            subjectCell: Cell<ValueT>,
        ): UpdateVerificationProcess.Total<ValueT>
    }

    abstract class Partial : UpdateVerificationStrategy() {
        companion object {
            val values by lazy {
                listOf(ViaSimultaneousSwitch)
            }
        }

        abstract override fun <ValueT> begin(
            subjectCell: Cell<ValueT>,
        ): UpdateVerificationProcess.Partial<ValueT>
    }

    data object ViaUpdatedValues : Total() {
        override fun <ValueT> begin(
            subjectCell: Cell<ValueT>,
        ): UpdateVerificationProcess.Total<ValueT> = UpdateVerificationProcess.Total.observeVia(
            subjectCell = subjectCell,
            extract = Cell<ValueT>::updatedValues,
        )
    }

    data object ViaNewValues : Total() {
        override fun <ValueT> begin(
            subjectCell: Cell<ValueT>,
        ): UpdateVerificationProcess.Total<ValueT> = UpdateVerificationProcess.Total.observeVia(
            subjectCell = subjectCell,
            extract = Cell<ValueT>::newValues,
        )
    }

    data object ViaSimultaneousSwitch : Partial() {
        override fun <ValueT> begin(
            subjectCell: Cell<ValueT>,
        ): UpdateVerificationProcess.Partial<ValueT> = object : UpdateVerificationProcess.Partial<ValueT>() {
            override fun prepareVerifier(
                onTriggered: EventStream<*>,
            ): UpdateVerifier<ValueT> {
                val helperOuterCell = MomentContext.execute {
                    Cell.define(
                        initialValue = Cell.of(subjectCell.sample()),
                        newValues = onTriggered.map { subjectCell },
                    )
                }

                val helperSwitchCell = Cell.switch(helperOuterCell)

                val helperVerifier: TotalUpdateVerifier<ValueT> = UpdateVerificationProcess.Total.observe(
                    subjectCell = helperSwitchCell,
                ).prepareVerifier()

                return helperVerifier
            }
        }
    }

    companion object {
        val values by lazy {
            Total.values + Partial.values
        }
    }

    abstract fun <ValueT> begin(
        subjectCell: Cell<ValueT>,
    ): UpdateVerificationProcess<ValueT>
}
