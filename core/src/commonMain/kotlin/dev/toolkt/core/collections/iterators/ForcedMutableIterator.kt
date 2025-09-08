package dev.toolkt.core.collections.iterators

fun <E> Iterator<E>.forceMutable(): MutableIterator<E> = ForcedMutableIterator(
    iterator = this,
)

private class ForcedMutableIterator<E>(
    private val iterator: Iterator<E>,
) : MutableIterator<E>, Iterator<E> by iterator {
    override fun remove() {
        throw UnsupportedOperationException()
    }
}
