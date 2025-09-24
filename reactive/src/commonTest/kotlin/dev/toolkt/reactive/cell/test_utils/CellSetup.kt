package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.MutableCell
import dev.toolkt.reactive.cell.map
import dev.toolkt.reactive.cell.newValues
import dev.toolkt.reactive.cell.sample
import dev.toolkt.reactive.cell.updatedValues
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.NeverEventStream
import dev.toolkt.reactive.event_stream.hold
import dev.toolkt.reactive.event_stream.map
import dev.toolkt.reactive.event_stream.mapNotNull
import dev.toolkt.reactive.event_stream.subscribe
import dev.toolkt.reactive.event_stream.subscribeCollecting
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

interface CellSetup<ValueT> {
    interface CellProvider<ValueT> {
        fun provide(): Cell<ValueT>
    }

    class ConstCellSetup<ValueT>(
        private val value: ValueT,
    ) : CellSetup<ValueT> {
        context(momentContext: MomentContext) override fun setup(
            preparationTickStream: EventStream<Unit>,
            propagationTickStream: EventStream<Unit>,
        ): CellProvider<ValueT> = object : CellProvider<ValueT> {
            override fun provide(): Cell<ValueT> = Cell.of(value)
        }
    }

    class NonConstCellSetup<ValueT>(
        private val value: ValueT,
    ) : CellSetup<ValueT> {
        context(momentContext: MomentContext) override fun setup(
            preparationTickStream: EventStream<Unit>,
            propagationTickStream: EventStream<Unit>,
        ): CellProvider<ValueT> = object : CellProvider<ValueT> {
            override fun provide(): Cell<ValueT> = MutableCell(initialValue = value)
        }
    }

    class HoldCellSetup<ValueT>(
        private val initialValue: ValueT,
        private val newValue: ValueT?,
    ) : CellSetup<ValueT> {
        context(momentContext: MomentContext) override fun setup(
            preparationTickStream: EventStream<Unit>,
            propagationTickStream: EventStream<Unit>,
        ): CellProvider<ValueT> {
            val holdCell = propagationTickStream.mapNotNull { newValue }.hold(
                initialValue = initialValue,
            )

            return object : CellProvider<ValueT> {
                override fun provide(): Cell<ValueT> = holdCell
            }
        }
    }

    class MapToStringCellSetup<ValueT>(
        private val sourceSetup: CellSetup<ValueT>,
    ) : CellSetup<String> {
        companion object {
            fun <ValueT : Any> configure(
                initialSourceValue: ValueT,
                newSourceValue: ValueT? = null,
            ): MapToStringCellSetup<ValueT> = MapToStringCellSetup(
                sourceSetup = HoldCellSetup(
                    initialValue = initialSourceValue,
                    newValue = newSourceValue,
                ),
            )
        }

        context(momentContext: MomentContext) override fun setup(
            preparationTickStream: EventStream<Unit>,
            propagationTickStream: EventStream<Unit>,
        ): CellProvider<String> {
            val sourceCellProvider = sourceSetup.setup(
                preparationTickStream,
                propagationTickStream,
            )

            return object : CellProvider<String> {
                override fun provide(): Cell<String> = sourceCellProvider.provide().map { it.toString() }
            }
        }
    }

