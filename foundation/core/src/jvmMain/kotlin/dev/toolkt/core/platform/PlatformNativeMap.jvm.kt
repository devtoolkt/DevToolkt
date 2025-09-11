package dev.toolkt.core.platform

actual class PlatformNativeMap<K : Any, V : Any>(
    private val mutableMap: MutableMap<K, V>,
) {
    actual constructor() : this(
        mutableMap = mutableMapOf(),
    )

    actual val size: Int
        get() = mutableMap.size

    actual operator fun get(
        key: K,
    ): V? = mutableMap[key]

    actual fun containsKey(
        key: K,
    ): Boolean = mutableMap.contains(key = key)

    actual fun containsValue(
        value: V,
    ): Boolean = mutableMap.containsValue(value = value)

    actual fun forEach(
        callback: (K, V) -> Unit,
    ) {
        mutableMap.forEach { (key, value) ->
            callback(key, value)
        }
    }

    actual fun put(
        key: K,
        value: V,
    ): V? = mutableMap.put(
        key = key,
        value = value,
    )

    actual fun set(
        key: K,
        value: V,
    ) {
        mutableMap[key] = value
    }

    actual fun extract(
        key: K,
    ): V? = mutableMap.remove(
        key = key,
    )

    actual fun remove(
        key: K,
    ): Boolean = mutableMap.remove(
        key = key,
    ) != null

    actual fun clear() {
        mutableMap.clear()
    }

    actual fun copy(): PlatformNativeMap<K, V> = PlatformNativeMap(
        mutableMap = mutableMap.toMutableMap(),
    )
}
