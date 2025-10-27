package dev.toolkt.reactive.cell.map4

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.test_utils.ConfigurationContext
import dev.toolkt.reactive.cell.test_utils.Tick
import dev.toolkt.reactive.cell.test_utils.TickAlike
import dev.toolkt.reactive.cell.test_utils.buildStatelessCellTestScenario
import dev.toolkt.reactive.cell.test_utils.defineDynamicCell

object Map4GenericScenario {
    data class Input<
            ValueT1,
            ValueT2,
            ValueT3,
            ValueT4,
            >(
        val sourceCell1: Cell<ValueT1>,
        val sourceCell2: Cell<ValueT2>,
        val sourceCell3: Cell<ValueT3>,
        val sourceCell4: Cell<ValueT4>,
    )

    enum class TimelineTick : TickAlike {
        /** */
        Initial,

        /** */
        Source4EarlierUpdateTick,

        /** */
        PostSource4EarlierUpdateTick,

        /** */
        Source2EarlierUpdateTick,

        /** */
        PostSource2EarlierUpdateTick,

        /** */
        Source1EarlierUpdateTick,

        /** */
        PostSource1EarlierUpdateTick,

        /** */
        Source3EarlierUpdateTick,

        /** */
        PostSource3EarlierUpdateTick,

        /** */
        PreFinalSourceUpdateTick,

        /** */
        FinalSourceUpdateTick;

        override val asTick: Tick
            get() = Tick(t = this.ordinal)
    }

    private object Constants {
        const val earliestValue1 = 10
        const val earlierValue1 = 11
        const val finalValue1 = 12

        const val earliestValue2 = 'A'
        const val earlierValue2 = 'B'
        const val finalValue2 = 'C'

        const val earliestValue3 = "xX"
        const val earlierValue3 = "yY"
        const val finalValue3 = "zZ"

        const val earliestValue4 = 4.0
        const val earlierValue4 = 4.1
        const val finalValue4 = 4.2
    }

    sealed class SourceCellDefinition<ValueT : Any> {
        data object SourceCell1Definition : SourceCellDefinition<Int>() {
            override val earlierUpdateTick: TimelineTick = TimelineTick.Source1EarlierUpdateTick
            override val postEarlierUpdateTick: TimelineTick = TimelineTick.PostSource1EarlierUpdateTick
            override val earliestValue: Int = Constants.earliestValue1
            override val earlierValue: Int = Constants.earlierValue1
            override val finalValue: Int = Constants.finalValue1
        }

        data object SourceCell2Definition : SourceCellDefinition<Char>() {
            override val earlierUpdateTick: TimelineTick = TimelineTick.Source2EarlierUpdateTick
            override val postEarlierUpdateTick: TimelineTick = TimelineTick.PostSource2EarlierUpdateTick
            override val earliestValue: Char = Constants.earliestValue2
            override val earlierValue: Char = Constants.earlierValue2
            override val finalValue: Char = Constants.finalValue2
        }

        data object SourceCell3Definition : SourceCellDefinition<String>() {
            override val earlierUpdateTick: TimelineTick = TimelineTick.Source3EarlierUpdateTick
            override val postEarlierUpdateTick: TimelineTick = TimelineTick.PostSource3EarlierUpdateTick
            override val earliestValue: String = Constants.earliestValue3
            override val earlierValue: String = Constants.earlierValue3
            override val finalValue: String = Constants.finalValue3
        }

        data object SourceCell4Definition : SourceCellDefinition<Double>() {
            override val earlierUpdateTick: TimelineTick = TimelineTick.Source4EarlierUpdateTick
            override val postEarlierUpdateTick: TimelineTick = TimelineTick.PostSource4EarlierUpdateTick
            override val earliestValue: Double = Constants.earliestValue4
            override val earlierValue: Double = Constants.earlierValue4
            override val finalValue: Double = Constants.finalValue4
        }

        abstract val earlierUpdateTick: TimelineTick
        abstract val postEarlierUpdateTick: TimelineTick
        abstract val earliestValue: ValueT
        abstract val earlierValue: ValueT
        abstract val finalValue: ValueT
    }

    sealed class SourceCellVariant {
        sealed class UpdatingNow : SourceCellVariant() {
            data object FreezingNow : UpdatingNow() {
                override val freezeTick = TimelineTick.FinalSourceUpdateTick
            }

