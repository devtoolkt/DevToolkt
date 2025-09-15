package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.DependencyVertex

interface DependencyEventStreamVertex<EventT> : DynamicEventStreamVertex<EventT>, DependencyVertex
