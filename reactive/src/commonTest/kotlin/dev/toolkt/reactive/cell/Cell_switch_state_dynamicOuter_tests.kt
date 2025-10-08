package dev.toolkt.reactive.cell

import dev.toolkt.core.platform.PlatformWeakReference
import dev.toolkt.reactive.cell.test_utils.CellVerificationStrategy
import dev.toolkt.reactive.cell.test_utils.DynamicCellFactory
import dev.toolkt.reactive.cell.test_utils.InertCellFactory
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.emit
import dev.toolkt.reactive.event_stream.map
import kotlin.test.Test

@Suppress("ClassName")
class Cell_switch_state_dynamicOuter_tests {
    private data class Setup<ValueT>(
        val innerCellWeakRef: PlatformWeakReference<Cell<ValueT>>,
        val outerCellWeakRef: PlatformWeakReference<Cell<Cell<ValueT>>>,
        val switchCell: Cell<ValueT>,
    )

    /**
     * Case 5)
     */
    private fun test_outerFrozenUpdateDynamicNewInnerFrozenUpdate(
        outerCellFactory: DynamicCellFactory,
        newInnerCellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doTriggerOuterInnerUpdate = EmitterEventStream<Unit>()

        val initialInnerCell = Cell.of(10)

        val setup = run {
            val newInnerCell = newInnerCellFactory.createFreezingExternally(
                initialValue = 20,
                doUpdateFreezing = doTriggerOuterInnerUpdate.map { 21 },
            )

            val outerCell = outerCellFactory.createFreezingExternally(
                initialValue = initialInnerCell,
                doUpdateFreezing = doTriggerOuterInnerUpdate.map { newInnerCell },
            )

            val switchCell = Cell.switch(outerCell)

            Setup(
                innerCellWeakRef = PlatformWeakReference(newInnerCell),
                outerCellWeakRef = PlatformWeakReference(outerCell),
                switchCell = switchCell,
            )
        }

        val verifier = verificationStrategy.begin(
            subjectCell = setup.switchCell,
        )

        verifier.verifyUpdates(
            doTriggerUpdate = doTriggerOuterInnerUpdate,
            expectedUpdatedValue = 21,
        )
    }

