package dev.toolkt.core.utils.iterator

fun <T : Any> Iterator<T>.nextOrNull(): T? = when {
    hasNext() -> next()
    else -> null
}
