package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.test_utils.DynamicTestContext

// Leave these unimplemented for now

fun <InputT, ValueT> testStatefulCell_observedInstantly_verifyInitial(
    configure: context (DynamicTestContext) () -> InputT,
    spawnTick: Tick,
    spawn: context(MomentContext) InputT.() -> Cell<ValueT>,
    expectedInitialObservedValue: ValueT,
) {
    TODO()
}

fun <InputT, ValueT> testStatefulCell_observedLater_verifyInitialInert(
    configure: context (DynamicTestContext) () -> InputT,
    spawnTick: Tick,
    spawn: context(MomentContext) InputT.() -> Cell<ValueT>,
    observationTick: Tick,
    expectedObservedValue: ValueT,
) {
    TODO()
}

fun <InputT, ValueT> testStatefulCell_observedInstantly_verifyFreezesLater(
    configure: context (DynamicTestContext) () -> InputT,
    spawnTick: Tick,
    spawn: context(MomentContext) InputT.() -> Cell<ValueT>,
    expectedFreezeTick: Tick,
) {
    TODO()
}

fun <InputT, ValueT> testStatefulCell_verifyFreezesInstantly(
    configure: context (DynamicTestContext) () -> InputT,
    spawnTick: Tick,
    spawn: context(MomentContext) InputT.() -> Cell<ValueT>,
) {
    TODO()
}

fun <InputT, ValueT> testStatefulCell_observedInstantly_verifyUpdatesLater(
    configure: context (DynamicTestContext) () -> InputT,
    spawnTick: Tick,
    spawn: context(MomentContext) InputT.() -> Cell<ValueT>,
    expectedUpdateTick: Tick,
    expectedUpdatedValue: ValueT,
) {
    TODO()
}

fun <InputT, ValueT> testStatefulCell_verifyUpdatesInstantly(
    configure: context (DynamicTestContext) () -> InputT,
    spawnTick: Tick,
    spawn: context(MomentContext) InputT.() -> Cell<ValueT>,
    expectedUpdatedValue: ValueT,
) {
    TODO()
}

fun <InputT, ValueT> testStatefulCell_observedInstantly_verifyUpdatesFreezingLater(
    configure: context (DynamicTestContext) () -> InputT,
    spawnTick: Tick,
    spawn: context(MomentContext) InputT.() -> Cell<ValueT>,
    expectedFreezingUpdateTick: Tick,
    expectedUpdatedValue: ValueT,
) {
    TODO()
}

fun <InputT, ValueT> testStatefulCell_verifyUpdatesFreezingInstantly(
    configure: context (DynamicTestContext) () -> InputT,
    spawnTick: Tick,
    spawn: context(MomentContext) InputT.() -> Cell<ValueT>,
    expectedUpdatedValue: ValueT,
) {
    TODO()
}
