package dev.toolkt.core.collections.lists

/**
 * An indexed list of integers providing an additional efficient operation for
 * calculating the prefix sum of selected elements.
 */
interface PrefixSumIndexedList<out ElementT> : IndexedList<ElementT> {
    /**
     * Calculates the sum of the first [count] selected elements in the list.
     * Guarantees logarithmic time complexity.
     */
    fun calculatePrefixSum(
        count: Int,
    ): Int
}