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

    private fun test_inertInner(
        outerCellFactory: InertCellFactory,
        innerCellFactory: InertCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val (outerCellWeakRef, switchCell) = run {
            val outerCell = outerCellFactory.createExternally(
                inertValue = innerCellFactory.createExternally(
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
    }

    private fun test_inertInner(
        verificationStrategy: CellVerificationStrategy,
    ) {
        InertCellFactory.values.forEach { outerCellFactory ->
            InertCellFactory.values.forEach { innerCellFactory ->
                test_inertInner(
                    outerCellFactory = outerCellFactory,
                    innerCellFactory = innerCellFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
        }
    }

    @Test
    fun test_inertInner_passive() {
        test_inertInner(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Ignore // FIXME: Subscription should not be null.
    @Test
    fun test_inertInner_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_inertInner(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_innerWarmUpdate_innerFreeze(
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

            val outerCell = outerCellFactory.createExternally(
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

    private fun test_innerWarmUpdate_innerFreeze(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { innerCellFactory ->
            InertCellFactory.values.forEach { outerCellFactory ->
                test_innerWarmUpdate_innerFreeze(
                    outerCellFactory = outerCellFactory,
                    innerCellFactory = innerCellFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
        }
    }

    @Test
    fun test_innerWarmUpdate_innerFreeze_passive() {
        test_innerWarmUpdate_innerFreeze(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_innerWarmUpdate_innerFreeze_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_innerWarmUpdate_innerFreeze(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_innerFrozenUpdate(
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

            val outerCell = outerCellFactory.createExternally(
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

    private fun test_innerFrozenUpdate(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { innerCellFactory ->
            InertCellFactory.values.forEach { outerCellFactory ->
                test_innerFrozenUpdate(
                    outerCellFactory = outerCellFactory,
                    innerCellFactory = innerCellFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
        }
    }

    @Test
    fun test_innerFrozenUpdate_passive() {
        test_innerFrozenUpdate(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_innerFrozenUpdate_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_innerFrozenUpdate(
                verificationStrategy = verificationStrategy,
            )
        }
    }
}
