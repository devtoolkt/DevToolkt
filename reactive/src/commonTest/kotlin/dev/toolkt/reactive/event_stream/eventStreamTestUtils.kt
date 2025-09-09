package dev.toolkt.reactive.event_stream

fun <EventT> EventStream<EventT>.subscribeCollecting(
    targetList: MutableList<EventT>,
): EventStream.Subscription = this.subscribe { event ->
    targetList.add(event)
}
