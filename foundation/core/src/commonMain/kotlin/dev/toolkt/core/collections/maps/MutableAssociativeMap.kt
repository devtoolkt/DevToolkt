package dev.toolkt.core.collections.maps

import dev.toolkt.core.collections.MutableAssociativeCollection

interface MutableAssociativeMap<K, V> : MutableMap<K, V>, MutableAssociativeCollection<K, V>
