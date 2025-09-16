package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.DynamicDependencyVertex

interface DependencyEventStreamVertex<EventT> : DynamicEventStreamVertex<EventT>, DynamicDependencyVertex