            data object NotFreezingNow : UpdatingNow() {
                override val freezeTick = null
            }

            context(context: ConfigurationContext) final override fun <ValueT : Any> createSourceCellExternally(
                definition: SourceCellDefinition<ValueT>,
            ): Cell<ValueT> = defineDynamicCell(
                initialValue = definition.earliestValue,
                updatedValueByTick = mapOf(
                    definition.earlierUpdateTick to definition.earlierValue,
                    TimelineTick.FinalSourceUpdateTick to definition.finalValue,
                ),
                freezeTick = null,
            )

            abstract val freezeTick: TimelineTick?
        }

        sealed class NotUpdatingNow : SourceCellVariant() {
            sealed class FrozenNow : NotUpdatingNow() {
                data object Const : FrozenNow() {
                    context(context: ConfigurationContext) override fun <ValueT : Any> createSourceCellExternally(
                        definition: SourceCellDefinition<ValueT>,
                    ): Cell<ValueT> {
                        TODO("Not yet implemented")
                    }
                }

                data object FrozeAtEarlierUpdate : FrozenNow() {
                    context(context: ConfigurationContext) override fun <ValueT : Any> createSourceCellExternally(
                        definition: SourceCellDefinition<ValueT>,
                    ): Cell<ValueT> {
                        TODO("Not yet implemented")
                    }
                }

                data object FrozeAfterEarlierUpdate : FrozenNow() {
                    context(context: ConfigurationContext) override fun <ValueT : Any> createSourceCellExternally(
                        definition: SourceCellDefinition<ValueT>,
                    ): Cell<ValueT> {
                        TODO("Not yet implemented")
                    }

                }
            }

            sealed class WarmNow : NotUpdatingNow() {
                data object FreezingNow : WarmNow() {
                    context(context: ConfigurationContext) override fun <ValueT : Any> createSourceCellExternally(
                        definition: SourceCellDefinition<ValueT>,
                    ): Cell<ValueT> {
                        TODO("Not yet implemented")
                    }
                }

                data object NotFreezingNow : WarmNow() {
                    context(context: ConfigurationContext) override fun <ValueT : Any> createSourceCellExternally(
                        definition: SourceCellDefinition<ValueT>,
                    ): Cell<ValueT> {
                        TODO("Not yet implemented")
                    }
                }
            }
        }

        context(context: ConfigurationContext) abstract fun <ValueT : Any> createSourceCellExternally(
            definition: SourceCellDefinition<ValueT>,
        ): Cell<ValueT>
    }

    fun build(
        sourceCell1Variant: SourceCellVariant,
        sourceCell2Variant: SourceCellVariant,
        sourceCell3Variant: SourceCellVariant,
        sourceCell4Variant: SourceCellVariant,
        shouldExpectFreeze: Boolean,
    ) = buildStatelessCellTestScenario(
        configure = {
            val sourceCell1 = sourceCell1Variant.createSourceCellExternally(
                definition = SourceCellDefinition.SourceCell1Definition,
            )

            val sourceCell2 = sourceCell2Variant.createSourceCellExternally(
                definition = SourceCellDefinition.SourceCell2Definition,
            )

            val sourceCell3 = sourceCell3Variant.createSourceCellExternally(
                definition = SourceCellDefinition.SourceCell3Definition,
            )

            val sourceCell4 = sourceCell4Variant.createSourceCellExternally(
                definition = SourceCellDefinition.SourceCell4Definition,
            )

            Input(
                sourceCell1 = sourceCell1,
                sourceCell2 = sourceCell2,
                sourceCell3 = sourceCell3,
                sourceCell4 = sourceCell4,
            )
        },
        instantiate = {
            Cell.Companion.map4(
                sourceCell1,
                sourceCell2,
                sourceCell3,
                sourceCell4,
            ) { value1, value2, value3, value4 ->
                "$value1:$value2:$value3:$value4"
            }
        },
        verificationTick = TimelineTick.FinalSourceUpdateTick,
        expectedUpdatedValue = "${Constants.finalValue1}:${Constants.finalValue2}:${Constants.finalValue3}:${Constants.finalValue4}",
        shouldExpectFreeze = shouldExpectFreeze,
    )
}