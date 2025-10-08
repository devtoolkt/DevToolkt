package dev.toolkt.reactive.cell

import dev.toolkt.reactive.cell.test_utils.CellVerificationStrategy
import dev.toolkt.reactive.cell.test_utils.DynamicCellFactory
import dev.toolkt.reactive.cell.test_utils.InertCellFactory
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.emit
import dev.toolkt.reactive.event_stream.map
import kotlin.test.Test

@Suppress("ClassName")
class Cell_switch_misc_tests {
    private fun test_outerUpdateInitialInnerUpdateNewInnerUpdate(
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
            doTriggerUpdate = doSwitch,
            expectedUpdatedValue = 21,
        )
    }

    private fun test_outerUpdateInitialInnerUpdateNewInnerUpdate(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { outerCellFactory ->
            DynamicCellFactory.values.forEach { initialInnerCellFactory ->
                DynamicCellFactory.values.forEach { newInnerCellFactory ->
                    test_outerUpdateInitialInnerUpdateNewInnerUpdate(
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
    fun test_outerUpdateInitialInnerUpdateNewInnerUpdate_passive() {
        test_outerUpdateInitialInnerUpdateNewInnerUpdate(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdateInitialInnerUpdateNewInnerUpdate_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerUpdateInitialInnerUpdateNewInnerUpdate(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerUpdateInitialInnerUpdateNewInnerUpdate_quick() {
        test_outerUpdateInitialInnerUpdateNewInnerUpdate(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }


    private fun test_outerUpdateInitialInnerUpdate(
        outerCellFactory: DynamicCellFactory,
        initialInnerCellFactory: DynamicCellFactory,
        newInnerCellFactory: InertCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doSwitch = EmitterEventStream<Unit>()

        val initialInnerCell = initialInnerCellFactory.createExternally(
            initialValue = 10,
            doUpdate = doSwitch.map { 11 },
        )

        val newInnerCell = newInnerCellFactory.createExternally(
            inertValue = 20,
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
            doTriggerUpdate = doSwitch,
            expectedUpdatedValue = 20,
        )
    }

    private fun test_outerUpdateInitialInnerUpdate(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { outerCellFactory ->
            DynamicCellFactory.values.forEach { initialInnerCellFactory ->
                InertCellFactory.values.forEach { newInnerCellFactory ->
                    test_outerUpdateInitialInnerUpdate(
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
    fun test_outerUpdateInitialInnerUpdate_passive() {
        test_outerUpdateInitialInnerUpdate(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdateInitialInnerUpdate_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerUpdateInitialInnerUpdate(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerUpdateInitialInnerUpdate_quick() {
        test_outerUpdateInitialInnerUpdate(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    private fun test_outerUpdateDynamic_initialInnerUpdate(
        outerCellFactory: DynamicCellFactory,
        initialInnerCellFactory: DynamicCellFactory,
        newInnerCellFactory: InertCellFactory,
        verificationStrategy: CellVerificationStrategy,
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
            doTriggerPotentialUpdate = doUpdateInitialInner,
            expectedNonUpdatedValue = 20,
        )
    }

    private fun test_outerUpdateDynamic_initialInnerUpdate(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { outerCellFactory ->
            DynamicCellFactory.values.forEach { initialInnerCellFactory ->
                InertCellFactory.values.forEach { newInnerCellFactory ->
                    test_outerUpdateDynamic_initialInnerUpdate(
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
    fun test_outerUpdateDynamic_initialInnerUpdate_passive() {
        test_outerUpdateDynamic_initialInnerUpdate(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdateDynamic_initialInnerUpdate_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerUpdateDynamic_initialInnerUpdate(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerUpdateDynamic_initialInnerUpdate_quick() {
        test_outerUpdateDynamic_initialInnerUpdate(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    private fun test_outerUpdate_sameCell(
        outerCellFactory: DynamicCellFactory,
        innerCellFactory: InertCellFactory,
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
            doTriggerUpdate = doUpdateOuter,
            expectedUpdatedValue = 20,
        )
    }

    private fun test_outerUpdate_sameCell(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { outerCellFactory ->
            InertCellFactory.values.forEach { innerCellFactory ->
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
        test_outerUpdate_sameCell(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    private fun test_deactivation(
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
    fun test_deactivation() {
        DynamicCellFactory.values.forEach { outerCellFactory ->
            DynamicCellFactory.values.forEach { initialInnerCellFactory ->
                DynamicCellFactory.values.forEach { newInnerCellFactory ->
                    CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
                        test_deactivation(
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

    private fun test_outerUpdate_deactivation(
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
    fun test_outerUpdate_deactivation() {
        DynamicCellFactory.values.forEach { overCellFactory ->
            DynamicCellFactory.values.forEach { initialInnerCellFactory ->
                DynamicCellFactory.values.forEach { newInnerCellFactory ->
                    DynamicCellFactory.values.forEach { newerInnerCellFactory ->
                        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
                            test_outerUpdate_deactivation(
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