    class Map2ConcatCellSetup(
        private val source1Setup: CellSetup<Int>,
        private val source2Setup: CellSetup<Char>,
    ) : CellSetup<String> {
        companion object {
            fun configure(
                initialSourceValue1: Int,
                newSourceValue1: Int? = null,
                initialSourceValue2: Char,
                newSourceValue2: Char? = null,
            ): Map2ConcatCellSetup = Map2ConcatCellSetup(
                source1Setup = HoldCellSetup(
                    initialValue = initialSourceValue1,
                    newValue = newSourceValue1,
                ),
                source2Setup = HoldCellSetup(
                    initialValue = initialSourceValue2,
                    newValue = newSourceValue2,
                ),
            )
        }

        context(momentContext: MomentContext) override fun setup(
            preparationTickStream: EventStream<Unit>,
            propagationTickStream: EventStream<Unit>,
        ): CellProvider<String> {
            val sourceCell1Provider = source1Setup.setup(
                preparationTickStream = preparationTickStream,
                propagationTickStream = propagationTickStream,
            )

            val sourceCell2Provider = source2Setup.setup(
                preparationTickStream = preparationTickStream,
                propagationTickStream = propagationTickStream,
            )

            return object : CellProvider<String> {
                override fun provide(): Cell<String> = Cell.map2(
                    sourceCell1Provider.provide(),
                    sourceCell2Provider.provide(),
                ) { value1, value2 ->
                    "$value1:$value2"
                }
            }
        }
    }

    class SwitchCellSetup<ValueT>(
        private val initialInnerCellSetup: CellSetup<ValueT>? = null,
        private val oldInnerCellSetup: CellSetup<ValueT>,
        private val newInnerCellSetup: CellSetup<ValueT>,
        private val shouldSwitch: Boolean,
    ) : CellSetup<ValueT> {
        context(momentContext: MomentContext) override fun setup(
            preparationTickStream: EventStream<Unit>,
            propagationTickStream: EventStream<Unit>,
        ): CellProvider<ValueT> {
            val effectiveInitialInnerCellSetup = initialInnerCellSetup ?: oldInnerCellSetup

            val initialInnerCellProvider = effectiveInitialInnerCellSetup.setup(
                preparationTickStream = preparationTickStream,
                propagationTickStream = propagationTickStream,
            )

            val oldInnerCellProvider = oldInnerCellSetup.setup(
                preparationTickStream = preparationTickStream,
                propagationTickStream = propagationTickStream,
            )

            val newInnerCellProvider = newInnerCellSetup.setup(
                preparationTickStream = preparationTickStream,
                propagationTickStream = propagationTickStream,
            )

            val outerCell = EventStream.merge2(
                preparationTickStream.map {
                    oldInnerCellProvider.provide()
                },
                propagationTickStream.mapNotNull {
                    when {
                        shouldSwitch -> newInnerCellProvider.provide()
                        else -> null
                    }
                },
            ).hold(
                initialValue = initialInnerCellProvider.provide(),
            )

            return object : CellProvider<ValueT> {
                override fun provide(): Cell<ValueT> = Cell.switch(outerCell)
            }
        }
    }

    context(momentContext: MomentContext) fun setup(
        preparationTickStream: EventStream<Unit>,
        propagationTickStream: EventStream<Unit>,
    ): CellProvider<ValueT>
}


interface SamplingStrategy<ValueT> {
    class Inactive<ValueT> : SamplingStrategy<ValueT> {
        override fun prepare(subjectCell: Cell<ValueT>) {
        }
    }

    class Active<ValueT> : SamplingStrategy<ValueT> {
        override fun prepare(subjectCell: Cell<ValueT>) {
            subjectCell.updatedValues.subscribe { }
        }
    }

    fun prepare(
        subjectCell: Cell<ValueT>,
    )
}


fun <ValueT> CellSetup<ValueT>.testSampleInitial(
    strategy: SamplingStrategy<ValueT>,
    expectedInitialValue: ValueT,
) {
    val preparationTickStream = EmitterEventStream<Unit>()

    val subjectCell = MomentContext.execute {
        setup(
            preparationTickStream = preparationTickStream,
            propagationTickStream = NeverEventStream,
        )
    }.provide()

    preparationTickStream.emit(Unit)

    strategy.prepare(
        subjectCell = subjectCell,
    )

    val sampledInitialValue = MomentContext.execute {
        subjectCell.sample()
    }

    assertEquals(
        expected = expectedInitialValue,
        actual = sampledInitialValue,
    )
}

