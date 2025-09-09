package dev.toolkt.core.collections.bags

import dev.toolkt.core.collections.StableCollection

/**
 * A read-only bag providing stable handles to its elements. In fact, any useful implementation of a bag should provide
 * stable handles.
 */
interface StableBag<out E> : StableCollection<E>
