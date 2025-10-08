package dev.toolkt.reactive.cell

import dev.toolkt.core.platform.PlatformWeakReference
import dev.toolkt.reactive.cell.test_utils.CellVerificationStrategy
import dev.toolkt.reactive.cell.test_utils.DynamicCellFactory
import dev.toolkt.reactive.cell.test_utils.InertCellFactory
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.map
import kotlin.test.Ignore
import kotlin.test.Test

@Suppress("ClassName")
class Cell_switch_state_inertOuter_tests {
    private data class Setup(
        val innerCellWeakRef: PlatformWeakReference<Cell<Int>>,
        val outerCellWeakRef: PlatformWeakReference<Cell<Cell<Int>>>,
        val switchCell: Cell<Int>,
    )

    private fun test_state_inertInner_initial(
        outerCellFactory: InertCellFactory,
        innerCellFactory: InertCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val (outerCellWeakRef, switchCell) = run {
            val outerCell = outerCellFactory.createInertExternally(
                inertValue = innerCellFactory.createInertExternally(
                    inertValue = 10,
                ),
            )

            val switchCell = Cell.switch(outerCell)

            Pair(
                PlatformWeakReference(outerCell),
                switchCell,
            )
        }

        val verifier = verificationStrategy.begin(
            subjectCell = switchCell,
        )

        verifier.verifyCurrentValue(
            expectedCurrentValue = 10,
        )

        // TODO: Verify collectibility
    }

    private fun test_state_inertInner_initial(
        verificationStrategy: CellVerificationStrategy,
    ) {
        InertCellFactory.values.forEach { outerCellFactory ->
            InertCellFactory.values.forEach { innerCellFactory ->
                test_state_inertInner_initial(
                    outerCellFactory = outerCellFactory,
                    innerCellFactory = innerCellFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
        }
    }

    @Test
    fun test_state_inertInner_initial_passive() {
        test_state_inertInner_initial(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Ignore // FIXME: Subscription should not be null.
    @Test
    fun test_state_inertInner_initial_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_state_inertInner_initial(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_state_dynamicInner(
        outerCellFactory: InertCellFactory,
        innerCellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val outerCell = outerCellFactory.createInertExternally(
            inertValue = innerCellFactory.createDynamicExternally(
                initialValue = 10,
            ),
        )

        val switchCell = Cell.switch(outerCell)

        val verifier = verificationStrategy.begin(
            subjectCell = switchCell,
        )

        verifier.verifyCurrentValue(
            expectedCurrentValue = 10,
        )
    }

    private fun test_state_dynamicInner(
        verificationStrategy: CellVerificationStrategy,
    ) {
        InertCellFactory.values.forEach { outerCellFactory ->
            DynamicCellFactory.values.forEach { innerCellFactory ->
                test_state_dynamicInner(
                    outerCellFactory = outerCellFactory,
                    innerCellFactory = innerCellFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
        }
    }

    @Test
    fun test_state_dynamicInner_passive() {
        test_state_dynamicInner(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Ignore // FIXME: Subscription should not be null.
    @Test
    fun test_state_dynamicInner_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_state_dynamicInner(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_state_innerWarmUpdate_innerFreeze(
        outerCellFactory: InertCellFactory,
        innerCellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doTriggerInnerUpdate = EmitterEventStream<Unit>()
        val doFreezeInner = EmitterEventStream<Unit>()

        val setup = run {
            val innerCell = innerCellFactory.createFreezingLaterExternally(
                initialValue = 10,
                doUpdate = doTriggerInnerUpdate.map { 20 },
                doFreezeLater = doFreezeInner,
            )

            val outerCell = outerCellFactory.createInertExternally(
                inertValue = innerCell,
            )

            val switchCell = Cell.switch(outerCell)

            Setup(
                innerCellWeakRef = PlatformWeakReference(innerCell),
                outerCellWeakRef = PlatformWeakReference(outerCell),
                switchCell = switchCell,
            )
        }

        val verifier = verificationStrategy.begin(
            subjectCell = setup.switchCell,
        )

        verifier.verifyUpdates(
            doTriggerUpdate = doTriggerInnerUpdate,
            expectedUpdatedValue = 20,
        )

        verifier.verifyDoesNotUpdate(
            doTriggerPotentialUpdate = doFreezeInner,
            expectedNonUpdatedValue = 20,
        )
    }

    private fun test_state_innerWarmUpdate_innerFreeze(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { innerCellFactory ->
            InertCellFactory.values.forEach { outerCellFactory ->
                test_state_innerWarmUpdate_innerFreeze(
                    outerCellFactory = outerCellFactory,
                    innerCellFactory = innerCellFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
        }
    }

    @Test
    fun test_state_innerWarmUpdate_innerFreeze_passive() {
        test_state_innerWarmUpdate_innerFreeze(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_state_innerWarmUpdate_innerFreeze_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_state_innerWarmUpdate_innerFreeze(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_state_innerFrozenUpdate(
        outerCellFactory: InertCellFactory,
        innerCellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doTriggerInnerFrozenUpdate = EmitterEventStream<Unit>()

        val setup = run {
            val innerCell = innerCellFactory.createFreezingExternally(
                initialValue = 10,
                doUpdateFreezing = doTriggerInnerFrozenUpdate.map { 20 },
            )

            val outerCell = outerCellFactory.createInertExternally(
                inertValue = innerCell,
            )

            val switchCell = Cell.switch(outerCell)

            Setup(
                innerCellWeakRef = PlatformWeakReference(innerCell),
                outerCellWeakRef = PlatformWeakReference(outerCell),
                switchCell = switchCell,
            )
        }

        val verifier = verificationStrategy.begin(
            subjectCell = setup.switchCell,
        )

        verifier.verifyUpdates(
            doTriggerUpdate = doTriggerInnerFrozenUpdate,
            expectedUpdatedValue = 20,
        )
    }

    private fun test_state_innerFrozenUpdate(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { innerCellFactory ->
            InertCellFactory.values.forEach { outerCellFactory ->
                test_state_innerFrozenUpdate(
                    outerCellFactory = outerCellFactory,
                    innerCellFactory = innerCellFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
        }
    }

    @Test
    fun test_state_innerFrozenUpdate_passive() {
        test_state_innerFrozenUpdate(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_state_innerFrozenUpdate_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_state_innerFrozenUpdate(
                verificationStrategy = verificationStrategy,
            )
        }
    }
}
