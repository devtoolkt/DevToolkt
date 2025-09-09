package dev.toolkt.core.collections

interface MutablePrefixSumIndexedList<E> : MutableStableList<E>, PrefixSumIndexedList<E>

private class MutablePrefixSumIndexedListImpl<E>(
    private val mutableIndexedList: MutableIndexedList<E>,
    private val selector: (E) -> Int,
) : MutablePrefixSumIndexedList<E>, MutableIndexedList<E> by mutableIndexedList {
    // TODO: Implement this using a fully-dynamic binary indexed tree ((order statistic)-alike tree)
    override fun calculatePrefixSum(
        count: Int,
    ): Int = mutableIndexedList.asSequence().take(count).sumOf(selector)
}

fun <E> mutablePrefixSumIndexedListOf(
    selector: (E) -> Int,
    vararg elements: E,
): MutablePrefixSumIndexedList<E> = MutablePrefixSumIndexedListImpl(
    mutableIndexedList = mutableIndexedListOf(*elements),
    selector = selector,
)
