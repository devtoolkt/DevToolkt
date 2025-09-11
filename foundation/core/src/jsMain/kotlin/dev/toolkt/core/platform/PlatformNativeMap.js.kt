package dev.toolkt.core.platform

import dev.toolkt.js.collections.JsMap
import dev.toolkt.js.collections.clear
import dev.toolkt.js.collections.delete
import dev.toolkt.js.collections.forEach
import dev.toolkt.js.collections.get
import dev.toolkt.js.collections.has
import dev.toolkt.js.collections.hasValue
import dev.toolkt.js.collections.set
import dev.toolkt.js.collections.size
import kotlin.js.collections.JsMap

actual class PlatformNativeMap<K : Any, V : Any>(
    private val jsMap: JsMap<K, V>,
) {
    actual constructor() : this(jsMap = JsMap())

    actual val size: Int
        get() = jsMap.size

    actual operator fun get(
        key: K,
    ): V? = jsMap.get(key)

    actual fun containsKey(
        key: K,
    ): Boolean = jsMap.has(key)

    actual fun containsValue(
        value: V,
    ): Boolean = jsMap.hasValue(value)

    actual fun forEach(
        callback: (K, V) -> Unit,
    ) {
        jsMap.forEach { value, key, _ ->
            callback(key, value)
        }
    }

    actual fun put(
        key: K,
        value: V,
    ): V? {
        val oldValue = get(key)

        jsMap.set(
            key = key,
            value = value,
        )

        return oldValue
    }

    actual fun set(
        key: K,
        value: V,
    ) {
        jsMap.set(
            key = key,
            value = value,
        )
    }

    actual fun extract(
        key: K,
    ): V? {
        val oldValue = get(key = key)

        jsMap.delete(key = key)

        return oldValue
    }

    actual fun remove(
        key: K,
    ): Boolean = jsMap.delete(key = key)

    actual fun clear() {
        jsMap.clear()
    }

    actual fun copy(): PlatformNativeMap<K, V> = PlatformNativeMap(
        JsMap(
            iterable = jsMap,
        ),
    )
}
