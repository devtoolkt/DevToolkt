package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.sample
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.emit
import kotlin.test.assertEquals

fun <ValueT> Cell<ValueT>.sampleExternally(): ValueT = MomentContext.execute {
    sample()
}

fun <ValueT> assertUpdates(
    subjectCell: Cell<ValueT>,
    updateVerificationProcess: UpdateVerificationProcess<ValueT>?,
    doTrigger: EmitterEventStream<Unit>,
    expectedUpdatedValue: ValueT,
) {
    val updateVerifier = updateVerificationProcess?.prepareVerifier(
        onTriggered = doTrigger,
    )

    doTrigger.emit()

    if (updateVerifier != null) {
        val updatedValue = updateVerifier.verifyUpdated()

        assertEquals(
            expected = expectedUpdatedValue,
            actual = updatedValue,
        )
    }

    val sampledNewValue = subjectCell.sampleExternally()

    assertEquals(
        expected = expectedUpdatedValue,
        actual = sampledNewValue,
    )
}

fun <ValueT> assertDoesNotUpdate(
    subjectCell: Cell<ValueT>,
    updateVerificationProcess: UpdateVerificationProcess.Total<ValueT>?,
    doTrigger: EmitterEventStream<Unit>,
    expectedNonUpdatedValue: ValueT,
) {
    val updateVerifier = updateVerificationProcess?.prepareVerifier(
        onTriggered = doTrigger,
    )

    doTrigger.emit()

    updateVerifier?.verifyDidNotUpdate()

    val sampledNewValue = subjectCell.sampleExternally()

    assertEquals(
        expected = expectedNonUpdatedValue,
        actual = sampledNewValue,
    )
}
