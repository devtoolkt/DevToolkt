package dev.toolkt.reactive.cell.test_utils.timeline

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.mapNotNull
import dev.toolkt.reactive.event_stream.take

class InputDefinitionContext(
    private val momentContext: MomentContext,
    private val ticker: TimelineTicker,
) {
    companion object {
        fun <InputT> build(
            defineInput: context (InputDefinitionContext) () -> InputT,
            ticker: TimelineTicker,
        ): Pair<InputT, RawTick> = MomentContext.execute {
            val inputDefinitionContext = InputDefinitionContext(
                momentContext = MomentContext.extract(),
                ticker = ticker,
            )

            val configuredInput = with(inputDefinitionContext) {
                defineInput()
            }

            val lastInputTick = inputDefinitionContext.getLastInputTick()

            inputDefinitionContext.close()

            Pair(configuredInput, lastInputTick)
        }
    }

    private var isClosed = false

    private var tLast = 0

    fun registerInputTick(
        tick: RawTick,
    ) {
        require(!isClosed) {
            "InputDefinitionContext is already closed."
        }

        if (tick.t > tLast) {
            tLast = tick.t
        }
    }

    fun getMomentContext(): MomentContext {
        require(!isClosed) {
            "InputDefinitionContext is already closed."
        }

        return momentContext
    }

    fun getTicker(): TimelineTicker {
        require(!isClosed) {
            "InputDefinitionContext is already closed."
        }

        return ticker
    }

    fun close() {
        isClosed = true
    }

    fun getLastInputTick(): RawTick {
        require(!isClosed) {
            "InputDefinitionContext is already closed."
        }

        return dev.toolkt.reactive.cell.test_utils.timeline.RawTick(t = tLast)
    }
}

context(inputDefinitionContext: InputDefinitionContext) fun <EventT : Any> defineInputEventStream(
    emittedEventByNamedTick: Map<BaseNamedTick, EventT>,
    terminationNamedTick: BaseNamedTick?,
): EventStream<EventT> {
    val onTick = inputDefinitionContext.getTicker().onTick
    val terminationTick = terminationNamedTick?.ordinalTick

    emittedEventByNamedTick.forEach { (tickAlike, _) ->
        inputDefinitionContext.registerInputTick(
            tick = tickAlike.ordinalTick,
        )
    }

    if (terminationTick != null) {
        emittedEventByNamedTick.forEach { (emissionTick) ->
            require(!emissionTick.ordinalTick.isLaterThan(terminationTick)) {
                "Emission tick (t=${emissionTick.ordinalTick.t}) cannot be later than the termination tick (t=${terminationTick.t})."
            }
        }

        inputDefinitionContext.registerInputTick(
            tick = terminationTick,
        )
    }

    val emittedEventByTick = emittedEventByNamedTick.entries.associate { (tickId, emittedEvent) ->
        tickId.ordinalTick to emittedEvent
    }

    val onTickTrimmed = when (terminationTick) {
        null -> onTick

        else -> MomentContext.execute {
            onTick.take(terminationTick.t + 1)
        }
    }

    return onTickTrimmed.mapNotNull { tick ->
        emittedEventByTick[tick]
    }
}

context(inputDefinitionContext: InputDefinitionContext) fun <ValueT : Any> defineInputCell(
    initialValue: ValueT,
    updatedValueByNamedTick: Map<BaseNamedTick, ValueT>,
    freezeNamedTick: BaseNamedTick?,
): Cell<ValueT> {
    val onTick = inputDefinitionContext.getTicker().onTick
    val freezeTick = freezeNamedTick?.ordinalTick

    updatedValueByNamedTick.forEach { (tickAlike, _) ->
        inputDefinitionContext.registerInputTick(
            tick = tickAlike.ordinalTick,
        )
    }

    freezeTick?.let {
        inputDefinitionContext.registerInputTick(
            tick = it,
        )
    }

    val updatedValueByTick = updatedValueByNamedTick.entries.associate { (tickId, updatedValue) ->
        tickId.ordinalTick to updatedValue
    }

    val onTickTrimmed = when (freezeTick) {
        null -> onTick

        else -> MomentContext.execute {
            onTick.take(freezeTick.t + 1)
        }
    }

    val newValues = onTickTrimmed.mapNotNull { tick ->
        updatedValueByTick[tick]
    }

    return with(inputDefinitionContext.getMomentContext()) {
        Cell.define(
            initialValue = initialValue,
            newValues = newValues,
        )
    }
}