fun <ValueT> CellSetup<ValueT>.testSampleNew(
    strategy: SamplingStrategy<ValueT>,
    expectedNewValue: ValueT,
) {
    val preparationTickStream = EmitterEventStream<Unit>()

    val propagationTickStream = EmitterEventStream<Unit>()

    val subjectCell = MomentContext.execute {
        setup(
            preparationTickStream = preparationTickStream,
            propagationTickStream = propagationTickStream,
        )
    }.provide()

    preparationTickStream.emit(Unit)

    strategy.prepare(
        subjectCell = subjectCell,
    )

    propagationTickStream.emit(Unit)

    val sampledNewValue = MomentContext.execute {
        subjectCell.sample()
    }

    assertEquals(
        expected = expectedNewValue,
        actual = sampledNewValue,
    )
}

interface UpdatePropagationStrategy<ValueT> {
    class UpdatedValues<ValueT> : UpdatePropagationStrategy<ValueT> {
        context(momentContext: MomentContext) override fun setup(
            tickStream: EmitterEventStream<Unit>,
            subjectCell: Cell<ValueT>,
        ): EventStream<ValueT> = subjectCell.updatedValues
    }

    class NewValues<ValueT> : UpdatePropagationStrategy<ValueT> {
        context(momentContext: MomentContext) override fun setup(
            tickStream: EmitterEventStream<Unit>,
            subjectCell: Cell<ValueT>,
        ): EventStream<ValueT> = subjectCell.newValues
    }

    class Switch<ValueT : Any> : UpdatePropagationStrategy<ValueT> {
        context(momentContext: MomentContext) override fun setup(
            tickStream: EmitterEventStream<Unit>,
            subjectCell: Cell<ValueT>,
        ): EventStream<ValueT?> {
            val intermediateOuterCell = tickStream.map {
                subjectCell
            }.hold(
                initialValue = Cell.of(null),
            )

            val intermediateSwitchCell = Cell.switch(intermediateOuterCell)

            return intermediateSwitchCell.updatedValues
        }
    }

    context(momentContext: MomentContext) fun setup(
        tickStream: EmitterEventStream<Unit>,
        subjectCell: Cell<ValueT>,
    ): EventStream<ValueT?>
}

fun <ValueT : Any> CellSetup<ValueT>.testUpdatePropagation(
    strategy: UpdatePropagationStrategy<ValueT>,
    expectedUpdatedValue: ValueT?,
) {
    val preparationTickStream = EmitterEventStream<Unit>()

    val propagationTickStream = EmitterEventStream<Unit>()

    val subjectCell = MomentContext.execute {
        setup(
            preparationTickStream = preparationTickStream,
            propagationTickStream = propagationTickStream,
        )
    }.provide()

    preparationTickStream.emit(Unit)

    val collectedUpdatedValues = mutableListOf<ValueT?>()

    val valueStream = MomentContext.execute {
        strategy.setup(
            tickStream = propagationTickStream,
            subjectCell = subjectCell,
        )
    }

    valueStream.subscribeCollecting(
        targetList = collectedUpdatedValues,
    )

    propagationTickStream.emit(Unit)

    val expectedUpdatedValues: List<ValueT?> = listOfNotNull(expectedUpdatedValue)

    assertEquals(
        expected = expectedUpdatedValues,
        actual = collectedUpdatedValues,
    )
}

fun <ValueT> CellSetup<ValueT>.testUpdatePropagationDeactivated() {
    val preparationTickStream = EmitterEventStream<Unit>()

    val propagationTickStream = EmitterEventStream<Unit>()

    val subjectCell = MomentContext.execute {
        setup(
            preparationTickStream = preparationTickStream,
            propagationTickStream = propagationTickStream,
        )
    }.provide()

    preparationTickStream.emit(Unit)

    val collectedEvents = mutableListOf<ValueT>()

    val subscription = assertNotNull(
        subjectCell.updatedValues.subscribeCollecting(
            targetList = collectedEvents,
        ),
    )

    subscription.cancel()

    propagationTickStream.emit(Unit)

    assertEquals(
        expected = emptyList(),
        actual = collectedEvents,
    )
}
