package dev.toolkt.core.collections.maps

data class MapEntry<K, out V>(
    override val key: K,
    override val value: V,
) : Map.Entry<K, V>