    private fun test_outerFrozenUpdateDynamicNewInnerFrozenUpdate(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { outerCellFactory ->
            DynamicCellFactory.values.forEach { newInnerCellFactory ->
                test_outerFrozenUpdateDynamicNewInnerFrozenUpdate(
                    outerCellFactory = outerCellFactory,
                    newInnerCellFactory = newInnerCellFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
        }
    }

    @Test
    fun test_outerFrozenUpdateDynamicNewInnerFrozenUpdate_passive() {
        test_outerFrozenUpdateDynamicNewInnerFrozenUpdate(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerFrozenUpdateDynamicNewInnerFrozenUpdate_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerFrozenUpdateDynamicNewInnerFrozenUpdate(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerFrozenUpdateDynamicNewInnerFrozenUpdate_quick() {
        test_outerFrozenUpdateDynamicNewInnerFrozenUpdate(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    /**
     * Case 4)
     */
    private fun test_outerFrozenUpdateDynamicNewInnerWarmUpdate_newInnerFreeze(
        outerCellFactory: DynamicCellFactory,
        newInnerCellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doTriggerOuterInnerUpdate = EmitterEventStream<Unit>()
        val doFreezeInner = EmitterEventStream<Unit>()

        val initialInnerCell = Cell.of(10)

        val setup = run {
            val newInnerCell = newInnerCellFactory.createFreezingLaterExternally(
                initialValue = 20,
                doUpdate = doTriggerOuterInnerUpdate.map { 21 },
                doFreezeLater = doFreezeInner,
            )

            val outerCell = outerCellFactory.createFreezingExternally(
                initialValue = initialInnerCell,
                doUpdateFreezing = doTriggerOuterInnerUpdate.map { newInnerCell },
            )

            val switchCell = Cell.switch(outerCell)

            Setup(
                innerCellWeakRef = PlatformWeakReference(newInnerCell),
                outerCellWeakRef = PlatformWeakReference(outerCell),
                switchCell = switchCell,
            )
        }

        val verifier = verificationStrategy.begin(
            subjectCell = setup.switchCell,
        )

        verifier.verifyUpdates(
            doTriggerUpdate = doTriggerOuterInnerUpdate,
            expectedUpdatedValue = 21,
        )

        verifier.verifyDoesNotUpdate(
            doTriggerPotentialUpdate = doFreezeInner,
            expectedNonUpdatedValue = 21,
        )
    }

    private fun test_outerFrozenUpdateDynamicNewInnerWarmUpdate_newInnerFreeze(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { outerCellFactory ->
            DynamicCellFactory.values.forEach { newInnerCellFactory ->
                test_outerFrozenUpdateDynamicNewInnerWarmUpdate_newInnerFreeze(
                    outerCellFactory = outerCellFactory,
                    newInnerCellFactory = newInnerCellFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
        }
    }

    @Test
    fun test_outerFrozenUpdateDynamicNewInnerWarmUpdate_newInnerFreeze_passive() {
        test_outerFrozenUpdateDynamicNewInnerWarmUpdate_newInnerFreeze(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerFrozenUpdateDynamicNewInnerWarmUpdate_newInnerFreeze_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerFrozenUpdateDynamicNewInnerWarmUpdate_newInnerFreeze(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerFrozenUpdateDynamicNewInnerWarmUpdate_newInnerFreeze_quick() {
        test_outerFrozenUpdateDynamicNewInnerWarmUpdate_newInnerFreeze(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    /**
     * Case 3)
     */
    private fun test_outerWarmUpdateDynamicNewInnerWarmUpdate(
        outerCellFactory: DynamicCellFactory,
        newInnerCellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doTriggerOuterInnerUpdate = EmitterEventStream<Unit>()

        val initialInnerCell = Cell.of(10)

        val newInnerCell = newInnerCellFactory.createExternally(
            initialValue = 20,
            doUpdate = doTriggerOuterInnerUpdate.map { 21 },
        )

        val outerCell = outerCellFactory.createExternally(
            initialValue = initialInnerCell,
            doUpdate = doTriggerOuterInnerUpdate.map { newInnerCell },
        )

        val switchCell = Cell.switch(outerCell)

        val verifier = verificationStrategy.begin(
            subjectCell = switchCell,
        )

        verifier.verifyUpdates(
            doTriggerUpdate = doTriggerOuterInnerUpdate,
            expectedUpdatedValue = 21,
        )
    }

    private fun test_outerWarmUpdateDynamicNewInnerWarmUpdate(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { outerCellFactory ->
            DynamicCellFactory.values.forEach { newInnerCellFactory ->
                test_outerWarmUpdateDynamicNewInnerWarmUpdate(
                    outerCellFactory = outerCellFactory,
                    newInnerCellFactory = newInnerCellFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
        }
    }

    @Test
    fun test_outerWarmUpdateDynamicNewInnerWarmUpdate_passive() {
        test_outerWarmUpdateDynamicNewInnerWarmUpdate(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerWarmUpdateDynamicNewInnerWarmUpdate_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerWarmUpdateDynamicNewInnerWarmUpdate(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerWarmUpdateDynamicNewInnerWarmUpdate_quick() {
        test_outerWarmUpdateDynamicNewInnerWarmUpdate(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    /**
     * Case 3) c)
     */
    private fun test_outerWarmUpdateDynamicNewInnerWarmUpdate_outerFreezeInnerFreeze(
        outerCellFactory: DynamicCellFactory,
        newInnerCellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doTriggerOuterInnerUpdate = EmitterEventStream<Unit>()
        val doFreezeOuterInner = EmitterEventStream<Unit>()

        val initialInnerCell = Cell.of(10)

        val setup = run {
            val newInnerCell = newInnerCellFactory.createFreezingLaterExternally(
                initialValue = 20,
                doUpdate = doTriggerOuterInnerUpdate.map { 21 },
                doFreezeLater = doFreezeOuterInner,
            )

            val outerCell = outerCellFactory.createFreezingLaterExternally(
                initialValue = initialInnerCell,
                doUpdate = doTriggerOuterInnerUpdate.map { newInnerCell },
                doFreezeLater = doFreezeOuterInner,
            )

            val switchCell = Cell.switch(outerCell)

            Setup(
                innerCellWeakRef = PlatformWeakReference(newInnerCell),
                outerCellWeakRef = PlatformWeakReference(outerCell),
                switchCell = switchCell,
            )
        }

        val verifier = verificationStrategy.begin(
            subjectCell = setup.switchCell,
        )

        doTriggerOuterInnerUpdate.emit()

        verifier.verifyDoesNotUpdate(
            doTriggerPotentialUpdate = doFreezeOuterInner,
            expectedNonUpdatedValue = 21,
        )
    }

    private fun test_outerWarmUpdateDynamicNewInnerWarmUpdate_outerFreezeInnerFreeze(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { outerCellFactory ->
            DynamicCellFactory.values.forEach { newInnerCellFactory ->
                test_outerWarmUpdateDynamicNewInnerWarmUpdate_outerFreezeInnerFreeze(
                    outerCellFactory = outerCellFactory,
                    newInnerCellFactory = newInnerCellFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
        }
    }

    @Test
    fun test_outerWarmUpdateDynamicNewInnerWarmUpdate_outerFreezeInnerFreeze_passive() {
        test_outerWarmUpdateDynamicNewInnerWarmUpdate_outerFreezeInnerFreeze(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerWarmUpdateDynamicNewInnerWarmUpdate_outerFreezeInnerFreeze_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerWarmUpdateDynamicNewInnerWarmUpdate_outerFreezeInnerFreeze(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerWarmUpdateDynamicNewInnerWarmUpdate_outerFreezeInnerFreeze_quick() {
        test_outerWarmUpdateDynamicNewInnerWarmUpdate_outerFreezeInnerFreeze(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    /**
     * Case 3) a)
     */
    private fun test_outerWarmUpdateDynamicNewInnerWarmUpdate_outerFreeze_innerFreeze(
        outerCellFactory: DynamicCellFactory,
        newInnerCellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doTriggerOuterInnerUpdate = EmitterEventStream<Unit>()
        val doFreezeOuter = EmitterEventStream<Unit>()
        val doFreezeInner = EmitterEventStream<Unit>()

        val initialInnerCell = Cell.of(10)

        val setup = run {
            val newInnerCell = newInnerCellFactory.createFreezingLaterExternally(
                initialValue = 20,
                doUpdate = doTriggerOuterInnerUpdate.map { 21 },
                doFreezeLater = doFreezeInner,
            )

            val outerCell = outerCellFactory.createFreezingLaterExternally(
                initialValue = initialInnerCell,
                doUpdate = doTriggerOuterInnerUpdate.map { newInnerCell },
                doFreezeLater = doFreezeOuter,
            )

            val switchCell = Cell.switch(outerCell)

            Setup(
                innerCellWeakRef = PlatformWeakReference(newInnerCell),
                outerCellWeakRef = PlatformWeakReference(outerCell),
                switchCell = switchCell,
            )
        }

        val verifier = verificationStrategy.begin(
            subjectCell = setup.switchCell,
        )

        doTriggerOuterInnerUpdate.emit()

        verifier.verifyDoesNotUpdate(
            doTriggerPotentialUpdate = doFreezeOuter,
            expectedNonUpdatedValue = 21,
        )

        verifier.verifyDoesNotUpdate(
            doTriggerPotentialUpdate = doFreezeInner,
            expectedNonUpdatedValue = 21,
        )
    }

    private fun test_outerWarmUpdateDynamicNewInnerWarmUpdate_outerFreeze_innerFreeze(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { outerCellFactory ->
            DynamicCellFactory.values.forEach { newInnerCellFactory ->
                test_outerWarmUpdateDynamicNewInnerWarmUpdate_outerFreeze_innerFreeze(
                    outerCellFactory = outerCellFactory,
                    newInnerCellFactory = newInnerCellFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
        }
    }

    @Test
    fun test_outerWarmUpdateDynamicNewInnerWarmUpdate_outerFreeze_innerFreeze_passive() {
        test_outerWarmUpdateDynamicNewInnerWarmUpdate_outerFreeze_innerFreeze(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerWarmUpdateDynamicNewInnerWarmUpdate_outerFreeze_innerFreeze_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerWarmUpdateDynamicNewInnerWarmUpdate_outerFreeze_innerFreeze(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerWarmUpdateDynamicNewInnerWarmUpdate_outerFreeze_innerFreeze_quick() {
        test_outerWarmUpdateDynamicNewInnerWarmUpdate_outerFreeze_innerFreeze(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    /**
     * Case 3) b)
     */
    private fun test_outerWarmUpdateDynamicNewInnerWarmUpdate_innerFreeze_outerFreeze(
        outerCellFactory: DynamicCellFactory,
        newInnerCellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doTriggerOuterInnerUpdate = EmitterEventStream<Unit>()
        val doFreezeInner = EmitterEventStream<Unit>()
        val doFreezeOuter = EmitterEventStream<Unit>()

        val initialInnerCell = Cell.of(10)

        val setup = run {
            val newInnerCell = newInnerCellFactory.createFreezingLaterExternally(
                initialValue = 20,
                doUpdate = doTriggerOuterInnerUpdate.map { 21 },
                doFreezeLater = doFreezeInner,
            )

            val outerCell = outerCellFactory.createFreezingLaterExternally(
                initialValue = initialInnerCell,
                doUpdate = doTriggerOuterInnerUpdate.map { newInnerCell },
                doFreezeLater = doFreezeOuter,
            )

            val switchCell = Cell.switch(outerCell)

            Setup(
                innerCellWeakRef = PlatformWeakReference(newInnerCell),
                outerCellWeakRef = PlatformWeakReference(outerCell),
                switchCell = switchCell,
            )
        }

        val verifier = verificationStrategy.begin(
            subjectCell = setup.switchCell,
        )

        doTriggerOuterInnerUpdate.emit()

        verifier.verifyDoesNotUpdate(
            doTriggerPotentialUpdate = doFreezeInner,
            expectedNonUpdatedValue = 21,
        )

        verifier.verifyDoesNotUpdate(
            doTriggerPotentialUpdate = doFreezeOuter,
            expectedNonUpdatedValue = 21,
        )
    }

    private fun test_outerWarmUpdateDynamicNewInnerWarmUpdate_innerFreeze_outerFreeze(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { outerCellFactory ->
            DynamicCellFactory.values.forEach { newInnerCellFactory ->
                test_outerWarmUpdateDynamicNewInnerWarmUpdate_innerFreeze_outerFreeze(
                    outerCellFactory = outerCellFactory,
                    newInnerCellFactory = newInnerCellFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
        }
    }

    @Test
    fun test_outerWarmUpdateDynamicNewInnerWarmUpdate_innerFreeze_outerFreeze_passive() {
        test_outerWarmUpdateDynamicNewInnerWarmUpdate_innerFreeze_outerFreeze(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerWarmUpdateDynamicNewInnerWarmUpdate_innerFreeze_outerFreeze_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerWarmUpdateDynamicNewInnerWarmUpdate_innerFreeze_outerFreeze(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerWarmUpdateDynamicNewInnerWarmUpdate_innerFreeze_outerFreeze_quick() {
        test_outerWarmUpdateDynamicNewInnerWarmUpdate_innerFreeze_outerFreeze(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    /**
     * Case 2) (inert)
     */
    private fun test_outerFrozenUpdateInert(
        outerCellFactory: DynamicCellFactory,
        newInnerCellFactory: InertCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doTriggerOuterFrozenUpdate = EmitterEventStream<Unit>()

        val initialInnerCell = Cell.of(10)

        val newInnerCell = newInnerCellFactory.createInertExternally(
            inertValue = 20,
        )

        val outerCell = outerCellFactory.createFreezingExternally(
            initialValue = initialInnerCell,
            doUpdateFreezing = doTriggerOuterFrozenUpdate.map { newInnerCell },
        )

        val switchCell = Cell.switch(outerCell)

        val verifier = verificationStrategy.begin(
            subjectCell = switchCell,
        )

        verifier.verifyUpdates(
            doTriggerUpdate = doTriggerOuterFrozenUpdate,
            expectedUpdatedValue = 20,
        )
    }

    private fun test_outerFrozenUpdateInert(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { outerCellFactory ->
            InertCellFactory.values.forEach { newInnerCellFactory ->
                test_outerFrozenUpdateInert(
                    outerCellFactory = outerCellFactory,
                    newInnerCellFactory = newInnerCellFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
        }
    }

    @Test
    fun test_outerFrozenUpdateInert_passive() {
        test_outerFrozenUpdateInert(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerFrozenUpdateInert_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerFrozenUpdateInert(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerFrozenUpdateInert_quick() {
        test_outerFrozenUpdateInert(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    /**
     * Case 2) (dynamic)
     */
    private fun test_outerFrozenUpdateDynamic(
        outerCellFactory: DynamicCellFactory,
        newInnerCellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doTriggerOuterFrozenUpdate = EmitterEventStream<Unit>()

        val initialInnerCell = Cell.of(10)

        val newInnerCell = newInnerCellFactory.createExternally(
            initialValue = 20,
            doUpdate = EmitterEventStream(),
        )

        val outerCell = outerCellFactory.createFreezingExternally(
            initialValue = initialInnerCell,
            doUpdateFreezing = doTriggerOuterFrozenUpdate.map { newInnerCell },
        )

        val switchCell = Cell.switch(outerCell)

        val verifier = verificationStrategy.begin(
            subjectCell = switchCell,
        )

        verifier.verifyUpdates(
            doTriggerUpdate = doTriggerOuterFrozenUpdate,
            expectedUpdatedValue = 20,
        )
    }

    private fun test_outerFrozenUpdateDynamic(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { outerCellFactory ->
            DynamicCellFactory.values.forEach { newInnerCellFactory ->
                test_outerFrozenUpdateDynamic(
                    outerCellFactory = outerCellFactory,
                    newInnerCellFactory = newInnerCellFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
        }
    }

    @Test
    fun test_outerFrozenUpdateDynamic_passive() {
        test_outerFrozenUpdateDynamic(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerFrozenUpdateDynamic_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerFrozenUpdateDynamic(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerFrozenUpdateDynamic_quick() {
        test_outerFrozenUpdateDynamic(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    /**
     * Case 2) b)
     */
    private fun test_outerFrozenUpdateDynamic_newInnerFrozenUpdate(
        outerCellFactory: DynamicCellFactory,
        newInnerCellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doTriggerOuterFrozenUpdate = EmitterEventStream<Unit>()
        val doTriggerInnerFrozenUpdate = EmitterEventStream<Unit>()

        val initialInnerCell = Cell.of(10)

        val setup = run {
            val newInnerCell = newInnerCellFactory.createFreezingExternally(
                initialValue = 20,
                doUpdateFreezing = doTriggerInnerFrozenUpdate.map { 21 },
            )

            val outerCell = outerCellFactory.createFreezingExternally(
                initialValue = initialInnerCell,
                doUpdateFreezing = doTriggerOuterFrozenUpdate.map { newInnerCell },
            )

            val switchCell = Cell.switch(outerCell)

            Setup(
                innerCellWeakRef = PlatformWeakReference(newInnerCell),
                outerCellWeakRef = PlatformWeakReference(outerCell),
                switchCell = switchCell,
            )
        }

        val verifier = verificationStrategy.begin(
            subjectCell = setup.switchCell,
        )

        verifier.verifyUpdates(
            doTriggerUpdate = doTriggerOuterFrozenUpdate,
            expectedUpdatedValue = 20,
        )

        // TODO: Verify collectibility
    }

    private fun test_outerFrozenUpdateDynamic_newInnerFrozenUpdate(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { outerCellFactory ->
            DynamicCellFactory.values.forEach { newInnerCellFactory ->
                test_outerFrozenUpdateDynamic_newInnerFrozenUpdate(
                    outerCellFactory = outerCellFactory,
                    newInnerCellFactory = newInnerCellFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
        }
    }

    @Test
    fun test_outerFrozenUpdateDynamic_newInnerFrozenUpdate_passive() {
        test_outerFrozenUpdateDynamic_newInnerFrozenUpdate(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerFrozenUpdateDynamic_newInnerFrozenUpdate_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerFrozenUpdateDynamic_newInnerFrozenUpdate(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerFrozenUpdateDynamic_newInnerFrozenUpdate_quick() {
        test_outerFrozenUpdateDynamic_newInnerFrozenUpdate(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    /**
     * Case 2) a)
     */
    private fun test_outerFrozenUpdateDynamic_newInnerWarmUpdate_newInnerFreeze(
        outerCellFactory: DynamicCellFactory,
        newInnerCellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doTriggerOuterFrozenUpdate = EmitterEventStream<Unit>()
        val doTriggerInnerUpdate = EmitterEventStream<Unit>()
        val doFreezeInner = EmitterEventStream<Unit>()

        val initialInnerCell = Cell.of(10)

        val setup = run {
            val newInnerCell = newInnerCellFactory.createFreezingLaterExternally(
                initialValue = 20,
                doUpdate = doTriggerInnerUpdate.map { 21 },
                doFreezeLater = doFreezeInner,
            )

            val outerCell = outerCellFactory.createFreezingExternally(
                initialValue = initialInnerCell,
                doUpdateFreezing = doTriggerOuterFrozenUpdate.map { newInnerCell },
            )

            val switchCell = Cell.switch(outerCell)

            Setup(
                innerCellWeakRef = PlatformWeakReference(newInnerCell),
                outerCellWeakRef = PlatformWeakReference(outerCell),
                switchCell = switchCell,
            )
        }

        val verifier = verificationStrategy.begin(
            subjectCell = setup.switchCell,
        )

        verifier.verifyUpdates(
            doTriggerUpdate = doTriggerOuterFrozenUpdate,
            expectedUpdatedValue = 20,
        )

        // TODO: Verify collectibility
    }

    private fun test_outerFrozenUpdateDynamic_newInnerWarmUpdate_newInnerFreeze(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { outerCellFactory ->
            DynamicCellFactory.values.forEach { newInnerCellFactory ->
                test_outerFrozenUpdateDynamic_newInnerWarmUpdate_newInnerFreeze(
                    outerCellFactory = outerCellFactory,
                    newInnerCellFactory = newInnerCellFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
        }
    }

    @Test
    fun test_outerFrozenUpdateDynamic_newInnerWarmUpdate_newInnerFreeze_passive() {
        test_outerFrozenUpdateDynamic_newInnerWarmUpdate_newInnerFreeze(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerFrozenUpdateDynamic_newInnerWarmUpdate_newInnerFreeze_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerFrozenUpdateDynamic_newInnerWarmUpdate_newInnerFreeze(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerFrozenUpdateDynamic_newInnerWarmUpdate_newInnerFreeze_quick() {
        test_outerFrozenUpdateDynamic_newInnerWarmUpdate_newInnerFreeze(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    /**
     * Case 1) (inert)
     */
    private fun test_outerWarmUpdateInert(
        outerCellFactory: DynamicCellFactory,
        newInnerCellFactory: InertCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doTriggerOuterUpdate = EmitterEventStream<Unit>()

        val initialInnerCell = Cell.of(10)

        val newInnerCell = newInnerCellFactory.createInertExternally(
            inertValue = 20,
        )

        val outerCell = outerCellFactory.createExternally(
            initialValue = initialInnerCell,
            doUpdate = doTriggerOuterUpdate.map { newInnerCell },
        )

        val switchCell = Cell.switch(outerCell)

        val verifier = verificationStrategy.begin(
            subjectCell = switchCell,
        )

        verifier.verifyUpdates(
            doTriggerUpdate = doTriggerOuterUpdate,
            expectedUpdatedValue = 20,
        )
    }

    private fun test_outerWarmUpdateInert(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { outerCellFactory ->
            InertCellFactory.values.forEach { innerCellFactory ->
                test_outerWarmUpdateInert(
                    outerCellFactory = outerCellFactory,
                    newInnerCellFactory = innerCellFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
        }
    }

    @Test
    fun test_outerWarmUpdateInert_passive() {
        test_outerWarmUpdateInert(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerWarmUpdateInert_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerWarmUpdateInert(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerWarmUpdateInert_quick() {
        test_outerWarmUpdateInert(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    /**
     * Case 1) (dynamic)
     */
    private fun test_outerWarmUpdateDynamic(
        outerCellFactory: DynamicCellFactory,
        initialInnerCellFactory: InertCellFactory,
        newInnerCellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doUpdateOuter = EmitterEventStream<Unit>()

        val initialInnerCell = initialInnerCellFactory.createInertExternally(10)

        val newInnerCell = newInnerCellFactory.createExternally(
            initialValue = 20,
            doUpdate = EmitterEventStream<Unit>(),
        )

        val outerCell = outerCellFactory.createExternally(
            initialValue = initialInnerCell,
            doUpdate = doUpdateOuter.map { newInnerCell },
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

    private fun test_outerWarmUpdateDynamic(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { outerCellFactory ->
            InertCellFactory.values.forEach { initialInnerCellFactory ->
                DynamicCellFactory.values.forEach { newInnerCellFactory ->
                    test_outerWarmUpdateDynamic(
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
    fun test_outerWarmUpdateDynamic_passive() {
        test_outerWarmUpdateDynamic(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerWarmUpdateDynamic_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerWarmUpdateDynamic(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerWarmUpdateDynamic_quick() {
        test_outerWarmUpdateDynamic(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    /**
     * Case 1) b) (inert)
     */
    private fun test_outerWarmUpdateInert_outerFreeze(
        outerCellFactory: DynamicCellFactory,
        newInnerCellFactory: InertCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doTriggerOuterUpdate = EmitterEventStream<Unit>()
        val doFreezeOuter = EmitterEventStream<Unit>()

        val initialInnerCell = Cell.of(10)

        val newInnerCell = newInnerCellFactory.createInertExternally(
            inertValue = 20,
        )

        val outerCell = outerCellFactory.createFreezingLaterExternally(
            initialValue = initialInnerCell,
            doUpdate = doTriggerOuterUpdate.map { newInnerCell },
            doFreezeLater = doFreezeOuter,
        )

        val switchCell = Cell.switch(outerCell)

        val verifier = verificationStrategy.begin(
            subjectCell = switchCell,
        )

        doTriggerOuterUpdate.emit()

        verifier.verifyDoesNotUpdate(
            doTriggerPotentialUpdate = doFreezeOuter,
            expectedNonUpdatedValue = 20,
        )
    }

    private fun test_outerWarmUpdateInert_outerFreeze(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { outerCellFactory ->
            InertCellFactory.values.forEach { innerCellFactory ->
                test_outerWarmUpdateInert_outerFreeze(
                    outerCellFactory = outerCellFactory,
                    newInnerCellFactory = innerCellFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
        }
    }

    @Test
    fun test_outerWarmUpdateInert_outerFreeze_passive() {
        test_outerWarmUpdateInert_outerFreeze(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerWarmUpdateInert_outerFreeze_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerWarmUpdateInert_outerFreeze(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerWarmUpdateInert_outerFreeze_quick() {
        test_outerWarmUpdateInert_outerFreeze(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    /**
     * Case 1) b) (dynamic)
     */
    private fun test_outerWarmUpdateDynamic_outerFreeze(
        outerCellFactory: DynamicCellFactory,
        newInnerCellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doTriggerOuterUpdate = EmitterEventStream<Unit>()
        val doFreezeOuter = EmitterEventStream<Unit>()

        val initialInnerCell = Cell.of(10)

        val newInnerCell = newInnerCellFactory.createExternally(
            initialValue = 20,
            doUpdate = EmitterEventStream(),
        )

        val outerCell = outerCellFactory.createFreezingLaterExternally(
            initialValue = initialInnerCell,
            doUpdate = doTriggerOuterUpdate.map { newInnerCell },
            doFreezeLater = doFreezeOuter,
        )

        val switchCell = Cell.switch(outerCell)

        val verifier = verificationStrategy.begin(
            subjectCell = switchCell,
        )

        doTriggerOuterUpdate.emit()

        verifier.verifyDoesNotUpdate(
            doTriggerPotentialUpdate = doFreezeOuter,
            expectedNonUpdatedValue = 20,
        )
    }

    private fun test_outerWarmUpdateDynamic_outerFreeze(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { outerCellFactory ->
            DynamicCellFactory.values.forEach { newInnerCellFactory ->
                test_outerWarmUpdateDynamic_outerFreeze(
                    outerCellFactory = outerCellFactory,
                    newInnerCellFactory = newInnerCellFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
        }
    }

    @Test
    fun test_outerWarmUpdateDynamic_outerFreeze_passive() {
        test_outerWarmUpdateDynamic_outerFreeze(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerWarmUpdateDynamic_outerFreeze_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerWarmUpdateDynamic_outerFreeze(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerWarmUpdateDynamic_outerFreeze_quick() {
        test_outerWarmUpdateDynamic_outerFreeze(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    /**
     * Case 1) b) ii)
     */
    private fun test_outerWarmUpdateDynamic_outerFreeze_newInnerFrozenUpdate(
        outerCellFactory: DynamicCellFactory,
        initialInnerCellFactory: InertCellFactory,
        newInnerCellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doTriggerOuterUpdate = EmitterEventStream<Unit>()
        val doFreezeOuter = EmitterEventStream<Unit>()
        val doTriggerInnerFrozenUpdate = EmitterEventStream<Unit>()

        val initialInnerCell = initialInnerCellFactory.createInertExternally(
            inertValue = "aA",
        )

        val setup = run {
            val newInnerCell = newInnerCellFactory.createFreezingExternally(
                initialValue = "xX",
                doUpdateFreezing = doTriggerInnerFrozenUpdate.map { "yY" },
            )

            val outerCell = outerCellFactory.createFreezingLaterExternally(
                initialValue = initialInnerCell,
                doUpdate = doTriggerOuterUpdate.map { newInnerCell },
                doFreezeLater = doFreezeOuter,
            )

            val switchCell = Cell.switch(outerCell)

            Setup(
                innerCellWeakRef = PlatformWeakReference(newInnerCell),
                outerCellWeakRef = PlatformWeakReference(outerCell),
                switchCell = switchCell,
            )
        }

        val verifier = verificationStrategy.begin(
            subjectCell = setup.switchCell,
        )

        doTriggerOuterUpdate.emit()
        doFreezeOuter.emit()

        verifier.verifyUpdates(
            doTriggerUpdate = doTriggerInnerFrozenUpdate,
            expectedUpdatedValue = "yY",
        )
    }

    private fun test_outerWarmUpdateDynamic_outerFreeze_newInnerFrozenUpdate(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { outerCellFactory ->
            InertCellFactory.values.forEach { initialInnerCellFactory ->
                DynamicCellFactory.values.forEach { newInnerCellFactory ->
                    test_outerWarmUpdateDynamic_outerFreeze_newInnerFrozenUpdate(
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
    fun test_outerWarmUpdateDynamic_outerFreeze_newInnerFrozenUpdate_passive() {
        test_outerWarmUpdateDynamic_outerFreeze_newInnerFrozenUpdate(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerWarmUpdateDynamic_outerFreeze_newInnerFrozenUpdate_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerWarmUpdateDynamic_outerFreeze_newInnerFrozenUpdate(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerWarmUpdateDynamic_outerFreeze_newInnerFrozenUpdate_quick() {
        test_outerWarmUpdateDynamic_outerFreeze_newInnerFrozenUpdate(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    /**
     * Case 1) b) i)
     */
    private fun test_outerWarmUpdateDynamic_outerFreeze_newInnerWarmUpdate_innerFreeze(
        outerCellFactory: DynamicCellFactory,
        initialInnerCellFactory: InertCellFactory,
        newInnerCellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doTriggerOuterUpdate = EmitterEventStream<Unit>()
        val doFreezeOuter = EmitterEventStream<Unit>()
        val doTriggerInnerUpdate = EmitterEventStream<Unit>()
        val doFreezeInner = EmitterEventStream<Unit>()

        val initialInnerCell = initialInnerCellFactory.createInertExternally(
            inertValue = "aA",
        )

        val setup = run {
            val newInnerCell = newInnerCellFactory.createFreezingLaterExternally(
                initialValue = "xX",
                doUpdate = doTriggerInnerUpdate.map { "yY" },
                doFreezeLater = doFreezeInner,
            )

            val outerCell = outerCellFactory.createFreezingLaterExternally(
                initialValue = initialInnerCell,
                doUpdate = doTriggerOuterUpdate.map { newInnerCell },
                doFreezeLater = doFreezeOuter,
            )

            val switchCell = Cell.switch(outerCell)

            Setup(
                innerCellWeakRef = PlatformWeakReference(newInnerCell),
                outerCellWeakRef = PlatformWeakReference(outerCell),
                switchCell = switchCell,
            )
        }

        val verifier = verificationStrategy.begin(
            subjectCell = setup.switchCell,
        )

        doTriggerOuterUpdate.emit()
        doFreezeOuter.emit()

        verifier.verifyUpdates(
            doTriggerUpdate = doTriggerInnerUpdate,
            expectedUpdatedValue = "yY",
        )

        verifier.verifyDoesNotUpdate(
            doTriggerPotentialUpdate = doFreezeInner,
            expectedNonUpdatedValue = "yY",
        )
    }

    private fun test_outerWarmUpdateDynamic_outerFreeze_newInnerWarmUpdate_innerFreeze(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { outerCellFactory ->
            InertCellFactory.values.forEach { initialInnerCellFactory ->
                DynamicCellFactory.values.forEach { newInnerCellFactory ->
                    test_outerWarmUpdateDynamic_outerFreeze_newInnerWarmUpdate_innerFreeze(
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
    fun test_outerWarmUpdateDynamic_outerFreeze_newInnerWarmUpdate_innerFreeze_passive() {
        test_outerWarmUpdateDynamic_outerFreeze_newInnerWarmUpdate_innerFreeze(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerWarmUpdateDynamic_outerFreeze_newInnerWarmUpdate_innerFreeze_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerWarmUpdateDynamic_outerFreeze_newInnerWarmUpdate_innerFreeze(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerWarmUpdateDynamic_outerFreeze_newInnerWarmUpdate_innerFreeze_quick() {
        test_outerWarmUpdateDynamic_outerFreeze_newInnerWarmUpdate_innerFreeze(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    /**
     * Case 1) a)
     */
    private fun test_outerWarmUpdateDynamic_newInnerWarmUpdate(
        outerCellFactory: DynamicCellFactory,
        initialInnerCellFactory: InertCellFactory,
        newInnerCellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doUpdateOuter = EmitterEventStream<Unit>()

        val doUpdateNewInner = EmitterEventStream<Unit>()

        val initialInnerCell = initialInnerCellFactory.createInertExternally(10)

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
            doTriggerUpdate = doUpdateNewInner,
            expectedUpdatedValue = 21,
        )
    }

    private fun test_outerWarmUpdateDynamic_newInnerWarmUpdate(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { outerCellFactory ->
            InertCellFactory.values.forEach { initialInnerCellFactory ->
                DynamicCellFactory.values.forEach { newInnerCellFactory ->
                    test_outerWarmUpdateDynamic_newInnerWarmUpdate(
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
    fun test_outerWarmUpdateDynamic_newInnerWarmUpdate_passive() {
        test_outerWarmUpdateDynamic_newInnerWarmUpdate(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerWarmUpdateDynamic_newInnerWarmUpdate_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerWarmUpdateDynamic_newInnerWarmUpdate(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerWarmUpdateDynamic_newInnerWarmUpdate_quick() {
        test_outerWarmUpdateDynamic_newInnerWarmUpdate(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    /**
     * Case 1) a) iii)
     */
    private fun test_outerWarmUpdateDynamic_newInnerWarmUpdate_outerFreezeInnerFreeze(
        outerCellFactory: DynamicCellFactory,
        initialInnerCellFactory: InertCellFactory,
        newInnerCellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doTriggerOuterUpdate = EmitterEventStream<Unit>()
        val doTriggerInnerUpdate = EmitterEventStream<Unit>()
        val doFreezeOuterInner = EmitterEventStream<Unit>()

        val initialInnerCell = initialInnerCellFactory.createInertExternally(
            inertValue = "A",
        )

        val newInnerCell = newInnerCellFactory.createFreezingLaterExternally(
            initialValue = "X",
            doUpdate = doTriggerInnerUpdate.map { "Y" },
            doFreezeLater = doFreezeOuterInner,
        )

        val outerCell = outerCellFactory.createFreezingLaterExternally(
            initialValue = initialInnerCell,
            doUpdate = doTriggerOuterUpdate.map { newInnerCell },
            doFreezeLater = doFreezeOuterInner,
        )

        val switchCell = Cell.switch(outerCell)

        val verifier = verificationStrategy.begin(
            subjectCell = switchCell,
        )

        doTriggerOuterUpdate.emit()
        doTriggerInnerUpdate.emit()

        verifier.verifyDoesNotUpdate(
            doTriggerPotentialUpdate = doFreezeOuterInner,
            expectedNonUpdatedValue = "Y",
        )
    }

    private fun test_outerWarmUpdateDynamic_newInnerWarmUpdate_outerFreezeInnerFreeze(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { outerCellFactory ->
            InertCellFactory.values.forEach { initialInnerCellFactory ->
                DynamicCellFactory.values.forEach { newInnerCellFactory ->
                    test_outerWarmUpdateDynamic_newInnerWarmUpdate_outerFreezeInnerFreeze(
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
    fun test_outerWarmUpdateDynamic_newInnerWarmUpdate_outerFreezeInnerFreeze_passive() {
        test_outerWarmUpdateDynamic_newInnerWarmUpdate_outerFreezeInnerFreeze(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerWarmUpdateDynamic_newInnerWarmUpdate_outerFreezeInnerFreeze_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerWarmUpdateDynamic_newInnerWarmUpdate_outerFreezeInnerFreeze(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerWarmUpdateDynamic_newInnerWarmUpdate_outerFreezeInnerFreeze_quick() {
        test_outerWarmUpdateDynamic_newInnerWarmUpdate_outerFreezeInnerFreeze(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    /**
     * Case 1) a) i)
     */
    private fun test_outerWarmUpdateDynamic_newInnerWarmUpdate_outerFreeze_innerFreeze(
        outerCellFactory: DynamicCellFactory,
        initialInnerCellFactory: InertCellFactory,
        newInnerCellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doTriggerOuterUpdate = EmitterEventStream<Unit>()
        val doTriggerInnerUpdate = EmitterEventStream<Unit>()
        val doFreezeOuter = EmitterEventStream<Unit>()
        val doFreezeInner = EmitterEventStream<Unit>()

        val initialInnerCell = initialInnerCellFactory.createInertExternally(
            inertValue = "A",
        )

        val newInnerCell = newInnerCellFactory.createFreezingLaterExternally(
            initialValue = "X",
            doUpdate = doTriggerInnerUpdate.map { "Y" },
            doFreezeLater = doFreezeInner,
        )

        val outerCell = outerCellFactory.createFreezingLaterExternally(
            initialValue = initialInnerCell,
            doUpdate = doTriggerOuterUpdate.map { newInnerCell },
            doFreezeLater = doFreezeOuter,
        )

        val switchCell = Cell.switch(outerCell)

        val verifier = verificationStrategy.begin(
            subjectCell = switchCell,
        )

        doTriggerOuterUpdate.emit()
        doTriggerInnerUpdate.emit()

        verifier.verifyDoesNotUpdate(
            doTriggerPotentialUpdate = doFreezeOuter,
            expectedNonUpdatedValue = "Y",
        )

        verifier.verifyDoesNotUpdate(
            doTriggerPotentialUpdate = doFreezeInner,
            expectedNonUpdatedValue = "Y",
        )
    }

    private fun test_outerWarmUpdateDynamic_newInnerWarmUpdate_outerFreeze_innerFreeze(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { outerCellFactory ->
            InertCellFactory.values.forEach { initialInnerCellFactory ->
                DynamicCellFactory.values.forEach { newInnerCellFactory ->
                    test_outerWarmUpdateDynamic_newInnerWarmUpdate_outerFreeze_innerFreeze(
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
    fun test_outerWarmUpdateDynamic_newInnerWarmUpdate_outerFreeze_innerFreeze_passive() {
        test_outerWarmUpdateDynamic_newInnerWarmUpdate_outerFreeze_innerFreeze(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerWarmUpdateDynamic_newInnerWarmUpdate_outerFreeze_innerFreeze_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerWarmUpdateDynamic_newInnerWarmUpdate_outerFreeze_innerFreeze(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerWarmUpdateDynamic_newInnerWarmUpdate_outerFreeze_innerFreeze_quick() {
        test_outerWarmUpdateDynamic_newInnerWarmUpdate_outerFreeze_innerFreeze(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    /**
     * Case 1) a) ii)
     */
    private fun test_outerWarmUpdateDynamic_newInnerWarmUpdate_innerFreeze_outerFreeze(
        outerCellFactory: DynamicCellFactory,
        initialInnerCellFactory: InertCellFactory,
        newInnerCellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doTriggerOuterUpdate = EmitterEventStream<Unit>()
        val doTriggerInnerUpdate = EmitterEventStream<Unit>()
        val doFreezeInner = EmitterEventStream<Unit>()
        val doFreezeOuter = EmitterEventStream<Unit>()

        val initialInnerCell = initialInnerCellFactory.createInertExternally(
            inertValue = "a",
        )

        val newInnerCell = newInnerCellFactory.createFreezingLaterExternally(
            initialValue = "x",
            doUpdate = doTriggerInnerUpdate.map { "y" },
            doFreezeLater = doFreezeInner,
        )

        val outerCell = outerCellFactory.createFreezingLaterExternally(
            initialValue = initialInnerCell,
            doUpdate = doTriggerOuterUpdate.map { newInnerCell },
            doFreezeLater = doFreezeOuter,
        )

        val switchCell = Cell.switch(outerCell)

        val verifier = verificationStrategy.begin(
            subjectCell = switchCell,
        )

        doTriggerOuterUpdate.emit()
        doTriggerInnerUpdate.emit()

        verifier.verifyDoesNotUpdate(
            doTriggerPotentialUpdate = doFreezeInner,
            expectedNonUpdatedValue = "y",
        )

        verifier.verifyDoesNotUpdate(
            doTriggerPotentialUpdate = doFreezeOuter,
            expectedNonUpdatedValue = "y",
        )
    }

    private fun test_outerWarmUpdateDynamic_newInnerWarmUpdate_innerFreeze_outerFreeze(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { outerCellFactory ->
            InertCellFactory.values.forEach { initialInnerCellFactory ->
                DynamicCellFactory.values.forEach { newInnerCellFactory ->
                    test_outerWarmUpdateDynamic_newInnerWarmUpdate_innerFreeze_outerFreeze(
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
    fun test_outerWarmUpdateDynamic_newInnerWarmUpdate_innerFreeze_outerFreeze_passive() {
        test_outerWarmUpdateDynamic_newInnerWarmUpdate_innerFreeze_outerFreeze(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerWarmUpdateDynamic_newInnerWarmUpdate_innerFreeze_outerFreeze_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerWarmUpdateDynamic_newInnerWarmUpdate_innerFreeze_outerFreeze(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerWarmUpdateDynamic_newInnerWarmUpdate_innerFreeze_outerFreeze_quick() {
        test_outerWarmUpdateDynamic_newInnerWarmUpdate_innerFreeze_outerFreeze(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    /**
     * Case 1) e)
     */
    private fun test_outerWarmUpdateDynamic_outerFreezeNewInnerFrozenUpdate(
        outerCellFactory: DynamicCellFactory,
        initialInnerCellFactory: InertCellFactory,
        newInnerCellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doTriggerOuterUpdate = EmitterEventStream<Unit>()
        val doFreezeOuterTriggerInnerFrozenUpdate = EmitterEventStream<Unit>()

        val initialInnerCell = initialInnerCellFactory.createInertExternally(
            inertValue = "aA",
        )

        val newInnerCell = newInnerCellFactory.createFreezingLaterExternally(
            initialValue = "xX",
            doUpdate = doFreezeOuterTriggerInnerFrozenUpdate.map { "yY" },
            doFreezeLater = doFreezeOuterTriggerInnerFrozenUpdate,
        )

        val outerCell = outerCellFactory.createFreezingExternally(
            initialValue = initialInnerCell,
            doUpdateFreezing = doTriggerOuterUpdate.map { newInnerCell },
        )

        val switchCell = Cell.switch(outerCell)

        val verifier = verificationStrategy.begin(
            subjectCell = switchCell,
        )

        doTriggerOuterUpdate.emit()

        verifier.verifyUpdates(
            doTriggerUpdate = doFreezeOuterTriggerInnerFrozenUpdate,
            expectedUpdatedValue = "yY",
        )
    }

    private fun test_outerWarmUpdateDynamic_outerFreezeNewInnerFrozenUpdate(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { outerCellFactory ->
            InertCellFactory.values.forEach { initialInnerCellFactory ->
                DynamicCellFactory.values.forEach { newInnerCellFactory ->
                    test_outerWarmUpdateDynamic_outerFreezeNewInnerFrozenUpdate(
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
    fun test_outerWarmUpdateDynamic_outerFreezeNewInnerFrozenUpdate_passive() {
        test_outerWarmUpdateDynamic_outerFreezeNewInnerFrozenUpdate(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerWarmUpdateDynamic_outerFreezeNewInnerFrozenUpdate_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerWarmUpdateDynamic_outerFreezeNewInnerFrozenUpdate(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerWarmUpdateDynamic_outerFreezeNewInnerFrozenUpdate_quick() {
        test_outerWarmUpdateDynamic_outerFreezeNewInnerFrozenUpdate(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    /**
     * Case 1) c)
     */
    private fun test_outerWarmUpdateDynamic_outerFreezeNewInnerWarmUpdate_newInnerFreeze(
        outerCellFactory: DynamicCellFactory,
        initialInnerCellFactory: InertCellFactory,
        newInnerCellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doTriggerOuterUpdate = EmitterEventStream<Unit>()
        val doFreezeOuterTriggerInnerUpdate = EmitterEventStream<Unit>()
        val doFreezeInner = EmitterEventStream<Unit>()

        val initialInnerCell = initialInnerCellFactory.createInertExternally(
            inertValue = "aA",
        )

        val newInnerCell = newInnerCellFactory.createFreezingLaterExternally(
            initialValue = "xX",
            doUpdate = doFreezeOuterTriggerInnerUpdate.map { "yY" },
            doFreezeLater = doFreezeInner,
        )

        val outerCell = outerCellFactory.createFreezingLaterExternally(
            initialValue = initialInnerCell,
            doUpdate = doTriggerOuterUpdate.map { newInnerCell },
            doFreezeLater = doFreezeOuterTriggerInnerUpdate,
        )

        val switchCell = Cell.switch(outerCell)

        val verifier = verificationStrategy.begin(
            subjectCell = switchCell,
        )

        doTriggerOuterUpdate.emit()

        verifier.verifyUpdates(
            doTriggerUpdate = doFreezeOuterTriggerInnerUpdate,
            expectedUpdatedValue = "yY",
        )

        verifier.verifyDoesNotUpdate(
            doTriggerPotentialUpdate = doFreezeInner,
            expectedNonUpdatedValue = "yY",
        )
    }

    private fun test_outerWarmUpdateDynamic_outerFreezeNewInnerWarmUpdate_newInnerFreeze(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { outerCellFactory ->
            InertCellFactory.values.forEach { initialInnerCellFactory ->
                DynamicCellFactory.values.forEach { newInnerCellFactory ->
                    test_outerWarmUpdateDynamic_outerFreezeNewInnerWarmUpdate_newInnerFreeze(
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
    fun test_outerWarmUpdateDynamic_outerFreezeNewInnerWarmUpdate_newInnerFreeze_passive() {
        test_outerWarmUpdateDynamic_outerFreezeNewInnerWarmUpdate_newInnerFreeze(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerWarmUpdateDynamic_outerFreezeNewInnerWarmUpdate_newInnerFreeze_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerWarmUpdateDynamic_outerFreezeNewInnerWarmUpdate_newInnerFreeze(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerWarmUpdateDynamic_outerFreezeNewInnerWarmUpdate_newInnerFreeze_quick() {
        test_outerWarmUpdateDynamic_outerFreezeNewInnerWarmUpdate_newInnerFreeze(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    /**
     * Case 1) d)
     */
    private fun test_outerWarmUpdateDynamic_newInnerFrozenUpdate_outerFreeze(
        outerCellFactory: DynamicCellFactory,
        initialInnerCellFactory: InertCellFactory,
        newInnerCellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doTriggerOuterUpdate = EmitterEventStream<Unit>()
        val doTriggerInnerFrozenUpdate = EmitterEventStream<Unit>()
        val doFreezeOuter = EmitterEventStream<Unit>()

        val initialInnerCell = initialInnerCellFactory.createInertExternally(
            inertValue = "aA",
        )

        val newInnerCell = newInnerCellFactory.createFreezingExternally(
            initialValue = "xX",
            doUpdateFreezing = doTriggerInnerFrozenUpdate.map { "yY" },
        )

        val outerCell = outerCellFactory.createFreezingLaterExternally(
            initialValue = initialInnerCell,
            doUpdate = doTriggerOuterUpdate.map { newInnerCell },
            doFreezeLater = doFreezeOuter,
        )

        val switchCell = Cell.switch(outerCell)

        val verifier = verificationStrategy.begin(
            subjectCell = switchCell,
        )

        doTriggerOuterUpdate.emit()

        verifier.verifyUpdates(
            doTriggerUpdate = doTriggerInnerFrozenUpdate,
            expectedUpdatedValue = "yY",
        )

        verifier.verifyDoesNotUpdate(
            doTriggerPotentialUpdate = doFreezeOuter,
            expectedNonUpdatedValue = "yY",
        )
    }

    private fun test_outerWarmUpdateDynamic_newInnerFrozenUpdate_outerFreeze(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { outerCellFactory ->
            InertCellFactory.values.forEach { initialInnerCellFactory ->
                DynamicCellFactory.values.forEach { newInnerCellFactory ->
                    test_outerWarmUpdateDynamic_newInnerFrozenUpdate_outerFreeze(
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
    fun test_outerWarmUpdateDynamic_newInnerFrozenUpdate_outerFreeze_passive() {
        test_outerWarmUpdateDynamic_newInnerFrozenUpdate_outerFreeze(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerWarmUpdateDynamic_newInnerFrozenUpdate_outerFreeze_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerWarmUpdateDynamic_newInnerFrozenUpdate_outerFreeze(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerWarmUpdateDynamic_newInnerFrozenUpdate_outerFreeze_quick() {
        test_outerWarmUpdateDynamic_newInnerFrozenUpdate_outerFreeze(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }
}
