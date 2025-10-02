package dev.toolkt.reactive.cell

import dev.toolkt.reactive.cell.test_utils.CellVerificationStrategy
import dev.toolkt.reactive.cell.test_utils.DynamicCellFactory
import dev.toolkt.reactive.cell.test_utils.StaticCellFactory
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.emit
import dev.toolkt.reactive.event_stream.map
import kotlin.test.Ignore
import kotlin.test.Test

@Suppress("ClassName")
class Cell_switch_combo_tests {
    private fun test_initial(
        outerCellFactory: StaticCellFactory,
        innerCellFactory: StaticCellFactory,
        verificationStrategy: CellVerificationStrategy.Total,
    ) {
        val outerCell = outerCellFactory.createExternally(
            innerCellFactory.createExternally(10),
        )

        val switchCell = Cell.switch(outerCell)

        verificationStrategy.begin(
            subjectCell = switchCell,
        ).verifyCurrentValue(
            expectedCurrentValue = 10,
        )
    }

    private fun test_initial(
        verificationStrategy: CellVerificationStrategy.Total,
    ) {
        StaticCellFactory.values.forEach { outerCellFactory ->
            StaticCellFactory.values.forEach { innerCellFactory ->
                test_initial(
                    outerCellFactory = outerCellFactory,
                    innerCellFactory = innerCellFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
        }
    }

    @Test
    fun test_initial_passive() {
        test_initial(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Ignore // FIXME: Subscription should not be null.
    @Test
    fun test_initial_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_initial(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_initialInnerUpdate(
        outerCellFactory: StaticCellFactory,
        initialInnerCellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doUpdateInner = EmitterEventStream<Unit>()

        val initialInnerCell = initialInnerCellFactory.createExternally(
            initialValue = 10,
            doUpdate = doUpdateInner.map { 20 },
        )

        val outerCell = outerCellFactory.createExternally(initialInnerCell)

        val switchCell = Cell.switch(outerCell)

        val verifier = verificationStrategy.begin(
            subjectCell = switchCell,
        )

        verifier.verifyUpdates(
            doTrigger = doUpdateInner,
            expectedUpdatedValue = 20,
        )
    }

    private fun test_initialInnerUpdate(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { initialInnerCellFactory ->
            StaticCellFactory.values.forEach { outerCellFactory ->
                test_initialInnerUpdate(
                    outerCellFactory = outerCellFactory,
                    initialInnerCellFactory = initialInnerCellFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
        }
    }

    @Test
    fun test_initialInnerUpdate_passive() {
        test_initialInnerUpdate(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_initialInnerUpdate_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_initialInnerUpdate(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_initialInnerUpdate_quick() {
        test_initialInnerUpdate(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    private fun test_outerUpdate(
        outerCellFactory: DynamicCellFactory,
        newInnerCellFactory: StaticCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doUpdateOuter = EmitterEventStream<Unit>()

        val initialInnerCell = Cell.of(10)

        val newInnerCell = newInnerCellFactory.createExternally(20)

        val outerCell = outerCellFactory.createExternally(
            initialValue = initialInnerCell,
            doUpdate = doUpdateOuter.map { newInnerCell },
        )

        val switchCell = Cell.switch(outerCell)

        val verifier = verificationStrategy.begin(
            subjectCell = switchCell,
        )

        verifier.verifyUpdates(
            doTrigger = doUpdateOuter,
            expectedUpdatedValue = 20,
        )
    }

    private fun test_outerUpdate(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { outerCellFactory ->
            StaticCellFactory.values.forEach { innerCellFactory ->
                test_outerUpdate(
                    outerCellFactory = outerCellFactory,
                    newInnerCellFactory = innerCellFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
        }
    }

    @Test
    fun test_outerUpdate_passive() {
        test_outerUpdate(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerUpdate(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerUpdate_quick() {
        test_outerUpdate(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    private fun test_outerUpdate_sameCell(
        outerCellFactory: DynamicCellFactory,
        innerCellFactory: StaticCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doUpdateOuter = EmitterEventStream<Unit>()

        val innerCell = innerCellFactory.createExternally(20)

        val outerCell = outerCellFactory.createExternally(
            initialValue = innerCell,
            doUpdate = doUpdateOuter.map { innerCell },
        )

        val switchCell = Cell.switch(outerCell)

        val verifier = verificationStrategy.begin(
            subjectCell = switchCell,
        )

        verifier.verifyUpdates(
            doTrigger = doUpdateOuter,
            expectedUpdatedValue = 20,
        )
    }

    private fun test_outerUpdate_sameCell(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { outerCellFactory ->
            StaticCellFactory.values.forEach { innerCellFactory ->
                test_outerUpdate_sameCell(
                    outerCellFactory = outerCellFactory,
                    innerCellFactory = innerCellFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
        }
    }

    @Test
    fun test_outerUpdate_sameCell_passive() {
        test_outerUpdate_sameCell(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_sameCell_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerUpdate_sameCell(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerUpdate_sameCell_quick() {
        test_outerUpdate(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    private fun test_outerUpdate_thenInitialInnerUpdate(
        outerCellFactory: DynamicCellFactory,
        initialInnerCellFactory: DynamicCellFactory,
        newInnerCellFactory: StaticCellFactory,
        verificationStrategy: CellVerificationStrategy.Total,
    ) {
        val doUpdateOuter = EmitterEventStream<Unit>()

        val doUpdateInitialInner = EmitterEventStream<Unit>()

        val initialInnerCell = initialInnerCellFactory.createExternally(
            initialValue = 10,
            doUpdate = doUpdateInitialInner.map { 11 },
        )

        val newInnerCell = newInnerCellFactory.createExternally(20)

        val outerCell = outerCellFactory.createExternally(
            initialValue = initialInnerCell,
            doUpdate = doUpdateOuter.map { newInnerCell },
        )

        val switchCell = Cell.switch(outerCell)

        val verifier = verificationStrategy.begin(
            subjectCell = switchCell,
        )

        doUpdateOuter.emit()

        verifier.verifyDoesNotUpdate(
            doTrigger = doUpdateInitialInner,
            expectedNonUpdatedValue = 20,
        )
    }

    private fun test_outerUpdate_thenInitialInnerUpdate(
        verificationStrategy: CellVerificationStrategy.Total,
    ) {
        DynamicCellFactory.values.forEach { outerCellFactory ->
            DynamicCellFactory.values.forEach { initialInnerCellFactory ->
                StaticCellFactory.values.forEach { newInnerCellFactory ->
                    test_outerUpdate_thenInitialInnerUpdate(
                        outerCellFactory = outerCellFactory,
                        initialInnerCellFactory = initialInnerCellFactory,
                        newInnerCellFactory = newInnerCellFactory,
                        verificationStrategy = verificationStrategy,
                    )
                }
            }
        }
    }

    @Test
    fun test_outerUpdate_thenInitialInnerUpdate_passive() {
        test_outerUpdate_thenInitialInnerUpdate(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_thenInitialInnerUpdate_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerUpdate_thenInitialInnerUpdate(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_outerUpdate_thenNewInnerUpdate(
        outerCellFactory: DynamicCellFactory,
        initialInnerCellFactory: StaticCellFactory,
        newInnerCellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doUpdateOuter = EmitterEventStream<Unit>()

        val doUpdateNewInner = EmitterEventStream<Unit>()

        val initialInnerCell = initialInnerCellFactory.createExternally(10)

        val newInnerCell = newInnerCellFactory.createExternally(
            initialValue = 20,
            doUpdate = doUpdateNewInner.map { 21 },
        )

        val outerCell = outerCellFactory.createExternally(
            initialValue = initialInnerCell,
            doUpdate = doUpdateOuter.map { newInnerCell },
        )

        val switchCell = Cell.switch(outerCell)

        val verifier = verificationStrategy.begin(
            subjectCell = switchCell,
        )

        doUpdateOuter.emit()

        verifier.verifyUpdates(
            doTrigger = doUpdateNewInner,
            expectedUpdatedValue = 21,
        )
    }

    private fun test_outerUpdate_thenNewInnerUpdate(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { outerCellFactory ->
            StaticCellFactory.values.forEach { initialInnerCellFactory ->
                DynamicCellFactory.values.forEach { newInnerCellFactory ->
                    test_outerUpdate_thenNewInnerUpdate(
                        outerCellFactory = outerCellFactory,
                        initialInnerCellFactory = initialInnerCellFactory,
                        newInnerCellFactory = newInnerCellFactory,
                        verificationStrategy = verificationStrategy,
                    )
                }
            }
        }
    }

    @Test
    fun test_outerUpdate_thenNewInnerUpdate_passive() {
        test_outerUpdate_thenNewInnerUpdate(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_thenNewInnerUpdate_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerUpdate_thenNewInnerUpdate(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerUpdate_thenNewInnerUpdate_quick() {
        test_outerUpdate_thenNewInnerUpdate(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    private fun test_outerUpdate_simultaneousInitialInnerUpdate(
        outerCellFactory: DynamicCellFactory,
        initialInnerCellFactory: DynamicCellFactory,
        newInnerCellFactory: StaticCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doSwitch = EmitterEventStream<Unit>()

        val initialInnerCell = initialInnerCellFactory.createExternally(
            initialValue = 10,
            doUpdate = doSwitch.map { 11 },
        )

        val newInnerCell = newInnerCellFactory.createExternally(20)

        val outerCell = outerCellFactory.createExternally(
            initialValue = initialInnerCell,
            doUpdate = doSwitch.map { newInnerCell },
        )

        val switchCell = Cell.switch(outerCell)

        val verifier = verificationStrategy.begin(
            subjectCell = switchCell,
        )

        verifier.verifyUpdates(
            doTrigger = doSwitch,
            expectedUpdatedValue = 20,
        )
    }

    private fun test_outerUpdate_simultaneousInitialInnerUpdate(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { outerCellFactory ->
            DynamicCellFactory.values.forEach { initialInnerCellFactory ->
                StaticCellFactory.values.forEach { newInnerCellFactory ->
                    test_outerUpdate_simultaneousInitialInnerUpdate(
                        outerCellFactory = outerCellFactory,
                        initialInnerCellFactory = initialInnerCellFactory,
                        newInnerCellFactory = newInnerCellFactory,
                        verificationStrategy = verificationStrategy,
                    )
                }
            }
        }
    }

    @Test
    fun test_outerUpdate_simultaneousInitialInnerUpdate_passive() {
        test_outerUpdate_simultaneousInitialInnerUpdate(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_simultaneousInitialInnerUpdate_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerUpdate_simultaneousInitialInnerUpdate(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerUpdate_simultaneousInitialInnerUpdate_quick() {
        test_outerUpdate_simultaneousInitialInnerUpdate(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    private fun test_outerUpdate_simultaneousNewInnerUpdate(
        outerCellFactory: DynamicCellFactory,
        initialInnerCellFactory: StaticCellFactory,
        newInnerCellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doSwitch = EmitterEventStream<Unit>()

        val initialInnerCell = initialInnerCellFactory.createExternally(10)

        val newInnerCell = newInnerCellFactory.createExternally(
            initialValue = 20,
            doUpdate = doSwitch.map { 21 },
        )

        val outerCell = outerCellFactory.createExternally(
            initialValue = initialInnerCell,
            doUpdate = doSwitch.map { newInnerCell },
        )

        val switchCell = Cell.switch(outerCell)

        val verifier = verificationStrategy.begin(
            subjectCell = switchCell,
        )

        verifier.verifyUpdates(
            doTrigger = doSwitch,
            expectedUpdatedValue = 21,
        )
    }

    private fun test_outerUpdate_simultaneousNewInnerUpdate(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { outerCellFactory ->
            StaticCellFactory.values.forEach { initialInnerCellFactory ->
                DynamicCellFactory.values.forEach { newInnerCellFactory ->
                    test_outerUpdate_simultaneousNewInnerUpdate(
                        outerCellFactory = outerCellFactory,
                        initialInnerCellFactory = initialInnerCellFactory,
                        newInnerCellFactory = newInnerCellFactory,
                        verificationStrategy = verificationStrategy,
                    )
                }
            }
        }
    }

    @Test
    fun test_outerUpdate_simultaneousNewInnerUpdate_passive() {
        test_outerUpdate_simultaneousNewInnerUpdate(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_simultaneousNewInnerUpdate_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerUpdate_simultaneousNewInnerUpdate(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerUpdate_simultaneousNewInnerUpdate_quick() {
        test_outerUpdate_simultaneousNewInnerUpdate(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    private fun test_outerUpdate_simultaneousBothInnerUpdates(
        outerCellFactory: DynamicCellFactory,
        initialInnerCellFactory: DynamicCellFactory,
        newInnerCellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doSwitch = EmitterEventStream<Unit>()

        val initialInnerCell = initialInnerCellFactory.createExternally(
            initialValue = 10,
            doUpdate = doSwitch.map { 11 },
        )

        val newInnerCell = newInnerCellFactory.createExternally(
            initialValue = 20,
            doUpdate = doSwitch.map { 21 },
        )

        val outerCell = outerCellFactory.createExternally(
            initialValue = initialInnerCell,
            doUpdate = doSwitch.map { newInnerCell },
        )

        val switchCell = Cell.switch(outerCell)

        val verifier = verificationStrategy.begin(
            subjectCell = switchCell,
        )

        verifier.verifyUpdates(
            doTrigger = doSwitch,
            expectedUpdatedValue = 21,
        )
    }

    private fun test_outerUpdate_simultaneousBothInnerUpdates(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { outerCellFactory ->
            DynamicCellFactory.values.forEach { initialInnerCellFactory ->
                DynamicCellFactory.values.forEach { newInnerCellFactory ->
                    test_outerUpdate_simultaneousBothInnerUpdates(
                        outerCellFactory = outerCellFactory,
                        initialInnerCellFactory = initialInnerCellFactory,
                        newInnerCellFactory = newInnerCellFactory,
                        verificationStrategy = verificationStrategy,
                    )
                }
            }
        }
    }

    @Test
    fun test_outerUpdate_simultaneousBothInnerUpdates_passive() {
        test_outerUpdate_simultaneousBothInnerUpdates(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_simultaneousBothInnerUpdates_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerUpdate_simultaneousBothInnerUpdates(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerUpdate_simultaneousBothInnerUpdates_quick() {
        test_outerUpdate_simultaneousBothInnerUpdates(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    private fun test_deactivation_initial(
        outerCellFactory: DynamicCellFactory,
        initialInnerCellFactory: DynamicCellFactory,
        newInnerCellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy.Active,
    ) {
        val doSwitch = EmitterEventStream<Unit>()

        val initialInnerCell = initialInnerCellFactory.createExternally(
            initialValue = 10,
            doUpdate = doSwitch.map { 11 },
        )

        val newInnerCell = newInnerCellFactory.createExternally(
            initialValue = 20,
            doUpdate = doSwitch.map { 21 },
        )

        val outerCell = outerCellFactory.createExternally(
            initialValue = initialInnerCell,
            doUpdate = doSwitch.map { newInnerCell },
        )

        val switchCell = Cell.switch(outerCell)

        verificationStrategy.verifyDeactivation(
            subjectCell = switchCell,
            doTrigger = doSwitch,
        )
    }

    @Test
    fun test_deactivation_initial() {
        DynamicCellFactory.values.forEach { outerCellFactory ->
            DynamicCellFactory.values.forEach { initialInnerCellFactory ->
                DynamicCellFactory.values.forEach { newInnerCellFactory ->
                    CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
                        test_deactivation_initial(
                            outerCellFactory = outerCellFactory,
                            initialInnerCellFactory = initialInnerCellFactory,
                            newInnerCellFactory = newInnerCellFactory,
                            verificationStrategy = verificationStrategy,
                        )
                    }
                }
            }
        }
    }

    private fun test_deactivation_afterOuterUpdate(
        overCellFactory: DynamicCellFactory,
        initialInnerCellFactory: DynamicCellFactory,
        newInnerCellFactory: DynamicCellFactory,
        newerInnerCellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy.Active,
    ) {
        val doPrepare = EmitterEventStream<Unit>()

        val doTrigger = EmitterEventStream<Unit>()

        val initialInnerCell = initialInnerCellFactory.createExternally(
            initialValue = 10,
            doUpdate = doTrigger.map { 11 },
        )

        val newInnerCell = newInnerCellFactory.createExternally(
            initialValue = 20,
            doUpdate = doTrigger.map { 21 },
        )

        val newerInnerCell = newerInnerCellFactory.createExternally(
            initialValue = 30,
            doUpdate = doTrigger.map { 31 },
        )

        val outerCell = overCellFactory.createExternally(
            initialValue = initialInnerCell,
            doUpdate = EventStream.merge2(
                doPrepare.map { newInnerCell },
                doTrigger.map { newerInnerCell },
            ),
        )

        val switchCell = Cell.switch(outerCell)

        doPrepare.emit()

        verificationStrategy.verifyDeactivation(
            subjectCell = switchCell,
            doTrigger = doTrigger,
        )
    }

    @Test
    fun test_deactivation_afterOuterUpdate() {
        DynamicCellFactory.values.forEach { overCellFactory ->
            DynamicCellFactory.values.forEach { initialInnerCellFactory ->
                DynamicCellFactory.values.forEach { newInnerCellFactory ->
                    DynamicCellFactory.values.forEach { newerInnerCellFactory ->
                        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
                            test_deactivation_afterOuterUpdate(
                                overCellFactory = overCellFactory,
                                initialInnerCellFactory = initialInnerCellFactory,
                                newInnerCellFactory = newInnerCellFactory,
                                newerInnerCellFactory = newerInnerCellFactory,
                                verificationStrategy = verificationStrategy,
                            )
                        }
                    }
                }
            }
        }
    }
}
